package com.splitthebill.server.repository;

import com.splitthebill.server.model.Group;
import com.splitthebill.server.model.user.PersonGroup;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonGroupRepository extends CrudRepository<PersonGroup, Long> {

    boolean existsByIdAndGroup(Long id, Group group);
}
