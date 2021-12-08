package com.instamic.authservice.service;

import com.instamic.authservice.model.User;
import com.instamic.authservice.model.request.LoginRequest;
import com.instamic.authservice.model.request.SignUpRequest;
import com.instamic.authservice.model.response.LoginResponse;

import java.util.Optional;

public interface UserService {

    User registerUser(SignUpRequest request);

    LoginResponse loginUser(LoginRequest request);

    Optional<User> findByUsername(String username);
}
