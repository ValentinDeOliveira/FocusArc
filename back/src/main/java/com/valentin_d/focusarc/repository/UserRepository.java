package com.valentin_d.focusarc.repository;

import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.model.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, UserId> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}