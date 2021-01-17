package com.splitthebill.server.repository;

import com.splitthebill.server.model.user.ThirdPartyUserAccount;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ThirdPartyUserAccountRepository extends CrudRepository<ThirdPartyUserAccount, Long> {
    Optional<ThirdPartyUserAccount> findByEmail(String email);
    boolean existsByEmail(String email);
}
