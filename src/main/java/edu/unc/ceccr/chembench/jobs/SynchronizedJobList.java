package edu.unc.ceccr.chembench.jobs;

import com.google.common.collect.Lists;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.Job;
import edu.unc.ceccr.chembench.persistence.JobRepository;
import edu.unc.ceccr.chembench.persistence.JobStats;
import edu.unc.ceccr.chembench.persistence.JobStatsRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configurable(autowire = Autowire.BY_TYPE)
public class SynchronizedJobList {

    private static final Logger logger = Logger.getLogger(SynchronizedJobList.class.getName());

    //stores a concurrent-access arraylist of jobs
    //In Chembench, one instance of this class holds all locally-running jobs
    //a second instance holds all the LSF-related jobs.
    //a third instance is used to store freshly-submitted jobs that have not yet been processed.

    private final List<Job> jobList = Collections.synchronizedList(new ArrayList<Job>());
    private final String name; //LSF, LOCAL, INCOMING, or ERROR

    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private JobStatsRepository jobStatsRepository;

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
            if (jobList.get(i).getId().equals(job.getId())) {
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
        Job job = jobRepository.findOne(jobId);
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

        jobRepository.delete(job);
        jobStatsRepository.save(js);
    }

    public void addJob(Job job) {
        synchronized (jobList) {
            jobList.add(job);
        }
    }

    public List<Job> getReadOnlyCopy() {
        synchronized (jobList) {
            List<Job> jobListCopy = Lists.newArrayList();
            try {

                //return a copy of it
                jobListCopy.addAll(jobList);
            } catch (Exception ex) {
                logger.error("", ex);
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
                if (j.getId().equals(jobId)) {
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
        Job databaseJob = jobRepository.findOne(j.getId());
        databaseJob.setEmailOnCompletion(j.getEmailOnCompletion());
        databaseJob.setLsfJobId(j.getLsfJobId());
        databaseJob.setMessage(j.getMessage());
        databaseJob.setStatus(j.getStatus());
        databaseJob.setTimeCreated(j.getTimeCreated());
        databaseJob.setTimeFinished(j.getTimeFinished());
        databaseJob.setTimeFinishedEstimate(j.getTimeFinishedEstimate());
        databaseJob.setTimeStarted(j.getTimeStarted());
        databaseJob.setTimeStartedByLsf(j.getTimeStartedByLsf());
        jobRepository.save(databaseJob);
    }

    public String getName() {
        return name;
    }
}
