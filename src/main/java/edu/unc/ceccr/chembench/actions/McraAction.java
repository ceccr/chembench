package edu.unc.ceccr.chembench.actions;

import com.google.common.collect.Sets;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.*;
import edu.unc.ceccr.chembench.workflows.datasets.DatasetFileOperations;
import edu.unc.ceccr.chembench.workflows.descriptors.GenerateDescriptors;
import edu.unc.ceccr.chembench.workflows.descriptors.ReadDescriptors;
import edu.unc.ceccr.chembench.workflows.modelingPrediction.RunSmilesPrediction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private String smiles;
    private String cutoff;
    // populated by the JSP form for the dataset prediction
    private Long selectedModelingDatasetId;
    private String cutOff = "0.5";
    private String jobName;
    private Long selectedPredictingDatasetId;

    private static Set<String> descriptorTypes = Sets.newHashSet();
    static {
        descriptorTypes.add(Constants.ISIDA);
        descriptorTypes.add(Constants.MACCS);
        descriptorTypes.add(Constants.MOE2D);
        descriptorTypes.add(Constants.DRAGONH);
    }
    private final static int N_NEAREST_NEIGHORS = 1;
    private List<McraPrediction> mcraPredictions;


    @Autowired
    public McraAction(DatasetRepository datasetRepository, PredictorRepository predictorRepository,
                      PredictionRepository predictionRepository, JobRepository jobRepository) {
        this.datasetRepository = datasetRepository;
        this.predictorRepository = predictorRepository;
        this.predictionRepository = predictionRepository;
        this.jobRepository = jobRepository;
    }

//    public String loadSelectPredictorPage() throws Exception {
//        userPredictors = predictorRepository.findByUserName(user.getUserName());
//        if (user.getShowPublicPredictors().equals(Constants.ALL)) {
//            userPredictors.addAll(predictorRepository.findPublicPredictors());
//        }
//        for (Predictor p : userPredictors) {
//            Dataset d = datasetRepository.findOne(p.getDatasetId());
//            p.setDatasetDisplay(d.getName());
//        }
//        return SUCCESS;
//    }


    public String makeDatasetPrediction() throws Exception {

        User user = User.getCurrentUser();
        logger.debug(user.getUserName());
        logger.debug("predids: " + selectedPredictingDatasetId);

        // get modeling Dataset
        Dataset modelingDataset = datasetRepository.findOne(selectedModelingDatasetId);

        // get path to SDF file of the predicting dataset
        Dataset predictingDataset = datasetRepository.findOne(selectedPredictingDatasetId);
        String directoryPath = predictingDataset.getDirectoryPath()+"/";
        String sdfFileName = predictingDataset.getSdfFile();

        // compute and store predictions
        computePredictionsFromSdfs(directoryPath, sdfFileName, modelingDataset, false);

        logger.info("making prediction run on dataset " + predictingDataset.getName() + " with predictors "
                + selectedPredictingDatasetId + " for " + user.getUserName());
        return SUCCESS;
    }

    public String makeSmilesPrediction() throws Exception {
        ActionContext context = ActionContext.getContext();
        User user = User.getCurrentUser();

       // Long predictorIds = Long.parseLong((String)context.getParameters().get("selectedModelingDatasetId"));
        Long predictorIds = selectedModelingDatasetId;
        logger.debug(" 1: " + smiles + " 2: " + cutoff + " 3: " + predictorIds);
        logger.debug(user.getUserName());
        logger.debug("SMILES predids: " + predictorIds);
        Dataset modelingDataset = datasetRepository.findOne(predictorIds);

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
        GenerateDescriptors.generateIsidaDescriptors(smilesDir+"smiles.sdf", smilesDir + "smiles.sdf.ISIDA");
        logger.info("Generated descriptors for SDF: " + descriptorTypes.toString());

        computePredictionsFromSdfs(smilesDir, "smiles.sdf", modelingDataset, true);

        logger.info("made SMILES prediction on string " + smiles + " with predictors " + predictorIds + " for " + user.getUserName());
        return SUCCESS;
    }

    /* takes full path name for the .sdf of the dataset/compound to predict */
    private void computePredictionsFromSdfs(String predictingDir, String predictingSdfName, Dataset modelingDataset, boolean isSmiles) throws Exception {

    String predictingSdf = isSmiles ?
            predictingDir + predictingSdfName :
            predictingDir + "Descriptors/" + predictingSdfName;

        List<List<DescriptorResult>> allDescriptorResults = new ArrayList<>(); //list of each descriptor type's sub-results for each compound
        List<Double> predictedValues = predictActivity( predictingSdf, modelingDataset, allDescriptorResults);
        //logger.info("Predicted value for SDF: " + predictedValues.get(0).toString());

        /* stores results for display */
        mcraPredictions = new ArrayList<>();

        List<String> compoundNames=null;
        if (!isSmiles){
            compoundNames = DatasetFileOperations.getSdfCompoundNames(predictingDir+predictingSdfName);
        }

        for (int i=0; i<predictedValues.size(); i++) {
            List<DescriptorResult> results = new ArrayList<>();
            for (List<DescriptorResult> descriptor : allDescriptorResults){    //every compound's result for one descriptor
                results.add(descriptor.get(i));
            }

            String compoundName = isSmiles ? smiles : compoundNames.get(i);
            McraPrediction prediction = new McraPrediction(N_NEAREST_NEIGHORS, predictedValues.get(i), compoundName, results);
            mcraPredictions.add(prediction);
        }

    }

    private String getDescriptorsPath(Dataset dataset){
        return dataset.getDirectoryPath()+"/Descriptors/"+dataset.getSdfFile();
    }

    /* returns the predicted activity of each compound in the predicting sdf, in the order of the sdf*/
    private List<Double> predictActivity(String predictingSdf, Dataset modelingDataset, List<List<DescriptorResult>> allDescriptorResults) throws Exception {
        List<String> descriptorNames = new ArrayList<>();   //unused, but need to pass to readDescriptors()

        String modelingSdf = getDescriptorsPath(modelingDataset);

        List<Neighbor>[] compoundNeighborLists = null;  //maps the index of a compound in the predicting sdf to a list of nearest neighbors

        for (String descriptorType : descriptorTypes) {

            List<Descriptors> modelingDescriptors = new ArrayList<>();
            List<Descriptors> predictingDescriptors = new ArrayList<>();

            // read Descriptor lists
            if (descriptorType.equals(Constants.ISIDA)){
                ReadDescriptors.readJoinedIsidaDescriptors(predictingSdf+".ISIDA", modelingSdf+".ISIDA", predictingDescriptors, modelingDescriptors);
            } else {
                ReadDescriptors.readDescriptors(descriptorType, modelingSdf, descriptorNames, modelingDescriptors);
                ReadDescriptors.readDescriptors(descriptorType, predictingSdf, descriptorNames, predictingDescriptors);
            }

            // if this is the first descriptor type being processed
            if (compoundNeighborLists == null){
                compoundNeighborLists = new List[predictingDescriptors.size()];     //size would be 1 for SMILES
            }

            List<DescriptorResult> singleDescriptorResults = new ArrayList(); //the results of every compound for one descriptor type

            /* for each compound in the predicting set, find the nearest neighbors for this descriptor only */
            for (int i=0; i<predictingDescriptors.size(); i++) {
                if (compoundNeighborLists[i] == null){
                    compoundNeighborLists[i] = new ArrayList<Neighbor>();
                }
                DescriptorResult descriptorResult = nearestNeighbors(N_NEAREST_NEIGHORS, predictingDescriptors.get(i).getDescriptorValues(),
                        modelingDescriptors, modelingDataset, compoundNeighborLists[i]);
                descriptorResult.name = descriptorType;
                singleDescriptorResults.add(descriptorResult);
            }

            allDescriptorResults.add(singleDescriptorResults);
        }

        return weightedAverage(compoundNeighborLists);
    }

    /* Return the final predicted value for each compound in the predicting .sdf */
    private List<Double> weightedAverage(List<Neighbor>[] compoundNeighborLists){
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


    /* append nearest neighbors for a single compound vs. a set of compounds, within this descriptor type*/
    private DescriptorResult nearestNeighbors(int n, List<Double> compound, List<Descriptors> modelingCompounds,
                                  Dataset modelingDataset, List<Neighbor> neighborList) throws Exception {

        List<String> actValues = DatasetFileOperations.getActFileValues(modelingDataset);

        PriorityQueue<Neighbor> queue = new PriorityQueue(n); //least similar at top
        for (int i=0; i<modelingCompounds.size(); i++){
            List<Double> c = modelingCompounds.get(i).getDescriptorValues();
            if (queue.size() < n){
                //queue.add(new Neighbor(similarity(compound, c), Integer.parseInt(actValues.get(i))));
                queue.add(new Neighbor(similarity(compound, c), Double.parseDouble(actValues.get(i))));
                continue;
            }
            // if this compound is more similar than the least similar compound in the queue
            if (similarity(compound, c) > queue.peek().similarity){
                queue.poll();
                queue.add(new Neighbor(similarity(compound, c), Double.parseDouble(actValues.get(i))));
            }
        }

        float totalSimilarity=0, totalActivity=0;
        while (!queue.isEmpty()){
            Neighbor neighbor = queue.poll();
            totalActivity += neighbor.activity;
            totalSimilarity += neighbor.similarity;
            neighborList.add(neighbor);
        }

        return new DescriptorResult(totalSimilarity/n, totalActivity/n);
    }

    private class Neighbor implements Comparable<Neighbor>{
        double similarity;
        double activity;   //0 or 1, in ACT file

        public Neighbor(double similarity, double activity) {
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


//    public String loadMakePredictionsPage() throws Exception {
//        this.loadSelectPredictorPage();
//        String result = SUCCESS;
//
//        // get list of predictor IDs from the checked checkboxes
//        if (predictorCheckBoxes == null || predictorCheckBoxes.trim().isEmpty()) {
//            logger.debug("no predictor chosen!");
//            addActionError("Please select at least one predictor.");
//            result = ERROR;
//            return result;
//        }
//        selectedPredictorIds = predictorCheckBoxes.replaceAll(",", " ");
//        String[] predictorIds = selectedPredictorIds.split("\\s+");
//
//        isUploadedDescriptors = false;
//        isMixDescriptors = false;
//        singleCompoundPredictionAllowed = true;
//        HashSet<String> predictorsModelDescriptors = new HashSet<String>();
//
//        for (int i = 0; i < predictorIds.length; i++) {
//            Predictor p = predictorRepository.findOne(Long.parseLong(predictorIds[i]));
//
//            if (p.getChildType() != null && p.getChildType().equals(Constants.NFOLD)) {
//                /* check if *any* child predictor has models */
//                String[] childIds = p.getChildIds().split("\\s+");
//                boolean childHasModels = false;
//                for (String pChildId : childIds) {
//                    Predictor pChild = predictorRepository.findOne(Long.parseLong(pChildId));
//                    if (pChild.getNumTestModels() > 0) {
//                        childHasModels = true;
//                    }
//                }
//                if (!childHasModels) {
//                    addActionError("The predictor '" + p.getName() + "' cannot be used for prediction"
//                            + " because it contains no usable models.");
//                    result = ERROR;
//                }
//            } else {
//                if (p.getNumTestModels() == 0) {
//                    /*
//                     * this predictor shouldn't be used for prediction. Error
//                     * out.
//                     */
//                    addActionError("The predictor '" + p.getName() + "' cannot be used for prediction because"
//                            + " it contains no usable models.");
//                    logger.warn("The predictor '" + p.getName() + "' cannot be used for prediction because"
//                            + " it contains no usable models.");
//                    result = ERROR;
//                } else {
//                    logger.debug("predictor " + p.getName() + " is fine, it has " + p.getNumTotalModels());
//                }
//            }
//            /* adding modeling_methods for each of the selected predictors */
//            if (!p.getModelMethod().trim().isEmpty()) {
//                predictorsModelDescriptors.addAll(Arrays.asList(p.getDescriptorGeneration().trim().split(" ")));
//            }
//            selectedPredictors.add(p);
//            if (p.getDescriptorGeneration().equals(Constants.UPLOADED)) {
//                isUploadedDescriptors = true;
//                singleCompoundPredictionAllowed = false;
//            }
//
//            if (p.getDescriptorGeneration().equals(Constants.DRAGONH) ||
//                    p.getDescriptorGeneration().equals(Constants.DRAGONNOH) ||
//                    p.getDescriptorGeneration().equals(Constants.MOE2D) ||
//                    p.getDescriptorGeneration().equals(Constants.MACCS) ||
//                    p.getDescriptorGeneration().equals(Constants.ISIDA) ||
//                    p.getDescriptorGeneration().equals(Constants.CDK)) {
//                isMixDescriptors = true;
//            }
//        }
//
//        isMixDescriptors = isMixDescriptors && isUploadedDescriptors;
//
//        if (result.equals(ERROR)) {
//            return result;
//        }
//
//        userDatasets = datasetRepository.findByUserName(user.getUserName());
//        if (user.getShowPublicDatasets().equals(Constants.ALL)) {
//            userDatasets.addAll(datasetRepository.findAllPublicDatasets());
//        } else if (user.getShowPublicDatasets().equals(Constants.SOME)) {
//            userDatasets.addAll(datasetRepository.findSomePublicDatasets());
//        }
//
//        /*
//         * set up any values that need to be populated onto the page
//         * (dropdowns, lists, display stuff)
//         */
//        userDatasetNames = Lists.transform(userDatasets, Utility.NAME_TRANSFORM);
//        userPredictorNames = Lists.transform(userPredictors, Utility.NAME_TRANSFORM);
//        userPredictionNames =
//                Lists.transform(predictionRepository.findByUserName(user.getUserName()), Utility.NAME_TRANSFORM);
//        userTaskNames = Lists.transform(jobRepository.findByUserName(user.getUserName()), Utility.NAME_TRANSFORM);
//
//        /*
//         * filtering userDatasets leaving only datasets that has same modeling
//         * method as predictor
//         */
//        List<Dataset> new_ds = new ArrayList<>();
//        for (Dataset ds : userDatasets) {
//            /*
//             * looking for arrays intersection if found then the Dataset is
//             * added to the list
//             */
//            boolean datasetRemovable = false;
//            List<String> dscrptrLst1 = Lists.newArrayList(predictorsModelDescriptors);
//            List<String> dscrptrLst2 = Arrays.asList(ds.getAvailableDescriptors().trim().split(" "));
//
//            // Find intersection, get iterator, then explicitly cast each
//            // element of intersection to a string (type safety issues)
//            List<String> dscrptrIntsct = new ArrayList<>();
//            Iterator<?> tempIterator = ListUtils.intersection(dscrptrLst1, dscrptrLst2).iterator();
//            while (tempIterator.hasNext()) {
//                dscrptrIntsct.add((String) tempIterator.next());
//            }
//
//            if (!ds.getAvailableDescriptors().trim().isEmpty() && !new_ds.contains(ds) && (dscrptrIntsct.size()
//                    == predictorsModelDescriptors.size())) {
//                for (int j = 0; j < selectedPredictors.size(); j++) {
//                    String selectedPredictorsDescriptorType = selectedPredictors.get(j).getDescriptorGeneration();
//                    if (!dscrptrLst2.contains(selectedPredictorsDescriptorType)) {
//                        datasetRemovable = true;
//                    }
//                }
//                if (!datasetRemovable) {
//                    new_ds.add(ds);
//                }
//            }
//        }
//        userDatasets.clear();
//        userDatasets.addAll(new_ds);
//
//        if (isUploadedDescriptors) {
//            userDatasets.clear();
//            boolean hasMultiUploadedDescriptors = false;
//            String descriptorTypeTest = null;
//            int times = 0;
//            for (Predictor prdctr : selectedPredictors) {
//                if (times == 0) {
//                    descriptorTypeTest = prdctr.getUploadedDescriptorType();
//                    times = 1;
//                }
//
//                if (descriptorTypeTest != null && prdctr.getUploadedDescriptorType() != null) {
//                    if (!prdctr.getUploadedDescriptorType().equals(descriptorTypeTest)) {
//                        hasMultiUploadedDescriptors = true;
//                    }
//                } else if (descriptorTypeTest == null && prdctr.getUploadedDescriptorType() == null) {
//                    hasMultiUploadedDescriptors = false;
//                } else {
//                    hasMultiUploadedDescriptors = true;
//                    break;
//                }
//
//            }
//            if (!hasMultiUploadedDescriptors) {
//                List<Dataset> datasets = datasetRepository.findByUserName(user.getUserName());
//                datasets.addAll(datasetRepository.findAllPublicDatasets());
//                for (Iterator<Dataset> iterator = datasets.iterator(); iterator.hasNext(); ) {
//                    Dataset d = iterator.next();
//                    if (Strings.isNullOrEmpty(descriptorTypeTest)) {
//                        if (!Strings.isNullOrEmpty(d.getUploadedDescriptorType()) || !d.getAvailableDescriptors()
//                                .contains(Constants.UPLOADED)) {
//                            iterator.remove();
//                        }
//                    } else {
//                        if (!d.getUploadedDescriptorType().equals(descriptorTypeTest)) {
//                            iterator.remove();
//                        }
//                    }
//                }
//                for (Dataset ds : datasets) {
//                    if (!userDatasets.contains(ds)) {
//                        userDatasets.add(ds);
//                    }
//                }
//            }
//        }
//
//        if (isMixDescriptors) {
//            userDatasets.clear();
//        }
//        return result;
//    }


    public String execute() throws Exception {
        return SUCCESS;
    }

    /* GETTERS AND SETTERS */

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

    public Long getSelectedModelingDatasetId() {
        return selectedModelingDatasetId;
    }

    public void setSelectedModelingDatasetId(Long selectedModelingDatasetId) {
        this.selectedModelingDatasetId = selectedModelingDatasetId;
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

    public Long getSelectedPredictingDatasetId() {
        return selectedPredictingDatasetId;
    }

    public void setSelectedPredictingDatasetId(Long selectedPredictingDatasetId) {
        this.selectedPredictingDatasetId = selectedPredictingDatasetId;
    }

    public List<McraPrediction> getMcraPredictions() {
        return mcraPredictions;
    }

    public void setMcraPredictions(List<McraPrediction> mcraPredictions) {
        this.mcraPredictions = mcraPredictions;
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


    public class McraPrediction {
        private int numNearestNeighbors;
        private double predictedActivity;
        private String name;    //SMILES or compound name
        private List<DescriptorResult> descriptors;

        public McraPrediction(int numNearestNeighbors, double predictedActivity, String name, List<DescriptorResult> descriptors) {
            this.numNearestNeighbors = numNearestNeighbors;
            this.predictedActivity = predictedActivity;
            this.name = name;
            this.descriptors = descriptors;
        }

        public int getNumNearestNeighbors() {
            return numNearestNeighbors;
        }

        public double getPredictedActivity() {
            return predictedActivity;
        }

        public String getName() {
            return name;
        }

        public List<DescriptorResult> getDescriptors() {
            return descriptors;
        }
    }

    public class DescriptorResult{

        public DescriptorResult(float averageSimilarity, float averageActivity) {
            this.averageSimilarity = averageSimilarity;
            this.averageActivity = averageActivity;
        }

        public String getName() {
            return name;
        }

        public float getAverageSimilarity() {
            return averageSimilarity;
        }

        public float getAverageActivity() {
            return averageActivity;
        }

        private String name;
        private float averageSimilarity;
        private float averageActivity;
    }

}
