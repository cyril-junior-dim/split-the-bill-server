package com.splitthebill.server.repository;

import com.splitthebill.server.model.user.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface PersonRepository extends CrudRepository<Person, Long> {
}
