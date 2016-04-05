package edu.unc.ceccr.chembench.actions.ViewPredictor;

import com.google.common.collect.Lists;
import edu.unc.ceccr.chembench.actions.ViewAction;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.Dataset;
import edu.unc.ceccr.chembench.persistence.HibernateUtil;
import edu.unc.ceccr.chembench.persistence.Predictor;
import edu.unc.ceccr.chembench.persistence.User;
import edu.unc.ceccr.chembench.utilities.PopulateDataObjects;
import org.apache.log4j.Logger;

import java.util.List;

// struts2

public class ViewPredictorAction extends ViewAction {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    // Basic parameters. Inherited by all subclasses.
    private static Logger logger = Logger.getLogger(ViewPredictorAction.class.getName());
    protected Predictor selectedPredictor;
    protected Dataset dataset;
    protected int currentFoldNumber = 0;
    // End basic parameters

    // used by ext validation and models pages.
    // usually conatins 1 to n; etx validation has "All"
    // Params used by all the models pages
    protected String isYRandomPage;
    protected String orderBy;
    protected String sortDirection;
    protected String mostFrequentDescriptors = "";
    List<Predictor> childPredictors;
    List<String> foldNums = Lists.newArrayList();

    public String getBasicParameters() throws Exception {
        // this function gets params that all subclasses will need.
        String basic = checkBasicParams();
        if (!basic.equals(SUCCESS)) {
            return basic;
        }
        session = HibernateUtil.getSession();
        selectedPredictor = PopulateDataObjects.getPredictorById(Long.parseLong(objectId), session);
        if (selectedPredictor == null || (!selectedPredictor.getUserName().equals(Constants.ALL_USERS_USERNAME) && !user
                .getUserName().equals(selectedPredictor.getUserName()))) {
            logger.debug("Invalid predictor ID supplied. ");
            errorStrings.add("Invalid predictor ID supplied.");
            session.close();
            return ERROR;
        }

        Long datasetId = selectedPredictor.getDatasetId();
        dataset = PopulateDataObjects.getDataSetById(datasetId, session);

        childPredictors = PopulateDataObjects.getChildPredictors(selectedPredictor, session);
        session.close();
        return SUCCESS;
    }

    // End params used by all models pages

    public String getModelsPageParameters() throws Exception {
        // gets parameters used by each modeling page
        // assumes getBasicParameters has already been called

        isYRandomPage = ((String[]) context.getParameters().get("isYRandomPage"))[0];

        if (context.getParameters().get("sortDirection") != null) {
            sortDirection = ((String[]) context.getParameters().get("sortDirection"))[0];
        }
        if (context.getParameters().get("orderBy") != null) {
            orderBy = ((String[]) context.getParameters().get("orderBy"))[0];
        }

        return SUCCESS;
    }

    public User getUser() {
        return user;
    }

    // getters and setters

    public void setUser(User user) {
        this.user = user;
    }

    public Predictor getSelectedPredictor() {
        return selectedPredictor;
    }

    public void setSelectedPredictor(Predictor selectedPredictor) {
        this.selectedPredictor = selectedPredictor;
    }

    public Dataset getDataset() {
        return dataset;
    }

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    public String getMostFrequentDescriptors() {
        return mostFrequentDescriptors;
    }

    public void setMostFrequentDescriptors(String mostFrequentDescriptors) {
        this.mostFrequentDescriptors = mostFrequentDescriptors;
    }

    public String getIsYRandomPage() {
        return isYRandomPage;
    }

    public void setIsYRandomPage(String isYRandomPage) {
        this.isYRandomPage = isYRandomPage;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

    public int getCurrentFoldNumber() {
        return currentFoldNumber;
    }

    public void setCurrentFoldNumber(int currentFoldNumber) {
        this.currentFoldNumber = currentFoldNumber;
    }

    public List<String> getFoldNums() {
        return foldNums;
    }

    public void setFoldNums(List<String> foldNums) {
        this.foldNums = foldNums;
    }

    public class descriptorFrequency {
        private String descriptor;
        private int numOccs;

        public String getDescriptor() {
            return descriptor;
        }

        public void setDescriptor(String descriptor) {
            this.descriptor = descriptor;
        }

        public int getNumOccs() {
            return numOccs;
        }

        public void setNumOccs(int numOccs) {
            this.numOccs = numOccs;
        }
    }

    // End getters and setters

}
