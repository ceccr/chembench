package edu.unc.ceccr.chembench.actions;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.jobs.CentralDogma;
import edu.unc.ceccr.chembench.persistence.*;
import edu.unc.ceccr.chembench.taskObjects.QsarPredictionTask;
import edu.unc.ceccr.chembench.utilities.RunExternalProgram;
import edu.unc.ceccr.chembench.utilities.Utility;
import edu.unc.ceccr.chembench.workflows.descriptors.ReadDescriptors;
import edu.unc.ceccr.chembench.workflows.modelingPrediction.RunSmilesPrediction;
import org.apache.commons.collections.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileReader;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static edu.unc.ceccr.chembench.global.Constants.ERROR;

public class PredictionAction extends ActionSupport {

    private static final Logger logger = LoggerFactory.getLogger(PredictionAction.class);
    private final DatasetRepository datasetRepository;
    private final PredictorRepository predictorRepository;
    private final PredictionRepository predictionRepository;
    private final JobRepository jobRepository;
    // variables used for JSP display
    private User user = User.getCurrentUser();
    private List<Predictor> userPredictors;
    private List<String> userDatasetNames;
    private List<String> userPredictorNames;
    private List<String> userPredictionNames;
    private List<String> userTaskNames;
    private List<Dataset> userDatasets;
    private String predictorCheckBoxes;
    /* a flag that indicate if we should display SMILES prediction or not */
    private boolean singleCompoundPredictionAllowed;
    private boolean isUploadedDescriptors;
    private boolean isMixDescriptors;
    private List<Predictor> selectedPredictors = new ArrayList<>();
    private List<SmilesPrediction> smilesPredictions;
    private String smiles;
    private String cutoff;

    // populated by the JSP form
    private Long selectedDatasetId;
    private String cutOff = "0.5";
    private String jobName;
    private String selectedPredictorIds;

    @Autowired
    public PredictionAction(DatasetRepository datasetRepository, PredictorRepository predictorRepository,
                            PredictionRepository predictionRepository, JobRepository jobRepository) {
        this.datasetRepository = datasetRepository;
        this.predictorRepository = predictorRepository;
        this.predictionRepository = predictionRepository;
        this.jobRepository = jobRepository;
    }

    public String makeSmilesPrediction() throws Exception {
        String result = SUCCESS;
        long startTime = System.nanoTime();
        ActionContext context = ActionContext.getContext();
        User user = User.getCurrentUser();

        String predictorIds = ((String[]) context.getParameters().get("predictorIds"))[0];
        logger.debug(" 1: " + smiles + " 2: " + cutoff + " 3: " + predictorIds);
        logger.debug(user.getUserName());
        logger.debug("SMILES predids: " + predictorIds);

        String[] selectedPredictorIdArray = predictorIds.split("\\s+");
        List<Predictor> predictors = new ArrayList<>();
        Set<String> descriptorTypes = Sets.newHashSet();
        for (String predictorId : selectedPredictorIdArray) {
            Predictor predictor = predictorRepository.findOne(Long.parseLong(predictorId));
            if (!predictor.getModelMethod().startsWith(Constants.RANDOMFOREST)) {
                throw new RuntimeException("Non-random forest predictors cannot be used for SMILES predictions");
            }
            String descriptorType = predictor.getDescriptorGeneration();
            // skip predictors with uploaded descriptors, since we can't
            // generate them for the SDF generated from the SMILES string
            if (!descriptorType.contains(Constants.UPLOADED)) {
                predictors.add(predictor);
                //add descriptor types for hybrids
                for (String descriptor : Splitter.on(", ").splitToList(descriptorType)) {
                    descriptorTypes.add(descriptor);
                }
            }
        }

        /* stores results */
        smilesPredictions = new ArrayList<>();

        for (Predictor predictor : predictors) {
            String zScore = "";

            /* make smiles dir */
            Path baseSmilesDir = Paths.get(Constants.CECCR_USER_BASE_PATH, user.getUserName(), "SMILES");
            Files.createDirectories(baseSmilesDir);
            Path predPath = Files.createTempDirectory(baseSmilesDir, predictor.getName());
            String smilesDir = predPath.toString() + "/";
            logger.debug("Created the directory " + smilesDir);

            // generate an SDF from this SMILES string
            RunSmilesPrediction.smilesToSdf(smiles, smilesDir);
            logger.info(String.format("Generated SDF file from SMILES \"%s\" written to %s", smiles, smilesDir));
            // generate descriptors using the given SDF file except for ISIDA
            if (!predictor.getDescriptorGeneration().contains(Constants.ISIDA)) {
                RunSmilesPrediction.generateDescriptorsForSdf(smilesDir, descriptorTypes);
            }else{
                logger.info(predictor.getDescriptorGeneration());
            }
            logger.info("Generated descriptors for SDF: " + descriptorTypes.toString());

            String[] predValues = new String[3];
            int totalModels = predictor.getNumTestModels();

            // for n-folded predictors
            if (predictor.getChildType() != null && predictor.getChildType().equals(Constants.NFOLD)) {
                Boolean computedAD = false;
                String[] ids = predictor.getChildIds().split("\\s+");
                logger.info("Predictor is n-folded.");
                List<String[]> tempPred = new ArrayList<>();
                for (String id : ids) {
                    Predictor tempP = predictorRepository.findOne(Long.parseLong(id));

                    // since predictions are made per-fold, each fold needs
                    // access to the SDF file as well as any descriptor matrices
                    new File(smilesDir, tempP.getName()).mkdirs();
                    for (String s : new File(smilesDir).list()) {
                        if (!(new File(s)).isDirectory() && s.startsWith("smiles")) {
                            Path target = Paths.get(smilesDir, s);
                            Path link = Paths.get(smilesDir, tempP.getName(), s);
                            try {
                                Files.createSymbolicLink(link, target);
                            } catch (FileAlreadyExistsException e) {
                                // pass
                            }
                        }
                    }

                    tempPred.add(RunSmilesPrediction
                            .predictSmilesSdf(smilesDir + tempP.getName() + "/", user.getUserName(), tempP));

                    totalModels += tempP.getNumTestModels();
                    logger.debug("Calculating predictions for " + tempP.getName());

                    // Calculate applicability domain
                    if (!computedAD) {
                        String execstr = "";
                        execstr = Constants.CECCR_BASE_PATH + "get_ad/get_ad64 " + smilesDir + tempP.getName()
                                + "/train_0.x " + "-4PRED=" + smilesDir + tempP.getName() + "/smiles.sdf.renorm.x " + ""
                                + " -OUT=" + smilesDir + "smiles_AD";
                        RunExternalProgram.runCommandAndLogOutput(execstr, smilesDir, "getAD");
                        computedAD = true;

                        // Read AD results
                        try {
                            String gadFile = smilesDir + "smiles_AD.gad";
                            File file = new File(gadFile);
                            FileReader fin = new FileReader(file);
                            Scanner src = new Scanner(fin);

                            while (src.hasNext()) {
                                String readLine = src.nextLine();
                                if (readLine.startsWith("ID")) {
                                    readLine = src.nextLine();
                                    if (readLine.startsWith("0")) {
                                        String[] values = readLine.split("\\s+");
                                        zScore = values[3];
                                        break;
                                    }
                                }
                            }
                            src.close();
                            fin.close();
                        } catch (Exception e) {
                            logger.error("User: " + user + "SMILES: " + smiles, e);
                        }
                    }
                }

                int predictingModels = 0;
                double predictedValue = 0d;
                double standardDeviation = 0d;

                /* getting average values */
                for (String[] s : tempPred) {
                    predictingModels += Integer.parseInt(s[0]);
                    predictedValue += Double.parseDouble(s[1]);
                    try {
                        standardDeviation += Double.parseDouble(s[2]);
                    } catch (NumberFormatException e) {
                        // pass (e.g. if only one model, stddev is N/A)
                    }
                    // debug part
                    logger.debug("Predicting models: " + s[0]);
                    logger.debug("Predicted value: " + s[1]);
                    logger.debug("Standard deviation: " + s[2]);
                }
                predValues[0] = String.valueOf(predictingModels);
                predValues[1] = Utility.roundSignificantFigures(String.valueOf(predictedValue / ids.length),
                        Constants.REPORTED_SIGNIFICANT_FIGURES);
                predValues[2] = Utility.roundSignificantFigures(String.valueOf(standardDeviation / ids.length),
                        Constants.REPORTED_SIGNIFICANT_FIGURES);
            }
            /*
             * create descriptors for the SDF, normalize them , and make a
             * prediction
             */
            else {
                predValues = RunSmilesPrediction.predictSmilesSdf(smilesDir, user.getUserName(), predictor);

                // Calculate applicability domian
                String execstr = "";
                execstr = Constants.CECCR_BASE_PATH + "get_ad/get_ad64 " + smilesDir + "/train_0.x " + "-4PRED="
                        + smilesDir + "/smiles.sdf.renorm.x " + " -OUT=" + smilesDir + "smiles_AD";
                RunExternalProgram.runCommandAndLogOutput(execstr, smilesDir, "getAD");

                // Read AD results
                try {
                    String gadFile = smilesDir + "smiles_AD.gad";
                    File file = new File(gadFile);
                    FileReader fin = new FileReader(file);
                    Scanner src = new Scanner(fin);

                    while (src.hasNext()) {
                        String readLine = src.nextLine();
                        if (readLine.startsWith("ID")) {
                            readLine = src.nextLine();
                            if (readLine.startsWith("0")) {
                                String[] values = readLine.split("\\s+");
                                zScore = values[3];
                                break;
                            }
                        }
                    }
                    src.close();
                    fin.close();
                } catch (Exception e) {
                    logger.error("User: " + user + "SMILES: " + smiles, e);
                }
            }

            // read predValues and build the prediction output object
            SmilesPrediction sp = new SmilesPrediction();
            sp.setPredictorName(predictor.getName());
            sp.setTotalModels(totalModels);

            sp.setPredictingModels(Integer.parseInt(predValues[0]));
            sp.setPredictedValue(predValues[1]);
            sp.setStdDeviation(predValues[2]);
            sp.setZscore(zScore);
            sp.setCutoff(cutoff);
            sp.setSmiles(smiles);
            sp.setShow(false);

            if (cutoff.equals("N/A")){
                sp.setShow(true);
            }
            else{
                if (!zScore.trim().isEmpty()) {
                    if (Double.parseDouble(cutoff) > Double.parseDouble(zScore)) {
                        sp.setShow(true);
                    }
                }
            }

            logger.debug("zScore: " + zScore);
            logger.debug("cutoff: " + cutoff);

            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1000000;
            logger.debug("Time it took " + duration + " ms");

            // add it to the array
            smilesPredictions.add(sp);
        }

        logger.info("made SMILES prediction on string " + smiles + " with predictors " + predictorIds + " for " + user
                .getUserName());

        return result;
    }

    public String makeDatasetPrediction() throws Exception {
        /*
         * prediction form submitted, so create a new prediction task and run
         * it
         */
        User user = User.getCurrentUser();
        Dataset predictionDataset = datasetRepository.findOne(selectedDatasetId);
        String sdf = predictionDataset.getSdfFile();

        if (jobName != null) {
            jobName = jobName.replaceAll(" ", "_");
            jobName = jobName.replaceAll("\\(", "_");
            jobName = jobName.replaceAll("\\)", "_");
            jobName = jobName.replaceAll("\\[", "_");
            jobName = jobName.replaceAll("\\]", "_");
            jobName = jobName.replaceAll("/", "_");
            jobName = jobName.replaceAll("&", "_");
        }

        logger.debug(user.getUserName());
        logger.debug("predids: " + selectedPredictorIds);

        QsarPredictionTask predTask =
                new QsarPredictionTask(user.getUserName(), jobName, sdf, cutOff, selectedPredictorIds,
                        predictionDataset);

        predTask.setUp();
        int numCompounds = predictionDataset.getNumCompound();
        String[] ids = selectedPredictorIds.split("\\s+");
        int numModels = 0;

        List<Predictor> selectedPredictors = new ArrayList<>();

        for (String id : ids) {
            Predictor sp = predictorRepository.findOne(Long.parseLong(id));
            selectedPredictors.add(sp);
            if (sp.getChildType() != null && sp.getChildType().equals(Constants.NFOLD)) {
                String[] childIds = sp.getChildIds().split("\\s+");
                for (String childId : childIds) {
                    Predictor cp = predictorRepository.findOne(Long.parseLong(childId));
                    numModels += cp.getNumTestModels();
                }
            } else {
                numModels += sp.getNumTestModels();
            }
        }

        /*
         * check descriptors of each of the selected predictors. Make sure
         * that the prediction dataset contains all of those descriptors,
         * otherwise error out.
         */
        for (Predictor sp : selectedPredictors) {
            String[] predictionDatasetDescriptors = predictionDataset.getAvailableDescriptors().split("\\s+");
            boolean descriptorsMatch = false;

            if (sp.getDescriptorGeneration().equals(Constants.UPLOADED)) {
                // get the uploaded descriptors for the dataset
                String predictionXFile = predictionDataset.getXFile();

                String predictionDatasetDir =
                        Constants.CECCR_USER_BASE_PATH + predictionDataset.getUserName() + "/DATASETS/"
                                + predictionDataset.getName() + "/";
                if (predictionXFile != null && !predictionXFile.trim().isEmpty()) {

                    logger.debug("Staring to read predictors from file: " + predictionDatasetDir + predictionXFile);
                    String[] predictionDescs =
                            ReadDescriptors.readDescriptorNamesFromX(predictionXFile, predictionDatasetDir);

                    // get the uploaded descriptors for the predictor
                    Dataset predictorDataset = datasetRepository.findOne(sp.getDatasetId());
                    String predictorDatasetDir =
                            Constants.CECCR_USER_BASE_PATH + predictorDataset.getUserName() + "/DATASETS/"
                                    + predictorDataset.getName() + "/";
                    String[] predictorDescs =
                            ReadDescriptors.readDescriptorNamesFromX(predictorDataset.getXFile(), predictorDatasetDir);

                    descriptorsMatch = true;
                    /*
                     * for each predictor desc, make sure there's a matching
                     * prediction desc.
                     */
                    for (int i = 0; i < predictorDescs.length; i++) {
                        boolean matchingDescriptor = false;
                        for (int j = 0; j < predictionDescs.length; j++) {
                            if (predictorDescs[i].equals(predictionDescs[j])) {
                                matchingDescriptor = true;
                                j = predictionDescs.length;
                            }
                        }
                        if (!matchingDescriptor) {
                            descriptorsMatch = false;
                            addActionError(
                                    "The predictor '" + sp.getName() + "' contains the descriptor '" + predictorDescs[i]
                                            + "', but this " + "descriptor was not found in "
                                            + "the prediction dataset.");
                        }
                    }

                    if (!descriptorsMatch) {
                        return ERROR;
                    }
                }
            } else {
                for (int i = 0; i < predictionDatasetDescriptors.length; i++) {
                    if (sp.getDescriptorGeneration().equals(Constants.CDK) && predictionDatasetDescriptors[i]
                            .equals(Constants.CDK)) {
                        descriptorsMatch = true;
                    } else if (sp.getDescriptorGeneration().equals(Constants.DRAGONH) && predictionDatasetDescriptors[i]
                            .equals(Constants.DRAGONH)) {
                        descriptorsMatch = true;
                    } else if (sp.getDescriptorGeneration().equals(Constants.DRAGONNOH)
                            && predictionDatasetDescriptors[i].equals(Constants.DRAGONNOH)) {
                        descriptorsMatch = true;
                    } else if (sp.getDescriptorGeneration().equals(Constants.MOE2D) && predictionDatasetDescriptors[i]
                            .equals(Constants.MOE2D)) {
                        descriptorsMatch = true;
                    } else if (sp.getDescriptorGeneration().equals(Constants.MACCS) && predictionDatasetDescriptors[i]
                            .equals(Constants.MACCS)) {
                        descriptorsMatch = true;
                    } else if (sp.getDescriptorGeneration().equals(Constants.ISIDA) && predictionDatasetDescriptors[i]
                            .equals(Constants.ISIDA)) {
                        descriptorsMatch = true;
                    }
                }

                if (!descriptorsMatch) {
                    addActionError("The predictor '" + sp.getName() + "' is based on " + sp.getDescriptorGeneration()
                            + " descriptors, but the dataset '" + predictionDataset.getName()
                            + "' does not have these descriptors. " + "You will not be able to make"
                            + " this prediction.");
                    return ERROR;
                }
            }

        }

        CentralDogma centralDogma = CentralDogma.getInstance();
        String emailOnCompletion = "false";
        centralDogma.addJobToIncomingList(user.getUserName(), jobName, predTask, numCompounds, numModels,
                emailOnCompletion);

        logger.info("making prediction run on dataset " + predictionDataset.getName() + " with predictors "
                + selectedPredictorIds + " for " + user.getUserName());
        return SUCCESS;
    }

    public String execute() throws Exception {
        return SUCCESS;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Predictor> getSelectedPredictors() {
        return selectedPredictors;
    }

    public void setSelectedPredictors(List<Predictor> selectedPredictors) {
        this.selectedPredictors = selectedPredictors;
    }

    public Long getSelectedDatasetId() {
        return selectedDatasetId;
    }

    public void setSelectedDatasetId(Long selectedDatasetId) {
        this.selectedDatasetId = selectedDatasetId;
    }

    public String getCutOff() {
        return cutOff;
    }

    public void setCutOff(String cutOff) {
        this.cutOff = cutOff;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getSelectedPredictorIds() {
        return selectedPredictorIds;
    }

    public void setSelectedPredictorIds(String selectedPredictorIds) {
        this.selectedPredictorIds = selectedPredictorIds;
    }

    public List<SmilesPrediction> getSmilesPredictions() {
        return smilesPredictions;
    }

    public void setSmilesPredictions(List<SmilesPrediction> smilesPredictions) {
        this.smilesPredictions = smilesPredictions;
    }

    public String getSmiles() {
        return smiles;
    }

    public void setSmiles(String smiles) {
        this.smiles = smiles;
    }

    public String getCutoff() {
        return cutoff;
    }

    public void setCutoff(String cutoff) {
        this.cutoff = cutoff;
    }

    public class SmilesPrediction {
        // used by makeSmilesPrediction()
        String predictedValue;
        String stdDeviation;
        String zscore;
        int predictingModels;
        int totalModels;
        String predictorName;
        String smiles;
        String cutoff;
        boolean show;

        public String getPredictedValue() {
            return predictedValue;
        }

        public void setPredictedValue(String predictedValue) {
            this.predictedValue = predictedValue;
        }

        public String getStdDeviation() {
            return stdDeviation;
        }

        public void setStdDeviation(String stdDeviation) {
            this.stdDeviation = stdDeviation;
        }

        public String getZscore() {
            return zscore;
        }

        public void setZscore(String zscore) {
            this.zscore = zscore;
        }

        public int getPredictingModels() {
            return predictingModels;
        }

        public void setPredictingModels(int predictingModels) {
            this.predictingModels = predictingModels;
        }

        public int getTotalModels() {
            return totalModels;
        }

        public void setTotalModels(int totalModels) {
            this.totalModels = totalModels;
        }

        public String getPredictorName() {
            return predictorName;
        }

        public void setPredictorName(String predictorName) {
            this.predictorName = predictorName;
        }

        public void setCutoff(String cutoff) {this.cutoff = cutoff;}

        public String getCutoff(){return cutoff;}

        public void setSmiles(String smiles) {this.smiles = smiles;}

        public String getSmiles(){return smiles;}

        public void setShow(Boolean show) {this.show = show;}

        public Boolean getShow(){return show;}
    }

}
