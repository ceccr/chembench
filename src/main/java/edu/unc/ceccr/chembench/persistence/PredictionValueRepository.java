package edu.unc.ceccr.chembench.persistence;

import java.util.List;

public interface PredictionValueRepository extends BaseRepository<PredictionValue, Long>,
        PredictionValueRepositoryCustom {
    List<PredictionValue> findByPredictionIdOrderByPredictorIdAsc(Long predictionId);

    List<PredictionValue> findByPredictionIdAndPredictorId(Long predictionId, Long predictorId);
}
