package edu.unc.ceccr.jobs;

import java.io.File;
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
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;

public class CentralDogma{
	//singleton.
	//Holds the LSF jobs list, the incoming jobs list, and the local processing jobs list.
	//Initiates the threads that work on these data structures.
	
	private final int numLocalThreads = 6; //as many as you want; tune it based on server load.
	private final int numLsfThreads = 1; //don't change this unless you've REALLY thought through all possible concurrency issues
	private final int numIncomingThreads = 1; //don't change this; the thread does no processing so having > 1 makes no sense

	public SynchronizedJobList incomingJobs;
	public SynchronizedJobList localJobs;
	public SynchronizedJobList lsfJobs;
	public SynchronizedJobList errorJobs;
	
	private IncomingJobProcessingThread inThread;
	
	private static CentralDogma instance = new CentralDogma(); 
	
	private CentralDogma(){
		try{
			
			lsfJobs = new SynchronizedJobList(Constants.LSF);
			incomingJobs = new SynchronizedJobList(Constants.INCOMING);
			localJobs = new SynchronizedJobList(Constants.LOCAL);
			errorJobs = new SynchronizedJobList(Constants.ERROR);
			
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
					j.workflowTask = wt;
					j.setStatus(Constants.QUEUED);
					
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
		j.setTimeCreated(new Date());
		j.setStatus(Constants.QUEUED);
		j.setJobList(Constants.INCOMING);
		j.setEmailOnCompletion(emailOnCompletion);
		j.setJobType(wt.jobType);
		j.setLookupId(wt.lookupId);
		j.workflowTask = wt;
		
		//commit job to DB
		Session s = HibernateUtil.getSession();
		Transaction tx = null;
		try {
			tx = s.beginTransaction();
			s.save(j);
			tx.commit();
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
	
	public void cancelJob(Long jobId){
		//Find job's information, then remove the job from any lists it's in.

		Utility.writeToDebug("Deleting job with id: " + jobId);
		
		Job j = incomingJobs.removeJob(jobId);
		if(j == null){
			Utility.writeToDebug("checking lsf queue");
			j = lsfJobs.removeJob(jobId);
		}
		if(j == null){
			Utility.writeToDebug("checking local queue");
			j = localJobs.removeJob(jobId);
		}
		
		if(j != null){
			Utility.writeToDebug("in main delete");
			//delete files associated with the job.
			//Generally this will cause any executables involved in the job
			//to just crash, so we don't worry about them. Crude but effective.

			String baseDir = Constants.CECCR_USER_BASE_PATH;
			String lsfBaseDir = Constants.LSFJOBPATH;
			
			File file=new File(baseDir+j.getUserName()+"/"+j.getJobName());
			FileAndDirOperations.deleteDir(file);
			file=new File(lsfBaseDir+j.getUserName()+"/"+j.getJobName());
			FileAndDirOperations.deleteDir(file);
	
			file=new File(baseDir+j.getUserName()+"/DATASETS/"+j.getJobName());
			FileAndDirOperations.deleteDir(file);
			file=new File(lsfBaseDir+j.getUserName()+"/DATASETS/"+j.getJobName());
			FileAndDirOperations.deleteDir(file);
	
			file=new File(baseDir+j.getUserName()+"/PREDICTORS/"+j.getJobName());
			FileAndDirOperations.deleteDir(file);
			file=new File(lsfBaseDir+j.getUserName()+"/PREDICTORS/"+j.getJobName());
			FileAndDirOperations.deleteDir(file);
			
			file=new File(baseDir+j.getUserName()+"/PREDICTIONS/"+j.getJobName());
			FileAndDirOperations.deleteDir(file);
			file=new File(lsfBaseDir+j.getUserName()+"/PREDICTIONS/"+j.getJobName());
			FileAndDirOperations.deleteDir(file);

			//delete corresponding workflowTask object (DataSet, Predictor, or Prediction)
			Session s = null; 
			Transaction tx = null;

			try{
				s = HibernateUtil.getSession();
				
				if(j.getJobType().equals(Constants.DATASET)){
					//delete corresponding DataSet in DB
					DataSet ds = PopulateDataObjects.getDataSetById(j.getLookupId(), s);
					tx = s.beginTransaction();
					s.delete(ds);
					tx.commit();
				}
				else if(j.getJobType().equals(Constants.MODELING)){
					//delete corresponding Predictor in DB
					Predictor p = PopulateDataObjects.getPredictorById(j.getLookupId(), s);
					tx = s.beginTransaction();
					s.delete(p);
					tx.commit();
					
				}
				else if(j.getJobType().equals(Constants.PREDICTION)){
					//delete corresponding Prediction in DB
					Prediction p = PopulateDataObjects.getPredictionById(j.getLookupId(), s);
					tx = s.beginTransaction();
					s.delete(p);
					tx.commit();
				}
			}
			catch(Exception ex){
				Utility.writeToDebug(ex);
			}
			finally{
				s.close();
			}
		}
		
		//doesn't matter which list it was in, this will delete the job's DB entry
		//and make a jobstats entry for it.
		incomingJobs.deleteJobFromDB(jobId);
	}
	
}