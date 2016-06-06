package edu.unc.ceccr.chembench.persistence;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PredictionRepository extends BaseRepository<Prediction, Long> {
    @Query("select p from Prediction p where p.jobCompleted = 'YES' and p.userName = :userName")
    List<Prediction> findByUserName(@Param("userName") String userName);

    Prediction findByNameAndUserName(String name, String userName);
}
