package edu.unc.ceccr.chembench.actions;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import edu.unc.ceccr.chembench.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class PredictionDetailAction extends DetailAction {
    private final PredictionRepository predictionRepository;
    private final DatasetRepository datasetRepository;
    private final PredictorRepository predictorRepository;
    private final Splitter splitter = Splitter.on(CharMatcher.WHITESPACE).omitEmptyStrings();

    private Prediction prediction;
    private Dataset predictionDataset;
    private List<Predictor> predictors;

    @Autowired
    public PredictionDetailAction(PredictionRepository predictionRepository, DatasetRepository datasetRepository,
                                  PredictorRepository predictorRepository) {
        this.predictionRepository = predictionRepository;
        this.datasetRepository = datasetRepository;
        this.predictorRepository = predictorRepository;
    }

    public String execute() {
        prediction = predictionRepository.findOne(id);
        String result = validateObject(prediction);
        if (!result.equals(SUCCESS)) {
            return result;
        }
        predictionDataset = datasetRepository.findOne(prediction.getDatasetId());
        predictors = Lists.newArrayList();
        for (String idString : splitter.split(prediction.getPredictorIds())) {
            predictors.add(predictorRepository.findOne(Long.parseLong(idString)));
        }
        return SUCCESS;
    }

    public Prediction getPrediction() {
        return prediction;
    }

    public void setPrediction(Prediction prediction) {
        this.prediction = prediction;
    }

    public Dataset getPredictionDataset() {
        return predictionDataset;
    }

    public void setPredictionDataset(Dataset predictionDataset) {
        this.predictionDataset = predictionDataset;
    }

    public List<Predictor> getPredictors() {
        return predictors;
    }

    public void setPredictors(List<Predictor> predictors) {
        this.predictors = predictors;
    }
}
