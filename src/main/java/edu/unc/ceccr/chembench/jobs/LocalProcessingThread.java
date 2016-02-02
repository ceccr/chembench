package edu.unc.ceccr.chembench.jobs;

import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.HibernateUtil;
import edu.unc.ceccr.chembench.persistence.Job;
import edu.unc.ceccr.chembench.persistence.User;
import edu.unc.ceccr.chembench.utilities.FileAndDirOperations;
import edu.unc.ceccr.chembench.utilities.PopulateDataObjects;
import edu.unc.ceccr.chembench.utilities.SendEmails;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;

public class LocalProcessingThread extends Thread {

    private static Logger logger = Logger.getLogger(LocalProcessingThread.class.getName());

    // this thread will work on the localJobs joblist.
    // There can be any number of these threads.

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
                                Session session = HibernateUtil.getSession();
                                // removing all quest data if guest time on
                                // site was out
                                if (!j.getUserName().isEmpty()
                                        && PopulateDataObjects.getUserByUserName(j.getUserName(), session) == null) {
                                    FileAndDirOperations
                                            .deleteDir(new File(Constants.CECCR_USER_BASE_PATH + j.getUserName()));
                                }
                                session.close();
                            }
                            // finished; remove job object
                            CentralDogma.getInstance().localJobs.removeJob(j.getId());
                            CentralDogma.getInstance().localJobs.deleteJobFromDB(j.getId());
                        } catch (Exception ex) {
                            // Job failed or threw an exception
                            logger.error("JOB FAILED: " + j.getUserName() + " " + j.getJobName());
                            if (j.getUserName().contains("guest")) {
                                Session session = HibernateUtil.getSession();
                                // removing all quest data if guest time on
                                // site was out
                                logger.error("JOB FAILED REMOVING GUEST: " + j.getUserName() + " " +
                                        PopulateDataObjects.getUserByUserName(j.getUserName(), session));
                                if (!j.getUserName().isEmpty()
                                        && PopulateDataObjects.getUserByUserName(j.getUserName(), session) == null) {
                                    FileAndDirOperations
                                            .deleteDir(new File(Constants.CECCR_USER_BASE_PATH + j.getUserName()));
                                    logger.error("JOB FAILED REMOVING FOR SURE GUEST: " + j.getUserName());
                                }
                                session.close();
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
                                Session s = HibernateUtil.getSession();
                                User sadUser = PopulateDataObjects.getUserByUserName(j.getUserName(), s);
                                s.close();

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
