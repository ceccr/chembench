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

public class GenerateSketchesTask implements WorkflowTask{

	private String userName = null;
	private String jobName = null;
	private String path;
	private String sdfName;
	private String structDir;
	private String sketchDir;
	private Queue queue = Queue.getInstance();
	
	public GenerateSketchesTask(String userName, String datasetName,String path, String sdFileName, String structDir, String sketchDir){
		this.jobName = datasetName+"_sketches_generation";
		this.userName = userName;
		this.path = path;
		this.sdfName = sdFileName;
		this.structDir = structDir;
		this.sketchDir = sketchDir;
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
		List<QueueTask> tasks = queue.getUserTasks(userName);
		for(Iterator<QueueTask> i=tasks.iterator();i.hasNext();){
			QueueTask temp = i.next();
			if(	temp.getJobType().equals(jobTypes.dataset) && temp.getJobName().equals(jobName)){
				queue.deleteTask(temp);
			}
		}
		List<QueueTask> qued_tasks = queue.getQueuedTasks();
		for(Iterator<QueueTask> i=qued_tasks.iterator();i.hasNext();){
			QueueTask temp = i.next();
			if(temp.getUserName().equals(userName) && temp.getJobType().equals(jobTypes.dataset) && temp.getJobName().equals(jobName)){
				throw new Exception("Duplicated entry in the queue!");
			}
		}
		
		Utility.writeToMSDebug("setUp()");
		
	}
	
public String getJobName() {
		
	return jobName;
	}

}
