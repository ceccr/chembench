package edu.unc.ceccr.chembench.jobs;

import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.Job;
import edu.unc.ceccr.chembench.persistence.User;
import edu.unc.ceccr.chembench.persistence.UserRepository;
import edu.unc.ceccr.chembench.utilities.FileAndDirOperations;
import edu.unc.ceccr.chembench.utilities.RunExternalProgram;
import edu.unc.ceccr.chembench.utilities.SendEmails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.io.*;
import java.util.*;

@Configurable(autowire = Autowire.BY_TYPE)
public class LsfProcessingThread extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(LsfProcessingThread.class);
    // this works on the LSFJobs joblist.
    // You should only ever have one of these threads running - don't start a
    // second one!

    // used to determine when a job goes from PEND to RUN.
    HashMap<String, String> oldLsfStatuses = new HashMap<String, String>();

    // used to determin when a job goes from RUNNING to COMPLETED
    Set<String> checkForCompletion = new HashSet<>();

    @Autowired
    private UserRepository userRepository;

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
        List<String> finishedJobNames = new ArrayList<>();

        return finishedJobNames;
    }

    public static List<LsfJobStatus> checkLsfStatus(String workingDir) throws Exception {
        // execs "squeue -u " and gets the status of each job
        // remove outfile if already exists

        if ((new File(workingDir + "sbatch-out.txt")).exists()) {
            FileAndDirOperations.deleteFile(workingDir + "sbatch-out.txt");
        }

        // run sbatch.sh
        String command = "sbatch.sh";
        RunExternalProgram.runCommand(command, workingDir);

        // read in results
        List<LsfJobStatus> lsfStatusList = new ArrayList<>();

        if ((new File(workingDir + "sbatch-out.txt")).exists()) {
            BufferedReader br = new BufferedReader(new FileReader(workingDir + "sbatch-out.txt"));
            String line = "";
            br.readLine(); // skip date
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                if (!line.trim().equals("")) {
                    // non empty line
                    LsfJobStatus l = new LsfJobStatus(line);
                    lsfStatusList.add(l);
                }
            }
            br.close();
        }

        return lsfStatusList;
    }

    public static List<String> checkFinished(Set<String> difference, String workingDir) throws Exception {
        List<String> finishedList = new ArrayList<>();
        String finishedFile = workingDir + "checkFinished.txt";

        if (!difference.isEmpty()) {
            for (String jobId : difference) {
                // remove outfile if already exists
                if ((new File(finishedFile)).exists()) {
                    FileAndDirOperations.deleteFile(finishedFile);
                }

                String cmd = "sacct -j " + jobId + " --format=JobID,State > " + finishedFile;
                RunExternalProgram.runCommandAndLogOutputLSF(cmd, workingDir);

                //JobID      State
                //------------ ---------
                //8687603       COMPLETED
                //8687603.bat+  COMPLETED

                if ((new File(finishedFile)).exists()) {
                    BufferedReader br = new BufferedReader(new FileReader(finishedFile));
                    String line = "";
                    br.readLine(); // skip JobID      State
                    br.readLine(); // skip ------------ ---------
                    while ((line = br.readLine()) != null) {
                        Scanner sc = new Scanner(line);
                        if (sc.next().equals(jobId)) {
                            String state = sc.next();
                            if (state.equals("COMPLETED") || state.equals("FAILED")) {
                                finishedList.add(jobId);
                            }
                        }
                    }
                    br.close();
                }
            }
        }

        return finishedList;
    }

    public void run() {
        while (!interrupted()) {
            try {
                sleep(1500);
                List<Job> readOnlyJobArray = CentralDogma.getInstance().lsfJobs.getReadOnlyCopy();

                // do not call checkLsfStatus more than once in this function
                List<LsfJobStatus> lsfJobStatuses = new ArrayList<>();
                try {
                    lsfJobStatuses = checkLsfStatus(Constants.CECCR_USER_BASE_PATH);
                } catch (Exception e) {
                    logger.error("Error checking lsf status", e);
                }

                if (lsfJobStatuses.size() > 1) {
                    logger.debug("current" + lsfJobStatuses.size());
                }
                
                Set<String> currentJobs = new HashSet<>();
                for (LsfJobStatus jobStatus : lsfJobStatuses) {
                    currentJobs.add(jobStatus.jobid);
                    checkForCompletion.add(jobStatus.jobid);
                }

                //elements in currentJobs are in checkForCompletion
                //check for the ones that are in checkForCompletion but not currentJobs
                //these jobs are either completed or failed
                Set<String> difference = new HashSet<>();
                difference.addAll(checkForCompletion);
                difference.removeAll(currentJobs);

                if (difference.size() > 1) {
                    logger.debug("difference occured" + difference.size());
                }

                List<String> finishedJobId = new ArrayList<>();
                try {
                    finishedJobId = checkFinished(difference, Constants.CECCR_BASE_PATH);
                } catch (Exception e) {
                    logger.error("Error checking finished job status", e);
                }
                if (finishedJobId.size() > 1) {
                    logger.debug("finished check" + finishedJobId.size());
                }
                if (!finishedJobId.isEmpty()) {
                    checkForCompletion.removeAll(difference);
                    // For every finished job, do postprocessing.
                    for (String jobId : finishedJobId) {
                        // check if this is a running job
                        for (Job j : readOnlyJobArray) {
                            if (j.getLsfJobId() != null && j.getLsfJobId().equals(jobId)) {
                                logger.debug("LSFQueue: trying postprocessing on job: " + j.getJobName() + " from "
                                        + "user: " + j.getUserName());
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
                                        User sadUser = userRepository.findByUserName(j.getUserName());

                                        // prepare a nice HTML-formatted
                                        // readable version of the exception
                                        StringWriter sw = new StringWriter();
                                        ex.printStackTrace(new PrintWriter(sw));
                                        String exceptionAsString = sw.toString();
                                        logger.error(exceptionAsString);
                                        exceptionAsString = exceptionAsString.replaceAll("at edu", "<br />at edu");
                                        logger.error(exceptionAsString);

                                        String message = "Heya, <br />" + j.getUserName() + "'s job \"" + j.getJobName()
                                                + "\" failed. You might want" + " to look into that. " + "Their "
                                                + "email is " + sadUser.getEmail() + " and their name is " + sadUser
                                                .getFirstName() + " " + sadUser.getLastName() + " in case you "
                                                + "want to " + "give them hope of a " + "brighter tomorrow." + "<br "
                                                + "/><br />Here's the " + "exception it threw: <br />" + ex.toString()
                                                + "<br /><br />Good " + "luck!<br />--Chembench";

                                        message += "<br /><br />The full " + "stack trace is below. " + "Happy "
                                                + "debugging!<br /><br />" + exceptionAsString;

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
                                        if (jobStatus.jobid.equals(j.getLsfJobId()) && (jobStatus.stat.equals("PENDING")
                                                || jobStatus.stat.equals("RUNNING") || jobStatus.stat
                                                .equals("SUSPENDED"))) {
                                            // job is already running, so
                                            // don't do anything to it
                                            jobIsRunningAlready = true;
                                            logger.info("LSFQueue: " + j.getJobName() + " was already running "
                                                    + "happily!");
                                            if (j.getJobType().equals(Constants.MODELING)) {
                                                j.workflowTask.setStep(Constants.MODELS);
                                            }
                                        }
                                    }
                                }

                                if (!jobIsRunningAlready) {
                                    // job is not already running; needs to be
                                    // started.
                                    logger.info("LSFQueue: " + j.getJobName() + " was not running already; it " + "is"
                                            + " being preprocessed.");
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
                                User sadUser = userRepository.findByUserName(j.getUserName());
                                String message = "Heya, <br />" + j.getUserName() + "'s job \"" + j.getJobName()
                                        + "\" failed. You might want" + " to look into that. Their " + "email is "
                                        + sadUser.getEmail() + " and their name is " + sadUser.getFirstName() + " "
                                        + sadUser.getLastName() + " in case you want to " + "give them hope of a "
                                        + "brighter tomorrow." + "<br /><br />Here's the " + "exception it threw: <br "
                                        + "/>" + ex.toString() + "<br /><br />Good " + "luck!<br />--Chembench";

                                message += "<br /><br />The full " + "stack trace is below. " + "Happy debugging!<br "
                                        + "/><br />" + exceptionAsString;

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
                                .equals("PENDING") && jobStatus.stat.equals("RUNNING")) || (
                                !oldLsfStatuses.containsKey(jobStatus.jobid) && jobStatus.stat.equals("RUNNING"))) {
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

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

}
