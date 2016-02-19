package edu.unc.ceccr.chembench.persistence;

import java.util.List;

public interface KnnModelRepository extends BaseRepository<KnnModel, Long> {
    List<KnnModel> findByPredictorId(Long predictorId);
}
