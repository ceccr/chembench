package edu.unc.ceccr.jobs;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Job;
import edu.unc.ceccr.persistence.SoftwareExpiration;
import edu.unc.ceccr.taskObjects.QsarModelingTask;
import edu.unc.ceccr.utilities.Utility;


public class IncomingJobProcessingThread extends Thread {

	//this takes jobs off the incomingJobs joblist and sends them to lsfJobs and localJobs.
	//You should only ever have one of these threads running - don't start a second one!
		
	public void run() {
		while(true){
			try {
				sleep(1000);

				/*
				CentralDogma.getInstance().incomingJobs.printJobListStates();
				CentralDogma.getInstance().localJobs.printJobListStates();
				CentralDogma.getInstance().lsfJobs.printJobListStates();
				*/
				
				//determine which jobs should be sent to the LSF jobs list, 
				//which should stay here, and which should go to the local jobs list.
				ArrayList<Job> incomingJobs = CentralDogma.getInstance().incomingJobs.getReadOnlyCopy();
				for(Job j : incomingJobs){
					boolean movedJob = false;
					if(j.getJobType().equals(Constants.DATASET)){
						//send it to local
						movedJob = true;
						Utility.writeToDebug("Sending job " + j.getJobName() + " to local queue");
						j.setJobList(Constants.LOCAL);
						j.workflowTask.jobList = Constants.LOCAL;
						CentralDogma.getInstance().localJobs.addJob(j);
						CentralDogma.getInstance().incomingJobs.removeJob(j.getId());
					}
					else if(j.getJobType().equals(Constants.PREDICTION)){
						//send it to local
						Utility.writeToDebug("Sending job " + j.getJobName() + " to local queue");
						movedJob = true;
						j.setJobList(Constants.LOCAL);
						j.workflowTask.jobList = Constants.LOCAL;
						CentralDogma.getInstance().localJobs.addJob(j);
						CentralDogma.getInstance().incomingJobs.removeJob(j.getId());
					}
					else if(j.getJobType().equals(Constants.MODELING)){
						//check LSF status. If LSF can accept another job, put it there.
						QsarModelingTask qs = (QsarModelingTask) j.workflowTask;
						if(qs.getModelType().equals(Constants.KNN) || 
								qs.getModelType().equals(Constants.KNNSA) ||
								qs.getModelType().equals(Constants.KNNGA) || qs.getModelType().equals(Constants.SVM)
								){
							if(LsfProcessingThread.lsfHasFreePendSlots()){
								Utility.writeToDebug("Sending job " + j.getJobName() + " to LSF queue");
								movedJob = true;
								j.setJobList(Constants.LSF);
								j.workflowTask.jobList = Constants.LSF;
								CentralDogma.getInstance().lsfJobs.addJob(j);
								CentralDogma.getInstance().incomingJobs.removeJob(j.getId());
							}
						}
						else{
							//it's an RF job.
							//send it to local
							Utility.writeToDebug("Sending job " + j.getJobName() + " to local queue");
							movedJob = true;
							j.setJobList(Constants.LOCAL);
							j.workflowTask.jobList = Constants.LOCAL;
							CentralDogma.getInstance().localJobs.addJob(j);
							CentralDogma.getInstance().incomingJobs.removeJob(j.getId());
						}
					}
					
					if(movedJob){
						//update job DB entry to reflect queue change
						Session s = HibernateUtil.getSession();
						Transaction tx = null;
						try {
							tx = s.beginTransaction();
							s.saveOrUpdate(j);
							tx.commit();
						}
						catch (RuntimeException e) {
							if (tx != null)
								tx.rollback();
							Utility.writeToDebug(e); 
						} 
						finally {
							s.close();
						}
					}
				}
			} catch (Exception ex) {
				Utility.writeToDebug(ex);
			}
		}
    }
	
}
