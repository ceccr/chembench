package edu.unc.ceccr.chembench.actions;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.jobs.CentralDogma;
import edu.unc.ceccr.chembench.persistence.*;
import edu.unc.ceccr.chembench.taskObjects.QsarPredictionTask;
import edu.unc.ceccr.chembench.utilities.CopyJobFiles;
import edu.unc.ceccr.chembench.utilities.FileAndDirOperations;
import edu.unc.ceccr.chembench.utilities.RunExternalProgram;
import edu.unc.ceccr.chembench.utilities.Utility;
import edu.unc.ceccr.chembench.workflows.datasets.DatasetFileOperations;
import edu.unc.ceccr.chembench.workflows.descriptors.GenerateDescriptors;
import edu.unc.ceccr.chembench.workflows.descriptors.ReadDescriptors;
import edu.unc.ceccr.chembench.workflows.descriptors.WriteDescriptors;
import edu.unc.ceccr.chembench.workflows.modelingPrediction.LegacyRandomForest;
import edu.unc.ceccr.chembench.workflows.modelingPrediction.RandomForest;
import edu.unc.ceccr.chembench.workflows.modelingPrediction.RunSmilesPrediction;
import edu.unc.ceccr.chembench.workflows.modelingPrediction.ScikitRandomForestPrediction;
import org.apache.commons.collections.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;

public class McraAction extends ActionSupport {

    private static final Logger logger = LoggerFactory.getLogger(McraAction.class);
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
  //  private List<PredictionAction.SmilesPrediction> smilesPredictions;
    private String smiles;
    private String cutoff;
    // populated by the JSP form
    private Long selectedDatasetId;
    private String cutOff = "0.5";
    private String jobName;
    private String selectedPredictorIds;    //selected modeling datasets


    private static Set<String> descriptorTypes = Sets.newHashSet();
    static {
        descriptorTypes.add(Constants.MACCS);
        descriptorTypes.add(Constants.MOE2D);
        descriptorTypes.add(Constants.ISIDA);
        descriptorTypes.add(Constants.DRAGONH);
    }
    private final static int N_NEAREST_NEIGHORS = 1;
    private List<PredictionAction.SmilesPrediction> smilesPredictions;


    @Autowired
    public McraAction(DatasetRepository datasetRepository, PredictorRepository predictorRepository,
                      PredictionRepository predictionRepository, JobRepository jobRepository) {
        this.datasetRepository = datasetRepository;
        this.predictorRepository = predictorRepository;
        this.predictionRepository = predictionRepository;
        this.jobRepository = jobRepository;
    }

    public String loadSelectPredictorPage() throws Exception {
        userPredictors = predictorRepository.findByUserName(user.getUserName());
        if (user.getShowPublicPredictors().equals(Constants.ALL)) {
            userPredictors.addAll(predictorRepository.findPublicPredictors());
        }
        for (Predictor p : userPredictors) {
            Dataset d = datasetRepository.findOne(p.getDatasetId());
            p.setDatasetDisplay(d.getName());
        }
        return SUCCESS;
    }

    public String makeSmilesPrediction() throws Exception {
        String result = SUCCESS;
        ActionContext context = ActionContext.getContext();
        User user = User.getCurrentUser();

        String predictorIds = ((String[]) context.getParameters().get("predictorIds"))[0];
        logger.debug(" 1: " + smiles + " 2: " + cutoff + " 3: " + predictorIds);
        logger.debug(user.getUserName());
        logger.debug("SMILES predids: " + predictorIds);

        String[] selectedPredictorIdArray = predictorIds.split("\\s+");
        List<Dataset> modelingDatasets = new ArrayList<>();

        for (String predictorId : selectedPredictorIdArray) {
            Dataset predictor = datasetRepository.findOne(Long.parseLong(predictorId));
            modelingDatasets.add(predictor);
        }

        /* stores results */
        smilesPredictions = new ArrayList<>();

        // WHY, WHAT IS THIS
        for (Dataset modelingDataset : modelingDatasets) {

            /* make smiles dir */
            Path baseSmilesDir = Paths.get(Constants.CECCR_USER_BASE_PATH, user.getUserName(), "SMILES");
            Files.createDirectories(baseSmilesDir);
            Path predPath = Files.createTempDirectory(baseSmilesDir, modelingDataset.getName());
            String smilesDir = predPath.toString() + "/";
            logger.debug("Created the directory " + smilesDir);

            // generate an SDF from this SMILES string
            RunSmilesPrediction.smilesToSdf(smiles, smilesDir);
            logger.info(String.format("Generated SDF file from SMILES \"%s\" written to %s", smiles, smilesDir));

            // generate descriptors using the given SDF file
            RunSmilesPrediction.generateDescriptorsForSdf(smilesDir, descriptorTypes);  //doesn't include ISIDA
            GenerateDescriptors.generateIsidaDescriptors(smilesDir+"smiles.sdf", smilesDir + "Descriptors/smiles.sdf.ISIDA");

            logger.info("Generated descriptors for SDF: " + descriptorTypes.toString());

            List<Double> predictedValues = predictActivity( smilesDir+"smiles.sdf", modelingDataset);
            logger.info("Predicted value for SMILES SDF: " + predictedValues.get(0).toString());

          //  predValues = predictSmilesSdf(smilesDir, user.getUserName(), predictor);
        }

        logger.info("made SMILES prediction on string " + smiles + " with predictors " + predictorIds + " for " + user
                .getUserName());

        return result;
    }

    /* returns the predicted activity of each compound in the predicting sdf, in the order of the sdf*/
    private List<Double> predictActivity(String predictingSdf, Dataset modelingDataset) throws Exception {
        List<String> descriptorNames = new ArrayList<>();

        List<Neighbor>[] compoundNeighborLists = null;  //maps the index of a compound in the predicting sdf to a list of nearest neighbors

        for (String descriptorType : descriptorTypes) {

            /* read descriptors for the modeling sdf */
            List<Descriptors> modelingDescriptors = new ArrayList<>();
            readDescriptors(descriptorType, modelingDataset.getDirectoryPath()+modelingDataset.getSdfFile(), descriptorNames, modelingDescriptors);

            /* read descriptors for the predicting sdf */
            List<Descriptors> predictingDescriptors = new ArrayList<>();
            readDescriptors(descriptorType, predictingSdf, descriptorNames, predictingDescriptors);

            if (predictingDescriptors == null){
                compoundNeighborLists = new List[predictingDescriptors.size()];     //size would be 1 for SMILES
            }

            /* for each compound in the predicting set, find the nearest neighbors for this descriptor only */
            for (int i=0; i<predictingDescriptors.size(); i++) {
                if (compoundNeighborLists[i] == null){
                    compoundNeighborLists[i] = new ArrayList<Neighbor>();
                }
                nearestNeighbors(N_NEAREST_NEIGHORS, predictingDescriptors.get(i).getDescriptorValues(),
                        modelingDescriptors, modelingDataset, compoundNeighborLists[i]);
            }
        }

        List<Double> predictionValues = new ArrayList<>();

        for (int i=0; i<compoundNeighborLists.length; i++) {
            double sa=0, s=0;
            for (Neighbor n : compoundNeighborLists[i]){
                sa += n.similarity*n.activity;
                s += n.similarity;
            }
            predictionValues.add(sa/s);
        }

        return predictionValues;
    }

    /*
    Reads a single descriptor file into the descriptorValueMatrix
     */
    private static void readDescriptors(String descriptorType, String sdfFile, List<String> descriptorNames,
                                       List<Descriptors> descriptorValueMatrix) throws Exception {
        if (descriptorType.equals(Constants.CDK)) {
            ReadDescriptors.readXDescriptors(sdfFile + ".cdk.x", descriptorNames, descriptorValueMatrix);
        } else if (descriptorType.equals(Constants.DRAGONH)) {
            ReadDescriptors.readDragonXDescriptors(sdfFile + ".dragonH", descriptorNames, descriptorValueMatrix);
        } else if (descriptorType.equals(Constants.DRAGONNOH)) {
            ReadDescriptors.readDragonXDescriptors(sdfFile + ".dragonNoH", descriptorNames, descriptorValueMatrix);
        } else if (descriptorType.equals(Constants.MOE2D)) {
            ReadDescriptors.readMoe2DDescriptors(sdfFile + ".moe2D", descriptorNames, descriptorValueMatrix);
        } else if (descriptorType.equals(Constants.MACCS)) {
            ReadDescriptors.readMaccsDescriptors(sdfFile + ".maccs", descriptorNames, descriptorValueMatrix);
        } else if (descriptorType.equals(Constants.ISIDA)) {
            ReadDescriptors.readIsidaDescriptors(sdfFile + ".ISIDA", descriptorNames, descriptorValueMatrix);
        } else if (descriptorType.equals(Constants.DRAGON7)) {
            ReadDescriptors.readDragon7Descriptors(sdfFile + ".dragon7", descriptorNames, descriptorValueMatrix);
        } else {
            throw new RuntimeException("Bad descriptor type: " + descriptorType);
        }
    }

    /* append nearest neighbors for a single compound vs. a set of compounds, within this descriptor type*/
    private void nearestNeighbors(int n, List<Double> compound, List<Descriptors> modelingCompounds, Dataset modelingDataset, List<Neighbor> neighborList) throws Exception {

        List<String> actValues = DatasetFileOperations.getActFileValues(modelingDataset);

        PriorityQueue<Neighbor> queue = new PriorityQueue(n); //least similar at top
        for (int i=0; i<modelingCompounds.size(); i++){
            List<Double> c = modelingCompounds.get(i).getDescriptorValues();
            if (queue.size() < n){
                queue.add(new Neighbor(similarity(compound, c), Integer.parseInt(actValues.get(i))));
                continue;
            }
            // if this compound is more similar than the least similar compound in the queue
            if (similarity(compound, c) > queue.peek().similarity){
                queue.poll();
                queue.add(new Neighbor(similarity(compound, c), Integer.parseInt(actValues.get(i))));
            }
        }

        while (!queue.isEmpty()){
            neighborList.add(queue.poll());
        }
        return;
    }

    private class Neighbor implements Comparable<Neighbor>{
        double similarity;
        int activity;   //0 or 1, in ACT file

        public Neighbor(double similarity, int activity) {
            this.similarity = similarity;
            this.activity=activity;
        }

        @Override
        public int compareTo(Neighbor o) {
            return ((Double)this.similarity).compareTo(o.similarity);
        }
    }

    /**
     * Evaluates the continuous Tanimoto coefficient for two real valued vectors.
     */
    private static double similarity(List<Double> features1, List<Double> features2) {
        // S = sum( ab / (a^2 + b^2 - ab) )

        if (features1.size() != features2.size()) {
            throw new IllegalArgumentException("Features vectors must be of the same length");
        }

        double ab = 0, a2 = 0, b2 = 0;

        for (int i = 0; i < features1.size(); i++) {
            ab += features1.get(i) * features2.get(i);
            a2 += features1.get(i) * features1.get(i);
            b2 += features2.get(i) * features2.get(i);
        }
        return ab/(a2+b2-ab);
    }

    public String loadMakePredictionsPage() throws Exception {
        this.loadSelectPredictorPage();
        String result = SUCCESS;

        // get list of predictor IDs from the checked checkboxes
        if (predictorCheckBoxes == null || predictorCheckBoxes.trim().isEmpty()) {
            logger.debug("no predictor chosen!");
            addActionError("Please select at least one predictor.");
            result = ERROR;
            return result;
        }
        selectedPredictorIds = predictorCheckBoxes.replaceAll(",", " ");
        String[] predictorIds = selectedPredictorIds.split("\\s+");

        isUploadedDescriptors = false;
        isMixDescriptors = false;
        singleCompoundPredictionAllowed = true;
        HashSet<String> predictorsModelDescriptors = new HashSet<String>();

        for (int i = 0; i < predictorIds.length; i++) {
            Predictor p = predictorRepository.findOne(Long.parseLong(predictorIds[i]));

            if (p.getChildType() != null && p.getChildType().equals(Constants.NFOLD)) {
                /* check if *any* child predictor has models */
                String[] childIds = p.getChildIds().split("\\s+");
                boolean childHasModels = false;
                for (String pChildId : childIds) {
                    Predictor pChild = predictorRepository.findOne(Long.parseLong(pChildId));
                    if (pChild.getNumTestModels() > 0) {
                        childHasModels = true;
                    }
                }
                if (!childHasModels) {
                    addActionError("The predictor '" + p.getName() + "' cannot be used for prediction"
                            + " because it contains no usable models.");
                    result = ERROR;
                }
            } else {
                if (p.getNumTestModels() == 0) {
                    /*
                     * this predictor shouldn't be used for prediction. Error
                     * out.
                     */
                    addActionError("The predictor '" + p.getName() + "' cannot be used for prediction because"
                            + " it contains no usable models.");
                    logger.warn("The predictor '" + p.getName() + "' cannot be used for prediction because"
                            + " it contains no usable models.");
                    result = ERROR;
                } else {
                    logger.debug("predictor " + p.getName() + " is fine, it has " + p.getNumTotalModels());
                }
            }
            /* adding modeling_methods for each of the selected predictors */
            if (!p.getModelMethod().trim().isEmpty()) {
                predictorsModelDescriptors.addAll(Arrays.asList(p.getDescriptorGeneration().trim().split(" ")));
            }
            selectedPredictors.add(p);
            if (p.getDescriptorGeneration().equals(Constants.UPLOADED)) {
                isUploadedDescriptors = true;
                singleCompoundPredictionAllowed = false;
            }

            if (p.getDescriptorGeneration().equals(Constants.DRAGONH) ||
                    p.getDescriptorGeneration().equals(Constants.DRAGONNOH) ||
                    p.getDescriptorGeneration().equals(Constants.MOE2D) ||
                    p.getDescriptorGeneration().equals(Constants.MACCS) ||
                    p.getDescriptorGeneration().equals(Constants.ISIDA) ||
                    p.getDescriptorGeneration().equals(Constants.CDK)) {
                isMixDescriptors = true;
            }
        }

        isMixDescriptors = isMixDescriptors && isUploadedDescriptors;

        if (result.equals(ERROR)) {
            return result;
        }

        userDatasets = datasetRepository.findByUserName(user.getUserName());
        if (user.getShowPublicDatasets().equals(Constants.ALL)) {
            userDatasets.addAll(datasetRepository.findAllPublicDatasets());
        } else if (user.getShowPublicDatasets().equals(Constants.SOME)) {
            userDatasets.addAll(datasetRepository.findSomePublicDatasets());
        }

        /*
         * set up any values that need to be populated onto the page
         * (dropdowns, lists, display stuff)
         */
        userDatasetNames = Lists.transform(userDatasets, Utility.NAME_TRANSFORM);
        userPredictorNames = Lists.transform(userPredictors, Utility.NAME_TRANSFORM);
        userPredictionNames =
                Lists.transform(predictionRepository.findByUserName(user.getUserName()), Utility.NAME_TRANSFORM);
        userTaskNames = Lists.transform(jobRepository.findByUserName(user.getUserName()), Utility.NAME_TRANSFORM);

        /*
         * filtering userDatasets leaving only datasets that has same modeling
         * method as predictor
         */
        List<Dataset> new_ds = new ArrayList<>();
        for (Dataset ds : userDatasets) {
            /*
             * looking for arrays intersection if found then the Dataset is
             * added to the list
             */
            boolean datasetRemovable = false;
            List<String> dscrptrLst1 = Lists.newArrayList(predictorsModelDescriptors);
            List<String> dscrptrLst2 = Arrays.asList(ds.getAvailableDescriptors().trim().split(" "));

            // Find intersection, get iterator, then explicitly cast each
            // element of intersection to a string (type safety issues)
            List<String> dscrptrIntsct = new ArrayList<>();
            Iterator<?> tempIterator = ListUtils.intersection(dscrptrLst1, dscrptrLst2).iterator();
            while (tempIterator.hasNext()) {
                dscrptrIntsct.add((String) tempIterator.next());
            }

            if (!ds.getAvailableDescriptors().trim().isEmpty() && !new_ds.contains(ds) && (dscrptrIntsct.size()
                    == predictorsModelDescriptors.size())) {
                for (int j = 0; j < selectedPredictors.size(); j++) {
                    String selectedPredictorsDescriptorType = selectedPredictors.get(j).getDescriptorGeneration();
                    if (!dscrptrLst2.contains(selectedPredictorsDescriptorType)) {
                        datasetRemovable = true;
                    }
                }
                if (!datasetRemovable) {
                    new_ds.add(ds);
                }
            }
        }
        userDatasets.clear();
        userDatasets.addAll(new_ds);

        if (isUploadedDescriptors) {
            userDatasets.clear();
            boolean hasMultiUploadedDescriptors = false;
            String descriptorTypeTest = null;
            int times = 0;
            for (Predictor prdctr : selectedPredictors) {
                if (times == 0) {
                    descriptorTypeTest = prdctr.getUploadedDescriptorType();
                    times = 1;
                }

                if (descriptorTypeTest != null && prdctr.getUploadedDescriptorType() != null) {
                    if (!prdctr.getUploadedDescriptorType().equals(descriptorTypeTest)) {
                        hasMultiUploadedDescriptors = true;
                    }
                } else if (descriptorTypeTest == null && prdctr.getUploadedDescriptorType() == null) {
                    hasMultiUploadedDescriptors = false;
                } else {
                    hasMultiUploadedDescriptors = true;
                    break;
                }

            }
            if (!hasMultiUploadedDescriptors) {
                List<Dataset> datasets = datasetRepository.findByUserName(user.getUserName());
                datasets.addAll(datasetRepository.findAllPublicDatasets());
                for (Iterator<Dataset> iterator = datasets.iterator(); iterator.hasNext(); ) {
                    Dataset d = iterator.next();
                    if (Strings.isNullOrEmpty(descriptorTypeTest)) {
                        if (!Strings.isNullOrEmpty(d.getUploadedDescriptorType()) || !d.getAvailableDescriptors()
                                .contains(Constants.UPLOADED)) {
                            iterator.remove();
                        }
                    } else {
                        if (!d.getUploadedDescriptorType().equals(descriptorTypeTest)) {
                            iterator.remove();
                        }
                    }
                }
                for (Dataset ds : datasets) {
                    if (!userDatasets.contains(ds)) {
                        userDatasets.add(ds);
                    }
                }
            }
        }

        if (isMixDescriptors) {
            userDatasets.clear();
        }
        return result;
    }

    public String makeDatasetPrediction() throws Exception {
        /*
         * prediction form submitted, so create a new prediction task and run it
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

        QsarPredictionTask predTask = new QsarPredictionTask(user.getUserName(), jobName, sdf, cutOff, selectedPredictorIds, predictionDataset);
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

//        CentralDogma centralDogma = CentralDogma.getInstance();
//        String emailOnCompletion = "false";
//        centralDogma.addJobToIncomingList(user.getUserName(), jobName, predTask, numCompounds, numModels, emailOnCompletion);

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

    public List<PredictionAction.SmilesPrediction> getSmilesPredictions() {
        return smilesPredictions;
    }

    public void setSmilesPredictions(List<PredictionAction.SmilesPrediction> smilesPredictions) {
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
/*
    public class SmilesPrediction {
        // used by makeSmilesPrediction()
        String predictedValue;
        String stdDeviation;
        String zScore;
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

        public String getZScore() {
            return zScore;
        }

        public void setZScore(String zScore) {
            this.zScore = zScore;
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
    */
}
