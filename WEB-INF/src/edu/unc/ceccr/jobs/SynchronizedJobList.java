package edu.unc.ceccr.jobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.unc.ceccr.persistence.Job;

public class SynchronizedJobList{
	//stores a concurrent-access arraylist of jobs
	//In Chembench, one instance of this class holds all locally-running jobs
	//a second instance holds all the LSF-related jobs.
	//a third instance is used to store freshly-submitted jobs that have not yet been processed.
	
	private List<Job> jobList = Collections.synchronizedList(new ArrayList<Job>());

	public void removeJob(String job){
		synchronized(jobList){
			for(int i = 0; i < jobList.size(); i++){
				if(jobList.get(i).equals(job)){
					jobList.remove(i);
				}
			}
		}
	}
	
	public void addJob(Job job){
		synchronized(jobList){
			jobList.add(job);
		}
	}
	
	public ArrayList<Job> getReadOnlyCopy(){
		ArrayList<Job> jobListCopy = new ArrayList<Job>();
		synchronized(jobList){
			jobListCopy.addAll(jobList);
		}
		return jobListCopy;
	}
	
	public void modifyJob(Job job, Job newJob){
		synchronized(jobList){
			for(int i = 0; i < jobList.size(); i++){
				if(jobList.get(i).equals(job)){
					jobList.set(i, newJob);
				}
			}
		}
	}
	
	
}