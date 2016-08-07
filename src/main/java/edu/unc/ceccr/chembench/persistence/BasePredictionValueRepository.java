package edu.unc.ceccr.chembench.persistence;

import java.util.List;

public interface BasePredictionValueRepository extends BaseRepository<PredictionValue, Long> {
    List<PredictionValue> findByPredictionIdOrderByPredictorIdAsc(Long predictionId);

    List<PredictionValue> findByPredictionIdAndPredictorId(Long predictionId, Long predictorId);
}
