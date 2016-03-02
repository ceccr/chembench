package edu.unc.ceccr.chembench.actions.api;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.opensymphony.xwork2.ActionSupport;
import edu.unc.ceccr.chembench.jobs.CentralDogma;
import edu.unc.ceccr.chembench.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.logging.Logger;

public class MyBenchAction extends ActionSupport {
    private static final Logger logger = Logger.getLogger(MyBenchAction.class);
    private final Splitter splitter = Splitter.on(CharMatcher.WHITESPACE);
    private final Joiner joiner = Joiner.on(';');
    private final DatasetRepository datasetRepository;
    private final PredictorRepository predictorRepository;
    private final PredictionRepository predictionRepository;

    private User user = User.getCurrentUser();
    private List<?> data = Lists.newArrayList();

    @Autowired
    public MyBenchAction(DatasetRepository datasetRepository, PredictorRepository predictorRepository,
                         PredictionRepository predictionRepository) {
        this.datasetRepository = datasetRepository;
        this.predictorRepository = predictorRepository;
        this.predictionRepository = predictionRepository;
    }

    public String getLocalJobs() {
        if (user == null) {
            return SUCCESS;
        }

        List<Job> jobList = CentralDogma.getInstance().localJobs.getReadOnlyCopy();
        for (Job j : jobList) {
            if (!(user.getIsAdmin().equals("YES") || user.getUserName().equals(j.getUserName()))) {
                jobList.remove(j);
            }
        }
        data = jobList;
        return SUCCESS;
    }

    public String getLsfJobs() {
        if (user == null) {
            return SUCCESS;
        }

        List<Job> jobList = CentralDogma.getInstance().lsfJobs.getReadOnlyCopy();
        for (Job j : jobList) {
            if (!(user.getIsAdmin().equals("YES") || user.getUserName().equals(j.getUserName()))) {
                jobList.remove(j);
            }
        }
        data = jobList;
        return SUCCESS;
    }

    public String getErrorJobs() {
        if (user == null) {
            return SUCCESS;
        }

        List<Job> jobList = CentralDogma.getInstance().errorJobs.getReadOnlyCopy();
        for (Job j : jobList) {
            if (!(user.getIsAdmin().equals("YES") || user.getUserName().equals(j.getUserName()))) {
                jobList.remove(j);
            }
        }
        data = jobList;
        return SUCCESS;
    }

    public String getDatasets() {
        List<Dataset> datasets = Lists.newArrayList();
        datasets.addAll(datasetRepository.findAllPublicDatasets());
        if (user != null) {
            // return user's datasets and public datasets
            datasets.addAll(datasetRepository.findByUserName(user.getUserName()));
        }
        this.data = datasets;
        return SUCCESS;
    }

    public String getModels() {
        List<Predictor> predictors = Lists.newArrayList();
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
        List<Prediction> predictions = Lists.newArrayList();
        predictions.addAll(predictionRepository.findByUserName(user.getUserName()));
        for (Prediction prediction : predictions) {
            List<String> predictorNames = Lists.newArrayList();
            Dataset predictionDataset = datasetRepository.findOne(prediction.getDatasetId());
            List<String> rawPredictorIds = splitter.splitToList(prediction.getPredictorIds());
            for (String rawPredictorId : rawPredictorIds) {
                Predictor predictor = predictorRepository.findOne(Long.parseLong(rawPredictorId));
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
