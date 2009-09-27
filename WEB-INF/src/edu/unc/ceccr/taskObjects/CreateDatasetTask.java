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
		String path = userName + "/DATASETS/" + jobName + "/";
		
		String vizFilePath = path + "Visualization/"; 
		String structDir = "Visualization/Structures";
		String sketchDir = "Visualization/Structures";

		if(!new File(vizFilePath).exists()) {
			new File(vizFilePath).mkdirs();
		}
		if(!new File(structDir).exists()) {
			new File(structDir).mkdirs();
		}
		if(!new File(sketchDir).exists()) {
			new File(sketchDir).mkdirs();
		}

		
		if(!sdfFileName.equals("") && standardize.equals("true")){
			//standardize the SDF	
			StandardizeMoleculesWorkflow.standardizeSdf(sdfFileName, sdfFileName + ".standardize", path);
		}
		
		if(!sdfFileName.equals("")){
			//generate compound sketches and visualization files
			this.numCompounds = DatasetFileOperations.getSDFCompoundList(path+sdfFileName).size();
			
			if(!new File(vizFilePath).exists()) new File(vizFilePath).mkdirs();
			else{
				new File(vizFilePath).mkdirs();
			}
			
			
			queue.runningTask.setMessage("Generating JPGs");
			Utility.writeToDebug("Generating JPGs", userName, jobName);
			SdfToJpgWorkflow.makeSketchFiles(path, sdfFileName, structDir, sketchDir);
			
			String viz_path = Constants.CECCR_USER_BASE_PATH + userName + "/DATASETS/" + jobName + "/Visualization/" + sdfFileName.substring(0,sdfFileName.lastIndexOf("."));

			queue.runningTask.setMessage("Generating Visualizations");
			Utility.writeToDebug("Generating Visualizations", userName, jobName);
			queue.runningTask.setMessage("Creating heatmap and tree using Mahalanobis distance measure");
			CSV_X_Workflow.performHeatMapAndTreeCreation(viz_path, "mahalanobis");

			queue.runningTask.setMessage("Creating heatmap and tree using Tanimoto similarity measure");
			CSV_X_Workflow.performHeatMapAndTreeCreation(viz_path, "tanimoto");

			if(!actFileName.equals("")){
				//generate ACT-file related visualizations
				this.numCompounds = DatasetFileOperations.getACTCompoundList(path+actFileName).size();
				
				String act_path  = Constants.CECCR_USER_BASE_PATH + userName + "/DATASETS/" + jobName + "/" + actFileName;
					
				queue.runningTask.setMessage("Creating PCA plots");
				CSV_X_Workflow.performPCAcreation(viz_path, act_path);
	
			}
		}
		
		if(!actFileName.equals("")){
			//grab header info for the database
			actFileHeader = DatasetFileOperations.getActFileHeader(path + actFileName);
		}
		else{
			actFileHeader = "";
		}
		
		if(!xFileName.equals("")){
			this.numCompounds = DatasetFileOperations.getXCompoundList(path+xFileName).size();
		}
		
		if(datasetType.equals(Constants.MODELING) || datasetType.equals(Constants.MODELINGWITHDESCRIPTORS)){
			//split dataset to get external set and modeling set
			queue.runningTask.setMessage("Creating External Validation Set");
			Utility.writeToDebug("Creating External Validation Set", userName, jobName);
			
			if(splitType.equals(Constants.RANDOM)){
				
				if(datasetType.equals(Constants.MODELING)){
					//we will need to make a .x file from the .act file
					DatasetFileOperations.makeXFromACT(path, actFileName);
					String tempXFileName = actFileName.substring(0, actFileName.lastIndexOf('.')) + "x";
					
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
				//get the list of compound IDs
				ArrayList<String> compoundIDs = DatasetFileOperations.getACTCompoundList(path + actFileName);
				externalCompoundList.replace(",", " ");
				externalCompoundList.replaceAll("\n", " ");
				
				String compoundIDString = compoundIDs.toString().replaceAll("[,\\[\\]]", "");
				DataSplitWorkflow.splitModelingExternalGivenList(path, actFileName, xFileName, compoundIDString);				
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
		dataSet.setActFormula(actFileHeader);
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
