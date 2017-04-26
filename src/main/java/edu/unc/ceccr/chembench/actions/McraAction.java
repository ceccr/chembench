package edu.unc.ceccr.chembench.actions;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.*;
import edu.unc.ceccr.chembench.workflows.datasets.DatasetFileOperations;
import edu.unc.ceccr.chembench.workflows.descriptors.GenerateDescriptors;
import edu.unc.ceccr.chembench.workflows.descriptors.ReadDescriptors;
import edu.unc.ceccr.chembench.workflows.download.WriteCsv;
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
    // variables used for JSP display
    private User user = User.getCurrentUser();

    // populated by the JSP form
    private String smiles;
    private Long selectedModelingDatasetId;
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
    public static final String JOB_TYPE ="MCRAPREDICTION";    //for passing to FileServlet. If "static", can't be accessed in JSP

    // result
    private List<McraPrediction> mcraPredictions;
    private String downloadPath;    //full path to the CSV of the results

    @Autowired
    public McraAction(DatasetRepository datasetRepository) {
        this.datasetRepository = datasetRepository;
    }

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

        // prep the file for possible download
        downloadPath = WriteCsv.writePredictionValuesAsCSV(mcraPredictions, user.getUserName(), modelingDataset.getName(), predictingDataset, jobName);

        logger.info("making prediction run on dataset " + predictingDataset.getName() + " with modeling dataset "
                + selectedModelingDatasetId + " for " + user.getUserName());
        return SUCCESS;
    }

    public String makeSmilesPrediction() throws Exception {
        User user = User.getCurrentUser();

       // Long predictorIds = Long.parseLong((String)context.getParameters().get("selectedModelingDatasetId"));
        Long predictorIds = selectedModelingDatasetId;
        logger.debug(" 1: " + smiles + " 2: " + predictorIds);
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

        String predictingSdf = isSmiles ? predictingDir + predictingSdfName :
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

        boolean binary = modelingDataset.getModelType().equals(Constants.CATEGORY);

        for (int i=0; i<predictedValues.size(); i++) {
            List<DescriptorResult> results = new ArrayList<>();
            for (List<DescriptorResult> descriptor : allDescriptorResults){    //every compound's result for one descriptor
                results.add(descriptor.get(i));
            }

            String compoundName = isSmiles ? smiles : compoundNames.get(i);
            McraPrediction prediction = new McraPrediction(N_NEAREST_NEIGHORS, predictedValues.get(i), compoundName, results, binary);
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
                    compoundNeighborLists[i] = new ArrayList<>();
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

        // can move these to being read before the nearestNeighbors call
        List<String> actValues = DatasetFileOperations.getActFileValues(modelingDataset);
        String modelingSdf = modelingDataset.getDirectoryPath() + "/" + modelingDataset.getSdfFile();
        List<String> modelingCompoundNames = DatasetFileOperations.getSdfCompoundNames(modelingSdf);

        PriorityQueue<Neighbor> queue = new PriorityQueue(n); //least similar at top
        for (int i=0; i<modelingCompounds.size(); i++){
            List<Double> c = modelingCompounds.get(i).getDescriptorValues();
            if (queue.size() < n){
                queue.add(new Neighbor(similarity(compound, c), Double.parseDouble(actValues.get(i)), modelingCompoundNames.get(i)));
                continue;
            }
            // if this compound is more similar than the least similar compound in the queue
            if (similarity(compound, c) > queue.peek().similarity){
                queue.poll();
                queue.add(new Neighbor(similarity(compound, c), Double.parseDouble(actValues.get(i)), modelingCompoundNames.get(i)));
            }
        }

        float totalSimilarity=0, totalActivity=0;
        String names = null;
        while (!queue.isEmpty()){
            Neighbor neighbor = queue.poll();
            totalActivity += neighbor.activity;
            totalSimilarity += neighbor.similarity;
            // build a comma-separated list of names in reverse order (end with most similar first)
            names = names==null ? neighbor.name : neighbor.name+ ", " + names;
            neighborList.add(neighbor);
        }

        return new DescriptorResult(totalSimilarity/n, totalActivity/n, names);
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

    public Long getSelectedModelingDatasetId() {
        return selectedModelingDatasetId;
    }

    public void setSelectedModelingDatasetId(Long selectedModelingDatasetId) {
        this.selectedModelingDatasetId = selectedModelingDatasetId;
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

    public static String getJobType() {
        return JOB_TYPE;
    }

    public static Set<String> getDescriptorTypes() {
        return descriptorTypes;
    }

    public String getDownloadPath() {
        return downloadPath;
    }


    /* INNER CLASSES */

    private class Neighbor implements Comparable<Neighbor>{
        private String name;
        private double similarity;
        private double activity;

        public Neighbor(double similarity, double activity, String name) {
            this.similarity = similarity;
            this.activity=activity;
            this.name = name;
        }

        @Override
        public int compareTo(Neighbor o) {
            return ((Double)this.similarity).compareTo(o.similarity);
        }
    }

    public class McraPrediction {
        private int numNearestNeighbors;
        private double predictedActivity;
        private int roundedPredictedActivity = -1;
        private String name;    //SMILES or compound name
        private List<DescriptorResult> descriptors;

        public McraPrediction(int numNearestNeighbors, double predictedActivity,
                              String name, List<DescriptorResult> descriptors, boolean binary) {
            this.numNearestNeighbors = numNearestNeighbors;
            this.predictedActivity = predictedActivity;
            if (binary) this.roundedPredictedActivity = predictedActivity>=.5 ? 1 : 0;
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

        public int getRoundedPredictedActivity() {
            return roundedPredictedActivity;
        }
    }

    public class DescriptorResult{

        private String name;
        private float averageSimilarity;
        private float averageActivity;
        private String neighborIds; //comma-separated list of IDs of neighbors, most similar first

        public DescriptorResult(float averageSimilarity, float averageActivity, String neighborIds) {
            this.averageSimilarity = averageSimilarity;
            this.averageActivity = averageActivity;
            this.neighborIds = neighborIds;
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

        public String getNeighborIds() {
            return neighborIds;
        }

    }

}
