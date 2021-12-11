package com.instamic.authservice.service;

import com.instamic.authservice.model.User;
import com.instamic.authservice.model.request.LoginRequest;
import com.instamic.authservice.model.request.SignUpRequest;
import com.instamic.authservice.model.response.LoginResponse;

public interface AuthService {

    LoginResponse loginUser(LoginRequest request);

    User registerUser(SignUpRequest request);

}
