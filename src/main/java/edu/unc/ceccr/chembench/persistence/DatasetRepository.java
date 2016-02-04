package edu.unc.ceccr.chembench.persistence;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DatasetRepository extends BaseRepository<Dataset, Long> {
    @Query("select d from Dataset d where d.jobCompleted = 'YES' and d.userName = :userName")
    List<Dataset> findByUserName(@Param("userName") String userName);

    @Query("select d from Dataset d where d.userName = edu.unc.ceccr.chembench.global.Constants.ALL_USERS_USERNAME")
    List<Dataset> findAllPublicDatasets();

    @Query("select d from Dataset d where d.userName = edu.unc.ceccr.chembench.global.Constants.ALL_USERS_USERNAME and d.showByDefault <> 'NO'")
    List<Dataset> findSomePublicDatasets();
}
