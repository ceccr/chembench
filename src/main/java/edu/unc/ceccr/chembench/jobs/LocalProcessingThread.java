package edu.unc.ceccr.chembench.jobs;

import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.*;
import edu.unc.ceccr.chembench.utilities.FileAndDirOperations;
import edu.unc.ceccr.chembench.utilities.SendEmails;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;

@Configurable(autowire = Autowire.BY_TYPE)
public class LocalProcessingThread extends Thread {

    private static final Logger logger = Logger.getLogger(LocalProcessingThread.class.getName());
    // this thread will work on the localJobs joblist.
    // There can be any number of these threads.

    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private UserRepository userRepository;

    public void run() {
        while (true) {
            try {
                sleep(1500);
                // logger.debug("LocalProcessingThread awake!");
                // pull out a job and start it running
                List<Job> jobs = CentralDogma.getInstance().localJobs.getReadOnlyCopy();
                for (Job j : jobs) {
                    // try to get this job. Note that another thread may be
                    // trying to get it too.
                    if (j != null && CentralDogma.getInstance().localJobs.startJob(j.getId())) {
                        logger.debug("Local queue: Started job " + j.getJobName());
                        j.setTimeStarted(new Date());
                        j.setStatus(Constants.PREPROC);
                        CentralDogma.getInstance().localJobs.saveJobChangesToList(j);

                        try {
                            j.workflowTask.preProcess();

                            j.setStatus(Constants.RUNNING);
                            CentralDogma.getInstance().localJobs.saveJobChangesToList(j);
                            j.workflowTask.executeLocal();

                            j.setStatus(Constants.POSTPROC);
                            CentralDogma.getInstance().localJobs.saveJobChangesToList(j);
                            j.workflowTask.postProcess();
                            j.setTimeFinished(new Date());

                            if (j.getEmailOnCompletion().equalsIgnoreCase("true")) {
                                SendEmails.sendJobCompletedEmail(j);
                            }
                            CentralDogma.getInstance().localJobs.saveJobChangesToList(j);

                            // if job was started by guest check if he still
                            // exists
                            if (j.getUserName().contains("guest")) {
                                // removing all quest data if guest time on
                                // site was out
                                if (!j.getUserName().isEmpty()
                                        && userRepository.findByUserName(j.getUserName()) == null) {
                                    FileAndDirOperations
                                            .deleteDir(new File(Constants.CECCR_USER_BASE_PATH + j.getUserName()));
                                }
                            }
                            // finished; remove job object
                            CentralDogma.getInstance().localJobs.removeJob(j.getId());
                            CentralDogma.getInstance().localJobs.deleteJobFromDB(j.getId());
                        } catch (Exception ex) {
                            // Job failed or threw an exception
                            logger.error("JOB FAILED: " + j.getUserName() + " " + j.getJobName());
                            if (j.getUserName().contains("guest")) {
                                // removing all quest data if guest time on site was out
                                if (!j.getUserName().isEmpty()
                                        && userRepository.findByUserName(j.getUserName()) == null) {
                                    FileAndDirOperations
                                            .deleteDir(new File(Constants.CECCR_USER_BASE_PATH + j.getUserName()));
                                    logger.error("JOB FAILED REMOVING FOR SURE GUEST: " + j.getUserName());
                                }
                            } else {
                                CentralDogma.getInstance().moveJobToErrorList(j.getId());
                                CentralDogma.getInstance().localJobs.saveJobChangesToList(j);
                                logger.error("", ex);

                                // prepare a nice HTML-formatted readable
                                // version of the exception
                                StringWriter sw = new StringWriter();
                                ex.printStackTrace(new PrintWriter(sw));
                                String exceptionAsString = sw.toString();
                                logger.error(exceptionAsString);
                                exceptionAsString = exceptionAsString.replaceAll("at edu", "<br />at edu");
                                logger.error(exceptionAsString);

                                // send an email to the site administrator
                                User sadUser = userRepository.findByUserName(j.getUserName());
                                String message = "Heya, <br />" + j.getUserName() + "'s job \"" + j.getJobName() +
                                        "\" failed. You might wanna look into that. Their email is " + sadUser
                                        .getEmail() + " and their name is " + sadUser.getFirstName() + " " + sadUser
                                        .getLastName() + " in case you want to give them hope of a brighter tomorrow" +
                                        "." + "<br /><br />Here's the exception it threw: <br />" + ex.toString() +
                                        "<br /><br />Good luck!<br />--Chembench";
                                message += "<br /><br />The full stack trace is below. Happy debugging!<br /><br />"
                                        + exceptionAsString;

                                for (String adminEmailAddress : Constants.ADMINEMAIL_LIST) {
                                    SendEmails.sendEmail(adminEmailAddress, "", "", "Job failed: " + j.getJobName(),
                                            message);
                                }
                            }
                        }
                    } else {
                        // some other thread already got this job. Don't worry
                        // about it.
                    }
                }

            } catch (Exception ex) {
                logger.error("", ex);
            }
        }
    }
}
