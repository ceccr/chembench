package edu.unc.ceccr.chembench.persistence;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.io.Serializable;
import java.util.List;

@NoRepositoryBean
interface BaseRepository<T, ID extends Serializable> extends Repository<T, ID> {

    void delete(T deleted);

    List<T> findAll();

    T findOne(ID id);

    T save(T persisted);
}
