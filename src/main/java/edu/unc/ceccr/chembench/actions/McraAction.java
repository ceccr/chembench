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
    private User user = User.getCurrentUser();

    // populated by the JSP form
    private String smiles;  //for predicting a single compound
    private Long selectedPredictingDatasetId;   //for predicting a dataset
    private Long selectedModelingDatasetId;
    private String jobName;

    private static Set<String> descriptorTypes = Sets.newHashSet();
    static {
        descriptorTypes.add(Constants.ISIDA);
        descriptorTypes.add(Constants.MACCS);
        descriptorTypes.add(Constants.MOE2D);
        descriptorTypes.add(Constants.DRAGONH);
    }
    private final static int N_NEAREST_NEIGHORS = 1;
    public static final String JOB_TYPE ="MCRAPREDICTION";    //for passing to FileServlet

    //result
    private List<McraPrediction> mcraPredictions;   //used in mcraDatasetResults.jsp
    private String downloadPath;    //full path to the CSV of the results

    @Autowired
    public McraAction(DatasetRepository datasetRepository) {
        this.datasetRepository = datasetRepository;
    }

    // the action for predicting a dataset
    public String makeDatasetPrediction() throws Exception {
        User user = User.getCurrentUser();
        logger.debug(user.getUserName());
        logger.debug("predids: " + selectedPredictingDatasetId);

        // get modeling Dataset
        Dataset modelingDataset = datasetRepository.findOne(selectedModelingDatasetId);

        // get location of the SDF file of the predicting dataset
        Dataset predictingDataset = datasetRepository.findOne(selectedPredictingDatasetId);
        String directoryPath = predictingDataset.getDirectoryPath()+"/";
        String sdfFileName = predictingDataset.getSdfFile();

        // compute and store predictions
        computePredictionsFromSdfs(directoryPath, sdfFileName, modelingDataset, false);

        // write the results to a file for possible download
        downloadPath = WriteCsv.writePredictionValuesAsCSV(mcraPredictions, user.getUserName(), modelingDataset.getName(), predictingDataset, jobName);

        logger.info("making prediction run on dataset " + predictingDataset.getName() + " with modeling dataset "
                + selectedModelingDatasetId + " for " + user.getUserName());
        return SUCCESS;
    }

    // the action for predicting a single compound
    public String makeSmilesPrediction() throws Exception {
        //User user = User.getCurrentUser();
        logger.debug(" 1: " + smiles + " 2: " + selectedModelingDatasetId);
        logger.debug(user.getUserName());

        // get the modeling dataset
        Dataset modelingDataset = datasetRepository.findOne(selectedModelingDatasetId);

        // make the folder for this SMILES prediction
        Path baseSmilesDir = Paths.get(Constants.CECCR_USER_BASE_PATH, user.getUserName(), "SMILES");
        Files.createDirectories(baseSmilesDir);
        Path predPath = Files.createTempDirectory(baseSmilesDir, modelingDataset.getName());
        String smilesDir = predPath.toString() + "/";
        logger.debug("Created the directory " + smilesDir);

        // generate an SDF from this SMILES string
        RunSmilesPrediction.smilesToSdf(smiles, smilesDir);
        logger.info(String.format("Generated SDF file from SMILES \"%s\" written to %s", smiles, smilesDir));

        // generate descriptors for the SMILES string using the given SDF file
        RunSmilesPrediction.generateDescriptorsForSdf(smilesDir, descriptorTypes);  //doesn't include ISIDA
        GenerateDescriptors.generateIsidaDescriptors(smilesDir+"smiles.sdf", smilesDir + "smiles.sdf.ISIDA");
        logger.info("Generated descriptors for SDF: " + descriptorTypes.toString());

        // compute and store predictions
        computePredictionsFromSdfs(smilesDir, "smiles.sdf", modelingDataset, true);

        logger.info("made SMILES prediction on string " + smiles + " with modeling dataset " + selectedModelingDatasetId + " for " + user.getUserName());
        return SUCCESS;
    }

    /* Takes the directory and file name of the .sdf of the dataset/compound to predict */
    private void computePredictionsFromSdfs(String predictingDir, String predictingSdfName, Dataset modelingDataset, boolean isSmiles) throws Exception {

        // if the predicting SDF is from SMILES, it's in the predictingDir. If from a dataset, it's in predictingDir/Descriptors
        String predictingSdf = isSmiles ? predictingDir + predictingSdfName :
            predictingDir + "Descriptors/" + predictingSdfName;

        List<List<DescriptorResult>> allDescriptorResults = new ArrayList<>(); //List of every descriptor's List of results for each compound
        List<Double> predictedValues = predictActivity( predictingSdf, modelingDataset, allDescriptorResults);

        /* stores results for display */
        mcraPredictions = new ArrayList<>();

        List<String> compoundNames=null;
        if (!isSmiles){
            compoundNames = DatasetFileOperations.getSdfCompoundNames(predictingDir+predictingSdfName);
        }

        boolean binary = modelingDataset.getModelType().equals(Constants.CATEGORY);

        for (int i=0; i<predictedValues.size(); i++) {
            List<DescriptorResult> results = new ArrayList<>();
            // for each descriptor's results for every compound in the dataset
            for (List<DescriptorResult> descriptor : allDescriptorResults){
                results.add(descriptor.get(i)); //get the DescriptorResult for this one compound
            }

            String compoundName = isSmiles ? smiles : compoundNames.get(i);
            McraPrediction prediction = new McraPrediction(N_NEAREST_NEIGHORS, predictedValues.get(i), compoundName, results, binary);
            mcraPredictions.add(prediction);
        }

    }

    /* Return the file path that needs to be passed to functions that read Descriptor files. Not the actual location of the Sdf */
    private String getDescriptorsPath(Dataset dataset){
        return dataset.getDirectoryPath()+"/Descriptors/"+dataset.getSdfFile();
    }

    /* returns the predicted activity of each compound in the predicting sdf.
    Populates allDescriptorResults with the breakdown of results from each descriptor*/
    private List<Double> predictActivity(String predictingSdf, Dataset modelingDataset,
                                         List<List<DescriptorResult>> allDescriptorResults) throws Exception {
        List<String> descriptorNames = new ArrayList<>();   //unused, but need to pass to readDescriptors()

        // get the Sdf of the modeling dataset
        String modelingSdf = getDescriptorsPath(modelingDataset);

        // read data from the modeling dataset
        List<String> actValues = DatasetFileOperations.getActFileValues(modelingDataset);
        String actualModelingSdf = modelingDataset.getDirectoryPath() + "/" + modelingDataset.getSdfFile(); //the real location of the sdf
        List<String> modelingCompoundNames = DatasetFileOperations.getSdfCompoundNames(actualModelingSdf);

        //maps the index of a compound in the predicting sdf to a list of its nearest neighbors from every descriptor
        List<Neighbor>[] compoundNeighborLists = null;

        for (String descriptorType : descriptorTypes) {

            List<Descriptors> modelingDescriptors = new ArrayList<>();
            List<Descriptors> predictingDescriptors = new ArrayList<>();

            // read Descriptor lists from files
            if (descriptorType.equals(Constants.ISIDA)){
                ReadDescriptors.readJoinedIsidaDescriptors(predictingSdf+".ISIDA", modelingSdf+".ISIDA", predictingDescriptors, modelingDescriptors);
            } else {
                ReadDescriptors.readDescriptors(descriptorType, modelingSdf, descriptorNames, modelingDescriptors);
                ReadDescriptors.readDescriptors(descriptorType, predictingSdf, descriptorNames, predictingDescriptors);
            }

            // initialize, if this is the first descriptor type being processed
            if (compoundNeighborLists == null){
                compoundNeighborLists = new List[predictingDescriptors.size()];     //size would be 1 for SMILES
            }

            //the descriptor results of every compound for one descriptor type
            List<DescriptorResult> singleDescriptorResults = new ArrayList();

            /* for each compound in the predicting set, find the nearest neighbors for this descriptor only */
            for (int i=0; i<predictingDescriptors.size(); i++) {
                if (compoundNeighborLists[i] == null){
                    compoundNeighborLists[i] = new ArrayList<>();
                }
                DescriptorResult descriptorResult = nearestNeighbors(N_NEAREST_NEIGHORS, predictingDescriptors.get(i).getDescriptorValues(),
                        modelingDescriptors, modelingCompoundNames, actValues, compoundNeighborLists[i]);
                descriptorResult.name = descriptorType;
                singleDescriptorResults.add(descriptorResult);
            }

            allDescriptorResults.add(singleDescriptorResults);
        }

        return weightedAverage(compoundNeighborLists);
    }

    /* Return the list of final predicted values for each compound in the predicting Sdf, given a list of each compound's neighbors from all descriptors */
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


    /* For one descriptor, for a single compound, append the nearest neighbors from the set of modelingCompounds*/
    private DescriptorResult nearestNeighbors(int n, List<Double> compound, List<Descriptors> modelingCompounds,
                                             List<String> modelingCompoundNames, List<String> actValues, List<Neighbor> neighborList) throws Exception {

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
            // build a semicolon-separated list of names in reverse order (end with most similar first)
            names = names==null ? neighbor.name : neighbor.name+ "; " + names;
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
        private int roundedPredictedActivity = -1;  //remains -1 if the modeling dataset isn't categorical/binary
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

        private String name;    //of the descriptor type
        private float averageSimilarity;
        private float averageActivity;
        private String neighborIds; //list of IDs of neighbors, most similar first, separated by "; "

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
