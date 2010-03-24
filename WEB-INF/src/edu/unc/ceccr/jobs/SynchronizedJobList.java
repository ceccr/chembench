package edu.unc.ceccr.jobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Job;
import edu.unc.ceccr.utilities.Utility;

public class SynchronizedJobList{
	//stores a concurrent-access arraylist of jobs
	//In Chembench, one instance of this class holds all locally-running jobs
	//a second instance holds all the LSF-related jobs.
	//a third instance is used to store freshly-submitted jobs that have not yet been processed.
	
	private List<Job> jobList = Collections.synchronizedList(new ArrayList<Job>());

	public void removeJob(Job job){
		synchronized(jobList){
			for(int i = 0; i < jobList.size(); i++){
				if(jobList.get(i).equals(job)){
					jobList.remove(i);

					Session s = null; 
					Transaction tx = null;
					try {
						s = HibernateUtil.getSession();
						tx = s.beginTransaction();
						s.delete(job);
						tx.commit();
					} catch (Exception e) {
						if (tx != null)
							tx.rollback();
						Utility.writeToDebug(e);
					} finally {
						s.close();
					}
					
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
					commitJobChanges(newJob);
				}
			}
		}
	}

	public boolean startJob(Job j) {
		synchronized(jobList){
			if(! j.getStatus().equals(Constants.QUEUED)){
				//some other thread has already grabbed this job and is working on it.
				return false;
			}
			else{
				j.setStatus(Constants.RUNNING);
				
				//commit the job's "running" status to DB
				commitJobChanges(j);
			
				return true;
			}
		}
	}
	
	private void commitJobChanges(Job j){
		Session s = null; 
		Transaction tx = null;
		try {
			s = HibernateUtil.getSession();
			tx = s.beginTransaction();
			s.saveOrUpdate(j);
			tx.commit();
		} catch (Exception e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			s.close();
		}
	}
	
}