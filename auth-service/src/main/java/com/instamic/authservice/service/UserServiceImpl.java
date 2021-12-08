package com.instamic.authservice.service;

import com.instamic.authservice.exception.EmailAlreadyExistsException;
import com.instamic.authservice.exception.UsernameAlreadyExistsException;
import com.instamic.authservice.model.Profile;
import com.instamic.authservice.model.Role;
import com.instamic.authservice.model.User;
import com.instamic.authservice.model.request.LoginRequest;
import com.instamic.authservice.model.request.SignUpRequest;
import com.instamic.authservice.model.response.LoginResponse;
import com.instamic.authservice.repository.UserRepository;
import com.instamic.authservice.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;


    @Override
    public User registerUser(SignUpRequest request) {

        log.info("registering user {}", request.getUsername());

        User user = User
                .builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .userProfile(Profile.builder().displayName(request.getName()).build())
                .build();

        if(userRepository.existsByUsername(request.getUsername())) {
            log.warn("username {} already exists.", user.getUsername());
            throw new UsernameAlreadyExistsException(
                    String.format("username %s already exists", user.getUsername()));
        }

        if(userRepository.existsByEmail(user.getEmail())) {
            log.warn("email {} already exists.", user.getEmail());
            throw new EmailAlreadyExistsException(
                    String.format("email %s already exists", user.getEmail()));
        }

        user.setActive(true);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(new HashSet<>() {{
            add(Role.USER);
        }});

        User savedUser = userRepository.save(user);
        // TODO: add async messaging
        return savedUser;
    }

    @Override
    public LoginResponse loginUser(LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(request.getUsername(),
                                request.getPassword())
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return new LoginResponse(jwtTokenProvider.generateToken(authentication));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        log.info("UserServiceImpl.findByUsername started with username: {}", username);
        return userRepository.findByUsername(username);
    }
}
