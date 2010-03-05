package edu.unc.ceccr.jobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SynchronizedJobSet{
	//stores a set of jobs
	//In Chembench, one instance of this class holds all locally-running jobs
	//a second instance holds all the LSF-related jobs.
	//a third instance is used to store freshly-submitted jobs that have not yet been processed.
	
	private List<String> jobSet = Collections.synchronizedList(new ArrayList<String>());
	
	public void deleteJob(int jobIndex){
		synchronized(jobSet){
			jobSet.remove(jobIndex);
		}
	}

	public void addJob(String job){
		synchronized(jobSet){
			jobSet.add(job);
		}
	}
	
	public ArrayList<String> getReadOnlyCopy(){
		ArrayList<String> jobSetCopy = new ArrayList<String>();
		synchronized(jobSet){
			jobSetCopy.addAll(jobSet);
		}
		return jobSetCopy;
	}
	
	public void modifyJob(String job, String newJob){
		synchronized(jobSet){
			for(int i = 0; i < jobSet.size(); i++){
				if(jobSet.get(i).equals(job)){
					jobSet.set(i, newJob);
				}
			}
		}
	}
	
}