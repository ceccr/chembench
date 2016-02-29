package edu.unc.ceccr.chembench.actions.ViewPredictor;

import com.google.common.collect.Lists;
import edu.unc.ceccr.chembench.actions.DetailAction;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DetailPredictorAction extends DetailAction {
    // Basic parameters. Inherited by all subclasses.
    private static final Logger logger = Logger.getLogger(DetailPredictorAction.class.getName());
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

    @Autowired
    protected DatasetRepository datasetRepository;
    @Autowired
    protected PredictorRepository predictorRepository;

    public String getBasicParameters() throws Exception {
        // this function gets params that all subclasses will need.
        String basic = checkBasicParams();
        if (!basic.equals(SUCCESS)) {
            return basic;
        }
        selectedPredictor = predictorRepository.findOne(Long.parseLong(objectId));
        if (selectedPredictor == null || (!selectedPredictor.getUserName().equals(Constants.ALL_USERS_USERNAME) && !user
                .getUserName().equals(selectedPredictor.getUserName()))) {
            logger.debug("Invalid predictor ID supplied. ");
            errorStrings.add("Invalid predictor ID supplied.");
            return ERROR;
        }

        Long datasetId = selectedPredictor.getDatasetId();
        dataset = datasetRepository.findOne(datasetId);
        selectedPredictor.setDatasetDisplay(dataset.getName());
        childPredictors = predictorRepository.findByParentId(selectedPredictor.getId());
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

    public void setDatasetRepository(DatasetRepository datasetRepository) {
        this.datasetRepository = datasetRepository;
    }

    public void setPredictorRepository(PredictorRepository predictorRepository) {
        this.predictorRepository = predictorRepository;
    }

    // End getters and setters

}
