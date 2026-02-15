package com.valentin_d.focusarc.repository;

import com.valentin_d.focusarc.model.User;
import com.valentin_d.focusarc.model.id.UserId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, UserId> {

}
