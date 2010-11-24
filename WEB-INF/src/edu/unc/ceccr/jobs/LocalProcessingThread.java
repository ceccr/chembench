package edu.unc.ceccr.jobs;

import java.io.PrintWriter;
import java.io.StringWriter;
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
							
							//finished; remove job object
							CentralDogma.getInstance().localJobs.removeJob(j.getId());							
							CentralDogma.getInstance().localJobs.deleteJobFromDB(j.getId());
						}
						catch(Exception ex){
							//Job failed or threw an exception
							Utility.writeToDebug("JOB FAILED: " + j.getUserName() + " " + j.getJobName());
							CentralDogma.getInstance().moveJobToErrorList(j.getId());
							CentralDogma.getInstance().localJobs.saveJobChangesToList(j);
							Utility.writeToDebug(ex);

							//prepare a nice HTML-formatted readable version of the exception
							StringWriter sw = new StringWriter();
							ex.printStackTrace(new PrintWriter(sw));
							String exceptionAsString = sw.toString();
							exceptionAsString.replaceAll("\n", "<br />");
							
							//send an email to the site administrator
							String message = "Heya, <br />" + j.getUserName() + "'s job \"" +
							j.getJobName() + "\" failed. You might wanna look into that. "
							+ "<br /><br />Here's the exception it threw: <br />" + ex.toString() + 
							"<br /><br />Good luck!<br />--Chembench";
							message += "<br /><br />The full stack trace is below. Happy debugging!<br /><br />" +
							exceptionAsString;
							SendEmails.sendEmail("ceccr@email.unc.edu", "", "", "Job failed: " + j.getJobName(), message);
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
