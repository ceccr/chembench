package edu.unc.ceccr.chembench.persistence;

public interface UserRepository extends BaseRepository<User, Long> {
    User findByUserName(String userName);
}
