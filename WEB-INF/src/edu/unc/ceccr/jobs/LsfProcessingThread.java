package edu.unc.ceccr.jobs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.Job;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.Utility;


public class LsfProcessingThread extends Thread {

	//this works on the LSFJobs joblist.
	//You should only ever have one of these threads running - don't start a second one!
	
	SynchronizedJobList jobList;

	LsfProcessingThread(SynchronizedJobList jobList) {
		this.jobList = jobList;
	}
	

	public void run() {
		try {
			sleep(500);
			
			//did any jobs just get added to this structure? If so, preprocess them and bsub them.
			ArrayList<Job> readOnlyJobArray = jobList.getReadOnlyCopy();
			for(Job j : readOnlyJobArray){
				
				
			}
			
			//If there's a finished job that needs postprocessing, do so.
			ArrayList<LsfJobStatus> lsfJobStatuses = checkLsfStatus(Constants.CECCR_USER_BASE_PATH);
			for(LsfJobStatus jobStatus : lsfJobStatuses){
				if(jobStatus.stat.equals("DONE") || jobStatus.stat.equals("EXIT")){
					//check if this is a running job
					for(Job j : readOnlyJobArray){
						
						
					}
				}
			}
			
		} catch (Exception ex) {
			Utility.writeToDebug(ex);
		}
    }
	
	//static functions for checking the status of the LSF queue(s) on Emerald.
	public static ArrayList<String> getCompletedJobNames(){
		ArrayList<String> finishedJobNames = new ArrayList<String>();
		
		return finishedJobNames;
	}
	
	//exec bjobs and get results
	public static ArrayList<LsfJobStatus> checkLsfStatus(String workingDir) throws Exception{
		//remove outfile if already exists
		FileAndDirOperations.deleteFile(workingDir + "bjobs-out.txt");
		
		//run bjobs
		String command = "bjobs.sh";
		Utility.writeToDebug("Running external program: " + command + " in dir " + workingDir);
		Process p = Runtime.getRuntime().exec(command, null, new File(workingDir));
		Utility.writeProgramLogfile(workingDir, "bjobs.sh", p.getInputStream(), p.getErrorStream());
		p.waitFor();
		
		//read in results
		ArrayList<LsfJobStatus> lsfStatusList = new ArrayList<LsfJobStatus>();
		BufferedReader br = new BufferedReader(new FileReader(workingDir + "bjobs-out.txt"));
		String line = "";
		br.readLine(); //skip header
		while((line = br.readLine()) != null){
			if(! line.trim().equals("")){
				//non empty line
				LsfJobStatus l = new LsfJobStatus(line);
				lsfStatusList.add(l);
			}
		}
		
		return lsfStatusList;
	}
	
}