package edu.unc.ceccr.chembench.persistence;

import java.util.List;

public interface KnnPlusModelRepository extends BaseRepository<KnnPlusModel, Long> {
    List<KnnPlusModel> findByPredictorId(Long predictorId);
}
