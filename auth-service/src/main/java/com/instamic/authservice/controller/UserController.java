package com.instamic.authservice.controller;

import com.instamic.authservice.model.User;
import com.instamic.authservice.model.UserSummary;
import com.instamic.authservice.model.request.LoginRequest;
import com.instamic.authservice.model.request.SignUpRequest;
import com.instamic.authservice.model.response.LoginResponse;
import com.instamic.authservice.security.UserDetailsImpl;
import com.instamic.authservice.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {

    private final AuthService authService;

    public UserController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping
    ResponseEntity<User> registerUser(@RequestBody SignUpRequest request) {
        return new ResponseEntity<>(authService.registerUser(request), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    ResponseEntity<LoginResponse> loginUser(@RequestBody @Valid LoginRequest request) {
        return new ResponseEntity<>(authService.loginUser(request), HttpStatus.OK);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    ResponseEntity<UserSummary> getCurrentUser(@AuthenticationPrincipal UserDetailsImpl loggedUser) {
        return new ResponseEntity<>(authService.getCurrentUser(loggedUser), HttpStatus.OK);
    }

}
