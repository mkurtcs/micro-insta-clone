package com.instamic.authservice.controller;

import com.instamic.authservice.model.User;
import com.instamic.authservice.model.request.LoginRequest;
import com.instamic.authservice.model.request.SignUpRequest;
import com.instamic.authservice.model.response.LoginResponse;
import com.instamic.authservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    ResponseEntity<User> registerUser(@RequestBody SignUpRequest request) {
        return new ResponseEntity<>(userService.registerUser(request), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    ResponseEntity<LoginResponse> loginUser(@RequestBody @Valid LoginRequest request) {
        return new ResponseEntity<>(userService.loginUser(request), HttpStatus.OK);
    }
}
