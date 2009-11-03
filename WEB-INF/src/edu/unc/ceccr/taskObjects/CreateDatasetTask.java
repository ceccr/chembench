package edu.unc.ceccr.taskObjects;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Queue;
import edu.unc.ceccr.persistence.Queue.QueueTask;
import edu.unc.ceccr.persistence.Queue.QueueTask.jobTypes;
import edu.unc.ceccr.task.WorkflowTask;
import edu.unc.ceccr.utilities.DatasetFileOperations;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.workflows.CSV_X_Workflow;
import edu.unc.ceccr.workflows.DataSplitWorkflow;
import edu.unc.ceccr.workflows.SdfToJpgWorkflow;
import edu.unc.ceccr.workflows.StandardizeMoleculesWorkflow;

public class CreateDatasetTask implements WorkflowTask{

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
	private int numCompounds;
	
	private String step = Constants.SETUP; //stores what step we're on 
	
	public String getProgress(){
		String percent = "";
		if(step.equals(Constants.SKETCHES)){
			//count the number of *.jpg files in the working directory
			String workingDir = Constants.CECCR_USER_BASE_PATH + userName + "/DATASETS/" + jobName + "/Visualization/Sketches/";
			float p = FileAndDirOperations.countFilesInDirMatchingPattern(workingDir, ".*jpg");
			//divide by the number of compounds in the dataset
			p /= numCompounds;
			p *= 100; //it's a percent
			percent = " (" + Math.round(p) + "%)"; 
		}
		return step + percent;
	}
	
	private Queue queue = Queue.getInstance();
	
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
	}

	public void execute() throws Exception {
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
			//generate compound sketches and visualization files
			this.numCompounds = DatasetFileOperations.getSDFCompoundList(path+sdfFileName).size();

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
			
			String viz_path = Constants.CECCR_USER_BASE_PATH + userName + "/DATASETS/" + jobName + "/Visualization/" + sdfFileName.substring(0,sdfFileName.lastIndexOf("."));

			step = Constants.VISUALIZATION;
			Utility.writeToDebug("Generating Visualizations", userName, jobName);
			
			CSV_X_Workflow.performMACCSCreation(path + sdfFileName, viz_path);
			
			CSV_X_Workflow.performXCreation(viz_path);
						
			CSV_X_Workflow.performHeatMapAndTreeCreation(viz_path, "mahalanobis");

			CSV_X_Workflow.performHeatMapAndTreeCreation(viz_path, "tanimoto");

			if(!actFileName.equals("")){
				//generate ACT-file related visualizations
				this.numCompounds = DatasetFileOperations.getACTCompoundList(path+actFileName).size();
				
				String act_path  = Constants.CECCR_USER_BASE_PATH + userName + "/DATASETS/" + jobName + "/" + actFileName;
					
				//PCA plot creation works
				//however, there is no way to visualize the result right now.
				//CSV_X_Workflow.performPCAcreation(viz_path, act_path);
	
			}
		}
		
		if(!xFileName.equals("")){
			this.numCompounds = DatasetFileOperations.getXCompoundList(path+xFileName).size();
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
				externalCompoundList.replaceAll(",", " ");
				externalCompoundList.replaceAll("\n", " ");
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
		Utility.writeToDebug("Finished creating dataset.", userName, jobName);
	}

	public void cleanUp() throws Exception {
		queue.deleteTask(this);
	}

	public void save() throws Exception {
		//move dataset to DATASET dir

		Utility.writeToDebug("Saving dataset to database", userName, jobName);
		//add dataset to DB
		DataSet dataSet = new DataSet();

		dataSet.setFileName(jobName);
		dataSet.setUserName(userName);
		dataSet.setDatasetType(datasetType);
		dataSet.setActFile(actFileName);
		dataSet.setSdfFile(sdfFileName);
		dataSet.setXFile(xFileName);
		dataSet.setModelType(actFileDataType);
		dataSet.setNumCompound(numCompounds);
		dataSet.setCreatedTime(new Date());
		dataSet.setDescription(dataSetDescription);
		//dataSet.setActFormula(actFileHeader);
		dataSet.setUploadedDescriptorType(descriptorType);

		Session session = HibernateUtil.getSession();
		Transaction tx = null;

		try {
			tx = session.beginTransaction();
			session.save(dataSet);
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			session.close();
		}

	}


	public void setUp() throws Exception {
	}
	
	public String getJobName() {
		return jobName;
	}

}
