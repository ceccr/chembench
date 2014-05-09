package edu.unc.ceccr.jobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Job;
import edu.unc.ceccr.persistence.JobStats;
import edu.unc.ceccr.utilities.PopulateDataObjects;

import org.apache.log4j.Logger;

public class SynchronizedJobList {

    private static Logger logger = Logger.getLogger(SynchronizedJobList.class.getName());

    //stores a concurrent-access arraylist of jobs
    //In Chembench, one instance of this class holds all locally-running jobs
    //a second instance holds all the LSF-related jobs.
    //a third instance is used to store freshly-submitted jobs that have not yet been processed.

    private List<Job> jobList = Collections.synchronizedList(new ArrayList<Job>());
    private String name; //LSF, LOCAL, INCOMING, or ERROR

    SynchronizedJobList(String name) {
        this.name = name;
    }

    public void saveJobChangesToList(Job job) {
        synchronized (jobList) {
            saveJobChangesToListSync(job);
        }
    }

    private void saveJobChangesToListSync(Job job) {
        //only call this from inside a synchronized block
        for (int i = 0; i < jobList.size(); i++) {
            if (jobList.get(i).getId() == job.getId()) {
                Job listJob = jobList.get(i);
                //note that this does not alter all possible job parameters,
                //just the ones expected to change.
                listJob.setEmailOnCompletion(job.getEmailOnCompletion());
                listJob.setLsfJobId(job.getLsfJobId());
                listJob.setMessage(job.getMessage());
                listJob.setStatus(job.getStatus());
                listJob.setTimeCreated(job.getTimeCreated());
                listJob.setTimeFinished(job.getTimeFinished());
                listJob.setTimeFinishedEstimate(job.getTimeFinishedEstimate());
                listJob.setTimeStarted(job.getTimeStarted());
                listJob.setTimeStartedByLsf(job.getTimeStartedByLsf());

                commitJobChanges(job);
            }
        }
    }

    public void printJobListStates() {
        synchronized (jobList) {
            for (Job j : jobList) {
                logger.debug(j.getJobName() + " : " + j.getJobList() + " : " + j.getStatus());
            }
        }
    }

    public Job removeJob(Long jobId) {
        //removes the job from this list.
        synchronized (jobList) {
            for (int i = 0; i < jobList.size(); i++) {
                if (jobList.get(i).getId().equals(jobId)) {
                    Job j = jobList.get(i);
                    logger.debug("removed job: " + j.getJobName());
                    jobList.remove(i);
                    return j;
                }
            }
        }
        return null;
    }

    public void deleteJobFromDB(Long jobId) {
        //used whenever a job is finished or canceled.
        //delete the job. Add its info to a new JobStats.

        Session s = null;
        Transaction tx = null;
        Job job = null;
        try {
            s = HibernateUtil.getSession();
            tx = s.beginTransaction();
            job = (Job) s.createCriteria(Job.class)
                    .add(Restrictions.eq("id", jobId))
                    .uniqueResult();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            logger.error(e);
        }

        JobStats js = new JobStats();
        try {
            js.setJobName(job.getJobName());
            js.setJobType(job.getJobType());
            js.setNumCompounds(job.getNumCompounds());
            js.setNumModels(job.getNumModels());
            js.setTimeCreated(job.getTimeCreated());
            js.setTimeFinished(job.getTimeFinished());
            js.setTimeStarted(job.getTimeStarted());
            js.setTimeStartedByLsf(job.getTimeStartedByLsf());
            js.setUserName(job.getUserName());
        } catch (Exception ex) {
            logger.error(ex);
        }

        try {
            tx = s.beginTransaction();
            s.delete(job);
            s.save(js);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            logger.error(e);
        } finally {
            s.close();
        }
    }

    public void addJob(Job job) {
        synchronized (jobList) {
            jobList.add(job);
        }
    }

/*
 * You basically never want to do update the jobList from the DB. Why?
 * While the server is running, it will write job updates to the DB, ensuring
 * that the DB record of each job is as updated as possible. However,
 * the job records are never read FROM the database, except when the server
 * starts (when CentralDogma is instantiated).
 * So, during normal server operation, you're never going to want to read from
 * the Jobs table. The Jobs table is really only there for recovering jobs if the server
 * needs to be stopped / restarted.
 * 
	private void updateJobsFromDB(){

		ArrayList<Job> freshJobList = new ArrayList<Job>();
		Session s = null; 
		try {
			s = HibernateUtil.getSession();
			freshJobList = (ArrayList<Job>) s.createCriteria(Job.class)
			.add(Expression.eq("jobList", name))
			.addOrder(Order.asc("id"))
			.list();
		} catch (Exception e) {
			logger.error(e);
		} finally {
			s.close();
		}

		synchronized(jobList){
			for(int i = 0; i < jobList.size(); i++){
				WorkflowTask wt = jobList.get(i).workflowTask;
				
			}
		}
	}*/

    public ArrayList<Job> getReadOnlyCopy() {
        synchronized (jobList) {

            ArrayList<Job> jobListCopy = new ArrayList<Job>();
            try {

                //return a copy of it
                jobListCopy.addAll(jobList);
            } catch (Exception ex) {
                logger.error(ex);
            }
            return jobListCopy;
        }
    }

    public boolean startJob(Long jobId) {
        //called when a thread picks a job from the list and starts working on it
        synchronized (jobList) {
            for (Job j : jobList) {
                if (j.getId() == jobId) {
                    if (!j.getStatus().equals(Constants.QUEUED)) {
                        //some other thread has already grabbed this job and is working on it.
                        return false;
                    } else {
                        j.setStatus(Constants.RUNNING);
                        //commit the job's "running" status to DB
                        saveJobChangesToListSync(j);
                        commitJobChanges(j);

                        return true;
                    }
                }
            }
            //if job not found
            return false;
        }
    }

    public boolean startPostJob(Long jobId) {
        //called when a thread picks a job from the list and starts working on postprocessing for it
        synchronized (jobList) {

            for (Job j : jobList) {
                if (j.getId() == jobId) {
                    if (!j.getStatus().equals(Constants.RUNNING)) {
                        //some other thread has already grabbed this job and is working on it.
                        return false;
                    } else {
                        j.setStatus(Constants.POSTPROC);

                        //commit the job's "running" status to DB
                        saveJobChangesToListSync(j);
                        commitJobChanges(j);

                        return true;
                    }
                }
            }
            //if job not found
            return false;
        }
    }

    private void commitJobChanges(Job j) {
        Transaction tx = null;
        Session session = null;
        try {
            session = HibernateUtil.getSession();
            Long id = j.getId();
            Job databaseJob = PopulateDataObjects.getTaskById(id, session);

            databaseJob.setEmailOnCompletion(j.getEmailOnCompletion());
            databaseJob.setLsfJobId(j.getLsfJobId());
            databaseJob.setMessage(j.getMessage());
            databaseJob.setStatus(j.getStatus());
            databaseJob.setTimeCreated(j.getTimeCreated());
            databaseJob.setTimeFinished(j.getTimeFinished());
            databaseJob.setTimeFinishedEstimate(j.getTimeFinishedEstimate());
            databaseJob.setTimeStarted(j.getTimeStarted());
            databaseJob.setTimeStartedByLsf(j.getTimeStartedByLsf());

            tx = session.beginTransaction();
            session.saveOrUpdate(databaseJob);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            logger.error(e);
        } finally {
            session.close();
        }

    }

}