package com.splitthebill.server.repository;

import com.splitthebill.server.model.user.PersonGroup;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonGroupRepository extends CrudRepository<PersonGroup, Long> {
}
