package edu.unc.ceccr.taskObjects;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import edu.unc.ceccr.persistence.Queue;
import edu.unc.ceccr.persistence.Queue.QueueTask;
import edu.unc.ceccr.persistence.Queue.QueueTask.jobTypes;
import edu.unc.ceccr.task.WorkflowTask;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.workflows.SdfToJpgWorkflow;

public class CreateDatasetTask implements WorkflowTask{

	private String userName = null;
	private String jobName = null;
	private String path;
	private String sdfName;
	private String structDir;
	private String sketchDir;
	private Queue queue = Queue.getInstance();
	
	public CreateDatasetTask(String userName, String datasetName, String path, String sdFileName, String structDir, String sketchDir){
		//for modeling sets without included descriptors
		
		this.jobName = datasetName;
		this.userName = userName;
		this.path = path;
		this.sdfName = sdFileName;
		this.structDir = structDir;
		this.sketchDir = sketchDir;
	}

	public CreateDatasetTask(String userName, String datasetName, String path, String sdFileName, String structDir, String sketchDir){
		//for prediction sets without included descriptors
		
	}
	public CreateDatasetTask(String userName, String datasetName, String path, String sdFileName, String structDir, String sketchDir){
		//for modeling sets that include descriptors
		
	}
	public CreateDatasetTask(String userName, String datasetName, String path, String sdFileName, String structDir, String sketchDir){
		//for prediction sets that include descriptors
		
	}
	
	public void cleanUp() throws Exception {
		queue.deleteTask(this);
	}

	public void execute() throws Exception {
		String vizFilePath =this.path+"Visualization/"; 
		if(!new File(vizFilePath).exists()) new File(vizFilePath).mkdirs();
		else{
			new File(vizFilePath).delete();
			new File(vizFilePath).mkdirs();
		}

		queue.runningTask.setMessage("Generating JPGs");
		SdfToJpgWorkflow.makeSketchFiles(this.path, this.sdfName, this.structDir, this.sketchDir);
		Utility.writeToMSDebug("execute()");
	}


	public void save() throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void setUp() throws Exception {
		
	}
	

}
