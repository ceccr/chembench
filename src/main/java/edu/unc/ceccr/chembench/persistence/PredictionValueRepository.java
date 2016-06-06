package edu.unc.ceccr.chembench.persistence;

import java.util.List;

public interface PredictionValueRepository extends BasePredictionValueRepository {
    List<PredictionValue> findByPredictionId(Long predictionId);
}
