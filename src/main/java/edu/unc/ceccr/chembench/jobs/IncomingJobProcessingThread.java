package edu.unc.ceccr.chembench.jobs;

import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.Job;
import edu.unc.ceccr.chembench.persistence.JobRepository;
import edu.unc.ceccr.chembench.taskObjects.QsarModelingTask;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.List;

@Configurable(autowire = Autowire.BY_TYPE)
public class IncomingJobProcessingThread extends Thread {
    private static final Logger logger = Logger.getLogger(IncomingJobProcessingThread.class.getName());
    // this takes jobs off the incomingJobs joblist and sends them to lsfJobs
    // and localJobs.
    // You should only ever have one of these threads running - don't start a
    // second one!
    @Autowired
    private JobRepository jobRepository;

    public void run() {
        while (true) {
            try {
                sleep(1000);

                // determine which jobs should be sent to the LSF jobs list,
                // which should stay here, and which should go to the local
                // jobs list.
                List<Job> incomingJobs = CentralDogma.getInstance().incomingJobs.getReadOnlyCopy();
                for (Job j : incomingJobs) {
                    boolean movedJob = false;
                    if (j.getJobType().equals(Constants.DATASET)) {
                        // send it to local
                        movedJob = true;
                        logger.info("Sending job " + j.getJobName() + " to local queue");
                        j.setJobList(Constants.LOCAL);
                        j.workflowTask.jobList = Constants.LOCAL;
                        CentralDogma.getInstance().localJobs.addJob(j);
                        CentralDogma.getInstance().incomingJobs.removeJob(j.getId());
                    } else if (j.getJobType().equals(Constants.PREDICTION)) {
                        // send it to local
                        logger.info("Sending job " + j.getJobName() + " to local queue");
                        movedJob = true;
                        j.setJobList(Constants.LOCAL);
                        j.workflowTask.jobList = Constants.LOCAL;
                        CentralDogma.getInstance().localJobs.addJob(j);
                        CentralDogma.getInstance().incomingJobs.removeJob(j.getId());
                    } else if (j.getJobType().equals(Constants.MODELING)) {
                        // check LSF status. If LSF can accept another job,
                        // put it there.
                        QsarModelingTask qs = (QsarModelingTask) j.workflowTask;
                        if (qs.getModelType().equals(Constants.KNN) || qs.getModelType().equals(Constants.KNNSA) || qs
                                .getModelType().equals(Constants.KNNGA) || qs.getModelType().equals(Constants.SVM)) {
                            if (LsfProcessingThread.lsfHasFreePendSlots()) {
                                logger.info("Sending job " + j.getJobName() + " to LSF queue");
                                movedJob = true;
                                j.setJobList(Constants.LSF);
                                j.workflowTask.jobList = Constants.LSF;
                                CentralDogma.getInstance().lsfJobs.addJob(j);
                                CentralDogma.getInstance().incomingJobs.removeJob(j.getId());
                            }
                        } else {
                            // it's an RF job.
                            // send it to local
                            logger.info("Sending job " + j.getJobName() + " to local queue");
                            movedJob = true;
                            j.setJobList(Constants.LOCAL);
                            j.workflowTask.jobList = Constants.LOCAL;
                            CentralDogma.getInstance().localJobs.addJob(j);
                            CentralDogma.getInstance().incomingJobs.removeJob(j.getId());
                        }
                    }

                    if (movedJob) {
                        // update job DB entry to reflect queue change
                        jobRepository.save(j);
                    }
                }
            } catch (Exception ex) {
                logger.error("", ex);
            }
        }
    }

}
