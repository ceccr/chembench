package edu.unc.ceccr.taskObjects;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;

import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.Descriptors;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.utilities.DatasetFileOperations;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.workflows.CSV_X_Workflow;
import edu.unc.ceccr.workflows.CheckDescriptorsFileWorkflow;
import edu.unc.ceccr.workflows.DataSplitWorkflow;
import edu.unc.ceccr.workflows.GenerateDescriptorWorkflow;
import edu.unc.ceccr.workflows.ReadDescriptorsFileWorkflow;
import edu.unc.ceccr.workflows.SdfToJpgWorkflow;
import edu.unc.ceccr.workflows.StandardizeMoleculesWorkflow;

public class CreateDatasetTask extends WorkflowTask{

	private String userName = null;
	private String datasetType; 
	private String sdfFileName; 
	private String actFileName; 
	private String xFileName;
	private String descriptorType;
	private String actFileDataType; 
	private String standardize;
	private String splitType;
	private String numExternalCompounds;
	private String useActivityBinning;
	private String externalCompoundList;
	private String jobName = null;
	private String paperReference;
	private String dataSetDescription;
	private String actFileHeader;
	private String availableDescriptors = "";
	private int numCompounds;
	private DataSet dataset; //contains pretty much all the member variables. This is dumb but hopefully temporary.
	
	private String step = Constants.SETUP; //stores what step we're on 
	
	public String getProgress(){
		String percent = "";
		
		if(step.equals(Constants.SKETCHES)){
			//count the number of *.jpg files in the working directory
			
			String workingDir = "";
			if(jobList.equals(Constants.LSF)){
				
			}
			else{
				workingDir = Constants.CECCR_USER_BASE_PATH + userName + "/DATASETS/" + jobName + "/Visualization/Sketches/";
			}
			float p = FileAndDirOperations.countFilesInDirMatchingPattern(workingDir, ".*jpg");
			//divide by the number of compounds in the dataset
			p /= numCompounds;
			p *= 100; //it's a percent
			percent = " (" + Math.round(p) + "%)"; 
		}
		
		return step + percent;
	}
	
	public CreateDatasetTask(DataSet dataset){
		this.dataset = dataset;

		userName = dataset.getUserName();
		jobName = dataset.getFileName();
		datasetType = dataset.getDatasetType();
		sdfFileName = dataset.getSdfFile();
		actFileName = dataset.getActFile();
		xFileName = dataset.getXFile();
		descriptorType = dataset.getUploadedDescriptorType();
		actFileDataType = dataset.getModelType();
		paperReference = dataset.getPaperReference();
		dataSetDescription = dataset.getDescription();

		standardize = dataset.getStandardize();
		splitType = dataset.getSplitType();
		numExternalCompounds = dataset.getNumExternalCompounds();
		useActivityBinning = dataset.getUseActivityBinning();
		externalCompoundList = dataset.getExternalCompoundList();
		
		String path = Constants.CECCR_USER_BASE_PATH + userName + "/DATASETS/" + jobName + "/";
		try{
			if(!sdfFileName.equals("")){
				this.numCompounds = DatasetFileOperations.getSDFCompoundNames(path+sdfFileName).size();
			}
			else if(!xFileName.equals("")){
				this.numCompounds = DatasetFileOperations.getXCompoundNames(path+xFileName).size();
			}
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
	}
	
	public CreateDatasetTask(String userName, 
			String datasetType, 
			String sdfFileName, 
			String actFileName, 
			String xFileName,
			String descriptorType,
			String actFileDataType, 
			String standardize,
			String splitType,
			String numExternalCompounds,
			String useActivityBinning,
			String externalCompoundList,
			String datasetName,
			String paperReference,
			String dataSetDescription){
		//for modeling sets without included descriptors

		this.userName = userName;
		this.datasetType = datasetType; 
		this.sdfFileName = sdfFileName; 
		this.actFileName = actFileName; 
		this.xFileName = xFileName;
		this.descriptorType = descriptorType;
		this.actFileDataType = actFileDataType; 
		this.standardize = standardize;
		this.splitType = splitType;
		this.numExternalCompounds = numExternalCompounds;
		this.useActivityBinning = useActivityBinning;
		this.externalCompoundList = externalCompoundList;
		this.jobName = datasetName;
		this.paperReference = paperReference;
		this.dataSetDescription = dataSetDescription;

		this.dataset = new DataSet();

		String path = Constants.CECCR_USER_BASE_PATH + userName + "/DATASETS/" + jobName + "/";
		try{
			if(!sdfFileName.equals("")){
				this.numCompounds = DatasetFileOperations.getSDFCompoundNames(path+sdfFileName).size();
			}
			else if(!xFileName.equals("")){
				this.numCompounds = DatasetFileOperations.getXCompoundNames(path+xFileName).size();
			}
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
	}
	

	public void setUp() throws Exception {
		//create DataSet object in DB to allow for recovery of this job if it fails.

		dataset.setFileName(jobName);
		dataset.setUserName(userName);
		dataset.setDatasetType(datasetType);
		dataset.setActFile(actFileName);
		dataset.setSdfFile(sdfFileName);
		dataset.setXFile(xFileName);
		dataset.setModelType(actFileDataType);
		dataset.setNumCompound(numCompounds);
		dataset.setCreatedTime(new Date());
		dataset.setDescription(dataSetDescription);
		dataset.setPaperReference(paperReference);
		dataset.setActFormula(actFileHeader);
		dataset.setUploadedDescriptorType(descriptorType);
		dataset.setHasBeenViewed(Constants.NO);
		dataset.setJobCompleted(Constants.NO);
		
		dataset.setStandardize(standardize);
		dataset.setSplitType(splitType);
		dataset.setNumExternalCompounds(numExternalCompounds);
		dataset.setUseActivityBinning(useActivityBinning);
		dataset.setExternalCompoundList(externalCompoundList);

		Session session = HibernateUtil.getSession();
		Transaction tx = null;

		try {
			tx = session.beginTransaction();
			session.saveOrUpdate(dataset);
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			session.close();
		}
		
		lookupId = dataset.getFileId();
		jobType = Constants.DATASET;
	}
	
	public void preProcess() throws Exception {
		String path = Constants.CECCR_USER_BASE_PATH + userName + "/DATASETS/" + jobName + "/";

		Utility.writeToDebug("executing task");

		if(!sdfFileName.equals("") && standardize.equals("true")){
			//standardize the SDF	
			StandardizeMoleculesWorkflow.standardizeSdf(sdfFileName, sdfFileName + ".standardize", path);
			File standardized = new File(path + sdfFileName + ".standardize");
			if(standardized.exists()){
				//replace old SDF with new standardized SDF
				FileAndDirOperations.copyFile(path + sdfFileName + ".standardize", path + sdfFileName);
				FileAndDirOperations.deleteFile(path + sdfFileName + ".standardize");
			}
		}
		
		if(!sdfFileName.equals("")){
			//generate descriptors
			this.numCompounds = DatasetFileOperations.getSDFCompoundNames(path+sdfFileName).size();
			
			String descriptorDir = "Descriptors/";
			if(!new File(path + descriptorDir).exists()) {
				new File(path + descriptorDir).mkdirs();
			}
			
			step = Constants.DESCRIPTORS;
			Utility.writeToDebug("Generating Descriptors", userName, jobName);
			
			//the dataset included an SDF so we need to generate descriptors from it
			Utility.writeToDebug("Generating MolconnZ Descriptors", userName, jobName);
			GenerateDescriptorWorkflow.GenerateMolconnZDescriptors(path + sdfFileName, path + descriptorDir + sdfFileName + ".molconnz");

			Utility.writeToDebug("Generating DragonH Descriptors", userName, jobName);
			GenerateDescriptorWorkflow.GenerateHExplicitDragonDescriptors(path + sdfFileName, path + descriptorDir + sdfFileName + ".dragonH");
			
			Utility.writeToDebug("Generating DragonNoH Descriptors", userName, jobName);
			GenerateDescriptorWorkflow.GenerateHDepletedDragonDescriptors(path + sdfFileName, path + descriptorDir + sdfFileName + ".dragonNoH");
			
			Utility.writeToDebug("Generating MOE2D Descriptors", userName, jobName);
			GenerateDescriptorWorkflow.GenerateMoe2DDescriptors(path + sdfFileName, path + descriptorDir + sdfFileName + ".moe2D");
			
			Utility.writeToDebug("Generating MACCS Descriptors", userName, jobName);
			GenerateDescriptorWorkflow.GenerateMaccsDescriptors(path + sdfFileName, path + descriptorDir + sdfFileName + ".maccs");

			step = Constants.CHECKDESCRIPTORS;
			//MolconnZ
			String errors = CheckDescriptorsFileWorkflow.checkMolconnZDescriptors(path + descriptorDir + sdfFileName + ".molconnz");
			if(errors.equals("")){
				availableDescriptors += Constants.MOLCONNZ + " ";
			}
			else{
				File errorSummaryFile = new File(path + descriptorDir + "Logs/molconnz.out");
				BufferedWriter errorSummary = new BufferedWriter(new FileWriter(errorSummaryFile));
				errorSummary.write(errors);
				errorSummary.close();
			}
			//DragonH
			errors = CheckDescriptorsFileWorkflow.checkDragonDescriptors(path + descriptorDir + sdfFileName + ".dragonH");
			if(errors.equals("")){
				availableDescriptors += Constants.DRAGONH + " ";
			}
			else{
				File errorSummaryFile = new File(path + descriptorDir + "Logs/dragonH.out");
				BufferedWriter errorSummary = new BufferedWriter(new FileWriter(errorSummaryFile));
				errorSummary.write(errors);
				errorSummary.close();
			}
			//DragonNoH
			errors = CheckDescriptorsFileWorkflow.checkDragonDescriptors(path + descriptorDir + sdfFileName + ".dragonNoH");
			if(errors.equals("")){
				availableDescriptors += Constants.DRAGONNOH + " ";
			}
			else{
				File errorSummaryFile = new File(path + descriptorDir + "Logs/dragonNoH.out");
				BufferedWriter errorSummary = new BufferedWriter(new FileWriter(errorSummaryFile));
				errorSummary.write(errors);
				errorSummary.close();
			}
			//MOE2D
			errors = CheckDescriptorsFileWorkflow.checkMoe2DDescriptors(path + descriptorDir + sdfFileName + ".moe2D");
			if(errors.equals("")){
				availableDescriptors += Constants.MOE2D + " ";
			}
			else{
				File errorSummaryFile = new File(path + descriptorDir + "Logs/moe2d.out");
				BufferedWriter errorSummary = new BufferedWriter(new FileWriter(errorSummaryFile));
				errorSummary.write(errors);
				errorSummary.close();
			}
			//MACCS
			errors = CheckDescriptorsFileWorkflow.checkMaccsDescriptors(path + descriptorDir + sdfFileName + ".maccs");
			if(errors.equals("")){
				availableDescriptors += Constants.MACCS + " ";
			}
			else{
				File errorSummaryFile = new File(path + descriptorDir + "Logs/maccs.out");
				BufferedWriter errorSummary = new BufferedWriter(new FileWriter(errorSummaryFile));
				errorSummary.write(errors);
				errorSummary.close();
			}
			
		}
		
		
		if(datasetType.equals(Constants.MODELING) || datasetType.equals(Constants.MODELINGWITHDESCRIPTORS)){
			//split dataset to get external set and modeling set

			step = Constants.SPLITDATA;
			
			Utility.writeToDebug("Creating " + splitType + " External Validation Set", userName, jobName);
			
			if(splitType.equals(Constants.RANDOM)){
				Utility.writeToDebug("Making random external split");
				if(datasetType.equals(Constants.MODELING)){
					//we will need to make a .x file from the .act file
					DatasetFileOperations.makeXFromACT(path, actFileName);
					String tempXFileName = actFileName.substring(0, actFileName.lastIndexOf('.')) + ".x";
					
					//now run datasplit on the resulting .x file to get a list of compounds
					DataSplitWorkflow.SplitModelingExternal(path, actFileName, tempXFileName, numExternalCompounds, useActivityBinning);
					
					//delete the temporary .x file
					FileAndDirOperations.deleteFile(path + tempXFileName);
				}
				else if(datasetType.equals(Constants.MODELINGWITHDESCRIPTORS)){
					//already got a .x file, so just split that
					DataSplitWorkflow.SplitModelingExternal(path, actFileName, xFileName, numExternalCompounds, useActivityBinning);
				}
				
			}
			else if(splitType.equals(Constants.USERDEFINED)){
				Utility.writeToDebug("Making user-defined external split");

				//get the list of compound IDs
				Utility.writeToDebug("externalCompoundList before: " + externalCompoundList);
				externalCompoundList = externalCompoundList.replaceAll(",", " ");
				externalCompoundList = externalCompoundList.replaceAll("\\\n", " ");
				Utility.writeToDebug("externalCompoundList after: " + externalCompoundList);
				
				if(datasetType.equals(Constants.MODELING)){

					//we will need to make a .x file from the .act file
					DatasetFileOperations.makeXFromACT(path, actFileName);
					String tempXFileName = actFileName.substring(0, actFileName.lastIndexOf('.')) + ".x";
					
					//now split the resulting .x file 
					DataSplitWorkflow.splitModelingExternalGivenList(path, actFileName, tempXFileName, externalCompoundList);	
					
					//delete the temporary .x file
					FileAndDirOperations.deleteFile(path + tempXFileName);
				}
				else if(datasetType.equals(Constants.MODELINGWITHDESCRIPTORS)){
					//already got a .x file, so just split that
					DataSplitWorkflow.splitModelingExternalGivenList(path, actFileName, xFileName, externalCompoundList);
				}
			}
		}
		
		if(jobList.equals(Constants.LSF)){
			//copy needed files out to LSF
		}
	}
	
	public String executeLSF() throws Exception {
		//this should do the same thing as executeLocal functionally
		//it will create a job on LSF and return immediately.
		return "";
	}
	
	public void executeLocal() throws Exception {

		String path = Constants.CECCR_USER_BASE_PATH + userName + "/DATASETS/" + jobName + "/";

		if(!sdfFileName.equals("")){
			//generate compound sketches and visualization files

			String descriptorDir = "Descriptors/";
			String vizFilePath = "Visualization/"; 
			String structDir = "Visualization/Structures/";
			String sketchDir = "Visualization/Sketches/";

			if(!new File(path + vizFilePath).exists()) {
				new File(path + vizFilePath).mkdirs();
			}
			if(!new File(path + structDir).exists()) {
				new File(path + structDir).mkdirs();
			}
			if(!new File(path + sketchDir).exists()) {
				new File(path + sketchDir).mkdirs();
			}
			
			step = Constants.SKETCHES;
			Utility.writeToDebug("Generating JPGs", userName, jobName);
			SdfToJpgWorkflow.makeSketchFiles(path, sdfFileName, structDir, sketchDir);
			
			if(numCompounds < 500){
				//totally not worth doing visualizations on huge datasets, the heatmap is 
				//just nonsense at that point and it wastes a ton of time.
				step = Constants.VISUALIZATION;
				Utility.writeToDebug("Generating Visualizations", userName, jobName);
				
				String viz_path = Constants.CECCR_USER_BASE_PATH + userName + "/DATASETS/" + jobName + "/Visualization/" + sdfFileName.substring(0,sdfFileName.lastIndexOf("."));
				FileAndDirOperations.copyFile(path + descriptorDir + sdfFileName + ".maccs", viz_path + ".maccs");
				CSV_X_Workflow.performXCreation(viz_path);
				CSV_X_Workflow.performHeatMapAndTreeCreation(viz_path, "mahalanobis");
				CSV_X_Workflow.performHeatMapAndTreeCreation(viz_path, "tanimoto");
	
				if(!actFileName.equals("")){
					//generate ACT-file related visualizations
					this.numCompounds = DatasetFileOperations.getACTCompoundNames(path+actFileName).size();
					String act_path  = Constants.CECCR_USER_BASE_PATH + userName + "/DATASETS/" + jobName + "/" + actFileName;
						
					//PCA plot creation works
					//however, there is no way to visualize the result right now.
					//also, it's broken for some reason, so fuck that - just fix it later.
					//CSV_X_Workflow.performPCAcreation(viz_path, act_path);
		
				}
			}

		}
		
		if(!xFileName.equals("")){
			this.numCompounds = DatasetFileOperations.getXCompoundNames(path+xFileName).size();
		}
		
	}
	
	public void postProcess() throws Exception {
		Utility.writeToDebug("Saving dataset to database", userName, jobName);
		
		if(jobList.equals(Constants.LSF)){
			//copy needed back from LSF
		}
		
		//add dataset to DB
		dataset.setHasBeenViewed(Constants.NO);
		dataset.setJobCompleted(Constants.YES);
		dataset.setAvailableDescriptors(availableDescriptors);

		Session session = HibernateUtil.getSession();
		Transaction tx = null;

		try {
			tx = session.beginTransaction();
			session.saveOrUpdate(dataset);
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			session.close();
		}
		
	}

	public void delete() throws Exception {
		
	}

	public String getStatus(){
		return step;
	}
}
