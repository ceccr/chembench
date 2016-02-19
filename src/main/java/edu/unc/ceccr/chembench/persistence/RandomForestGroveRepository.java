package edu.unc.ceccr.chembench.persistence;

import java.util.List;

public interface RandomForestGroveRepository extends BaseRepository<RandomForestGrove, Long> {
    List<RandomForestGrove> findByPredictorId(Long predictorId);
}
