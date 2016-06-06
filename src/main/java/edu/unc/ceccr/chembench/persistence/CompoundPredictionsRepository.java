package edu.unc.ceccr.chembench.persistence;

import java.util.List;

public interface CompoundPredictionsRepository {
    List<CompoundPredictions> findByPredictionId(Long predictionId);
}
