package edu.unc.ceccr.chembench.jobs;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.*;
import edu.unc.ceccr.chembench.taskObjects.CreateDatasetTask;
import edu.unc.ceccr.chembench.taskObjects.QsarModelingTask;
import edu.unc.ceccr.chembench.taskObjects.QsarPredictionTask;
import edu.unc.ceccr.chembench.taskObjects.WorkflowTask;
import edu.unc.ceccr.chembench.utilities.FileAndDirOperations;
import edu.unc.ceccr.chembench.utilities.RunExternalProgram;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Configurable(autowire = Autowire.BY_TYPE)
public class CentralDogma {
    // singleton.
    // Holds the LSF jobs list, the incoming jobs list, and the local
    // processing jobs list. Initiates the threads that work on these
    // data structures.

    private static final Logger logger = Logger.getLogger(CentralDogma.class.getName());
    private static CentralDogma instance = new CentralDogma();

    private final int numLocalThreads = 5;  // as many as you want; tune it based on server load.
    // Limiting factors on numLocalThreads: JVM memory size, number of file handles, number of database connections,
    // server processing power. Jobs will fail in weird ways if any of those isn't high enough.

    private final int numLsfThreads = 1;  // don't change this unless you've REALLY thought through all possible
    // concurrency issues
    public SynchronizedJobList incomingJobs;
    public SynchronizedJobList localJobs;
    public SynchronizedJobList lsfJobs;
    public SynchronizedJobList errorJobs;
    private IncomingJobProcessingThread inThread;

    private Set<Thread> threads = Sets.newHashSet();

    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private DatasetRepository datasetRepository;
    @Autowired
    private PredictorRepository predictorRepository;
    @Autowired
    private PredictionRepository predictionRepository;

    private CentralDogma() {
    }

    @PostConstruct
    public void init() {
        try {
            lsfJobs = new SynchronizedJobList(Constants.LSF);
            incomingJobs = new SynchronizedJobList(Constants.INCOMING);
            localJobs = new SynchronizedJobList(Constants.LOCAL);
            errorJobs = new SynchronizedJobList(Constants.ERROR);

            // Fill job lists from the database
            List<Job> jobs = jobRepository.findAll();
            if (jobs == null) {
                jobs = Lists.newArrayList();
            }
            for (Job j : jobs) {
                WorkflowTask wt = null;
                if (j.getLookupId() != null && !j.getJobList().equals("LIMBO")) {
                    try {
                        logger.info("Restoring job: " + j.getJobName());
                        if (j.getJobType().equals(Constants.DATASET)) {
                            Long datasetId = j.getLookupId();
                            Dataset dataset = datasetRepository.findOne(datasetId);
                            wt = new CreateDatasetTask(dataset);
                        } else if (j.getJobType().equals(Constants.MODELING)) {
                            Long modelingId = j.getLookupId();
                            Predictor predictor = predictorRepository.findOne(modelingId);
                            wt = new QsarModelingTask(predictor);
                        } else if (j.getJobType().equals(Constants.PREDICTION)) {
                            Long predictionId = j.getLookupId();
                            Prediction prediction = predictionRepository.findOne(predictionId);
                            wt = new QsarPredictionTask(prediction);
                        }
                        wt.jobList = j.getJobList();
                        j.workflowTask = wt;
                        j.setStatus(Constants.QUEUED);

                        if (j.getJobList().equals(Constants.INCOMING)) {
                            incomingJobs.addJob(j);
                        } else if (j.getJobList().equals(Constants.LOCAL)) {
                            localJobs.addJob(j);
                        } else if (j.getJobList().equals(Constants.LSF)) {
                            lsfJobs.addJob(j);
                        } else if (j.getJobList().equals(Constants.ERROR)) {
                            errorJobs.addJob(j);
                        }
                    } catch (Exception ex) {
                        logger.error("Error restoring job with id: " + j.getLookupId() + "\n", ex);
                    }
                }
            }

            // start job processing threads
            for (int i = 0; i < numLocalThreads; i++) {
                LocalProcessingThread localThread = new LocalProcessingThread();
                localThread.start();
                threads.add(localThread);
            }

            for (int i = 0; i < numLsfThreads; i++) {
                LsfProcessingThread lsfThread = new LsfProcessingThread();
                lsfThread.start();
                threads.add(lsfThread);
            }

            inThread = new IncomingJobProcessingThread();
            inThread.start();
            threads.add(inThread);

        } catch (Exception ex) {
            logger.error("", ex);
        }
    }

    public static synchronized CentralDogma getInstance() {
        if (instance == null) {
            instance = new CentralDogma();
        }
        return instance;
    }

    public void addJobToIncomingList(String userName, String jobName, WorkflowTask wt, int numCompounds, int numModels,
                                     String emailOnCompletion) throws Exception {
        // first, run setUp on the workflowTask
        // this will make sure the workflowTask gets into the DB. Then we can
        // create a job to contain it.
        wt.setUp(); // create dataset, predictor, or prediction object in DB

        Job j = new Job();
        j.setJobName(jobName);
        j.setUserName(userName);
        j.setNumCompounds(numCompounds);
        j.setNumModels(numModels);
        j.setTimeCreated(new Date());
        j.setStatus(Constants.QUEUED);
        j.setJobList(Constants.INCOMING);
        j.setEmailOnCompletion(emailOnCompletion);
        j.setJobType(wt.jobType);
        j.setLookupId(wt.lookupId);
        j.workflowTask = wt;
        jobRepository.save(j);
        // put into incoming queue
        incomingJobs.addJob(j);
    }

    public void cancelJob(Long jobId) throws Exception {
        // Find job's information, then remove the job from any lists it's in.

        logger.info("Deleting job with id: " + jobId);

        Job j = incomingJobs.removeJob(jobId);
        if (j == null) {
            logger.debug("checking lsf queue");
            j = lsfJobs.removeJob(jobId);

            if (j != null) {
                List<LsfJobStatus> lsfJobStatuses = LsfProcessingThread.checkLsfStatus(Constants.CECCR_USER_BASE_PATH);
                for (LsfJobStatus jobStatus : lsfJobStatuses) {
                    if (j.getLsfJobId() != null && j.getLsfJobId().equals(jobStatus.jobid)) {
                        // kill the job
                        String cmd = "bkill " + jobStatus.jobid;
                        RunExternalProgram.runCommand(cmd, Constants.CECCR_USER_BASE_PATH);
                    }
                }
            }
        }
        if (j == null) {
            logger.debug("checking local queue");
            j = localJobs.removeJob(jobId);
        }
        if (j == null) {
            logger.debug("checking local queue");
            j = errorJobs.removeJob(jobId);
        }

        if (j != null) {
            logger.debug("in main delete");
            // delete files associated with the job.
            // Generally this will cause any executables involved in the job
            // to just crash, so we don't worry about them. Crude but
            // effective.

            String baseDir = Constants.CECCR_USER_BASE_PATH;
            String lsfBaseDir = Constants.LSFJOBPATH;

            File file = new File(baseDir + j.getUserName() + "/" + j.getJobName());
            FileAndDirOperations.deleteDir(file);
            file = new File(lsfBaseDir + j.getUserName() + "/" + j.getJobName());
            FileAndDirOperations.deleteDir(file);

            file = new File(baseDir + j.getUserName() + "/DATASETS/" + j.getJobName());
            FileAndDirOperations.deleteDir(file);
            file = new File(lsfBaseDir + j.getUserName() + "/DATASETS/" + j.getJobName());
            FileAndDirOperations.deleteDir(file);

            file = new File(baseDir + j.getUserName() + "/PREDICTORS/" + j.getJobName());
            FileAndDirOperations.deleteDir(file);
            file = new File(lsfBaseDir + j.getUserName() + "/PREDICTORS/" + j.getJobName());
            FileAndDirOperations.deleteDir(file);

            file = new File(baseDir + j.getUserName() + "/PREDICTIONS/" + j.getJobName());
            FileAndDirOperations.deleteDir(file);
            file = new File(lsfBaseDir + j.getUserName() + "/PREDICTIONS/" + j.getJobName());
            FileAndDirOperations.deleteDir(file);

            // delete corresponding workflowTask object (Dataset, Predictor,
            // or Prediction)
            if (j.getJobType().equals(Constants.DATASET)) {
                // delete corresponding Dataset in DB
                Dataset ds = datasetRepository.findOne(j.getLookupId());
                datasetRepository.delete(ds);
            } else if (j.getJobType().equals(Constants.MODELING)) {
                // delete corresponding Predictor in DB
                Predictor p = predictorRepository.findOne(j.getLookupId());
                predictorRepository.delete(p);
            } else if (j.getJobType().equals(Constants.PREDICTION)) {
                Prediction p = predictionRepository.findOne(j.getLookupId());
                predictionRepository.delete(p);
            }
        }

        // doesn't matter which list it was in, this will delete the job's DB
        // entry and make a jobstats entry for it.
        incomingJobs.deleteJobFromDB(jobId);
    }

    public void moveJobToErrorList(Long jobId) {
        Job j = incomingJobs.removeJob(jobId);
        if (j == null) {
            logger.debug("checking lsf queue");
            j = lsfJobs.removeJob(jobId);
        }
        if (j == null) {
            logger.debug("checking local queue");
            j = localJobs.removeJob(jobId);
        }
        if (j != null) {
            j.setJobList(Constants.ERROR);
            errorJobs.addJob(j);
        }
    }

    public Set<Thread> getThreads() {
        return threads;
    }
}

