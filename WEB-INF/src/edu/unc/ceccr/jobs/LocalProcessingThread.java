package edu.unc.ceccr.jobs;

import java.util.ArrayList;
import java.util.List;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.Job;
import edu.unc.ceccr.utilities.Utility;


public class LocalProcessingThread extends Thread {

	//this thread will work on the localJobs joblist.
	//There can be any number of these threads.
	
	public void run() {
		while(true){
			try {
				sleep(500);
				Utility.writeToDebug("LocalProcessingThread awake!");
				//pull out a job and start it running
				ArrayList<Job> jobs = CentralDogma.getInstance().localJobs.getReadOnlyCopy();
				for(Job j: jobs){
					if(j.getStatus().equals(Constants.QUEUED)){
						//try to get this job. Note that another thread may be trying to get it too.
						if(CentralDogma.getInstance().localJobs.startJob(j)){
	
							Utility.writeToDebug("Local queue: Started job " + j.getJobName());
							
							j.setStatus(Constants.PREPROC);
							j.workflowTask.preProcess();
							j.setStatus(Constants.RUNNING);
							j.workflowTask.executeLocal();
							j.setStatus(Constants.POSTPROC);
							j.workflowTask.postProcess();
						}
						else{
							//some other thread already got this job. Don't worry about it.
						}
					}
				}
				
			} catch (Exception ex) {
				Utility.writeToDebug(ex);
			}
		}
    }
}
