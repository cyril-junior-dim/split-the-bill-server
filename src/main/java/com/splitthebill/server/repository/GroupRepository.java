package com.splitthebill.server.repository;

import com.splitthebill.server.model.Group;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface GroupRepository extends CrudRepository<Group, Long> {
}
