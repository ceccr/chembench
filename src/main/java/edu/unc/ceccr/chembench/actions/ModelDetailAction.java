package edu.unc.ceccr.chembench.actions;

import com.google.common.base.Splitter;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.*;
import edu.unc.ceccr.chembench.utilities.Utility;
import edu.unc.ceccr.chembench.workflows.calculations.ConfusionMatrix;
import edu.unc.ceccr.chembench.workflows.calculations.PredictorEvaluation;
import edu.unc.ceccr.chembench.workflows.modelingPrediction.RandomForest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ModelDetailAction extends DetailAction {
    private static final Logger logger = LoggerFactory.getLogger(ModelDetailAction.class);

    private final DatasetRepository datasetRepository;
    private final PredictorRepository predictorRepository;
    private final RandomForestTreeRepository randomForestTreeRepository;
    private final SvmModelRepository svmModelRepository;
    private final KnnPlusModelRepository knnPlusModelRepository;
    private final RandomForestParametersRepository randomForestParametersRepository;
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
    private ExternalValidationGroup[] evGroups;
    private List<?> models;
    private List<?> yRandomModels;
    private boolean isYRandom;
    private int foldNumber = 0;
    private Iterable<Integer> foldNumbers;
    private Object data;
    private String importanceMeasure;
    private List<Map<String, Double>> randomForestDescriptorImportances;

    @Autowired
    public ModelDetailAction(DatasetRepository datasetRepository, PredictorRepository predictorRepository,
                             RandomForestGroveRepository randomForestGroveRepository,
                             RandomForestTreeRepository randomForestTreeRepository,
                             SvmModelRepository svmModelRepository, KnnPlusModelRepository knnPlusModelRepository,
                             RandomForestParametersRepository randomForestParametersRepository,
                             KnnPlusParametersRepository knnPlusParametersRepository,
                             SvmParametersRepository svmParametersRepository,
                             ExternalValidationRepository externalValidationRepository) {
        this.datasetRepository = datasetRepository;
        this.predictorRepository = predictorRepository;
        this.randomForestGroveRepository = randomForestGroveRepository;
        this.randomForestTreeRepository = randomForestTreeRepository;
        this.svmModelRepository = svmModelRepository;
        this.knnPlusModelRepository = knnPlusModelRepository;
        this.randomForestParametersRepository = randomForestParametersRepository;
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
            if (predictor.getModelMethod().startsWith(Constants.RANDOMFOREST)) {
                randomForestDescriptorImportances = new ArrayList<>(childPredictors.size() + 1);
                randomForestDescriptorImportances.add(0, null);
                Map<String, Double> averagedImportance = new HashMap<>();
                for (int i = 0; i < childPredictors.size(); i++) {
                    Predictor child = childPredictors.get(i);
                    Map<String, Double> childImportance = readRandomForestDescriptorImportance(child, predictor);
                    randomForestDescriptorImportances.add(childImportance);
                    for (String key : childImportance.keySet()) {
                        Double childValue = childImportance.get(key);
                        Double totalValue = averagedImportance.get(key);
                        if (totalValue == null) {
                            totalValue = 0d;
                        }
                        averagedImportance.put(key, childValue + totalValue);
                    }
                }
                for (String key : averagedImportance.keySet()) {
                    averagedImportance.put(key, averagedImportance.get(key) / childPredictors.size());
                }
                randomForestDescriptorImportances.set(0, averagedImportance);
            }
        } else {
            models = readModels(predictor, false);
            yRandomModels = readModels(predictor, true);
            evGroups = new ExternalValidationGroup[1];
            evGroups[0] = buildExternalValidationGroup(predictor);
            if (predictor.getModelMethod().startsWith(Constants.RANDOMFOREST)) {
                randomForestDescriptorImportances.add(readRandomForestDescriptorImportance(predictor, null));
            }
        }
        switch (predictor.getModelMethod()) {
            case Constants.RANDOMFOREST:
            case Constants.RANDOMFOREST_R:
                modelParameters = randomForestParametersRepository.findOne(predictor.getModelingParametersId());
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

    private Map<String, Double> readRandomForestDescriptorImportance(Predictor p, Predictor parent) {
        Path basePath = Paths.get(Constants.CECCR_USER_BASE_PATH, p.getUserName(), "PREDICTORS");
        if (parent != null) {
            basePath = basePath.resolve(parent.getName());
        }
        basePath = basePath.resolve(p.getName());

        Map<String, Double> data = Maps.newHashMap();
        if (p.getModelMethod().equals(Constants.RANDOMFOREST)) {
            importanceMeasure = "Relative Importance";
            data = RandomForest.getDescriptorImportance(basePath);
        } else if (p.getModelMethod().equals(Constants.RANDOMFOREST_R)) {
            // XXX new models will never generate more than one RData file (named "RF_rand_sets_0_trn0.RData")
            // however, old models may have more than one RData file due to how splitting was implemented for legacy RF.
            // (in the past we allowed RF models to have more than one split.)
            // in these cases the descriptor importance table will only show the importance data for the first split.
            String[] filenames = basePath.toFile().list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".rdata");
                }
            });
            Arrays.sort(filenames);

            File outFile = basePath.resolve("importance.csv").toFile();
            int exitValue = 0;
            if (outFile.length() == 0) {
                try {
                    ProcessBuilder pb = new ProcessBuilder("Rscript",
                            Paths.get(Constants.CECCR_BASE_PATH, Constants.SCRIPTS_PATH, "get_importance.R").toString(),
                            basePath.resolve(filenames[0]).toString());
                    pb.redirectOutput(outFile);
                    exitValue = pb.start().waitFor();
                } catch (IOException e) {
                    throw new RuntimeException("R descriptor importance extraction failed", e);
                } catch (InterruptedException e) {
                    throw new RuntimeException("Interrupted while waiting for descriptor importance extraction", e);
                }

                if (outFile.length() == 0) {
                    throw new RuntimeException("Descriptor importance extraction produced no output");
                } else if (exitValue != 0) {
                    outFile.delete();
                    throw new RuntimeException(
                            "Descriptor importance extraction exited with non-zero exit code: " + exitValue);
                }
            }

            Splitter splitter = Splitter.on('\t');
            try (BufferedReader reader = Files.newBufferedReader(outFile.toPath(), StandardCharsets.UTF_8)) {
                List<String> headerFields = splitter.splitToList(reader.readLine());
                // XXX if the RF call has importance = FALSE (the default), only IncNodePurity (continuous) or
                // MeanDecreaseGini (category) is generated. If importance = TRUE, then %IncMse (continuous)
                // or MeanDecreaseAccuracy (category) are also generated. ideally we'd report both measures, but for
                // now,
                // report IncNodePurity / MeanDecreaseGini as that'll always be there
                int importanceMeasureIndex = -1;
                for (int i = 0; i < headerFields.size(); i++) {
                    String currField = headerFields.get(i);
                    if (currField.equals("IncNodePurity") || currField.equals("MeanDecreaseGini")) {
                        importanceMeasureIndex = i;
                        importanceMeasure = currField;
                        break;
                    }
                }
                String line;
                while ((line = reader.readLine()) != null) {
                    List<String> fields = splitter.splitToList(line);
                    data.put(fields.get(0), Double.parseDouble(fields.get(importanceMeasureIndex)));
                }
            } catch (IOException e) {
                throw new RuntimeException("Couldn't read descriptor importance output", e);
            }
        }
        return data;
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
            displayedValue.predictedValueStandardDeviation = Utility.safeStringToFloat(extVal.getStandDev());
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
        // XXX ExternalValidation#get/setNumTotalModels() is declared transient, so if it is not set after fetching
        // from the db all the total model values will be zero. Set these based on the parent predictor now.
        // (As for _why_ we don't persist that value I'm not sure, because this value shouldn't change.)
        for (ExternalValidation ev : extVals) {
            if (predictor.getModelMethod().equals(Constants.KNNGA)) {
                // for KNNGA specifically we use the model count as the total number of models
                ev.setNumTotalModels(knnPlusModelRepository.findByPredictorId(predictor.getId()).size());
            } else {
                // all other types (including KNN_SA_)
                ev.setNumTotalModels(predictor.getNumTotalModels());
            }
        }
        List<Double> residuals = PredictorEvaluation.calculateResiduals(extVals);
        evGroup.extVals = extVals;
        evGroup.residuals = residuals;
        List<DisplayedExternalValidationValue> displayedExtVals =
                buildDisplayedExternalValidationValues(extVals, residuals,
                        predictor.getModelMethod().startsWith(Constants.RANDOMFOREST));
        ConfusionMatrix confusionMatrix = null;
        ContinuousStatistics continuousStatistics = null;
        if (modelingDataset.isCategory()) {
            confusionMatrix = PredictorEvaluation.calculateConfusionMatrix(extVals);
        } else if (modelingDataset.isContinuous()) {
            continuousStatistics = new ContinuousStatistics();
            continuousStatistics.qSquared = PredictorEvaluation.calculateQSquared(extVals, residuals);
            continuousStatistics.rmse = PredictorEvaluation.calculateRmse(extVals);
            continuousStatistics.mae = PredictorEvaluation.calculateMae(extVals);
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

    public Object getData() {
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

    public String getImportanceMeasure() {
        return importanceMeasure;
    }

    public void setImportanceMeasure(String importanceMeasure) {
        this.importanceMeasure = importanceMeasure;
    }

    public List<Map<String, Double>> getRandomForestDescriptorImportances() {
        return randomForestDescriptorImportances;
    }

    public void setRandomForestDescriptorImportances(List<Map<String, Double>> randomForestDescriptorImportances) {
        this.randomForestDescriptorImportances = randomForestDescriptorImportances;
    }

    private static class ContinuousStatistics {
        private Double qSquared;
        private Double mae;
        private Double stddev;
        private Double rmse;

        public Double getQsquared() {
            return qSquared;
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
                confusionMatrix = PredictorEvaluation.calculateConfusionMatrix(allExtVals);
            } else if (activityType.equals(Constants.CONTINUOUS)) {
                continuousStatistics = new ContinuousStatistics();
                continuousStatistics.qSquared = PredictorEvaluation.calculateQSquared(allExtVals, allResiduals);
                continuousStatistics.rmse = PredictorEvaluation.calculateRmse(allExtVals);
                continuousStatistics.mae = PredictorEvaluation.calculateMae(allExtVals);
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

    private static class DisplayedExternalValidationValue {
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
