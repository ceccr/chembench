package edu.unc.ceccr.chembench.persistence;

import java.util.List;

public interface RandomForestTreeRepository extends BaseRepository<RandomForestTree, Long> {
    List<RandomForestTree> findByRandomForestGroveId(Long randomForestGroveId);
}
