package edu.unc.ceccr.chembench.jobs;

import com.google.common.collect.Lists;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.HibernateUtil;
import edu.unc.ceccr.chembench.persistence.Job;
import edu.unc.ceccr.chembench.persistence.User;
import edu.unc.ceccr.chembench.utilities.FileAndDirOperations;
import edu.unc.ceccr.chembench.utilities.PopulateDataObjects;
import edu.unc.ceccr.chembench.utilities.RunExternalProgram;
import edu.unc.ceccr.chembench.utilities.SendEmails;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class LsfProcessingThread extends Thread {
    private static final Logger logger = Logger.getLogger(LsfProcessingThread.class.getName());
    // this works on the LSFJobs joblist.
    // You should only ever have one of these threads running - don't start a
    // second one!

    // used to determine when a job goes from PEND to RUN.
    HashMap<String, String> oldLsfStatuses = new HashMap<String, String>();

    public static boolean lsfHasFreePendSlots() {
        // check how many pending jobs there are
        // if that number is less than the limit return true
        // else return false

        if (CentralDogma.getInstance().lsfJobs.getReadOnlyCopy().size() > Constants.MAXLSFJOBS) {
            return false;
        } else {
            return true;
        }
    }

    // static functions for checking the status of the LSF queue(s) on
    // Emerald.
    public static List<String> getCompletedJobNames() {
        List<String> finishedJobNames = Lists.newArrayList();

        return finishedJobNames;
    }

    public static List<LsfJobStatus> checkLsfStatus(String workingDir) throws Exception {
        // execs "bjobs -aw" and gets the status of each job
        // remove outfile if already exists

        if ((new File(workingDir + "bjobs-out.txt")).exists()) {
            FileAndDirOperations.deleteFile(workingDir + "bjobs-out.txt");
        }

        // run bjobs
        String command = "bjobs.sh";
        RunExternalProgram.runCommand(command, workingDir);

        // read in results
        List<LsfJobStatus> lsfStatusList = Lists.newArrayList();

        BufferedReader br = new BufferedReader(new FileReader(workingDir + "bjobs-out.txt"));
        String line = "";
        br.readLine(); // skip header
        while ((line = br.readLine()) != null) {
            if (!line.trim().equals("")) {
                // non empty line
                LsfJobStatus l = new LsfJobStatus(line);
                lsfStatusList.add(l);
            }
        }

        br.close();

        return lsfStatusList;
    }

    public void run() {

        while (true) {
            try {
                sleep(1500);
                List<Job> readOnlyJobArray = CentralDogma.getInstance().lsfJobs.getReadOnlyCopy();

                // do not call checkLsfStatus more than once in this function
                List<LsfJobStatus> lsfJobStatuses = checkLsfStatus(Constants.CECCR_USER_BASE_PATH);

                // For every finished job, do postprocessing.
                for (LsfJobStatus jobStatus : lsfJobStatuses) {
                    if (jobStatus.stat.equals("DONE") || jobStatus.stat.equals("EXIT")) {
                        // check if this is a running job
                        for (Job j : readOnlyJobArray) {
                            if (j.getLsfJobId() != null && j.getLsfJobId().equals(jobStatus.jobid)) {
                                logger.debug("LSFQueue: trying postprocessing on job: " + j.getJobName() + " from " +
                                        "user: " + j.getUserName());
                                if (CentralDogma.getInstance().lsfJobs.startPostJob(j.getId())) {
                                    try {
                                        logger.debug("Postprocessing job: " + j.getJobName() + " from user: " + j
                                                .getUserName());
                                        j.workflowTask.postProcess();
                                        j.setTimeFinished(new Date());

                                        if (j.getEmailOnCompletion().equalsIgnoreCase("true")) {
                                            SendEmails.sendJobCompletedEmail(j);
                                        }
                                        CentralDogma.getInstance().lsfJobs.saveJobChangesToList(j);

                                        // finished; remove job object
                                        CentralDogma.getInstance().lsfJobs.removeJob(j.getId());
                                        CentralDogma.getInstance().lsfJobs.deleteJobFromDB(j.getId());
                                    } catch (Exception ex) {
                                        // Job failed or threw an exception
                                        logger.warn("JOB FAILED: " + j.getUserName() + " " + j.getJobName());
                                        CentralDogma.getInstance().moveJobToErrorList(j.getId());
                                        CentralDogma.getInstance().lsfJobs.saveJobChangesToList(j);
                                        logger.error("", ex);

                                        // send an email to the site
                                        // administrator
                                        Session s = HibernateUtil.getSession();
                                        User sadUser = PopulateDataObjects.getUserByUserName(j.getUserName(), s);
                                        s.close();

                                        // prepare a nice HTML-formatted
                                        // readable version of the exception
                                        StringWriter sw = new StringWriter();
                                        ex.printStackTrace(new PrintWriter(sw));
                                        String exceptionAsString = sw.toString();
                                        logger.error(exceptionAsString);
                                        exceptionAsString = exceptionAsString.replaceAll("at edu", "<br />at edu");
                                        logger.error(exceptionAsString);

                                        String message = "Heya, <br />" + j.getUserName() + "'s job \"" + j.getJobName()
                                                + "\" failed. You might want" + " to look into that. " +
                                                "Their " + "email is " + sadUser.getEmail() + " and their name is " +
                                                sadUser.getFirstName() + " " + sadUser.getLastName() + " in case you " +
                                                "want to " + "give them hope of a " + "brighter tomorrow." + "<br " +
                                                "/><br />Here's the " + "exception it threw: <br />" + ex.toString()
                                                + "<br /><br />Good " + "luck!<br />--Chembench";

                                        message += "<br /><br />The full " + "stack trace is below. " + "Happy " +
                                                "debugging!<br /><br />" + exceptionAsString;

                                        for (String adminEmailAddress : Constants.ADMINEMAIL_LIST) {
                                            SendEmails.sendEmail(adminEmailAddress, "", "",
                                                    "Job failed: " + j.getJobName(), message);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // did a job just get added to this structure? If so,
                // preprocess it and bsub it.
                // This only preproc's one job at a time, because finishing
                // off completed jobs is a higher priority
                // than starting new ones.
                for (Job j : readOnlyJobArray) {
                    if (j.getStatus().equals(Constants.QUEUED)) {
                        // try to grab the job and preproc it
                        if (CentralDogma.getInstance().lsfJobs.startJob(j.getId())) {

                            try {
                                logger.info(
                                        "LSFQueue: Starting job " + j.getJobName() + " from user " + j.getUserName());

                                boolean jobIsRunningAlready = false;
                                if (j.getLsfJobId() != null && !j.getLsfJobId().isEmpty()) {
                                    // check if the job is already running in
                                    // LSF; try to resume it if so.
                                    // This will happen if the system was
                                    // rebooted while the job was running.
                                    for (LsfJobStatus jobStatus : lsfJobStatuses) {
                                        if (jobStatus.jobid.equals(j.getLsfJobId()) && (jobStatus.stat.equals("PEND")
                                                || jobStatus.stat.equals("RUN") || jobStatus.stat.equals("SSUSP"))) {
                                            // job is already running, so
                                            // don't do anything to it
                                            jobIsRunningAlready = true;
                                            logger.info("LSFQueue: " + j.getJobName() + " was already running " +
                                                    "happily!");
                                            if (j.getJobType().equals(Constants.MODELING)) {
                                                j.workflowTask.setStep(Constants.MODELS);
                                            }
                                        }
                                    }
                                }

                                if (!jobIsRunningAlready) {
                                    // job is not already running; needs to be
                                    // started.
                                    logger.info("LSFQueue: " + j.getJobName() + " was not running already; it " + "is" +
                                            " being preprocessed.");
                                    j.setTimeStarted(new Date());
                                    j.setStatus(Constants.PREPROC);
                                    j.workflowTask.preProcess();
                                    j.setLsfJobId(j.workflowTask.executeLSF());
                                }

                                j.setStatus(Constants.RUNNING);
                                CentralDogma.getInstance().lsfJobs.saveJobChangesToList(j);
                                break;
                            } catch (Exception ex) {
                                // Job failed or threw an exception
                                logger.warn("JOB FAILED: " + j.getUserName() + " " + j.getJobName());
                                CentralDogma.getInstance().moveJobToErrorList(j.getId());
                                CentralDogma.getInstance().lsfJobs.saveJobChangesToList(j);
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
                                        "\" failed. You might want" + " to look into that. Their " + "email is " +
                                        sadUser.getEmail() + " and their name is " + sadUser.getFirstName() + " " +
                                        sadUser.getLastName() + " in case you want to " + "give them hope of a " +
                                        "brighter tomorrow." + "<br /><br />Here's the " + "exception it threw: <br " +
                                        "/>" + ex.toString() + "<br /><br />Good " + "luck!<br />--Chembench";

                                message += "<br /><br />The full " + "stack trace is below. " + "Happy debugging!<br " +
                                        "/><br />" + exceptionAsString;

                                for (String adminEmailAddress : Constants.ADMINEMAIL_LIST) {
                                    SendEmails.sendEmail(adminEmailAddress, "", "", "Job failed: " + j.getJobName(),
                                            message);
                                }
                                break;
                            }
                        }
                    }
                }

                try {
                    // determine if any pending jobs in LSF have started;
                    // update the Job objects if so.
                    // compare the new job statuses against the ones from the
                    // previous check
                    for (LsfJobStatus jobStatus : lsfJobStatuses) {
                        if ((oldLsfStatuses.containsKey(jobStatus.jobid) && oldLsfStatuses.get(jobStatus.jobid)
                                .equals("PEND") && jobStatus.stat.equals("RUN")) || (
                                !oldLsfStatuses.containsKey(jobStatus.jobid) && jobStatus.stat.equals("RUN"))) {
                            // the job *just* started on LSF. Find the job
                            // with this lsfJobId and set its date.
                            for (Job j : readOnlyJobArray) {
                                if (j.getLsfJobId() != null && j.getLsfJobId().equals(jobStatus.jobid)) {
                                    j.setTimeStartedByLsf(new Date());
                                    CentralDogma.getInstance().lsfJobs.saveJobChangesToList(j);
                                }
                            }
                        }
                        oldLsfStatuses.put(jobStatus.jobid, jobStatus.stat);
                    }
                } catch (Exception ex) {
                    logger.error("Error checking job completion.\n" + ex);
                }

            } catch (Exception ex) {
                logger.error("", ex);
            }
        }
    }

}
