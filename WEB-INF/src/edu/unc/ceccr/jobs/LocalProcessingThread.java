package edu.unc.ceccr.jobs;

import java.util.ArrayList;
import java.util.List;

import edu.unc.ceccr.utilities.Utility;


public class LocalProcessingThread extends Thread {

	//this thread will work on the localJobs joblist.
	//There can be any number of these threads.
	
	SynchronizedJobList jobList;

	LocalProcessingThread(SynchronizedJobList jobList) {
		this.jobList = jobList;
	}
	

	public void run() {
		try {
			sleep(500);
			
			
			
			
			
		} catch (Exception ex) {
			Utility.writeToDebug(ex);
		}
    }
}
