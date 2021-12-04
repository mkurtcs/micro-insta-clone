package com.instamic.authservice.repository;

import com.instamic.authservice.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}
