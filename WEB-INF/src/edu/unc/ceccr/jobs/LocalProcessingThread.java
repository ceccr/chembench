package edu.unc.ceccr.jobs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.Job;
import edu.unc.ceccr.utilities.SendEmails;
import edu.unc.ceccr.utilities.Utility;


public class LocalProcessingThread extends Thread {

	//this thread will work on the localJobs joblist.
	//There can be any number of these threads.
	
	public void run() {
		while(true){
			try {
				sleep(1500);
				//Utility.writeToDebug("LocalProcessingThread awake!");
				//pull out a job and start it running
				ArrayList<Job> jobs = CentralDogma.getInstance().localJobs.getReadOnlyCopy();
				for(Job j: jobs){
					//try to get this job. Note that another thread may be trying to get it too.
					if(j != null && CentralDogma.getInstance().localJobs.startJob(j.getId())){
						Utility.writeToDebug("Local queue: Started job " + j.getJobName());
						j.setTimeStarted(new Date());
						j.setStatus(Constants.PREPROC);
						CentralDogma.getInstance().localJobs.saveJobChangesToList(j);
						
						try{
							j.workflowTask.preProcess();
							
							j.setStatus(Constants.RUNNING);
							CentralDogma.getInstance().localJobs.saveJobChangesToList(j);
							j.workflowTask.executeLocal();
							
							j.setStatus(Constants.POSTPROC);
							CentralDogma.getInstance().localJobs.saveJobChangesToList(j);
							j.workflowTask.postProcess();
							j.setTimeFinished(new Date());
							
							if(j.getEmailOnCompletion().equalsIgnoreCase("true")){
								SendEmails.sendJobCompletedEmail(j);
							}
							CentralDogma.getInstance().localJobs.saveJobChangesToList(j);
						}
						catch(Exception ex){
							//Job failed or threw an exception
							Utility.writeToDebug("JOB FAILED: " + j.getUserName() + " " + j.getJobName());
							Utility.writeToDebug(ex);

							//send an email to the site administrator
							String message = "Heya, \n" + j.getUserName() + "'s job \"" +
							j.getJobName() + "\" failed. You might wanna look into that. "
							+ "\n\nHere's the exception it threw: \n" + ex.toString() + 
							"\n\nGood luck!\n--Chembench";
							SendEmails.sendEmail("ceccr@email.unc.edu", "", "", "Job failed: " + j.getJobName(), message);
						}
						finally{
							CentralDogma.getInstance().localJobs.removeJob(j.getId());							
							CentralDogma.getInstance().localJobs.deleteJobFromDB(j.getId());
						}						
					}
					else{
						//some other thread already got this job. Don't worry about it.
					}
				}
				
			} catch (Exception ex) {
				Utility.writeToDebug(ex);
			}
		}
    }
}
