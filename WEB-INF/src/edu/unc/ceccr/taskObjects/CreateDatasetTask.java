package edu.unc.ceccr.taskObjects;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.Queue;
import edu.unc.ceccr.persistence.Queue.QueueTask;
import edu.unc.ceccr.persistence.Queue.QueueTask.jobTypes;
import edu.unc.ceccr.task.WorkflowTask;
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

		queue.runningTask.setMessage("Generating JPGs");
		SdfToJpgWorkflow.makeSketchFiles(path, sdfFileName, structDir, sketchDir);
		
		if(!sdfFileName.equals("") && standardize.equals("true")){
			//standardize the SDF	
			StandardizeMoleculesWorkflow.standardizeSdf(sdfFileName, sdfFileName + ".standardize", path);
		}
		
		if(!sdfFileName.equals("")){
			//generate compound sketches and visualization files
			
			if(!new File(vizFilePath).exists()) new File(vizFilePath).mkdirs();
			else{
				new File(vizFilePath).mkdirs();
			}
			
			queue.runningTask.setMessage("Generating JPGs");
			SdfToJpgWorkflow.makeSketchFiles(path, sdfFileName, structDir, sketchDir);
			Utility.writeToMSDebug("execute()");
			
			String viz_path = Constants.CECCR_USER_BASE_PATH + userName + "/DATASETS/" + jobName + "/Visualization/" + sdfFileName.substring(0,sdfFileName.lastIndexOf("."));
			
			queue.runningTask.setMessage("Creating heatmap and tree using Mahalanobis distance measure");
			CSV_X_Workflow.performHeatMapAndTreeCreation(viz_path, "mahalanobis");

			queue.runningTask.setMessage("Creating heatmap and tree using Tanimoto similarity measure");
			CSV_X_Workflow.performHeatMapAndTreeCreation(viz_path, "tanimoto");
	
			if(!actFileName.equals("")){
				//generate ACT-file related visualizations
				
				String act_path  = Constants.CECCR_USER_BASE_PATH + userName + "/DATASETS/" + jobName + "/" + actFileName;
					
				queue.runningTask.setMessage("Creating PCA plots");
				CSV_X_Workflow.performPCAcreation(viz_path, act_path);
	
				/*
				//Old dataset code. May be needed for generation of... something? 
				Vector<DistanceMeasure> dmv = new Vector<DistanceMeasure>();
				for(int i=0;i<similarityMeasure.length;i++){
					Utility.writeToMSDebug("SIM::"+similarityMeasure[i]+"__"+datasetName+"____");
					if(similarityMeasure[i].equals("Tanimoto")){

						queue.runningTask.setMessage("Calculating Tanimoto Distances");
						TanimotoDistanceMeasure tm = new TanimotoDistanceMeasure(userName, datasetName, sdf_name);
						dmv.add(tm);
					}
					if(similarityMeasure[i].equals("Mahalanobis")){
						
						queue.runningTask.setMessage("Calculating Mahalanobis Distances");
						MahalanobisDistanceMeasure mm = new MahalanobisDistanceMeasure(userName, datasetName,sdf_name);
						dmv.add(mm);
					}
				}
				*/
				
			}
		}
				
		//split dataset to get external set and modeling set
		String dataSplitParameters = "";
		if(splitType.equals(Constants.RANDOM)){
			
			if(useActivityBinning.equals("true")){
				
			}
			else{
				
			}
		}
		else if(splitType.equals(Constants.USERDEFINED)){
			//process the list of compound IDs
			
			//get the list of compounds and find the index of each of the compound IDs provided
			
		}
		//run datasplit
		DataSplitWorkflow.SplitModelingExternal(dataSplitParameters, path, sdFile, actFile, randomSeed, numCompoundsExternalSet);
		
	}

	public void cleanUp() throws Exception {
		queue.deleteTask(this);
	}

	public void save() throws Exception {
		//add dataset to DB
		
	}

	public void setUp() throws Exception {
	}
	
	public String getJobName() {
		return jobName;
	}

}
