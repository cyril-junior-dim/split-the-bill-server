package com.splitthebill.server.repository;

import com.splitthebill.server.model.Group;
import com.splitthebill.server.model.user.Person;
import com.splitthebill.server.model.user.PersonGroup;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonGroupRepository extends CrudRepository<PersonGroup, Long> {

    boolean existsByIdAndGroup(Long id, Group group);
    Optional<PersonGroup> findByPersonAndGroup_Id(Person person, Long groupId);
}
