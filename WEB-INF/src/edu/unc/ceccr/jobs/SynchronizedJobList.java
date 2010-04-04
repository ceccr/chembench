package edu.unc.ceccr.jobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Job;
import edu.unc.ceccr.persistence.JobStats;
import edu.unc.ceccr.persistence.PredictionValue;
import edu.unc.ceccr.utilities.Utility;

public class SynchronizedJobList{
	//stores a concurrent-access arraylist of jobs
	//In Chembench, one instance of this class holds all locally-running jobs
	//a second instance holds all the LSF-related jobs.
	//a third instance is used to store freshly-submitted jobs that have not yet been processed.
	
	private List<Job> jobList = Collections.synchronizedList(new ArrayList<Job>());
	private String name; //LSF, LOCAL, or INCOMING
	
	SynchronizedJobList(String name){
		this.name = name;
	}
	
	public void removeJob(Job job){
		//removes the job from this list.
		synchronized(jobList){
			for(int i = 0; i < jobList.size(); i++){
				if(jobList.get(i).equals(job)){
					jobList.remove(i);
				}
			}
		}
	}
	
	public void deleteJob(Job job){
		//delete the job. Add its info to a new JobStats.
		JobStats js = new JobStats();
		js.setJobName(job.getJobName());
		js.setJobType(job.getJobType());
		js.setNumCompounds(job.getNumCompounds());
		js.setNumModels(job.getNumModels());
		js.setTimeCreated(job.getTimeCreated());
		js.setTimeFinished(job.getTimeFinished());
		js.setTimeStarted(job.getTimeStarted());
		js.setTimeStartedByLsf(job.getTimeStartedByLsf());
		js.setUserName(job.getUserName());
		
		Session s = null; 
		Transaction tx = null;
		try {
			s = HibernateUtil.getSession();
			tx = s.beginTransaction();
			s.delete(job);
			s.save(js);
			tx.commit();
		} catch (Exception e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			s.close();
		}
	}
	
	public void addJob(Job job){
		synchronized(jobList){
			jobList.add(job);
		}
	}


	public ArrayList<Job> getReadOnlyCopy(){
		synchronized(jobList){
			//refresh jobList from database
			
			jobList = null;
			Session s = null; 
			Transaction tx = null;
			try {
				s = HibernateUtil.getSession();
				tx = s.beginTransaction();
				jobList = (ArrayList<Job>) s.createCriteria(Job.class)
				.add(Expression.eq("jobList", name))
				.addOrder(Order.asc("id"))
				.list();
			} catch (Exception e) {
				if (tx != null)
					tx.rollback();
				Utility.writeToDebug(e);
			} finally {
				s.close();
			}
			if(jobList == null){
				jobList = new ArrayList<Job>();
			}
			
			//return a copy of it
			ArrayList<Job> jobListCopy = new ArrayList<Job>();
			jobListCopy.addAll(jobList);
			return jobListCopy;
		}
	}

	public boolean startJob(Job j) {
		//called when a thread picks a job from the list and starts working on it
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

	public boolean startPostJob(Job j) {
		//called when a thread picks a job from the list and starts working on postprocessing for it
		synchronized(jobList){
			if(! j.getStatus().equals(Constants.RUNNING)){
				//some other thread has already grabbed this job and is working on it.
				return false;
			}
			else{
				j.setStatus(Constants.POSTPROC);
				
				//commit the job's "running" status to DB
				commitJobChanges(j);
			
				return true;
			}
		}
	}
	
	public void finishJob(Job j){
		//called when a thread finishes work on a job
		synchronized(jobList){
			j.setStatus(Constants.QUEUED);
			
			//commit the job's "queued" status to DB
			commitJobChanges(j);
			
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
		
		/*
		//could limit this to only jobs that are actually in this list..?
		synchronized(jobList){
			for(int i = 0; i < jobList.size(); i++){
				if(jobList.get(i).equals(job)){
					jobList.set(i, newJob);
					commitJobChanges(newJob);
				}
			}
		}
		 */
	}
	
}