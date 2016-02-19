package edu.unc.ceccr.chembench.persistence;

import java.util.List;

public interface SvmModelRepository extends BaseRepository<SvmModel, Long> {
    List<SvmModel> findByPredictorId(Long predictorId);
}
