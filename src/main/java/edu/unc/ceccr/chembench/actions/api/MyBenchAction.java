package edu.unc.ceccr.chembench.actions.api;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.opensymphony.xwork2.ActionSupport;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.jobs.CentralDogma;
import edu.unc.ceccr.chembench.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MyBenchAction extends ActionSupport {
    private static final Logger logger = LoggerFactory.getLogger(MyBenchAction.class);
    private final Splitter splitter = Splitter.on(CharMatcher.WHITESPACE).omitEmptyStrings();
    private final Joiner joiner = Joiner.on(';');
    private final DatasetRepository datasetRepository;
    private final PredictorRepository predictorRepository;
    private final PredictionRepository predictionRepository;

    private User user = User.getCurrentUser();
    private List<?> data = new ArrayList<>();

    @Autowired
    public MyBenchAction(DatasetRepository datasetRepository, PredictorRepository predictorRepository,
                         PredictionRepository predictionRepository) {
        this.datasetRepository = datasetRepository;
        this.predictorRepository = predictorRepository;
        this.predictionRepository = predictionRepository;
    }

    private String getFilteredJobs(List<Job> jobList) {
        if (user == null) {
            return SUCCESS;
        }
        for (Iterator<Job> iterator = jobList.iterator(); iterator.hasNext(); ) {
            Job j = iterator.next();
            if (!(user.getIsAdmin().equals("YES") || user.getUserName().equals(j.getUserName()))) {
                iterator.remove();
            }
            j.setMessage(j.workflowTask.getProgress(user.getUserName()));
        }
        data = jobList;
        return SUCCESS;
    }

    public String getUnassignedJobs() {
        return getFilteredJobs(CentralDogma.getInstance().incomingJobs.getReadOnlyCopy());
    }

    public String getLocalJobs() {
        return getFilteredJobs(CentralDogma.getInstance().localJobs.getReadOnlyCopy());
    }

    public String getLsfJobs() {
        return getFilteredJobs(CentralDogma.getInstance().lsfJobs.getReadOnlyCopy());
    }

    public String getErrorJobs() {
        return getFilteredJobs(CentralDogma.getInstance().errorJobs.getReadOnlyCopy());
    }

    private List<Dataset> getDatasetObjects() {
        List<Dataset> datasets = new ArrayList<>();
        if (user == null) {
            datasets.addAll(datasetRepository.findAllPublicDatasets());
        } else {
            // return user's datasets and public datasets
            if (user.getShowPublicDatasets().equals(Constants.SOME)) {
                datasets.addAll(datasetRepository.findSomePublicDatasets());
            } else if (user.getShowPublicDatasets().equals(Constants.ALL)) {
                datasets.addAll(datasetRepository.findAllPublicDatasets());
            }
            datasets.addAll(datasetRepository.findByUserName(user.getUserName()));
        }
        return datasets;
    }

    public String getDatasets() {
        this.data = getDatasetObjects();
        return SUCCESS;
    }

    public String getModelingDatasets() {
        List<Dataset> datasets = getDatasetObjects();
        for (Iterator<Dataset> iterator = datasets.iterator(); iterator.hasNext(); ) {
            Dataset d = iterator.next();
            String activityType = d.getModelType();
            if (!(activityType.equals(Constants.CONTINUOUS) || activityType.equals(Constants.CATEGORY))) {
                iterator.remove();
            }
        }
        this.data = datasets;
        return SUCCESS;
    }

    public String getModels() {
        List<Predictor> predictors = new ArrayList<>();
        predictors.addAll(predictorRepository.findPublicPredictors());
        if (user != null) {
            // return user's models and public models
            predictors.addAll(predictorRepository.findByUserName(user.getUserName()));
        }
        for (Predictor predictor : predictors) {
            Dataset predictorDataset = datasetRepository.findOne(predictor.getDatasetId());
            predictor.setDatasetDisplay(predictorDataset.getName());
        }
        this.data = predictors;
        return SUCCESS;
    }

    public String getPredictions() {
        if (user == null) {
            return SUCCESS;
        }
        List<Prediction> predictions = new ArrayList<>();
        predictions.addAll(predictionRepository.findByUserName(user.getUserName()));
        for (Prediction prediction : predictions) {
            List<String> predictorNames = new ArrayList<>();
            Dataset predictionDataset = datasetRepository.findOne(prediction.getDatasetId());
            List<String> rawPredictorIds = splitter.splitToList(prediction.getPredictorIds());
         
		logger.debug("MyBenchAction log: " + rawPredictorIds);

	   for (String rawPredictorId : rawPredictorIds) {
                Predictor predictor = predictorRepository.findOne(Long.parseLong(rawPredictorId));
                
			logger.debug("This is predictor.getName() log: " + predictor.getName());
			logger.debug("This is predictor.getDescriptorGeneration() log: " + predictor.getDescriptorGeneration());
			logger.debug("This is predictor.getModelMethod() log: " + predictor.getModelMethod());	
	
		predictorNames.add(String.format("%s (%s,%s)", predictor.getName(), predictor.getDescriptorGeneration(),
                        predictor.getModelMethod()));
            }
            prediction.setDatasetDisplay(predictionDataset.getName());
            prediction.setPredictorNames(joiner.join(predictorNames));
        }
        data = predictions;
        return SUCCESS;
    }

    public List<?> getData() {
        return data;
    }
}
