package edu.unc.ceccr.chembench.actions;

import com.google.common.collect.Lists;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ModelDetailAction extends DetailAction {
    private static final Logger logger = Logger.getLogger(ModelDetailAction.class);
    private final DatasetRepository datasetRepository;
    private final PredictorRepository predictorRepository;
    private final RandomForestGroveRepository randomForestGroveRepository;
    private final RandomForestTreeRepository randomForestTreeRepository;
    private final SvmModelRepository svmModelRepository;
    private final KnnModelRepository knnModelRepository;
    private final KnnPlusModelRepository knnPlusModelRepository;
    private List<?> models;
    private List<?> yRandomModels;
    private Predictor predictor;
    private boolean editable;
    private List<Integer> foldNumbers;
    private List<List<?>> foldModels;
    private List<List<?>> foldYRandomModels;

    @Autowired
    public ModelDetailAction(DatasetRepository datasetRepository, PredictorRepository predictorRepository,
                             RandomForestGroveRepository randomForestGroveRepository,
                             RandomForestTreeRepository randomForestTreeRepository,
                             SvmModelRepository svmModelRepository, KnnModelRepository knnModelRepository,
                             KnnPlusModelRepository knnPlusModelRepository) {
        this.datasetRepository = datasetRepository;
        this.predictorRepository = predictorRepository;
        this.randomForestGroveRepository = randomForestGroveRepository;
        this.randomForestTreeRepository = randomForestTreeRepository;
        this.svmModelRepository = svmModelRepository;
        this.knnModelRepository = knnModelRepository;
        this.knnPlusModelRepository = knnPlusModelRepository;
    }

    public String execute() {
        predictor = predictorRepository.findOne(id);
        String result = validateObject(predictor);
        if (!result.equals(SUCCESS)) {
            return result;
        }
        editable = predictor.isEditableBy(user);
        Dataset modelingDataset = datasetRepository.findOne(predictor.getDatasetId());
        predictor.setDatasetDisplay(modelingDataset.getName());
        if (predictor.getChildType().equals(Constants.NFOLD)) {
            foldNumbers = Lists.newArrayList();
            foldModels = Lists.newArrayList();
            foldYRandomModels = Lists.newArrayList();
            int currentFoldNumber = 1;
            for (Predictor childPredictor : predictorRepository.findByParentId(predictor.getId())) {
                foldNumbers.add(currentFoldNumber++);
                foldModels.add(readModels(childPredictor, false));
                foldYRandomModels.add(readModels(childPredictor, true));
            }
        } else {
            models = readModels(predictor, false);
            yRandomModels = readModels(predictor, true);
        }
        return SUCCESS;
    }

    private List<?> readModels(Predictor predictor, boolean isYRandom) {
        String isYRandomString = (isYRandom) ? Constants.YES : Constants.NO;
        switch (predictor.getModelMethod()) {
            case Constants.RANDOMFOREST:
            case Constants.RANDOMFOREST_R:
                RandomForestGrove grove = randomForestGroveRepository
                        .findByPredictorIdAndIsYRandomModel(predictor.getId(), isYRandomString);
                return randomForestTreeRepository.findByRandomForestGroveId(grove.getId());
            case Constants.SVM:
                return svmModelRepository.findByPredictorIdAndIsYRandomModel(predictor.getId(), isYRandomString);
            case Constants.KNNSA: // knn+
            case Constants.KNNGA:
                return knnPlusModelRepository.findByPredictorIdAndIsYRandomModel(predictor.getId(), isYRandomString);
            case Constants.KNN: // legacy knn
                return (isYRandom) ? Lists.newArrayList() : knnModelRepository.findByPredictorId(predictor.getId());
        }
        logger.warn("Unrecognized model method: " + predictor.getModelMethod());
        return Lists.newArrayList();
    }

    public Predictor getPredictor() {
        return predictor;
    }

    public void setPredictor(Predictor predictor) {
        this.predictor = predictor;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public List<?> getModels() {
        return models;
    }

    public void setModels(List<?> models) {
        this.models = models;
    }

    public List<?> getYRandomModels() {
        return yRandomModels;
    }

    public void setYRandomModels(List<?> yRandomModels) {
        this.yRandomModels = yRandomModels;
    }

    public List<Integer> getFoldNumbers() {
        return foldNumbers;
    }

    public void setFoldNumbers(List<Integer> foldNumbers) {
        this.foldNumbers = foldNumbers;
    }

    public List<List<?>> getFoldModels() {
        return foldModels;
    }

    public void setFoldModels(List<List<?>> foldModels) {
        this.foldModels = foldModels;
    }

    public List<List<?>> getFoldYRandomModels() {
        return foldYRandomModels;
    }

    public void setFoldYRandomModels(List<List<?>> foldYRandomModels) {
        this.foldYRandomModels = foldYRandomModels;
    }
}
