package edu.unc.ceccr.chembench.taskObjects;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import edu.unc.ceccr.chembench.actions.ModelingFormActions;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.*;
import edu.unc.ceccr.chembench.utilities.*;
import edu.unc.ceccr.chembench.workflows.calculations.RSquaredAndCCR;
import edu.unc.ceccr.chembench.workflows.datasets.DatasetFileOperations;
import edu.unc.ceccr.chembench.workflows.descriptors.ReadDescriptors;
import edu.unc.ceccr.chembench.workflows.descriptors.WriteDescriptors;
import edu.unc.ceccr.chembench.workflows.modelingPrediction.*;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
// logs being written to ../logs/chembench-jobs.mm-dd-yyyy.log

public class QsarModelingTask extends WorkflowTask {
    private static final Logger logger = Logger.getLogger(QsarModelingTask.class.getName());
    // predicted external set values
    List<ExternalValidation> externalSetPredictions = Lists.newArrayList();
    Path baseDir;
    Path yRandomDir;
    // job details
    private String sdFileName;
    private String actFileName;
    private String userName;
    private String jobName;
    private String modelType;
    // (svm, knn, etc) dataset
    private String datasetName;
    private Long datasetID;
    private String filePath;
    private String datasetPath;
    private String actFileDataType;
    private Dataset dataset;
    private String categoryWeights;
    // descriptors
    private String descriptorGenerationType;
    private String scalingType;
    private String stdDevCutoff;
    private String correlationCutoff;
    private String uploadedDescriptorType;
    // datasplit
    private String numSplits;
    private String trainTestSplitType;
    // if random split
    private String randomSplitMinTestSize;
    private String randomSplitMaxTestSize;
    private String randomSplitSampleWithReplacement;
    // if sphere exclusion
    private String splitIncludesMin;
    private String splitIncludesMax;
    private String sphereSplitMinTestSize;
    private String selectionNextTrainPt;
    // sets of input parameters
    private KnnParameters knnParameters;
    private SvmParameters svmParameters;
    private RandomForestParameters randomForestParameters;
    private KnnPlusParameters knnPlusParameters;
    // predictor object created during task
    private Predictor predictor;
    private int numExternalCompounds = 0;
    private String step = Constants.SETUP;

    @Autowired
    private RandomForestParametersRepository randomForestParametersRepository;
    @Autowired
    private SvmParametersRepository svmParametersRepository;
    @Autowired
    private KnnParametersRepository knnParametersRepository;
    @Autowired
    private KnnPlusParametersRepository knnPlusParametersRepository;

    public QsarModelingTask(Predictor predictor) throws Exception {
        logger.info("Recovering job, " + jobName + " from predictor: " + predictor.getName() + " submitted by user, "
                + userName + ".");
        this.predictor = predictor;

        // get dataset
        datasetID = predictor.getDatasetId();
        Session s = HibernateUtil.getSession();
        dataset = PopulateDataObjects.getDataSetById(datasetID, s);
        categoryWeights = predictor.getCategoryWeights();
        datasetName = dataset.getName();
        sdFileName = dataset.getSdfFile();
        actFileName = dataset.getActFile();
        actFileDataType = dataset.getModelType();
        datasetPath += dataset.getUserName();
        datasetPath += "/DATASETS/" + datasetName + "/";

        userName = predictor.getUserName();
        jobName = predictor.getName();
        filePath = Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/";

        modelType = predictor.getModelMethod();

        // descriptors
        descriptorGenerationType = predictor.getDescriptorGeneration();
        scalingType = predictor.getScalingType();
        stdDevCutoff = predictor.getStdDevCutoff();
        correlationCutoff = predictor.getCorrelationCutoff();
        uploadedDescriptorType = predictor.getUploadedDescriptorType();

        // datasplit
        numSplits = predictor.getNumSplits();
        trainTestSplitType = predictor.getTrainTestSplitType();

        // if random split
        randomSplitMinTestSize = predictor.getRandomSplitMinTestSize();
        randomSplitMaxTestSize = predictor.getRandomSplitMaxTestSize();
        randomSplitSampleWithReplacement = predictor.getRandomSplitSampleWithReplacement();

        // if sphere exclusion
        splitIncludesMin = predictor.getSplitIncludesMin();
        splitIncludesMax = predictor.getSplitIncludesMax();
        sphereSplitMinTestSize = predictor.getSphereSplitMinTestSize();
        selectionNextTrainPt = predictor.getSelectionNextTrainPt();

        if ((new File(filePath + "ext_0.x")).exists()) {
            List<String> extCompoundArray = DatasetFileOperations.getXCompoundNames(filePath + "ext_0.x");
            numExternalCompounds = extCompoundArray.size();
            logger.info("Recovering: numExternalCompounds set to " + numExternalCompounds + " by user, " + userName
                    + " in job, " + jobName + ".");
        } else {
            logger.info(
                    "Recovering: could not find " + filePath + "ext_0.x . numExternalCompounds set to 0." + " For job, "
                            + jobName + " submitted by user, " + userName + ".");
            numExternalCompounds = 0;
        }

        // modeling params
        if (predictor.getModelMethod().equals(Constants.KNN)) {
            knnParameters = knnParametersRepository.findOne(predictor.getModelingParametersId());
        } else if (predictor.getModelMethod().equals(Constants.SVM)) {
            svmParameters = svmParametersRepository.findOne(predictor.getModelingParametersId());
        } else if (predictor.getModelMethod().equals(Constants.KNNSA) || predictor.getModelMethod()
                .equals(Constants.KNNGA)) {
            knnPlusParameters = knnPlusParametersRepository.findOne(predictor.getModelingParametersId());
        } else if (predictor.getModelMethod().equals(Constants.RANDOMFOREST)) {
            randomForestParameters = randomForestParametersRepository.findOne(predictor.getModelingParametersId());
        }
        s.close();

        baseDir = Paths.get(Constants.CECCR_USER_BASE_PATH, userName, jobName);
    }

    public QsarModelingTask(String userName, ModelingFormActions ModelingForm) throws Exception {

        // This function just loads all the ModelingForm parameters into local
        // variables
        logger.info(
                "Modeling Type: " + ModelingForm.getModelingType() + " submitted by user, " + userName + " for job, "
                        + ModelingForm.getJobName() + ".");
        modelType = ModelingForm.getModelingType();
        scalingType = ModelingForm.getScalingType();
        logger.info("scalingType in QsarModelingTask: " + scalingType);

        stdDevCutoff = ModelingForm.getStdDevCutoff();
        correlationCutoff = ModelingForm.getCorrelationCutoff();

        Session session = HibernateUtil.getSession();
        dataset = PopulateDataObjects.getDataSetById(ModelingForm.getSelectedDatasetId(), session);
        session.close();

        this.userName = userName;
        jobName = ModelingForm.getJobName();
        actFileName = dataset.getActFile();
        sdFileName = dataset.getSdfFile();
        datasetName = dataset.getName();
        datasetID = ModelingForm.getSelectedDatasetId();

        categoryWeights = ModelingForm.getCategoryWeights();
        actFileDataType = ModelingForm.getActFileDataType();
        descriptorGenerationType = ModelingForm.getDescriptorGenerationType();
        uploadedDescriptorType = dataset.getUploadedDescriptorType();

        // start datasplit parameters
        selectionNextTrainPt = ModelingForm.getSelectionNextTrainPt();

        trainTestSplitType = ModelingForm.getTrainTestSplitType();
        if (trainTestSplitType.equalsIgnoreCase(Constants.RANDOM)) {
            // random datasplit params
            numSplits = ModelingForm.getNumSplitsInternalRandom();
            randomSplitMinTestSize = ModelingForm.getRandomSplitMinTestSize();
            randomSplitMaxTestSize = ModelingForm.getRandomSplitMaxTestSize();
            randomSplitSampleWithReplacement = ModelingForm.getRandomSplitSampleWithReplacement();
        } else if (trainTestSplitType.equalsIgnoreCase(Constants.SPHEREEXCLUSION)) {
            // sphere exclusion datasplit params
            numSplits = ModelingForm.getNumSplitsInternalSphere();
            splitIncludesMin = ModelingForm.getSplitIncludesMin();
            splitIncludesMax = ModelingForm.getSplitIncludesMax();
            sphereSplitMinTestSize = ModelingForm.getSphereSplitMinTestSize();
            selectionNextTrainPt = ModelingForm.getSelectionNextTrainPt();
        }

        // end datasplit parameters

        // load modeling parameters from form
        if (ModelingForm.getModelingType().equals(Constants.KNN)) {
            knnParameters = new KnnParameters();

            knnParameters.setT1(ModelingForm.getT1());
            knnParameters.setT2(ModelingForm.getT2());
            knnParameters.setTcOverTb(ModelingForm.getTcOverTb());
            knnParameters.setMinSlopes(ModelingForm.getMinSlopes());
            knnParameters.setMaxSlopes(ModelingForm.getMaxSlopes());
            knnParameters.setRelativeDiffRR0(ModelingForm.getRelativeDiffRR0());
            knnParameters.setDiffR01R02(ModelingForm.getDiffR01R02());
            knnParameters.setKnnCategoryOptimization(ModelingForm.getKnnCategoryOptimization());
            knnParameters.setMinNumDescriptors(ModelingForm.getMinNumDescriptors());
            knnParameters.setMaxNumDescriptors(ModelingForm.getMaxNumDescriptors());
            knnParameters.setStepSize(ModelingForm.getStepSize());
            knnParameters.setNumCycles(ModelingForm.getNumCycles());
            knnParameters.setNumMutations(ModelingForm.getNumMutations());
            knnParameters.setMinAccTraining(ModelingForm.getMinAccTraining());
            knnParameters.setMinAccTest(ModelingForm.getMinAccTest());
            knnParameters.setCutoff(ModelingForm.getCutoff());
            knnParameters.setMu(ModelingForm.getMu());
            knnParameters.setNumRuns(ModelingForm.getNumRuns());
            knnParameters.setNearestNeighbors(ModelingForm.getNearest_Neighbors());
            knnParameters.setPseudoNeighbors(ModelingForm.getPseudo_Neighbors());
            knnParameters.setStopCond(ModelingForm.getStop_cond());
        } else if (ModelingForm.getModelingType().equals(Constants.SVM)) {
            svmParameters = new SvmParameters();
            svmParameters.setSvmDegreeFrom(ModelingForm.getSvmDegreeFrom());
            svmParameters.setSvmDegreeTo(ModelingForm.getSvmDegreeTo());
            svmParameters.setSvmDegreeStep(ModelingForm.getSvmDegreeStep());
            svmParameters.setSvmGammaFrom(ModelingForm.getSvmGammaFrom());
            svmParameters.setSvmGammaTo(ModelingForm.getSvmGammaTo());
            svmParameters.setSvmGammaStep(ModelingForm.getSvmGammaStep());
            svmParameters.setSvmCostFrom(ModelingForm.getSvmCostFrom());
            svmParameters.setSvmCostTo(ModelingForm.getSvmCostTo());
            svmParameters.setSvmCostStep(ModelingForm.getSvmCostStep());
            svmParameters.setSvmNuFrom(ModelingForm.getSvmNuFrom());
            svmParameters.setSvmNuTo(ModelingForm.getSvmNuTo());
            svmParameters.setSvmNuStep(ModelingForm.getSvmNuStep());
            svmParameters.setSvmPEpsilonFrom(ModelingForm.getSvmPEpsilonFrom());
            svmParameters.setSvmPEpsilonTo(ModelingForm.getSvmPEpsilonTo());
            svmParameters.setSvmPEpsilonStep(ModelingForm.getSvmPEpsilonStep());
            svmParameters.setSvmCrossValidation(ModelingForm.getSvmCrossValidation());
            svmParameters.setSvmEEpsilon(ModelingForm.getSvmEEpsilon());
            svmParameters.setSvmHeuristics(ModelingForm.getSvmHeuristics());
            svmParameters.setSvmKernel(ModelingForm.getSvmKernel());
            svmParameters.setSvmProbability(ModelingForm.getSvmProbability());
            svmParameters.setSvmTypeCategory(ModelingForm.getSvmTypeCategory());
            svmParameters.setSvmTypeContinuous(ModelingForm.getSvmTypeContinuous());
            svmParameters.setSvmWeight(ModelingForm.getSvmWeight());
            svmParameters.setSvmCutoff(ModelingForm.getSvmCutoff());
        } else if (ModelingForm.getModelingType().equals(Constants.KNNSA) || ModelingForm.getModelingType()
                .equals(Constants.KNNGA)) {

            knnPlusParameters = new KnnPlusParameters();
            knnPlusParameters.setGaMaxNumGenerations(ModelingForm.getGaMaxNumGenerations());
            knnPlusParameters.setGaMinFitnessDifference(ModelingForm.getGaMinFitnessDifference());
            knnPlusParameters.setGaNumStableGenerations(ModelingForm.getGaNumStableGenerations());
            knnPlusParameters.setGaPopulationSize(ModelingForm.getGaPopulationSize());
            knnPlusParameters.setGaTournamentGroupSize(ModelingForm.getGaTournamentGroupSize());
            knnPlusParameters.setKnnApplicabilityDomain(ModelingForm.getKnnApplicabilityDomain());
            knnPlusParameters.setKnnDescriptorStepSize(ModelingForm.getKnnDescriptorStepSize());
            knnPlusParameters.setKnnSaErrorBasedFit(ModelingForm.getKnnSaErrorBasedFit());
            knnPlusParameters.setKnnGaErrorBasedFit(ModelingForm.getKnnGaErrorBasedFit());
            knnPlusParameters.setKnnMaxNearestNeighbors(ModelingForm.getKnnMaxNearestNeighbors());
            knnPlusParameters.setKnnMinNearestNeighbors(ModelingForm.getKnnMinNearestNeighbors());
            knnPlusParameters.setKnnMaxNumDescriptors(ModelingForm.getKnnMaxNumDescriptors());
            knnPlusParameters.setKnnMinNumDescriptors(ModelingForm.getKnnMinNumDescriptors());
            knnPlusParameters.setKnnMinTest(ModelingForm.getKnnMinTest());
            knnPlusParameters.setKnnMinTraining(ModelingForm.getKnnMinTraining());
            knnPlusParameters.setSaFinalTemp(ModelingForm.getSaFinalTemp());
            knnPlusParameters.setSaLogInitialTemp(ModelingForm.getSaLogInitialTemp());
            knnPlusParameters
                    .setSaMutationProbabilityPerDescriptor(ModelingForm.getSaMutationProbabilityPerDescriptor());
            knnPlusParameters.setSaNumBestModels(ModelingForm.getSaNumBestModels());
            knnPlusParameters.setSaNumRuns(ModelingForm.getSaNumRuns());
            knnPlusParameters.setSaTempConvergence(ModelingForm.getSaTempConvergence());
            knnPlusParameters.setSaTempDecreaseCoefficient(ModelingForm.getSaTempDecreaseCoefficient());
        } else if (ModelingForm.getModelingType().equals(Constants.RANDOMFOREST)) {
            randomForestParameters = new RandomForestParameters();
            randomForestParameters.setNumTrees(ModelingForm.getNumTrees());
            randomForestParameters.setSeed(ModelingForm.getSeed());
        }

        // end load modeling parameters from form

        this.predictor = new Predictor();

        filePath = Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/";
        datasetPath = Constants.CECCR_USER_BASE_PATH;
        datasetPath += dataset.getUserName();
        datasetPath += "/DATASETS/" + datasetName + "/";

        baseDir = Paths.get(Constants.CECCR_USER_BASE_PATH, userName, jobName);
    }

    // stores what step we're on
    public String getProgress(String user) {
        try {
            String percent = "";
            if (step.equals(Constants.MODELS)) {
                String workingDir = "";
                if (jobList.equals(Constants.LSF)) {
                    // running on LSF so check LSF dir
                    workingDir = Constants.LSFJOBPATH + userName + "/" + jobName + "/";
                } else {
                    // running locally so check local dir
                    workingDir = Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/";
                }

                if (modelType.equals(Constants.KNNSA)) {
                    // number of models produced so far can be gotten by:
                    // cat knn+_models.log | grep q2= | wc

                    float p = KnnPlus.getSaModelingProgress(workingDir);
                    p += KnnPlus.getSaModelingProgress(workingDir + "yRandom/");
                    p /= (getNumTotalModels() * 2);
                    p *= 100; // it's a percent
                    if (p > 100) {
                        p = 100;
                    }
                    percent = " (" + Math.round(p) + "%)";
                } else if (modelType.equals(Constants.KNNGA)) {
                    percent = "";
                    float p = KnnPlus.getGaModelingProgress(workingDir);
                    p += KnnPlus.getGaModelingProgress(workingDir + "yRandom/");
                    p /= (getNumTotalModels() * 2);
                    p *= 100; // it's a percent
                    if (p < 0) {
                        p = 0;
                    }
                    if (p > 100) {
                        p = 100;
                    }
                    percent = " (" + Math.round(p) + "%)";
                } else if (modelType.equals(Constants.RANDOMFOREST)) {
                    double ratio = RandomForest.getProgress(baseDir);
                    int p = (int) (ratio * 100);
                    percent = String.format(" (%d%%)", p);
                } else if (modelType.equals(Constants.SVM)) {
                    // get num of models produced so far
                    float p = 0;
                    if (new File(workingDir + "svm-results.txt").exists()) {
                        p += FileAndDirOperations.getNumLinesInFile(workingDir + "svm-results.txt");
                    }
                    if (new File(workingDir + "yRandom/svm-results.txt").exists()) {
                        p += FileAndDirOperations.getNumLinesInFile(workingDir + "yRandom/svm-results.txt");
                    }
                    // divide by (number of models * 2 because of yRandom)
                    p /= (getNumTotalModels() * 2);
                    p *= 100;
                    if (p > 100) {
                        p = 100;
                    }
                    percent = " (" + Math.round(p) + "%)";
                }

            }
            return step + percent;
        } catch (Exception ex) {
            // checking progress is non essential, it shouldn't be able to
            // throw exceptions or anything.
            return step;
        }
    }

    public Long setUp() throws Exception {

        // create Predictor object in DB to allow for recovery of this job if
        // it fails.

        predictor.setName(jobName);
        predictor.setUserName(userName);
        predictor.setJobCompleted(Constants.NO);

        predictor.setDatasetId(datasetID);
        predictor.setSdFileName(dataset.getSdfFile());
        predictor.setActFileName(dataset.getActFile());
        predictor.setActivityType(actFileDataType);
        predictor.setCategoryWeights(categoryWeights);
        predictor.setModelMethod(modelType);

        // descriptors
        predictor.setDescriptorGeneration(descriptorGenerationType);
        predictor.setScalingType(scalingType);
        predictor.setStdDevCutoff(stdDevCutoff);
        predictor.setCorrelationCutoff(correlationCutoff);
        predictor.setUploadedDescriptorType(uploadedDescriptorType);

        // datasplit
        predictor.setNumSplits(numSplits);
        predictor.setTrainTestSplitType(trainTestSplitType);

        // if random split
        predictor.setRandomSplitMinTestSize(randomSplitMinTestSize);
        predictor.setRandomSplitMaxTestSize(randomSplitMaxTestSize);
        predictor.setRandomSplitSampleWithReplacement(randomSplitSampleWithReplacement);

        // if sphere exclusion
        predictor.setSplitIncludesMin(splitIncludesMin);
        predictor.setSplitIncludesMax(splitIncludesMax);
        predictor.setSphereSplitMinTestSize(sphereSplitMinTestSize);
        predictor.setSelectionNextTrainPt(selectionNextTrainPt);

        // save modeling params to database
        Session session = HibernateUtil.getSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            if (knnParameters != null) {
                session.saveOrUpdate(knnParameters);
            }
            if (svmParameters != null) {
                session.saveOrUpdate(svmParameters);
            }
            if (knnPlusParameters != null) {
                session.saveOrUpdate(knnPlusParameters);
            }
            if (randomForestParameters != null) {
                session.saveOrUpdate(randomForestParameters);
            }

            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) {
                tx.rollback();
            }
            logger.error("Runtime Exception encountered for job, " +
                    jobName + " submitted by user, " + userName + ".\n" +
                    e.toString());
        }

        // set modeling params id in predictor
        if (modelType.equals(Constants.SVM)) {
            predictor.setModelingParametersId(svmParameters.getId());
        } else if (modelType.equals(Constants.KNNGA) || modelType.equals(Constants.KNNSA)) {
            predictor.setModelingParametersId(knnPlusParameters.getId());
        } else if (modelType.equals(Constants.RANDOMFOREST)) {
            predictor.setModelingParametersId(randomForestParameters.getId());
        }

        // save predictor to DB
        try {
            tx = session.beginTransaction();
            session.saveOrUpdate(predictor);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) {
                tx.rollback();
            }
            logger.error("Runtime Exception encountered for job, " +
                    jobName + " submitted by user, " + userName + ".\n" +
                    e.toString());
        } finally {
            session.close();
        }

        lookupId = predictor.getId();
        jobType = Constants.MODELING;

        // make sure job dir exists and is empty
        CreateJobDirectories.createDirs(userName, jobName);
        FileAndDirOperations.deleteDirContents(Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/");

        return lookupId;
    }

    public void preProcess() throws Exception {
        logger.info("Beginning pre-processing for " + jobName);
        // copy the dataset files to the working directory
        step = Constants.SETUP;

        CopyJobFiles.getDatasetFiles(userName, dataset, Constants.MODELING, filePath);

        // read in the descriptors for the dataset
        List<String> descriptorNames = Lists.newArrayList();
        List<Descriptors> descriptorValueMatrix = Lists.newArrayList();
        List<String> chemicalNames = DatasetFileOperations.getACTCompoundNames(filePath + actFileName);

        Session session = HibernateUtil.getSession();
        Dataset dataset = PopulateDataObjects.getDataSetById(datasetID, session);
        session.close();

        String xFileName = "";

        // read in descriptors from the dataset
        step = Constants.PROCDESCRIPTORS;
        if (descriptorGenerationType.equals(Constants.MOLCONNZ)) {
            logger.debug("Converting MolconnZ output to .x format and reading " + "for job, " + jobName
                    + " submitted by user, " +
                    userName + ".");
            ReadDescriptors.readMolconnZDescriptors(filePath + sdFileName + ".molconnz", descriptorNames,
                    descriptorValueMatrix);

            // ReadDescriptorsFileWorkflow.convertMzToX(filePath + sdFileName
            // + ".mz", filePath);
            // ReadDescriptorsFileWorkflow.readXDescriptors(filePath +
            // sdFileName + ".mz.x", descriptorNames, descriptorValueMatrix);
        } else if (descriptorGenerationType.equals(Constants.CDK)) {
            logger.debug("Processing CDK descriptors for job, " + jobName + " submitted by user, " + userName);

            ReadDescriptors.convertCDKToX(filePath + sdFileName + ".cdk", filePath);
            ReadDescriptors.readXDescriptors(filePath + sdFileName + ".cdk.x", descriptorNames, descriptorValueMatrix);

            // for CDK descriptors, compounds with errors are skipped.
            // Make sure that any skipped compounds are removed from the list
            // of external compounds
            DatasetFileOperations.removeSkippedCompoundsFromExternalSetList(sdFileName + ".cdk.x", filePath, "ext_0.x");
            DatasetFileOperations.removeSkippedCompoundsFromActFile(sdFileName + ".cdk.x", filePath, actFileName);
            chemicalNames = DatasetFileOperations.getACTCompoundNames(filePath + actFileName);
        } else if (descriptorGenerationType.equals(Constants.DRAGONH)) {
            logger.debug("Processing DragonH descriptors for job, " + jobName + "submitted by user, " + userName);
            ReadDescriptors
                    .readDragonDescriptors(filePath + sdFileName + ".dragonH", descriptorNames, descriptorValueMatrix);
        } else if (descriptorGenerationType.equals(Constants.DRAGONNOH)) {
            logger.debug("Processing DragonNoH descriptors for job, " + jobName + "submitted by user, " + userName);
            ReadDescriptors.readDragonDescriptors(filePath + sdFileName + ".dragonNoH", descriptorNames,
                    descriptorValueMatrix);
        } else if (descriptorGenerationType.equals(Constants.MOE2D)) {
            logger.debug("Processing MOE2D descriptors for job, " + jobName + "submitted by user, " + userName);
            ReadDescriptors
                    .readMoe2DDescriptors(filePath + sdFileName + ".moe2D", descriptorNames, descriptorValueMatrix);
        } else if (descriptorGenerationType.equals(Constants.MACCS)) {
            logger.debug("Processing MACCS descriptors for job, " + jobName + "submitted by user, " + userName);
            ReadDescriptors
                    .readMaccsDescriptors(filePath + sdFileName + ".maccs", descriptorNames, descriptorValueMatrix);
        } else if (descriptorGenerationType.equals(Constants.ISIDA)) {
            logger.debug("Processing ISIDA descriptors for job, " + jobName + "submitted by user, " + userName);
            ReadDescriptors
                    .readISIDADescriptors(filePath + sdFileName + ".ISIDA", descriptorNames, descriptorValueMatrix);
        } else if (descriptorGenerationType.equals(Constants.UPLOADED)) {
            logger.debug("Processing UPLOADED descriptors for job, " + jobName + "submitted by user, " + userName);
            ReadDescriptors.readXDescriptors(filePath + dataset.getXFile(), descriptorNames, descriptorValueMatrix);
        }

        // write out the descriptors into a .x file for modeling
        if (descriptorGenerationType.equals(Constants.UPLOADED)) {
            xFileName = dataset.getXFile();
        } else {
            xFileName = sdFileName + ".x";
        }
        String descriptorString = Utility.StringListToString(descriptorNames);

        WriteDescriptors
                .writeModelingXFile(chemicalNames, descriptorValueMatrix, descriptorString, filePath + xFileName,
                        scalingType, stdDevCutoff, correlationCutoff);

        // apply the dataset's external split(s) to the generated .X file
        step = Constants.SPLITDATA;

        List<String> extCompoundArray = DatasetFileOperations.getXCompoundNames(filePath + "ext_0.x");
        numExternalCompounds = extCompoundArray.size();
        String externalCompoundIdString = Utility.StringListToString(extCompoundArray);
        DataSplit.splitModelingExternalGivenList(filePath, actFileName, xFileName, externalCompoundIdString);

        // make internal training / test sets for each model
        if (!modelType.equals(Constants.RANDOMFOREST)) {
            if (trainTestSplitType.equals(Constants.RANDOM)) {
                DataSplit.SplitTrainTestRandom(userName, jobName, numSplits, randomSplitMinTestSize,
                        randomSplitMaxTestSize, randomSplitSampleWithReplacement);
            } else if (trainTestSplitType.equals(Constants.SPHEREEXCLUSION)) {
                DataSplit.SplitTrainTestSphereExclusion(userName, jobName, numSplits, splitIncludesMin, splitIncludesMax,
                        sphereSplitMinTestSize, selectionNextTrainPt);
            }
        }

        if (jobList.equals(Constants.LSF)) {
            String lsfPath = Constants.LSFJOBPATH + userName + "/" + jobName + "/";

            // get y-randomization ready
            step = Constants.YRANDOMSETUP;

            if (modelType.equals(Constants.KNNGA) || modelType.equals(Constants.KNNSA)) {
                ModelingUtilities.SetUpYRandomization(userName, jobName);
                ModelingUtilities.YRandomization(userName, jobName);
            } else if (modelType.equals(Constants.SVM)) {
                ModelingUtilities.SetUpYRandomization(userName, jobName);
                ModelingUtilities.YRandomization(userName, jobName);
                Svm.writeSvmModelingParamsFile(svmParameters, actFileDataType, filePath + "svm-params.txt", lsfPath);
                Svm.svmPreProcess(svmParameters, actFileDataType, filePath);
                Svm.svmPreProcess(svmParameters, actFileDataType, filePath + "yRandom/");
            }
            // copy needed files out to LSF
            LsfUtilities.makeLsfModelingDirectory(filePath, lsfPath);
        }
        logger.info("Finished pre-processing for " + jobName);
    }

    public String executeLSF() throws Exception {
        logger.info("Beginning LSF submission for " + jobName);
        // this function will submit a single LSF job.
        // To submit this workflowTask as multiple jobs (to distribute the
        // computation)
        // change this function and the LsfProcessingThread so that it will
        // work with
        // an LSF jobArray instead.

        String lsfPath = Constants.LSFJOBPATH + userName + "/" + jobName + "/";
        String lsfJobId = "";

        step = Constants.MODELS;
        if (modelType.equals(Constants.KNNGA) || modelType.equals(Constants.KNNSA)) {
            lsfJobId = KnnPlus.buildKnnPlusModelsLsf(knnPlusParameters, actFileDataType, modelType, userName, jobName,
                    lsfPath);
        } else if (modelType.equals(Constants.SVM)) {
            lsfJobId = Svm.buildSvmModelsLsf(lsfPath, userName, jobName);
        }
        logger.info(String.format("Finished LSF submission for %s. LSF ID is %s", jobName, lsfJobId));
        return lsfJobId;
    }


    public void executeLocal() throws Exception {
        logger.info("Beginning local execution for " + jobName);
        String path = Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/";

        // Run modeling process
        if (modelType.equals(Constants.SVM)) {
            step = Constants.YRANDOMSETUP;
            ModelingUtilities.SetUpYRandomization(userName, jobName);
            ModelingUtilities.YRandomization(userName, jobName);

            Svm.svmPreProcess(svmParameters, actFileDataType, filePath);
            Svm.svmPreProcess(svmParameters, actFileDataType, filePath + "yRandom/");
            Svm.writeSvmModelingParamsFile(svmParameters, actFileDataType, filePath + "svm-params.txt", filePath);

            step = Constants.MODELS;
            Svm.buildSvmModels(filePath);

            step = Constants.PREDEXT;
            if (numExternalCompounds > 0) {
                Svm.runSvmPrediction(path, "ext_0.x");
            }

        } else if (modelType.equals(Constants.KNNSA) || modelType.equals(Constants.KNNGA)) {
            step = Constants.YRANDOMSETUP;
            ModelingUtilities.SetUpYRandomization(userName, jobName);
            ModelingUtilities.YRandomization(userName, jobName);

            KnnPlus.buildKnnPlusModels(knnPlusParameters, actFileDataType, modelType, path);

            step = Constants.PREDEXT;
            if (numExternalCompounds > 0) {
                KnnPlus.predictExternalSet(userName, jobName, path, knnPlusParameters.getKnnApplicabilityDomain());
            }
        } else if (modelType.equals(Constants.RANDOMFOREST)) {
            step = Constants.YRANDOMSETUP;
            logger.debug("making X files for job, " + jobName + " submitted by " + "user, " + userName + ".");
            RandomForest.preprocessXFiles(baseDir, Constants.ScalingType.valueOf(scalingType));
            logger.debug("setting up y-randomization, " + jobName + " submitted by " + "user, " + userName + ".");
            yRandomDir = RandomForest.setUpYRandomization(baseDir);

            step = Constants.MODELS;
            Constants.ActivityType activityType = Constants.ActivityType.valueOf(actFileDataType);
            logger.debug("building models, " + jobName + " submitted by " + "user, " + userName + ".");
            RandomForest.growForest(baseDir, activityType, randomForestParameters);
            logger.debug("building y-random models, " + jobName + " submitted by " + "user, " + userName + ".");
            RandomForest.growForest(yRandomDir, activityType, randomForestParameters);
            logger.debug("modeling phase done, " + jobName + " submitted by " + "user, " + userName + ".");
        }
        logger.info("Finished local execution for " + jobName);
    }

    public void postProcess() throws Exception {
        logger.info("Beginning post-processing for " + jobName);
        step = Constants.READING;
        // done with modeling. Read output files.

        // first, copy needed files back from LSF if needed
        if (jobList.equals(Constants.LSF)) {

            String lsfPath = Constants.LSFJOBPATH + userName + "/" + jobName + "/";
            LsfUtilities.retrieveCompletedPredictor(filePath, lsfPath);

            if (modelType.startsWith(Constants.KNN)) {
                KnnPlus.checkModelsFile(filePath);
            }

            if (numExternalCompounds > 0) {
                step = Constants.PREDEXT;
                if (modelType.equals(Constants.KNNSA) || modelType.equals(Constants.KNNGA)) {
                    KnnPlus.predictExternalSet(userName, jobName, filePath,
                            knnPlusParameters.getKnnApplicabilityDomain());
                } else if (modelType.equals(Constants.SVM)) {
                    Svm.runSvmPrediction(filePath, "ext_0.x");
                }
            }
        }

        // the next step is to read in the results from the modeling program,
        // getting data about the models and external prediction values so we
        // can save it to the database.
        Session session = HibernateUtil.getSession();
        Transaction tx = null;

        List<KnnPlusModel> knnPlusModels = Lists.newArrayList();
        List<SvmModel> svmModels = Lists.newArrayList();
        List<RandomForestTree> randomForestTrees = Lists.newArrayList();
        List<RandomForestTree> randomForestYRandomTrees = Lists.newArrayList();

        if (modelType.equals(Constants.KNNGA) || modelType.equals(Constants.KNNSA)) {
            // read external set predictions
            if (numExternalCompounds > 0) {
                externalSetPredictions = KnnPlus.readExternalPredictionOutput(filePath, predictor);
            }

            // read in models and associate them with the predictor
            knnPlusModels = KnnPlus.readModelsFile(filePath, predictor, Constants.NO);
            List<KnnPlusModel> knnPlusYRandomModels =
                    KnnPlus.readModelsFile(filePath + "yRandom/", predictor, Constants.YES);
            predictor.setNumTotalModels(getNumTotalModels());
            predictor.setNumTestModels(knnPlusModels.size());
            predictor.setNumyTotalModels(getNumTotalModels());
            predictor.setNumyTestModels(knnPlusYRandomModels.size());

            if (!knnPlusYRandomModels.isEmpty()) {
                knnPlusModels.addAll(knnPlusYRandomModels);
            }
        } else if (modelType.equals(Constants.RANDOMFOREST)) {
            Map<String, Double> groundTruth = Maps.newHashMap();
            try (BufferedReader br = Files
                    .newBufferedReader(baseDir.resolve(Constants.EXTERNAL_SET_A_FILE), StandardCharsets.UTF_8)) {
                String line;
                Splitter splitter = Splitter.on(CharMatcher.WHITESPACE);
                while ((line = br.readLine()) != null) {
                    List<String> items = splitter.splitToList(line);
                    String key = items.get(0);
                    double value = Double.parseDouble(items.get(1));
                    groundTruth.put(key, value);
                }
            } catch (IOException e) {
                logger.error("Couldn't read external set activities", e);
                throw e;
            }

            for (Path dir : new Path[]{baseDir, yRandomDir}) {
                ScikitRandomForestPrediction pred = RandomForest.readPrediction(dir);
                RandomForestGrove grove = pred.getGrove(predictor, dir == yRandomDir);
                grove.save();

                if (dir == baseDir) {
                    randomForestTrees.addAll(pred.getTrees(grove));
                    if (numExternalCompounds > 0) {
                        externalSetPredictions.addAll(pred.getExternalSetPredictions(groundTruth, predictor.getId()));
                    } else {
                        logger.debug("No external compounds; skipping external set prediction!");
                    }
                } else {
                    randomForestYRandomTrees.addAll(pred.getTrees(grove));
                }
            }

            // numTotalModels is what's displayed for numTestModels on the output webpage;
            // reason is, we may decide to discard some of the models so they will not be used in external set
            // prediction. hence, numTestModels may not equal numTotalModels in the future.
            predictor.setNumTotalModels(getNumTotalModels());
            predictor.setNumTestModels(getNumTotalModels());
        } else if (modelType.equals(Constants.SVM)) {
            // read in models and associate them with the predictor
            svmModels = Lists.newArrayList();
            svmModels.addAll(Svm.readSvmModels(filePath, svmParameters.getSvmCutoff()));
            svmModels.addAll(Svm.readSvmModels(filePath + "yRandom/", svmParameters.getSvmCutoff()));

            // get num models info for predictor
            predictor.setNumTotalModels(getNumTotalModels());
            File dir = new File(filePath);
            int numTestModels = (dir.list(new FilenameFilter() {
                public boolean accept(File arg0, String arg1) {
                    return arg1.endsWith(".mod");
                }
            }).length);
            predictor.setNumTestModels(numTestModels);

            predictor.setNumyTotalModels(getNumTotalModels());
            File ydir = new File(filePath + "yRandom/");
            int numYTestModels = (ydir.list(new FilenameFilter() {
                public boolean accept(File arg0, String arg1) {
                    return arg1.endsWith(".mod");
                }
            }).length);
            predictor.setNumyTestModels(numYTestModels);

            // read external set predictions
            if (numExternalCompounds > 0) {
                externalSetPredictions = Svm.readExternalPredictionOutput(filePath, predictor.getId());
            }

            // clean junk
            Svm.cleanExcessFilesFromDir(filePath);
            Svm.cleanExcessFilesFromDir(filePath + "yRandom/");
        }

        // save updated predictor to database
        predictor.setScalingType(scalingType);
        predictor.setCategoryWeights(categoryWeights);
        predictor.setDescriptorGeneration(descriptorGenerationType);
        predictor.setModelMethod(modelType);
        predictor.setName(jobName);
        predictor.setUserName(userName);
        predictor.setActFileName(actFileName);
        predictor.setSdFileName(sdFileName);
        predictor.setActivityType(actFileDataType);
        predictor.setStatus("saved");

        if (dataset.getSplitType().equals(Constants.NFOLD)) {
            predictor.setPredictorType(Constants.HIDDEN);
        } else {
            predictor.setPredictorType(Constants.PRIVATE);
        }
        predictor.setDatasetId(datasetID);
        predictor.setHasBeenViewed(Constants.NO);
        predictor.setJobCompleted(Constants.YES);

        // commit the predictor, models, and external set predictions
        try {
            tx = session.beginTransaction();
            session.saveOrUpdate(predictor);

            for (KnnPlusModel m : knnPlusModels) {
                m.setPredictorId(predictor.getId());
                session.saveOrUpdate(m);
            }
            for (SvmModel m : svmModels) {
                m.setPredictorId(predictor.getId());
                session.saveOrUpdate(m);
            }
            for (RandomForestTree t : randomForestTrees) {
                session.saveOrUpdate(t);
            }
            for (RandomForestTree t : randomForestYRandomTrees) {
                session.saveOrUpdate(t);
            }
            for (ExternalValidation ev : externalSetPredictions) {
                session.saveOrUpdate(ev);
            }
            tx.commit();
        } catch (RuntimeException e) {
            logger.error("Runtime Exception encountered for job, " +
                    jobName + " submitted by user, " + userName + ".\n" +
                    e.toString());
            if (tx != null) {
                tx.rollback();
            }
        }

        // clean up dirs
        if (modelType.equals(Constants.RANDOMFOREST)) {
            RandomForest.cleanUp(baseDir);
        }

        // calculate outputs based on ext set predictions and save
        RSquaredAndCCR.addRSquaredAndCCRToPredictor(predictor, session);
        try {
            tx = session.beginTransaction();
            session.saveOrUpdate(predictor);
            tx.commit();
        } catch (Exception ex) {
            logger.error("Error while executing job, " + jobName + " submitted by " + userName + ".\n" + ex.toString());
            tx.rollback();
        }

        if (dataset.getSplitType().equals(Constants.NFOLD)) {
            // find parent predictor
            String parentPredictorName = jobName.substring(0, jobName.lastIndexOf("_fold"));
            Predictor parentPredictor =
                    PopulateDataObjects.getPredictorByName(parentPredictorName, predictor.getUserName(), session);
            if (parentPredictor != null && parentPredictor.getJobCompleted() != null) {
                // check if all its other children are completed.
                String[] childIdArray = parentPredictor.getChildIds().split("\\s+");
                int finishedChildPredictors = 0;
                int numTotalModelsTotal = 0;
                for (String childId : childIdArray) {
                    Predictor childPredictor = PopulateDataObjects.getPredictorById(Long.parseLong(childId), session);
                    if (childPredictor.getJobCompleted().equals(Constants.YES)) {
                        numTotalModelsTotal += childPredictor.getNumTotalModels();
                        finishedChildPredictors++;
                    }
                }
                int numFolds = Integer.parseInt(dataset.getNumExternalFolds());
                if (finishedChildPredictors == numFolds) {
                    // if all children are now done, set jobCompleted to YES
                    // in the parent predictor.
                    parentPredictor.setJobCompleted(Constants.YES);
                    parentPredictor.setNumTotalModels(finishedChildPredictors);
                    parentPredictor.setModelingParametersId(predictor.getModelingParametersId());
                }
            }

            predictor.setParentId(parentPredictor.getId());

            // calc r^2 etc for parent as well
            RSquaredAndCCR.addRSquaredAndCCRToPredictor(parentPredictor, session);

            // save
            try {
                tx = session.beginTransaction();
                session.saveOrUpdate(parentPredictor);
                session.saveOrUpdate(predictor);
                tx.commit();
            } catch (Exception ex) {
                logger.error(
                        "Error while executing job, " + jobName + " submitted by " + userName + ".\n" + ex.toString());
            }

            ModelingUtilities.MoveToPredictorsDir(userName, jobName, parentPredictorName);
        } else {
            ModelingUtilities.MoveToPredictorsDir(userName, jobName, "");
        }
        session.close();
        logger.info("Finished post-processing for " + jobName);
    }

    public void delete() throws Exception {

    }

    public String getStatus() {
        return step;
    }

    // helper functions and get/sets defined below this point.

    private int getNumTotalModels() {
        if (numSplits == null) {
            return 0;
        }
        int numModels = Integer.parseInt(numSplits);

        if (modelType.equals(Constants.KNNSA)) {
            numModels *= Integer.parseInt(knnPlusParameters.getSaNumRuns());
            numModels *= Integer.parseInt(knnPlusParameters.getSaNumBestModels());

            int numDescriptorSizes = 1;
            if (Integer.parseInt(knnPlusParameters.getKnnDescriptorStepSize()) != 0) {
                numDescriptorSizes += (Integer.parseInt(knnPlusParameters.getKnnMaxNumDescriptors()) - Integer
                        .parseInt(knnPlusParameters.getKnnMinNumDescriptors())) / Integer
                        .parseInt(knnPlusParameters.getKnnDescriptorStepSize());
            }
            numModels *= numDescriptorSizes;
        } else if (modelType.equals(Constants.RANDOMFOREST)) {
            numModels = Integer.parseInt(predictor.getNumSplits());
        } else if (modelType.equals(Constants.SVM)) {
            numModels = Integer.parseInt(predictor.getNumSplits());
            Double numDifferentCosts = Math.ceil((Double.parseDouble(svmParameters.getSvmCostTo()) - Double
                    .parseDouble(svmParameters.getSvmCostFrom())) / Double.parseDouble(svmParameters.getSvmCostStep())
                    + 0.0001);

            Double numDifferentDegrees = Math.ceil((Double.parseDouble(svmParameters.getSvmDegreeTo()) - Double
                    .parseDouble(svmParameters.getSvmDegreeFrom())) / Double
                    .parseDouble(svmParameters.getSvmDegreeStep()) + 0.0001);

            Double numDifferentGammas = Math.ceil((Double.parseDouble(svmParameters.getSvmGammaTo()) - Double
                    .parseDouble(svmParameters.getSvmGammaFrom())) / Double.parseDouble(svmParameters.getSvmGammaStep())
                    + 0.0001);

            Double numDifferentNus = Math.ceil(
                    (Double.parseDouble(svmParameters.getSvmNuTo()) - Double.parseDouble(svmParameters.getSvmNuFrom()))
                            / Double.parseDouble(svmParameters.getSvmNuStep()) + 0.0001);

            Double numDifferentPEpsilons = Math.ceil((Double.parseDouble(svmParameters.getSvmPEpsilonTo()) - Double
                    .parseDouble(svmParameters.getSvmPEpsilonFrom())) / Double
                    .parseDouble(svmParameters.getSvmPEpsilonStep()) + 0.0001);

            String svmType = "";
            if (actFileDataType.equals(Constants.CATEGORY)) {
                svmType = svmParameters.getSvmTypeCategory();
            } else {
                svmType = svmParameters.getSvmTypeContinuous();
            }

            if (svmType.equals("0")) {
                numDifferentPEpsilons = 1.0;
                numDifferentNus = 1.0;
            } else if (svmType.equals("1")) {
                numDifferentPEpsilons = 1.0;
                numDifferentCosts = 1.0;
            } else if (svmType.equals("3")) {
                numDifferentNus = 1.0;
            } else if (svmType.equals("4")) {
                numDifferentPEpsilons = 1.0;
            }

            if (svmParameters.getSvmKernel().equals("0")) {
                numDifferentGammas = 1.0;
                numDifferentDegrees = 1.0;
            } else if (svmParameters.getSvmKernel().equals("1")) {
                // no change
            } else if (svmParameters.getSvmKernel().equals("2")) {
                numDifferentDegrees = 1.0;
            } else if (svmParameters.getSvmKernel().equals("3")) {
                numDifferentDegrees = 1.0;
            }

            numModels *= numDifferentCosts * numDifferentDegrees * numDifferentGammas * numDifferentNus
                    * numDifferentPEpsilons;

        }
        return numModels;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getActFileName() {
        return actFileName;
    }

    public void setActFileName(String actFileName) {
        this.actFileName = actFileName;
    }

    public String getSdFileName() {
        return sdFileName;
    }

    public void setSdFileName(String sdFileName) {
        this.sdFileName = sdFileName;
    }

    public Long getDatasetID() {
        return datasetID;
    }

    public void setDatasetID(Long datasetID) {
        this.datasetID = datasetID;
    }

    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    public void setRandomForestParametersRepository(RandomForestParametersRepository randomForestParametersRepository) {
        this.randomForestParametersRepository = randomForestParametersRepository;
    }

    public void setKnnPlusParametersRepository(KnnPlusParametersRepository knnPlusParametersRepository) {
        this.knnPlusParametersRepository = knnPlusParametersRepository;
    }

    public void setKnnParametersRepository(KnnParametersRepository knnParametersRepository) {
        this.knnParametersRepository = knnParametersRepository;
    }

    public void setSvmParametersRepository(SvmParametersRepository svmParametersRepository) {
        this.svmParametersRepository = svmParametersRepository;
    }
}
