package edu.unc.ceccr.jobs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.Job;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.SendEmails;
import edu.unc.ceccr.utilities.Utility;


public class LsfProcessingThread extends Thread {

	//this works on the LSFJobs joblist.
	//You should only ever have one of these threads running - don't start a second one!
	
	HashMap<String, String> oldLsfStatuses = new HashMap<String, String>(); //used to determine when a job goes from PEND to RUN.
	
	public void run() {

		while(true){
			try {
				sleep(1500);
				
				//did any jobs just get added to this structure? If so, preprocess them and bsub them.
				ArrayList<Job> readOnlyJobArray = CentralDogma.getInstance().lsfJobs.getReadOnlyCopy();
				for(Job j : readOnlyJobArray){
					if(j.getStatus().equals(Constants.QUEUED)){
						//try to grab the job and preproc it
						if(CentralDogma.getInstance().lsfJobs.startJob(j.getId())){
							

							try{
								Utility.writeToDebug("LSFQueue: Starting job " + j.getJobName() + " from user " + j.getUserName());
								j.setTimeStarted(new Date());
								j.setStatus(Constants.PREPROC);
								j.workflowTask.preProcess();
								j.setStatus(Constants.RUNNING);
								j.setLsfJobId(j.workflowTask.executeLSF());
								CentralDogma.getInstance().lsfJobs.saveJobChangesToList(j);
							}
							catch(Exception ex){
								//Job failed or threw an exception
								Utility.writeToDebug("JOB FAILED: " + j.getUserName() + " " + j.getJobName());
								Utility.writeToDebug(ex);
								CentralDogma.getInstance().localJobs.removeJob(j.getId());							
								CentralDogma.getInstance().localJobs.deleteJobFromDB(j.getId());
							}
							
						}
					}
				}
				
				
				ArrayList<LsfJobStatus> lsfJobStatuses = checkLsfStatus(Constants.CECCR_USER_BASE_PATH);
				
				//determine if any pending jobs in LSF have started; update the Job objects if so.
				//compare the new job statuses against the ones from the previous check
				for(LsfJobStatus jobStatus : lsfJobStatuses){
					if( (oldLsfStatuses.containsKey(jobStatus.jobid) && 
							oldLsfStatuses.get(jobStatus.jobid).equals("PEND") && 
							jobStatus.stat.equals("RUN")) ||
							(! oldLsfStatuses.containsKey(jobStatus.jobid) && 
									jobStatus.stat.equals("RUN"))){
						//the job *just* started on LSF. Find the job with this lsfJobId and set its date.
						for(Job j: readOnlyJobArray){
							if(j.getLsfJobId().equals(jobStatus.jobid)){
								j.setTimeStartedByLsf(new Date());
								CentralDogma.getInstance().lsfJobs.saveJobChangesToList(j);
							}
						}
					}
					oldLsfStatuses.put(jobStatus.jobid, jobStatus.stat);
				}

				//If there's a finished job that needs postprocessing, do so.
				for(LsfJobStatus jobStatus : lsfJobStatuses){
					if(jobStatus.stat.equals("DONE") || jobStatus.stat.equals("EXIT")){
						//check if this is a running job
						for(Job j : readOnlyJobArray){
							if(j.getLsfJobId() != null && j.getLsfJobId().equals(jobStatus.jobid)){
								Utility.writeToDebug("trying postprocessing on job: " + j.getJobName() + " from user: " + j.getUserName());
								if(CentralDogma.getInstance().lsfJobs.startPostJob(j.getId())){
									
									try{
										Utility.writeToDebug("Postprocessing job: " + j.getJobName() + " from user: " + j.getUserName());
										j.workflowTask.postProcess();
										j.setTimeFinished(new Date());
										
										if(j.getEmailOnCompletion().equalsIgnoreCase("true")){
											SendEmails.sendJobCompletedEmail(j);
										}
										
										CentralDogma.getInstance().lsfJobs.saveJobChangesToList(j);

									}
									catch(Exception ex){
										//Job failed or threw an exception
										Utility.writeToDebug("JOB FAILED: " + j.getUserName() + " " + j.getJobName());
										Utility.writeToDebug(ex);
									}
									finally{
										//finished; remove job object
										CentralDogma.getInstance().lsfJobs.removeJob(j.getId());						
										CentralDogma.getInstance().lsfJobs.deleteJobFromDB(j.getId());
									}
									
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
		
		if(CentralDogma.getInstance().lsfJobs.getReadOnlyCopy().size() > Constants.MAXLSFJOBS){
			return false;
		}
		else{
			return true;
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