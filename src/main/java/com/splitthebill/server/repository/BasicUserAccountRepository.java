package com.splitthebill.server.repository;

import com.splitthebill.server.model.user.BasicUserAccount;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BasicUserAccountRepository extends CrudRepository<BasicUserAccount, Long> {
    Optional<BasicUserAccount> findByUsername(String username);
    boolean existsByUsernameOrEmail(String username, String email);
}
