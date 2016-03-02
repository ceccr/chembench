package edu.unc.ceccr.chembench.persistence;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.utilities.Utility;
import edu.unc.ceccr.chembench.workflows.datasets.DatasetFileOperations;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Component
public class CompoundPredictionsRepositoryImpl implements CompoundPredictionsRepository {
    // not actually a Spring Data repository because CompoundPredictions is not persisted yet
    // however, our application code tries to fetch CompoundPredictions objects in multiple places, and it needs to
    // be refactored somewhere
    private static final Logger logger = Logger.getLogger(CompoundPredictionsRepositoryImpl.class);

    private final DatasetRepository datasetRepository;
    private final PredictionRepository predictionRepository;
    private final PredictionValueRepository predictionValueRepository;

    @Autowired
    public CompoundPredictionsRepositoryImpl(DatasetRepository datasetRepository, PredictionRepository predictionRepository,
                                             PredictionValueRepository predictionValueRepository) {
        this.datasetRepository = datasetRepository;
        this.predictionRepository = predictionRepository;
        this.predictionValueRepository = predictionValueRepository;
    }

    public List<CompoundPredictions> findByDatasetIdAndPredictionId(Long datasetId, Long predictionId) {
        Dataset dataset = datasetRepository.findOne(datasetId);
        Prediction prediction = predictionRepository.findOne(predictionId);
        Path datasetPath = dataset.getDirectoryPath();
        List<String> compounds = null;

        try {
            if (dataset.getXFile() != null && !dataset.getXFile().isEmpty()) {
                compounds = DatasetFileOperations.getXCompoundNames(datasetPath.resolve(dataset.getXFile()));
                logger.info("" + compounds.size() + " compounds found in X file.");
            } else {
                compounds = DatasetFileOperations.getSDFCompoundNames(datasetPath.resolve(dataset.getSdfFile()));
                logger.info("" + compounds.size() + " compounds found in SDF.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to retrieve compound names", e);
        }
        List<PredictionValue> predictorPredictionValues =
                predictionValueRepository.findByPredictionId(prediction.getId());

        Collections.sort(predictorPredictionValues, new Comparator<PredictionValue>() {
            public int compare(PredictionValue p1, PredictionValue p2) {
                return p1.getPredictorId().compareTo(p2.getPredictorId());
            }
        });

        Map<String, List<PredictionValue>> predictionValueMap = Maps.newHashMap();
        for (PredictionValue pv : predictorPredictionValues) {
            List<PredictionValue> compoundPredValues = predictionValueMap.get(pv.getCompoundName());
            if (compoundPredValues == null) {
                compoundPredValues = Lists.newArrayList();
            }
            compoundPredValues.add(pv);
            predictionValueMap.put(pv.getCompoundName(), compoundPredValues);
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
