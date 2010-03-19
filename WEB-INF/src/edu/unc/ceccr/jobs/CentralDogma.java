package edu.unc.ceccr.jobs;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Job;
import edu.unc.ceccr.persistence.Prediction;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.taskObjects.CreateDatasetTask;
import edu.unc.ceccr.taskObjects.QsarModelingTask;
import edu.unc.ceccr.taskObjects.QsarPredictionTask;
import edu.unc.ceccr.taskObjects.WorkflowTask;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;

public class CentralDogma{
	//singleton.
	//Holds the LSF jobs list, the incoming jobs list, and the local processing jobs list.
	//Initiates the threads that work on these data structures.
	
	int numLocalThreads = 4; //as many as you want; tune it based on server load.
	int numLsfThreads = 1; //don't change this unless you've REALLY thought through all possible concurrency issues
	int numIncomingThreads = 1; //don't change this; the thread does no processing so having > 1 makes no sense

	SynchronizedJobList incomingJobs;
	SynchronizedJobList localJobs;
	SynchronizedJobList lsfJobs;
	
	private static CentralDogma instance = new CentralDogma(); 
	
	private CentralDogma(){
		try{
			lsfJobs = new SynchronizedJobList();
			incomingJobs = new SynchronizedJobList();
			localJobs = new SynchronizedJobList();
			
			//Fill job lists from the database
			Session s = HibernateUtil.getSession();
			ArrayList<Job> jobs = PopulateDataObjects.populateJobs(s);
			for(Job j : jobs){
				WorkflowTask wt = null;
				if(j.getJobType().equals(Constants.DATASET)){
					Long datasetId = Long.parseLong(j.getLookupId());
					DataSet dataset = PopulateDataObjects.getDataSetById(datasetId, s);
					wt = new CreateDatasetTask(dataset);
				}
				else if(j.getJobType().equals(Constants.MODELING)){
					Long modelingId = Long.parseLong(j.getLookupId());
					Predictor predictor = PopulateDataObjects.getPredictorById(modelingId, s);
					wt = new QsarModelingTask(predictor);
				} 
				else if(j.getJobType().equals(Constants.PREDICTION)){
					Long predictionId = Long.parseLong(j.getLookupId());
					Prediction prediction = PopulateDataObjects.getPredictionById(predictionId, s);
					wt = new QsarPredictionTask(prediction);
				} 
				j.setWorkflowTask(wt);
				
				if(j.getJobList().equals(Constants.INCOMING)){
					incomingJobs.addJob(j);
				}
				else if(j.getJobList().equals(Constants.LOCAL)){
					localJobs.addJob(j);
				}
				else if(j.getJobList().equals(Constants.LSF)){
					lsfJobs.addJob(j);
				}
			}
		}catch(Exception ex){
			Utility.writeToDebug(ex);
		}
	}
	
	public static synchronized CentralDogma getInstance(){ 
		 if (instance==null) { 
			 instance = new CentralDogma(); 
		 }
		 return instance; 
	} 

	public void addJobToIncomingList(String userName, String jobName, WorkflowTask wt, int numCompounds, int numModels) throws Exception{
		//first, run setUp on the workflowTask
		//this will make sure the workflowTask gets into the DB. Then we can create a job to contain it.
		wt.setUp();
		
		Job j = new Job();
		Job modelingJob = new Job();
		modelingJob.setJobName(jobName);
		modelingJob.setUserName(userName);
		modelingJob.setNumCompounds(numCompounds);
		modelingJob.setNumModels(numModels);
		modelingJob.setWorkflowTask(wt);
		modelingJob.setTimeCreated(new Date());
		modelingJob.setStatus("Queued");
		modelingJob.setJobList(Constants.INCOMING);
		Session s = HibernateUtil.getSession();
		//commit job to DB
		s.close();
	}
	
}