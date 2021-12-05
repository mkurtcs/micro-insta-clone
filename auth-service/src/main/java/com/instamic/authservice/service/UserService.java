package com.instamic.authservice.service;

import com.instamic.authservice.model.User;
import com.instamic.authservice.model.request.SignUpRequest;

import java.util.Optional;

public interface UserService {

    User registerUser(SignUpRequest request);

    Optional<User> findByUsername(String username);
}
