package edu.unc.ceccr.jobs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.Job;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.Utility;


public class LsfProcessingThread extends Thread {

	//this works on the LSFJobs joblist.
	//You should only ever have one of these threads running - don't start a second one!
	
	public void run() {

		while(true){
			try {
				sleep(1500);
				
				//did any jobs just get added to this structure? If so, preprocess them and bsub them.
				ArrayList<Job> readOnlyJobArray = CentralDogma.getInstance().lsfJobs.getReadOnlyCopy();
				for(Job j : readOnlyJobArray){
					if(j.getStatus().equals(Constants.QUEUED)){
						//try to grab the job and preproc it
						if(CentralDogma.getInstance().lsfJobs.startJob(j)){
							Utility.writeToDebug("LSFQueue: Starting job " + j.getJobName() + " from user " + j.getUserName());
							j.setStatus(Constants.PREPROC);
							j.workflowTask.preProcess();
							j.setStatus(Constants.RUNNING);
							j.setLsfJobId(j.workflowTask.executeLSF());
							//get job ID from job submission logfile
							
							
						}
					}
				}
				
				//If there's a finished job that needs postprocessing, do so.
				ArrayList<LsfJobStatus> lsfJobStatuses = checkLsfStatus(Constants.CECCR_USER_BASE_PATH);
				for(LsfJobStatus jobStatus : lsfJobStatuses){
					if(jobStatus.stat.equals("DONE") || jobStatus.stat.equals("EXIT")){
						//check if this is a running job
						for(Job j : readOnlyJobArray){
							
							//WARNING - bug if user submits two jobs with the same name within a short time period
							if(j.getLsfJobId() != null && j.getLsfJobId().equals(jobStatus.jobid)){
								Utility.writeToDebug("trying postprocessing on job: " + j.getJobName() + " from user: " + j.getUserName());
								if(CentralDogma.getInstance().lsfJobs.startPostJob(j)){
									Utility.writeToDebug("Postprocessing job: " + j.getJobName() + " from user: " + j.getUserName());
									j.workflowTask.postProcess();
									//finished; remove job object
									CentralDogma.getInstance().lsfJobs.removeJob(j);						
									CentralDogma.getInstance().lsfJobs.deleteJob(j);
								}
							}
						}
					}
				}
				
			} catch (Exception ex) {
				Utility.writeToDebug(ex);
			}
		}
    }
	
	public static boolean lsfHasFreePendSlots(){
		//check how many pending jobs there are
		//if that number is less than the limit return true
		//else return false
		//to do : look up these limits
		return true;
	}
	
	//static functions for checking the status of the LSF queue(s) on Emerald.
	public static ArrayList<String> getCompletedJobNames(){
		ArrayList<String> finishedJobNames = new ArrayList<String>();
		
		return finishedJobNames;
	}
	
	//exec bjobs and get results
	public static ArrayList<LsfJobStatus> checkLsfStatus(String workingDir) throws Exception{
		//remove outfile if already exists
		
		if((new File(workingDir + "bjobs-out.txt")).exists()){
			FileAndDirOperations.deleteFile(workingDir + "bjobs-out.txt");
		}
		
		//run bjobs
		String command = "bjobs.sh";
		//Utility.writeToDebug("Running external program: " + command + " in dir " + workingDir);
		
		Process p = Runtime.getRuntime().exec(command, null, new File(workingDir));
		p.waitFor();
		if (p != null) {
	        Utility.close(p.getOutputStream());
	        Utility.close(p.getInputStream());
	        Utility.close(p.getErrorStream());
	        p.destroy();
	    }
		
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
		
		br.close();
		
		return lsfStatusList;
	}
	
}