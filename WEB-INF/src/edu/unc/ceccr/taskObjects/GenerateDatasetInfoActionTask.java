package edu.unc.ceccr.taskObjects;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import edu.unc.ceccr.distance.DistanceMeasure;
import edu.unc.ceccr.distance.MahalanobisDistanceMeasure;
import edu.unc.ceccr.distance.TanimotoDistanceMeasure;
import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.Queue;
import edu.unc.ceccr.persistence.Queue.QueueTask;
import edu.unc.ceccr.persistence.Queue.QueueTask.jobTypes;

import edu.unc.ceccr.task.WorkflowTask;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.workflows.CSV_X_Workflow;

/**
 * Class which should call all methods for generation of the additional files 
 * for dataset visualization.
 */
public class GenerateDatasetInfoActionTask implements WorkflowTask {
	private String userName = null;
	private String datasetName = null;
	private String sdf_name = null;
	private String act_name = null;
	//private String[] sketches = null;
	private String[] similarityMeasure = null;
	private String[] representations = null;
	private String jobName = null;
	private Queue queue = Queue.getInstance();
	
	public GenerateDatasetInfoActionTask(String datasetName,
			String[] representations, String[] similarityMeasure,
			/*String[] sketches,*/ String userName, String sdfName, String actName) {
		
		this.datasetName = datasetName;
		this.jobName = datasetName;
		this.representations = representations;
		this.similarityMeasure = similarityMeasure;
		//this.sketches = sketches;
		this.userName = userName;
		this.sdf_name = sdfName;
		this.act_name = actName;
	}

	public void cleanUp() throws Exception {
		queue.deleteTask(this);
		
	}

	public void execute() throws Exception {
		
		String file = userName + "/DATASETS/" +datasetName+"/";
		String filePath = Constants.CECCR_USER_BASE_PATH + file;
		//String vizFilePath =filePath+"Visualization/"; 
		//new File(vizFilePath).delete();
		//new File(vizFilePath).mkdir();
		
		Utility.writeToMSDebug("filePath:::"+filePath);
		
			Utility.writeToMSDebug("Starting X creation!!!");
			CSV_X_Workflow csv_x_workflow = new CSV_X_Workflow(userName, datasetName, sdf_name, act_name);
			
			queue.runningTask.setMessage("Creating MACCS keys");
			csv_x_workflow.performMACCSCreation();
			Utility.writeToMSDebug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>MACCS created<<<<<<<<<<<<<<<<<<<<<");
			
			queue.runningTask.setMessage("Creating X file");
			csv_x_workflow.performXCreation();
			
			queue.runningTask.setMessage("Creating CSV file");
			csv_x_workflow.performCSVCreation();
			Utility.writeToMSDebug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>X created<<<<<<<<<<<<<<<<<<<<<");
			//DistanceMeasure dm = new DistanceMeasure(user.getUserName(), datasetname, knnType);
			Utility.writeToMSDebug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>`Data readed1<<<<<<<<<<<<<<<<<<<<<");
			//dm.readData();
			
		/*Utility.writeToMSDebug("getSketches::END:");
		Vector<DistanceMeasure> dmv = new Vector<DistanceMeasure>();
		if(similarityMeasure!=null){
			if(similarityMeasure.length>0){
				
						
				for(int i=0;i<similarityMeasure.length;i++){
					Utility.writeToMSDebug("SIM::"+similarityMeasure[i]+"__"+datasetName+"____");
					if(similarityMeasure[i].equals("Tanimoto")){
						Utility.writeToMSDebug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>performMatrixCreation<<<<<<<<<<<<<<<<<<<<<");

						queue.runningTask.setMessage("Calculating Tanimoto Distances");
						TanimotoDistanceMeasure tm = new TanimotoDistanceMeasure(userName, datasetName, sdf_name);
						Utility.writeToMSDebug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>performMatrixCreation2<<<<<<<<<<<<<<<<<<<<<");
						//tm.performMatrixCreation();
						dmv.add(tm);
					}
					if(similarityMeasure[i].equals("Mahalanobis")){
						Utility.writeToMSDebug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>Tanimoto<<<<<<<<<<<<<<<<<<<<<");
						
						queue.runningTask.setMessage("Calculating Mahalanobis Distances");
						MahalanobisDistanceMeasure mm = new MahalanobisDistanceMeasure(userName, datasetName,sdf_name);
						Utility.writeToMSDebug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>performMatrixCreation<<<<<<<<<<<<<<<<<<<<<");
						//mm.performMatrixCreation();
						dmv.add(mm);
					}
				}
			}
		}
		*/
			
			if(similarityMeasure.length>0){
				for(int j=0;j<representations.length;j++){
					if(representations[j].equals("PCA")){
						queue.runningTask.setMessage("Creating PCA plots");
						csv_x_workflow.performPCAcreation();
					}
					for(int i=0;i<similarityMeasure.length;i++){
						if(similarityMeasure[i].equals("Mahalanobis")){
							if(representations[j].equals("HeatmapAndTree")){
								queue.runningTask.setMessage("Creating heatmap and tree using Mahalanobis distance measure");
								csv_x_workflow.performHeatMapAndTreeCreation("mahalanobis");
							}
						
						}
						if(similarityMeasure[i].equals("Tanimoto")){
							if(representations[j].equals("HeatmapAndTree")){
								queue.runningTask.setMessage("Creating heatmap and tree using Tanimoto similarity measure");
								csv_x_workflow.performHeatMapAndTreeCreation("tanimoto");
							}
						}
					}
				}
			}
		Utility.writeToMSDebug("getSketches::END2:");
		
	/*	if(representations!=null){
			if(representations.length>0){
								
				for(int i=0;i<representations.length;i++){
					Utility.writeToMSDebug("HMP::"+representations[i]);
					if(!dmv.isEmpty()){
						for(Iterator<DistanceMeasure> ii=dmv.iterator();ii.hasNext();){
							if(representations[i].equals("Heatmap")){
								Utility.writeToMSDebug("HMP::"+representations[i]);
								ii.next().performMatrixCreation();	
							}
							if(representations[i].equals("Trees")){
								Utility.writeToMSDebug("TRE::"+representations[i]);
								ii.next().performXMLCreation();
							}
							if(representations[i].equals("PCA")){
								Utility.writeToMSDebug("PCA::"+representations[i]);
								ii.next();//.generatePCA();
							}
						}
					}
				}*/
				/*csv_x_workflow.performCSVCreation();
				Utility.writeToMSDebug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>CSV created<<<<<<<<<<<<<<<<<<<<<");
				*/
			//}
		//}
		
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
	}


	public String getJobName() {
		
		// TODO Auto-generated method stub
		return jobName;
	}

}
