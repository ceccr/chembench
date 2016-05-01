package edu.unc.ceccr.chembench.persistence;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.utilities.Utility;
import edu.unc.ceccr.chembench.workflows.datasets.DatasetFileOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

@Component
public class CompoundPredictionsRepositoryImpl implements CompoundPredictionsRepository {
    // not actually a Spring Data repository because CompoundPredictions is not persisted yet
    // however, our application code tries to fetch CompoundPredictions objects in multiple places, and it needs to
    // be refactored somewhere
    private static final Logger logger = LoggerFactory.getLogger(CompoundPredictionsRepositoryImpl.class);

    private final DatasetRepository datasetRepository;
    private final PredictionRepository predictionRepository;
    private final PredictionValueRepository predictionValueRepository;

    @Autowired
    public CompoundPredictionsRepositoryImpl(DatasetRepository datasetRepository,
                                             PredictionRepository predictionRepository,
                                             PredictionValueRepository predictionValueRepository) {
        this.datasetRepository = datasetRepository;
        this.predictionRepository = predictionRepository;
        this.predictionValueRepository = predictionValueRepository;
    }

    public List<CompoundPredictions> findByPredictionId(Long predictionId) {
        Prediction prediction = predictionRepository.findOne(predictionId);
        Dataset dataset = datasetRepository.findOne(prediction.getDatasetId());
        Path datasetPath = dataset.getDirectoryPath();
        List<String> compounds = null;
        try {
            if (dataset.getXFile() != null && !dataset.getXFile().isEmpty()) {
                compounds = DatasetFileOperations.getXCompoundNames(datasetPath.resolve(dataset.getXFile()));
            } else {
                compounds = DatasetFileOperations.getSdfCompoundNames(datasetPath.resolve(dataset.getSdfFile()));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to retrieve compound names", e);
        }

        final Splitter splitter = Splitter.on(CharMatcher.WHITESPACE).omitEmptyStrings();
        List<PredictionValue> allPredictionValues = Lists.newArrayList();
        for (String idString : splitter.split(prediction.getPredictorIds())) {
            allPredictionValues.addAll(predictionValueRepository
                    .findByPredictionIdAndPredictorId(predictionId, Long.parseLong(idString)));
        }

        Map<String, List<PredictionValue>> predictionValueMap = Maps.newHashMap();
        for (PredictionValue pv : allPredictionValues) {
            List<PredictionValue> compoundPredictionValues = predictionValueMap.get(pv.getCompoundName());
            if (compoundPredictionValues == null) {
                compoundPredictionValues = Lists.newArrayList();
            }
            compoundPredictionValues.add(pv);
            predictionValueMap.put(pv.getCompoundName(), compoundPredictionValues);
        }

        // get prediction values for each compound
        List<CompoundPredictions> cps = Lists.newArrayList();
        for (String compound : compounds) {
            CompoundPredictions cp = new CompoundPredictions();
            cp.setCompound(compound);

            // get the prediction values for this compound
            cp.setPredictionValues(predictionValueMap.get(cp.getCompound()));

            // round them to a reasonable number of significant figures
            if (cp.getPredictionValues() != null) {
                for (PredictionValue pv : cp.getPredictionValues()) {
                    int sigfigs = Constants.REPORTED_SIGNIFICANT_FIGURES;
                    if (pv.getPredictedValue() != null) {
                        String predictedValue =
                                DecimalFormat.getInstance().format(pv.getPredictedValue()).replaceAll(",", "");
                        pv.setPredictedValue(
                                Float.parseFloat(Utility.roundSignificantFigures(predictedValue, sigfigs)));
                    }
                    if (pv.getStandardDeviation() != null) {
                        String stddev =
                                DecimalFormat.getInstance().format(pv.getStandardDeviation()).replaceAll(",", "");
                        pv.setStandardDeviation(Float.parseFloat(Utility.roundSignificantFigures(stddev, sigfigs)));
                    }
                }
            }
            cps.add(cp);
        }
        return cps;
    }
}
