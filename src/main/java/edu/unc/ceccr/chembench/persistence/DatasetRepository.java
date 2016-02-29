package edu.unc.ceccr.chembench.persistence;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DatasetRepository extends BaseRepository<Dataset, Long> {
    @Query("select d from Dataset d where d.jobCompleted = 'YES' and d.userName = :userName")
    List<Dataset> findByUserName(@Param("userName") String userName);

    @Query("select d from Dataset d where d.userName = edu.unc.ceccr.chembench.global.Constants.ALL_USERS_USERNAME")
    List<Dataset> findAllPublicDatasets();

    @Query("select d from Dataset d where d.userName = edu.unc.ceccr.chembench.global.Constants.ALL_USERS_USERNAME "
            + "and d.showByDefault <> edu.unc.ceccr.chembench.global.Constants.NO")
    List<Dataset> findSomePublicDatasets();

    @Query("select d from Dataset d where d.jobCompleted = 'YES' and d.userName = :userName and d.modelType = "
            + ":activityType")
    List<Dataset> findByUserNameAndActivityType(@Param("userName") String userName,
                                                @Param("activityType") String activityType);

    @Query("select d from Dataset d where d.userName = edu.unc.ceccr.chembench.global.Constants.ALL_USERS_USERNAME "
            + "and d.modelType = edu.unc.ceccr.chembench.global.Constants.CONTINUOUS")
    List<Dataset> findAllPublicContinuousDatasets();

    @Query("select d from Dataset d where d.userName = edu.unc.ceccr.chembench.global.Constants.ALL_USERS_USERNAME "
            + "and d.modelType = edu.unc.ceccr.chembench.global.Constants.CATEGORY")
    List<Dataset> findAllPublicCategoryDatasets();


    @Query("select d from Dataset d where d.userName = edu.unc.ceccr.chembench.global.Constants.ALL_USERS_USERNAME "
            + "and d.showByDefault <> edu.unc.ceccr.chembench.global.Constants.NO and d.modelType = edu.unc.ceccr"
            + ".chembench.global.Constants.CONTINUOUS")
    List<Dataset> findSomePublicContinuousDatasets();


    @Query("select d from Dataset d where d.userName = edu.unc.ceccr.chembench.global.Constants.ALL_USERS_USERNAME "
            + "and d.showByDefault <> edu.unc.ceccr.chembench.global.Constants.NO and d.modelType = edu.unc.ceccr"
            + ".chembench.global.Constants.CATEGORY")
    List<Dataset> findSomePublicCategoryDatasets();

    Dataset findByNameAndUserName(String name, String userName);
}
