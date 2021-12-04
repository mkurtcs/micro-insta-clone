package com.instamic.authservice.service;

import com.instamic.authservice.model.User;
import com.instamic.authservice.model.request.SignUpRequest;

public interface UserService {

    User registerUser(SignUpRequest request);
}
