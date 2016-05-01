package edu.unc.ceccr.chembench.actions;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.*;
import edu.unc.ceccr.chembench.workflows.calculations.ConfusionMatrix;
import edu.unc.ceccr.chembench.workflows.calculations.RSquaredAndCCR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class ModelDetailAction extends DetailAction {
    private static final Logger logger = LoggerFactory.getLogger(ModelDetailAction.class);

    private final DatasetRepository datasetRepository;
    private final PredictorRepository predictorRepository;
    private final RandomForestTreeRepository randomForestTreeRepository;
    private final SvmModelRepository svmModelRepository;
    private final KnnModelRepository knnModelRepository;
    private final KnnPlusModelRepository knnPlusModelRepository;
    private final RandomForestParametersRepository randomForestParametersRepository;
    private final KnnParametersRepository knnParametersRepository;
    private final KnnPlusParametersRepository knnPlusParametersRepository;
    private final SvmParametersRepository svmParametersRepository;
    private final ExternalValidationRepository externalValidationRepository;
    private final RandomForestGroveRepository randomForestGroveRepository;

    private Predictor predictor;
    private Dataset modelingDataset;
    private Object modelParameters;
    private boolean editable;
    private String description;
    private String paperReference;

    // random split
    private ExternalValidationGroup[] evGroups;
    private List<?> models;
    private List<?> yRandomModels;

    // n-fold
    private boolean isYRandom;
    private int foldNumber = 0;
    private Iterable<Integer> foldNumbers;
    private List<?> data;

    @Autowired
    public ModelDetailAction(DatasetRepository datasetRepository, PredictorRepository predictorRepository,
                             RandomForestGroveRepository randomForestGroveRepository,
                             RandomForestTreeRepository randomForestTreeRepository,
                             SvmModelRepository svmModelRepository, KnnModelRepository knnModelRepository,
                             KnnPlusModelRepository knnPlusModelRepository,
                             RandomForestParametersRepository randomForestParametersRepository,
                             KnnParametersRepository knnParametersRepository,
                             KnnPlusParametersRepository knnPlusParametersRepository,
                             SvmParametersRepository svmParametersRepository,
                             ExternalValidationRepository externalValidationRepository) {
        this.datasetRepository = datasetRepository;
        this.predictorRepository = predictorRepository;
        this.randomForestGroveRepository = randomForestGroveRepository;
        this.randomForestTreeRepository = randomForestTreeRepository;
        this.svmModelRepository = svmModelRepository;
        this.knnModelRepository = knnModelRepository;
        this.knnPlusModelRepository = knnPlusModelRepository;
        this.randomForestParametersRepository = randomForestParametersRepository;
        this.knnParametersRepository = knnParametersRepository;
        this.knnPlusParametersRepository = knnPlusParametersRepository;
        this.svmParametersRepository = svmParametersRepository;
        this.externalValidationRepository = externalValidationRepository;
    }

    public String execute() {
        predictor = predictorRepository.findOne(id);
        String result = validateObject(predictor);
        if (!result.equals(SUCCESS)) {
            return result;
        }
        editable = predictor.isEditableBy(user);
        if (request.getMethod().equals("POST")) {
            return updateModel();
        }
        modelingDataset = datasetRepository.findOne(predictor.getDatasetId());
        predictor.setDatasetDisplay(modelingDataset.getName());
        if (predictor.getChildType() != null && predictor.getChildType().equals(Constants.NFOLD)) {
            List<Predictor> childPredictors = predictorRepository.findByParentId(predictor.getId());
            foldNumbers = ContiguousSet.create(Range.closed(1, childPredictors.size()), DiscreteDomain.integers());
            evGroups = new ExternalValidationGroup[childPredictors.size() + 1];
            for (int i = 0; i < childPredictors.size(); i++) {
                // XXX careful: evGroup is 1-indexed, childPredictors.get() is 0-indexed
                evGroups[i + 1] = buildExternalValidationGroup(childPredictors.get(i));
            }
            evGroups[0] = ExternalValidationGroup.coalesce(predictor.getActivityType(), evGroups);
        } else {
            models = readModels(predictor, false);
            yRandomModels = readModels(predictor, true);
            evGroups = new ExternalValidationGroup[1];
            evGroups[0] = buildExternalValidationGroup(predictor);
        }
        switch (predictor.getModelMethod()) {
            case Constants.RANDOMFOREST:
            case Constants.RANDOMFOREST_R:
                modelParameters = randomForestParametersRepository.findOne(predictor.getModelingParametersId());
                break;
            case Constants.KNN: // legacy knn
                modelParameters = knnParametersRepository.findOne(predictor.getModelingParametersId());
                break;
            case Constants.KNNGA: // knn+
            case Constants.KNNSA:
                modelParameters = knnPlusParametersRepository.findOne(predictor.getModelingParametersId());
                break;
            case Constants.SVM:
                modelParameters = svmParametersRepository.findOne(predictor.getModelingParametersId());
                break;
            default:
                logger.warn(
                        String.format("Unrecognized model method %s for predictor id %d", predictor.getModelMethod(),
                                predictor.getId()));
                break;
        }
        return SUCCESS;
    }

    private String updateModel() {
        if (editable && predictor != null && description != null && paperReference != null) {
            predictor.setDescription(description);
            predictor.setPaperReference(paperReference);
            predictorRepository.save(predictor);
            return SUCCESS;
        }
        return ERROR;
    }

    public String getFold() {
        predictor = predictorRepository.findOne(id);
        String result = validateObject(predictor);
        if (!result.equals(SUCCESS)) {
            return result;
        }
        List<Predictor> childPredictors = predictorRepository.findByParentId(predictor.getId());
        if (foldNumber < 1 || foldNumber > childPredictors.size()) {
            return "badrequest";
        }
        data = readModels(childPredictors.get(foldNumber - 1), isYRandom);
        return SUCCESS;
    }

    private List<DisplayedExternalValidationValue> buildDisplayedExternalValidationValues(
            List<ExternalValidation> extVals, List<Double> residuals, boolean isRandomForest) {
        List<DisplayedExternalValidationValue> displayedExtVals = new ArrayList<>();
        assert extVals.size() == residuals.size();
        for (int i = 0; i < extVals.size(); i++) {
            ExternalValidation extVal = extVals.get(i);
            Double residual = residuals.get(i);
            DisplayedExternalValidationValue displayedValue = new DisplayedExternalValidationValue();
            displayedValue.compoundName = extVal.getCompoundId();
            displayedValue.observedValue = extVal.getActualValue();
            displayedValue.predictedValue = extVal.getPredictedValue();
            displayedValue.residual = residual;
            if (!predictor.getModelMethod().startsWith(Constants.RANDOMFOREST)) {
                displayedValue.predictingModels = extVal.getNumModels();
                displayedValue.totalModels = extVal.getNumTotalModels();
            }
            displayedExtVals.add(displayedValue);
        }
        return displayedExtVals;
    }

    private ExternalValidationGroup buildExternalValidationGroup(Predictor predictor) {
        ExternalValidationGroup evGroup = new ExternalValidationGroup();
        List<ExternalValidation> extVals = externalValidationRepository.findByPredictorId(predictor.getId());
        List<Double> residuals = RSquaredAndCCR.calculateResiduals(extVals);
        evGroup.extVals = extVals;
        evGroup.residuals = residuals;
        List<DisplayedExternalValidationValue> displayedExtVals =
                buildDisplayedExternalValidationValues(extVals, residuals,
                        predictor.getModelMethod().startsWith(Constants.RANDOMFOREST));
        ConfusionMatrix confusionMatrix = null;
        ContinuousStatistics continuousStatistics = null;
        if (modelingDataset.isCategory()) {
            confusionMatrix = RSquaredAndCCR.calculateConfusionMatrix(extVals);
        } else if (modelingDataset.isContinuous()) {
            continuousStatistics = new ContinuousStatistics();
            continuousStatistics.rSquared = RSquaredAndCCR.calculateRSquared(extVals, residuals);
        }
        evGroup.displayedExternalValidationValues = displayedExtVals;
        evGroup.confusionMatrix = confusionMatrix;
        evGroup.continuousStatistics = continuousStatistics;
        return evGroup;
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
                return (isYRandom) ? new ArrayList<>() : knnModelRepository.findByPredictorId(predictor.getId());
        }
        logger.warn("Unrecognized model method: " + predictor.getModelMethod());
        return new ArrayList<>();
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

    public Iterable<Integer> getFoldNumbers() {
        return foldNumbers;
    }

    public void setFoldNumbers(Iterable<Integer> foldNumbers) {
        this.foldNumbers = foldNumbers;
    }

    public boolean getIsYRandom() {
        return isYRandom;
    }

    public void setIsYRandom(boolean YRandom) {
        isYRandom = YRandom;
    }

    public int getFoldNumber() {
        return foldNumber;
    }

    public void setFoldNumber(int foldNumber) {
        this.foldNumber = foldNumber;
    }

    public List<?> getData() {
        return data;
    }

    public void setData(List<?> data) {
        this.data = data;
    }

    public Dataset getModelingDataset() {
        return modelingDataset;
    }

    public void setModelingDataset(Dataset modelingDataset) {
        this.modelingDataset = modelingDataset;
    }

    public Object getModelParameters() {
        return modelParameters;
    }

    public void setModelParameters(Object modelParameters) {
        this.modelParameters = modelParameters;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPaperReference() {
        return paperReference;
    }

    public void setPaperReference(String paperReference) {
        this.paperReference = paperReference;
    }

    public boolean isYRandom() {
        return isYRandom;
    }

    public void setYRandom(boolean YRandom) {
        isYRandom = YRandom;
    }

    public List<?> getyRandomModels() {
        return yRandomModels;
    }

    public void setyRandomModels(List<?> yRandomModels) {
        this.yRandomModels = yRandomModels;
    }

    public ExternalValidationGroup[] getEvGroups() {
        return evGroups;
    }

    private static class ContinuousStatistics {
        private Double rSquared;
        private Double mae;
        private Double stddev;
        private Double rmse;

        public Double getrSquared() {
            return rSquared;
        }

        public Double getMae() {
            return mae;
        }

        public Double getStddev() {
            return stddev;
        }

        public Double getRmse() {
            return rmse;
        }
    }

    private static class ExternalValidationGroup {
        private ContinuousStatistics continuousStatistics;
        private ConfusionMatrix confusionMatrix;
        private List<DisplayedExternalValidationValue> displayedExternalValidationValues;
        private List<ExternalValidation> extVals; // for internal use only. needed to coalesce
        private List<Double> residuals; // for internal use only. needed to coalesce

        static ExternalValidationGroup coalesce(String activityType, ExternalValidationGroup... groups) {
            ExternalValidationGroup combined = new ExternalValidationGroup();
            List<DisplayedExternalValidationValue> allDisplayedExtVals = new ArrayList<>();
            List<ExternalValidation> allExtVals = new ArrayList<>();
            List<Double> allResiduals = new ArrayList<>();
            for (ExternalValidationGroup group : groups) {
                if (group != null) {
                    allExtVals.addAll(group.extVals);
                    allResiduals.addAll(group.residuals);
                    allDisplayedExtVals.addAll(group.displayedExternalValidationValues);
                }
            }
            ConfusionMatrix confusionMatrix = null;
            ContinuousStatistics continuousStatistics = null;
            if (activityType.equals(Constants.CATEGORY)) {
                confusionMatrix = RSquaredAndCCR.calculateConfusionMatrix(allExtVals);
            } else if (activityType.equals(Constants.CONTINUOUS)) {
                continuousStatistics = new ContinuousStatistics();
                continuousStatistics.rSquared = RSquaredAndCCR.calculateRSquared(allExtVals, allResiduals);
            }
            combined.extVals = allExtVals;
            combined.displayedExternalValidationValues = allDisplayedExtVals;
            combined.confusionMatrix = confusionMatrix;
            combined.continuousStatistics = continuousStatistics;
            return combined;
        }

        public ContinuousStatistics getContinuousStatistics() {
            return continuousStatistics;
        }

        public ConfusionMatrix getConfusionMatrix() {
            return confusionMatrix;
        }

        public List<DisplayedExternalValidationValue> getDisplayedExternalValidationValues() {
            return displayedExternalValidationValues;
        }
    }

    private class DisplayedExternalValidationValue {
        private String compoundName;
        private float observedValue;
        private float predictedValue;
        private float predictedValueStandardDeviation;
        private Double residual;
        private int predictingModels;
        private int totalModels;

        public String getCompoundName() {
            return compoundName;
        }

        public float getObservedValue() {
            return observedValue;
        }

        public float getPredictedValue() {
            return predictedValue;
        }

        public float getPredictedValueStandardDeviation() {
            return predictedValueStandardDeviation;
        }

        public Double getResidual() {
            return residual;
        }

        public int getPredictingModels() {
            return predictingModels;
        }

        public int getTotalModels() {
            return totalModels;
        }
    }
}
