package com.splitthebill.server.repository;

import com.splitthebill.server.model.user.Friendship;
import com.splitthebill.server.model.user.Person;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends CrudRepository<Friendship, Long> {
    List<Friendship> getAllByPerson1AndConfirmed(Person person, boolean confirmed);
    List<Friendship> getAllByPerson2AndConfirmed(Person person, boolean confirmed);
    Optional<Friendship> getByPerson1AndPerson2AndConfirmedIsTrue(Person person1, Person person2);
}
