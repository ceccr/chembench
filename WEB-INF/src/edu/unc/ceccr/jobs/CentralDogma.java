package edu.unc.ceccr.jobs;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Job;
import edu.unc.ceccr.persistence.JobStats;
import edu.unc.ceccr.persistence.Prediction;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.SoftwareExpiration;
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
	
	private final int numLocalThreads = 4; //as many as you want; tune it based on server load.
	private final int numLsfThreads = 1; //don't change this unless you've REALLY thought through all possible concurrency issues
	private final int numIncomingThreads = 1; //don't change this; the thread does no processing so having > 1 makes no sense

	public SynchronizedJobList incomingJobs;
	public SynchronizedJobList localJobs;
	public SynchronizedJobList lsfJobs;
	
	private IncomingJobProcessingThread inThread;
	
	private static CentralDogma instance = new CentralDogma(); 
	
	private CentralDogma(){
		try{
			
			lsfJobs = new SynchronizedJobList();
			incomingJobs = new SynchronizedJobList();
			localJobs = new SynchronizedJobList();
			
			//Fill job lists from the database
			Session s = HibernateUtil.getSession();
			
			ArrayList<Job> jobs = PopulateDataObjects.populateJobs(s);
			if(jobs == null){
				jobs = new ArrayList<Job>();
			}
			for(Job j : jobs){
				WorkflowTask wt = null;
				if(j.getLookupId() != null){
					if(j.getJobType().equals(Constants.DATASET)){
						Long datasetId = j.getLookupId();
						DataSet dataset = PopulateDataObjects.getDataSetById(datasetId, s);
						wt = new CreateDatasetTask(dataset);
					}
					else if(j.getJobType().equals(Constants.MODELING)){
						Long modelingId = j.getLookupId();
						Predictor predictor = PopulateDataObjects.getPredictorById(modelingId, s);
						wt = new QsarModelingTask(predictor);
					} 
					else if(j.getJobType().equals(Constants.PREDICTION)){
						Long predictionId = j.getLookupId();
						Prediction prediction = PopulateDataObjects.getPredictionById(predictionId, s);
						wt = new QsarPredictionTask(prediction);
					} 
					wt.jobList = j.getJobList();
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
			}
			
			//start job processing threads
			for(int i = 0; i < numLocalThreads; i++){
				LocalProcessingThread localThread = new LocalProcessingThread();
				localThread.start();
			}
			
			for(int i = 0; i < numLsfThreads; i++){
				LsfProcessingThread lsfThread = new LsfProcessingThread();
				lsfThread.start();
			}
			
			inThread = new IncomingJobProcessingThread();
			inThread.start();

			
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

	public void addJobToIncomingList(String userName, String jobName, WorkflowTask wt, int numCompounds, int numModels, String emailOnCompletion) throws Exception{
		//first, run setUp on the workflowTask
		//this will make sure the workflowTask gets into the DB. Then we can create a job to contain it.
		wt.setUp(); //create dataset, predictor, or prediction object in DB
		
		Job j = new Job();
		j.setJobName(jobName);
		j.setUserName(userName);
		j.setNumCompounds(numCompounds);
		j.setNumModels(numModels);
/*		j.setWorkflowTask(wt);
		j.setTimeCreated(new Date());
		j.setStatus(Constants.QUEUED);
		j.setJobList(Constants.INCOMING);
		j.setEmailOnCompletion(emailOnCompletion);
		j.setJobType(wt.jobType);
		j.setLookupId(wt.lookupId);*/

		Utility.writeToDebug("Creating Job in job table: " + jobName);
		//commit job to DB
		Session s = HibernateUtil.getSession();
		Transaction tx = null;
		try {
			tx = s.beginTransaction();
			s.saveOrUpdate(j);
			tx.commit();
			Utility.writeToDebug("Job created job table: " + jobName);
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			s.close();
		}
		
		//put into incoming queue
		incomingJobs.addJob(j);
		
	}
	
}