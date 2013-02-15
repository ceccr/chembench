package edu.unc.ceccr.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.ListUtils;
import org.hibernate.Session;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.jobs.CentralDogma;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.taskObjects.QsarPredictionTask;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.workflows.descriptors.ReadDescriptors;
import edu.unc.ceccr.workflows.modelingPrediction.RunSmilesPrediction;


import org.apache.log4j.Logger;
// struts2

public class PredictionFormActions extends ActionSupport
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static Logger logger 
                = Logger.getLogger(PredictionFormActions.class.getName());
    
    public String loadSelectPredictorPage() throws Exception
    {
        String result = SUCCESS;
        /* check that the user is logged in */
        ActionContext context = ActionContext.getContext();
        if (context == null) {
            logger.debug("No ActionContext available");
        }
        else {
            user = (User) context.getSession().get("user");
            if (user == null) {
                logger.debug("No user is logged in.");
                result = LOGIN;
                return result;
            }
        }
        Session session = HibernateUtil.getSession();
        /* get predictors */
        if (user.getShowPublicPredictors().equals(Constants.ALL)) {
            /* get the user's predictors and all public ones */
            userPredictors = PopulateDataObjects.populatePredictors(user
                    .getUserName(), true, true, session);
        }
        else {
            /* just get the user's predictors */
            userPredictors = PopulateDataObjects.populatePredictors(user
                    .getUserName(), false, true, session);
        }
        session.close();
        return result;
    }

    public String makeSmilesPrediction() throws Exception
    {
        String result = SUCCESS;
        ActionContext context = ActionContext.getContext();
        user = (User) context.getSession().get("user");
        /* use the same session for all data requests */
        Session session = HibernateUtil.getSession();
        String smiles = ((String[]) context.getParameters().get("smiles"))[0];
        smilesString = smiles;
        String cutoff = ((String[]) context.getParameters().get("cutoff"))[0];
        smilesCutoff = cutoff;
        String predictorIds = ((String[]) context.getParameters().get(
                "predictorIds"))[0];
        logger.debug(" 1: " + smiles + " 2: " + cutoff + " 3: "
                + predictorIds);
        logger.debug(user.getUserName());
        logger.debug("SMILES predids: " + predictorIds);
        String[] selectedPredictorIdArray = predictorIds.split("\\s+");
        ArrayList<Predictor> predictors = new ArrayList<Predictor>();
        for (int i = 0; i < selectedPredictorIdArray.length; i++) {
            Predictor predictor = PopulateDataObjects.getPredictorById(Long
                    .parseLong(selectedPredictorIdArray[i]), session);
            if (!predictor.getDescriptorGeneration().equals(
                    Constants.UPLOADED)) {
                /*
                 * uploaded descriptors won't work, since we can't generate
                 * them
                 */
                predictors.add(predictor);
            }
        }
        /* we don't need the session again */
        session.close();
        /* stores results */
        smilesPredictions = new ArrayList<SmilesPrediction>();
        for (int i = 0; i < predictors.size(); i++) {
            Predictor predictor = predictors.get(i);

            /* make smiles dir */
            String smilesDir = Constants.CECCR_USER_BASE_PATH
                    + user.getUserName() + "/SMILES/" + predictor.getName()
                    + "/";
            new File(smilesDir).mkdirs();
            logger.debug("Created the directory " + smilesDir);
            // make sure there's nothing in the dir already.
            FileAndDirOperations.deleteDirContents(smilesDir);

            // generate an SDF from this SMILES string
            RunSmilesPrediction.smilesToSDF(smiles, smilesDir);
            logger.debug("Generated the SDF file from provided smiles.");
            String[] predValues = new String[3];

            int totalModels = predictor.getNumTestModels();
            
            // for n-folded predictors
            if (predictor.getChildType() != null
                    && predictor.getChildType().equals(Constants.NFOLD)) {
                String[] ids = predictor.getChildIds().split("\\s+");
                logger.debug("Predictor is n-folded.");
                List<String[]> tempPred = new ArrayList<String[]>();
                for (int j = 0; j < ids.length; j++) {
                    logger.debug("Predictor is n-folded." + ids[j]);
                    session = HibernateUtil.getSession();
                    Predictor tempP = PopulateDataObjects.getPredictorById(
                            Long.parseLong(ids[j]), session);
                    session.close();
                    tempPred.add(RunSmilesPrediction.PredictSmilesSDF(
                            smilesDir, user.getUserName(), tempP, Float
                                    .parseFloat(cutoff)));
                    totalModels += tempP.getNumTestModels();
                    logger.debug("Calculating predictions for "
                            + tempP.getName());
                }

                int predictingModels = 0;
                double predictedValue = 0d;
                double standartDeviation = 0d;

                /* getting average values */
                for (String[] s : tempPred) {
                    predictingModels += Integer.parseInt(s[0]);
                    predictedValue += Double.parseDouble(s[1]);
                    standartDeviation += Double.parseDouble(s[2]);
                    // debug part
                    logger.debug("Predicting models: " + s[0]);
                    logger.debug("Predicted value: " + s[1]);
                    logger.debug("Standart deviation: " + s[2]);
                }
                predValues[0] = String.valueOf(predictingModels);
                predValues[1] = Utility.roundSignificantFigures(String
                        .valueOf(predictedValue / ids.length),
                        Constants.REPORTED_SIGNIFICANT_FIGURES);
                predValues[2] = Utility.roundSignificantFigures(String
                        .valueOf(standartDeviation / ids.length),
                        Constants.REPORTED_SIGNIFICANT_FIGURES);
            }
            /*
             * create descriptors for the SDF, normalize them , and make a
             * prediction
             */
            else
                predValues = RunSmilesPrediction.PredictSmilesSDF(smilesDir,
                        user.getUserName(), predictor, Float
                                .parseFloat(cutoff));

            // read predValues and build the prediction output object
            SmilesPrediction sp = new SmilesPrediction();
            sp.setPredictorName(predictor.getName());
            sp.setTotalModels(totalModels);

            sp.setPredictingModels(Integer.parseInt(predValues[0]));
            sp.setPredictedValue(predValues[1]);
            sp.setStdDeviation(predValues[2]);

            // add it to the array
            smilesPredictions.add(sp);
        }

        logger.info("made SMILES prediction on string " + smiles
                + " with predictors " + predictorIds +" for "+ user.getUserName());
        return result;
    }

    public String loadMakePredictionsPage() throws Exception
    {

        String result = SUCCESS;
        // check that the user is logged in
        ActionContext context = ActionContext.getContext();

        if (context == null) {
            logger.debug("No ActionContext available");
        }
        else {
            user = (User) context.getSession().get("user");

            if (user == null) {
                logger.debug("No user is logged in.");
                result = LOGIN;
                return result;
            }
        }

        // use the same session for all data requests
        Session session = HibernateUtil.getSession();

        // get list of predictor IDs from the checked checkboxes
        if (predictorCheckBoxes == null
                || predictorCheckBoxes.trim().isEmpty()) {
            logger.debug("no predictor chosen!");
            errorStrings.add("Please select at least one predictor.");
            result = ERROR;
            return result;
        }
        selectedPredictorIds = predictorCheckBoxes.replaceAll(",", " ");
        String[] predictorIds = selectedPredictorIds.split("\\s+");

        isUploadedDescriptors = false;
        singleCompoundPredictionAllowed = true;
        HashSet<String> predictorsModelDescriptors = new HashSet<String>();

        for (int i = 0; i < predictorIds.length; i++) {
            Predictor p = PopulateDataObjects.getPredictorById(Long
                    .parseLong(predictorIds[i]), session);

            if (p.getChildType() != null
                    && p.getChildType().equals(Constants.NFOLD)) {
                /* check if *any* child predictor has models */
                String[] childIds = p.getChildIds().split("\\s+");
                boolean childHasModels = false;
                for (String pChildId : childIds) {
                    Predictor pChild = PopulateDataObjects.getPredictorById(
                            Long.parseLong(pChildId), session);
                    if (pChild.getNumTestModels() > 0) {
                        childHasModels = true;
                    }
                }
                if (!childHasModels) {
                    errorStrings.add("The predictor '" + p.getName()
                            + "' cannot be used for prediction"
                            + " because it contains no usable models.");
                    result = ERROR;
                }
            }
            else {
                if (p.getNumTestModels() == 0) {
                    /*
                     * this predictor shouldn't be used for prediction. Error
                     * out.
                     */
                    errorStrings.add("The predictor '" + p.getName()
                            + "' cannot be used for prediction because"
                            + " it contains no usable models.");
                    logger.warn("The predictor '" + p.getName()
                            + "' cannot be used for prediction because"
                            + " it contains no usable models.");
                    result = ERROR;
                }
                else {
                    logger.debug("predictor " + p.getName()
                            + " is fine, it has " + p.getNumTotalModels());
                }
            }
            /* adding modeling_methods for each of the selected predictors */
            if (!p.getModelMethod().trim().isEmpty())
                predictorsModelDescriptors.addAll(Arrays.asList(p
                        .getDescriptorGeneration().trim().split(" ")));
            selectedPredictors.add(p);
            if (p.getDescriptorGeneration().equals(Constants.UPLOADED)) {
                isUploadedDescriptors = true;
                singleCompoundPredictionAllowed = false;
            }
        }

        if (result.equals(ERROR)) {
            return result;
        }

        /*
         * set up any values that need to be populated onto the page
         * (dropdowns, lists, display stuff)
         */
        userDatasetNames = PopulateDataObjects.populateDatasetNames(user
                .getUserName(), true, session);
        userPredictorNames = PopulateDataObjects.populatePredictorNames(user
                .getUserName(), true, session);
        userPredictionNames = PopulateDataObjects.populatePredictionNames(
                user.getUserName(), true, session);
        userTaskNames = PopulateDataObjects.populateTaskNames(user
                .getUserName(), false, session);

        if (user.getShowPublicDatasets().equals(Constants.ALL)) {
            /* get user and public datasets */
            userDatasets = PopulateDataObjects.populateDatasetsForPrediction(
                    user.getUserName(), true, session);
        }
        else if (user.getShowPublicDatasets().equals(Constants.NONE)) {
            /* just get user datasets */
            userDatasets = PopulateDataObjects.populateDatasetsForPrediction(
                    user.getUserName(), false, session);
        }
        else if (user.getShowPublicDatasets().equals(Constants.SOME)) {
            /*
             * get all datasets and filter out all the public ones that aren't
             * "show by default"
             */
            userDatasets = PopulateDataObjects.populateDatasetsForPrediction(
                    user.getUserName(), true, session);

            if (userDatasets != null)
                for (int i = 0; i < userDatasets.size(); i++) {
                    String s = userDatasets.get(i).getShowByDefault();
                    if (s != null && s.equals(Constants.NO)) {
                        userDatasets.remove(i);
                        i--;
                    }
                }
        }

        /*
         * filtering userDatasets leaving only datasets that has same modeling
         * method as predictor
         */
        List<DataSet> new_ds = new ArrayList<DataSet>();
        for (DataSet ds : userDatasets) {
            /*
             * looking for arrays intersection if found then the Dataset is
             * added to the list
             */
            List<String> dscrptrLst1 = new ArrayList<String>(
                    predictorsModelDescriptors);
            List<String> dscrptrLst2 = Arrays.asList(ds
                    .getAvailableDescriptors().trim().split(" "));

            // Find intersection, get iterator, then explicitly cast each
            // element of intersection to a string (type safety issues)
            List<String> dscrptrIntsct = new ArrayList<String>();
            Iterator<?> tempIterator = ListUtils.intersection(dscrptrLst1,
                    dscrptrLst2).iterator();
            while (tempIterator.hasNext()) {
                dscrptrIntsct.add((String) tempIterator.next());
            }

            if (!ds.getAvailableDescriptors().trim().isEmpty()
                    && !new_ds.contains(ds)
                    && (dscrptrIntsct.size() == predictorsModelDescriptors
                            .size()))
                new_ds.add(ds);
        }
        userDatasets.clear();
        userDatasets.addAll(new_ds);

        if (isUploadedDescriptors) {
            userDatasets.clear();
            // for(Iterator<Predictor>
            // i=selectedPredictors.iterator();i.hasNext();){
            for (Predictor prdctr : selectedPredictors) {
                // Predictor p = i.next();
                if (prdctr.getDescriptorGeneration().equals(
                        Constants.UPLOADED)) {
                    List<DataSet> dss = PopulateDataObjects
                            .populateDatasetNamesForUploadedPredicors(user
                                    .getUserName(), prdctr
                                    .getUploadedDescriptorType(), true,
                                    session);
                    for (Iterator<DataSet> j = dss.iterator(); j.hasNext();) {
                        DataSet ds = j.next();
                        if (!userDatasets.contains(ds))
                            userDatasets.add(ds);
                    }
                }
            }
        }
        // give back the session at the end
        session.close();
        return result;
    }

    public String makeDatasetPrediction() throws Exception
    {
        /*
         * prediction form submitted, so create a new prediction task and run
         * it
         */
        ActionContext context = ActionContext.getContext();
        user = (User) context.getSession().get("user");

        /* use the same session for all data requests */
        Session session = HibernateUtil.getSession();

        DataSet predictionDataset = PopulateDataObjects.getDataSetById(
                selectedDatasetId, session);
        String sdf = predictionDataset.getSdfFile();

        if (jobName != null) {
            jobName = jobName.replaceAll(" ", "_");
            jobName = jobName.replaceAll("\\(", "_");
            jobName = jobName.replaceAll("\\)", "_");
            jobName = jobName.replaceAll("\\[", "_");
            jobName = jobName.replaceAll("\\]", "_");
        }

        logger.debug(user.getUserName());
        logger.debug("predids: " + selectedPredictorIds);

        QsarPredictionTask predTask = new QsarPredictionTask(user
                .getUserName(), jobName, sdf, cutOff, selectedPredictorIds,
                predictionDataset);

        predTask.setUp();
        int numCompounds = predictionDataset.getNumCompound();
        String[] ids = selectedPredictorIds.split("\\s+");
        int numModels = 0;

        ArrayList<Predictor> selectedPredictors = new ArrayList<Predictor>();

        for (int i = 0; i < ids.length; i++) {
            Predictor sp = PopulateDataObjects.getPredictorById(Long
                    .parseLong(ids[i]), session);
            selectedPredictors.add(sp);
            if (sp.getChildType() != null
                    && sp.getChildType().equals(Constants.NFOLD)) {
                String[] childIds = sp.getChildIds().split("\\s+");
                for (String childId : childIds) {
                    Predictor cp = PopulateDataObjects.getPredictorById(Long
                            .parseLong(childId), session);
                    numModels += cp.getNumTestModels();
                }
            }
            else {
                numModels += sp.getNumTestModels();
            }
        }

        /*
         * check descriptors of each of the selected predictors. Make sure
         * that the prediction dataset contains all of those descriptors,
         * otherwise error out.
         */
        for (Predictor sp : selectedPredictors) {
            String[] predictionDatasetDescriptors = predictionDataset
                    .getAvailableDescriptors().split("\\s+");

            boolean descriptorsMatch = false;

            if (sp.getDescriptorGeneration().equals(Constants.UPLOADED)) {
                // get the uploaded descriptors for the dataset
                String predictionXFile = predictionDataset.getXFile();

                String predictionDatasetDir = Constants.CECCR_USER_BASE_PATH
                        + predictionDataset.getUserName() + "/DATASETS/"
                        + predictionDataset.getName() + "/";
                if (predictionXFile != null
                        && !predictionXFile.trim().isEmpty()) {

                    logger.debug("Staring to read predictors from file: "
                            + predictionDatasetDir + predictionXFile);
                    String[] predictionDescs = ReadDescriptors
                            .readDescriptorNamesFromX(predictionXFile,
                                    predictionDatasetDir);

                    // get the uploaded descriptors for the predictor
                    DataSet predictorDataset = PopulateDataObjects
                            .getDataSetById(sp.getDatasetId(), session);
                    String predictorDatasetDir = Constants.CECCR_USER_BASE_PATH
                            + predictorDataset.getUserName()
                            + "/DATASETS/"
                            + predictorDataset.getName() + "/";
                    String[] predictorDescs = ReadDescriptors
                            .readDescriptorNamesFromX(predictorDataset
                                    .getXFile(), predictorDatasetDir);

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
                            errorStrings.add("The predictor '" + sp.getName()
                                    + "' contains the descriptor '"
                                    + predictorDescs[i] + "', but this "
                                    + "descriptor was not found in "
                                    + "the prediction dataset.");
                        }
                    }

                    if (!descriptorsMatch) {
                        return ERROR;
                    }
                }
            }
            else {
                for (int i = 0; i < predictionDatasetDescriptors.length; i++) {
                    if (sp.getDescriptorGeneration().equals(
                            Constants.MOLCONNZ)
                            && predictionDatasetDescriptors[i]
                                    .equals(Constants.MOLCONNZ)) {
                        descriptorsMatch = true;
                    }
                    else if (sp.getDescriptorGeneration().equals(
                            Constants.CDK)
                            && predictionDatasetDescriptors[i]
                                    .equals(Constants.CDK)) {
                        descriptorsMatch = true;
                    }
                    else if (sp.getDescriptorGeneration().equals(
                            Constants.DRAGONH)
                            && predictionDatasetDescriptors[i]
                                    .equals(Constants.DRAGONH)) {
                        descriptorsMatch = true;
                    }
                    else if (sp.getDescriptorGeneration().equals(
                            Constants.DRAGONNOH)
                            && predictionDatasetDescriptors[i]
                                    .equals(Constants.DRAGONNOH)) {
                        descriptorsMatch = true;
                    }
                    else if (sp.getDescriptorGeneration().equals(
                            Constants.MOE2D)
                            && predictionDatasetDescriptors[i]
                                    .equals(Constants.MOE2D)) {
                        descriptorsMatch = true;
                    }
                    else if (sp.getDescriptorGeneration().equals(
                            Constants.MACCS)
                            && predictionDatasetDescriptors[i]
                                    .equals(Constants.MACCS)) {
                        descriptorsMatch = true;
                    }
                }

                if (!descriptorsMatch) {
                    errorStrings.add("The predictor '" + sp.getName()
                            + "' is based on " + sp.getDescriptorGeneration()
                            + " descriptors, but the dataset '"
                            + predictionDataset.getName()
                            + "' does not have these descriptors. "
                            + "You will not be able to make"
                            + " this prediction.");
                    return ERROR;
                }
            }

        }

        CentralDogma centralDogma = CentralDogma.getInstance();
        String emailOnCompletion = "false";
        centralDogma.addJobToIncomingList(user.getUserName(), jobName,
                predTask, numCompounds, numModels, emailOnCompletion);

        logger.info("making prediction run on dataset "
                + predictionDataset.getName() + " with predictors "
                + selectedPredictorIds + " for " + user.getUserName());
 
        // give back the session at the end
        session.close();
        return SUCCESS;
    }

    public String execute() throws Exception
    {
        return SUCCESS;
    }

    // variables used for JSP display
    private User                   user;
    private List<Predictor>        userPredictors;
    private List<String>           userDatasetNames;
    private List<String>           userPredictorNames;
    private List<String>           userPredictionNames;
    private List<String>           userTaskNames;
    private List<DataSet>          userDatasets;
    private String                 predictorCheckBoxes;
    /* a flag that indicate if we should display SMILES prediction or not */
    private boolean                singleCompoundPredictionAllowed;
    private boolean                isUploadedDescriptors;
    private List<Predictor>        selectedPredictors = new ArrayList<Predictor>();

    private List<SmilesPrediction> smilesPredictions;
    private String                 smilesString;
    private String                 smilesCutoff;

    ArrayList<String>              errorStrings       = new ArrayList<String>();

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public List<Predictor> getUserPredictors()
    {
        return userPredictors;
    }

    public void setUserPredictors(List<Predictor> userPredictors)
    {
        this.userPredictors = userPredictors;
    }

    public List<String> getUserDatasetNames()
    {
        return userDatasetNames;
    }

    public void setUserDatasetNames(List<String> userDatasetNames)
    {
        this.userDatasetNames = userDatasetNames;
    }

    public List<String> getUserPredictorNames()
    {
        return userPredictorNames;
    }

    public void setUserPredictorNames(List<String> userPredictorNames)
    {
        this.userPredictorNames = userPredictorNames;
    }

    public List<String> getUserPredictionNames()
    {
        return userPredictionNames;
    }

    public void setUserPredictionNames(List<String> userPredictionNames)
    {
        this.userPredictionNames = userPredictionNames;
    }

    public List<String> getUserTaskNames()
    {
        return userTaskNames;
    }

    public void setUserTaskNames(List<String> userTaskNames)
    {
        this.userTaskNames = userTaskNames;
    }

    public List<DataSet> getUserDatasets()
    {
        return userDatasets;
    }

    public void setUserDatasets(List<DataSet> userDatasets)
    {
        this.userDatasets = userDatasets;
    }

    public List<Predictor> getSelectedPredictors()
    {
        return selectedPredictors;
    }

    public void setSelectedPredictors(List<Predictor> selectedPredictors)
    {
        this.selectedPredictors = selectedPredictors;
    }

    // populated by the JSP form
    private Long   selectedDatasetId;
    private String cutOff = "0.5";
    private String jobName;
    private String selectedPredictorIds;

    public Long getSelectedDatasetId()
    {
        return selectedDatasetId;
    }

    public void setSelectedDatasetId(Long selectedDatasetId)
    {
        this.selectedDatasetId = selectedDatasetId;
    }

    public String getCutOff()
    {
        return cutOff;
    }

    public void setCutOff(String cutOff)
    {
        this.cutOff = cutOff;
    }

    public String getJobName()
    {
        return jobName;
    }

    public void setJobName(String jobName)
    {
        this.jobName = jobName;
    }

    public String getPredictorCheckBoxes()
    {
        return predictorCheckBoxes;
    }

    public void setPredictorCheckBoxes(String predictorCheckBoxes)
    {
        this.predictorCheckBoxes = predictorCheckBoxes;
    }

    public String getSelectedPredictorIds()
    {
        return selectedPredictorIds;
    }

    public void setSelectedPredictorIds(String selectedPredictorIds)
    {
        this.selectedPredictorIds = selectedPredictorIds;
    }

    public List<SmilesPrediction> getSmilesPredictions()
    {
        return smilesPredictions;
    }

    public void
            setSmilesPredictions(List<SmilesPrediction> smilesPredictions)
    {
        this.smilesPredictions = smilesPredictions;
    }

    public String getSmilesString()
    {
        return smilesString;
    }

    public void setSmilesString(String smilesString)
    {
        this.smilesString = smilesString;
    }

    public String getSmilesCutoff()
    {
        return smilesCutoff;
    }

    public void setSmilesCutoff(String smilesCutoff)
    {
        this.smilesCutoff = smilesCutoff;
    }

    public class SmilesPrediction
    {
        // used by makeSmilesPrediction()
        String predictedValue;
        String stdDeviation;
        int    predictingModels;
        int    totalModels;
        String predictorName;

        public String getPredictedValue()
        {
            return predictedValue;
        }

        public void setPredictedValue(String predictedValue)
        {
            this.predictedValue = predictedValue;
        }

        public String getStdDeviation()
        {
            return stdDeviation;
        }

        public void setStdDeviation(String stdDeviation)
        {
            this.stdDeviation = stdDeviation;
        }

        public int getPredictingModels()
        {
            return predictingModels;
        }

        public void setPredictingModels(int predictingModels)
        {
            this.predictingModels = predictingModels;
        }

        public int getTotalModels()
        {
            return totalModels;
        }

        public void setTotalModels(int totalModels)
        {
            this.totalModels = totalModels;
        }

        public String getPredictorName()
        {
            return predictorName;
        }

        public void setPredictorName(String predictorName)
        {
            this.predictorName = predictorName;
        }
    }

    public ArrayList<String> getErrorStrings()
    {
        return errorStrings;
    }

    public void setErrorStrings(ArrayList<String> errorStrings)
    {
        this.errorStrings = errorStrings;
    }

    public boolean getSingleCompoundPredictionAllowed()
    {
        return singleCompoundPredictionAllowed;
    }

    public void
            setSingleCompoundPredictionAllowed(boolean isSingleCompoundPredictionAllowed)
    {
        this.singleCompoundPredictionAllowed = isSingleCompoundPredictionAllowed;
    }

}
