package edu.unc.ceccr.chembench.persistence;

import java.util.List;

public interface JobRepository extends BaseRepository<Job, Long> {
    List<Job> findByUserName(String userName);
}
