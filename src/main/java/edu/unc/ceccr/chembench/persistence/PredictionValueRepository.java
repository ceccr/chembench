package edu.unc.ceccr.chembench.persistence;

import java.util.List;

public interface PredictionValueRepository extends BaseRepository<PredictionValue, Long> {
    List<PredictionValue> findByPredictionId(Long predictionId);

    long countByPredictionIdAndPredictorId(Long predictionId, Long predictorId);
}
