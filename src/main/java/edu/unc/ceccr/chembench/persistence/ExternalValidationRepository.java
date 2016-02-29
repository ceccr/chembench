package edu.unc.ceccr.chembench.persistence;

import java.util.List;

public interface ExternalValidationRepository extends BaseRepository<ExternalValidation, Long> {
    List<ExternalValidation> findByPredictorId(Long predictorId);
}
