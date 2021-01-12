package com.splitthebill.server.repository;

import com.splitthebill.server.model.user.UserAccount;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface UserAccountRepository extends CrudRepository<UserAccount, Long> {
}
