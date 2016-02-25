package edu.unc.ceccr.chembench.persistence;

import java.util.List;

public interface CompoundPredictionsRepository {
    List<CompoundPredictions> findByDatasetIdAndPredictionId(Long datasetId, Long predictionId);
}
