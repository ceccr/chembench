package edu.unc.ceccr.jobs;

import java.sql.SQLException;
import java.util.ArrayList;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Job;
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
	
	public SynchronizedJobList getLsfJobs() {
		return lsfJobs;
	}
	public SynchronizedJobList getIncomingJobs() {
		return incomingJobs;
	}
	public SynchronizedJobList getLocalJobs() {
		return localJobs;
	}
}