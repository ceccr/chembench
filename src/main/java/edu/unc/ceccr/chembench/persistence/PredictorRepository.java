package edu.unc.ceccr.chembench.persistence;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PredictorRepository extends BaseRepository<Predictor, Long> {
    @Query("select p from Predictor p where p.jobCompleted = edu.unc.ceccr.chembench.global.Constants.YES "
            + "and p.predictorType <> edu.unc.ceccr.chembench.global.Constants.HIDDEN and p.userName = :userName")
    List<Predictor> findByUserName(@Param("userName") String userName);

    @Query("select p from Predictor p where p.predictorType <> edu.unc.ceccr.chembench.global.Constants.HIDDEN "
            + "and p.userName = edu.unc.ceccr.chembench.global.Constants.ALL_USERS_USERNAME")
    List<Predictor> findPublicPredictors();

    @Query("select p from Predictor p where p.jobCompleted = edu.unc.ceccr.chembench.global.Constants.YES "
            + "and p.predictorType <> edu.unc.ceccr.chembench.global.Constants.HIDDEN "
            + "and p.name = :name and p.userName = :userName")
    Predictor findByNameAndUserName(@Param("name") String name, @Param("userName") String userName);

    List<Predictor> findByParentId(Long parentId);
}
