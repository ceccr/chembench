package edu.unc.ceccr.jobs;

import java.util.ArrayList;
import java.util.List;

import edu.unc.ceccr.utilities.Utility;


public class IncomingJobProcessingThread extends Thread {

	//this works on the incomingJobs joblist.
	//You should only ever have one of these threads running - don't start a second one!
	
	SynchronizedJobList jobList;

	IncomingJobProcessingThread(SynchronizedJobList jobList) {
		this.jobList = jobList;
	}
	
	public void run() {
		try {
			sleep(500);
			//determine which jobs should be sent to the LSF jobs list, 
			//which should stay here, and which should go to the local jobs list.
			
			
		} catch (Exception ex) {
			Utility.writeToDebug(ex);
		}
    }
	
}
