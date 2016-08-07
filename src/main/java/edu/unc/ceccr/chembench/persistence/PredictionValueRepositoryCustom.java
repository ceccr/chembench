package edu.unc.ceccr.chembench.persistence;

import java.util.List;

interface PredictionValueRepositoryCustom {
    List<PredictionValue> findByPredictionId(Long predictionId);

    List<PredictionValue> findByPredictionIdAndPredictorId(Long predictionId, Long predictorId);
}
