package com.instamic.authservice.service;

import com.instamic.authservice.exception.EmailAlreadyExistsException;
import com.instamic.authservice.exception.UsernameAlreadyExistsException;
import com.instamic.authservice.model.Profile;
import com.instamic.authservice.model.Role;
import com.instamic.authservice.model.User;
import com.instamic.authservice.model.UserSummary;
import com.instamic.authservice.model.request.LoginRequest;
import com.instamic.authservice.model.request.SignUpRequest;
import com.instamic.authservice.model.response.LoginResponse;
import com.instamic.authservice.repository.UserRepository;
import com.instamic.authservice.security.JwtTokenProvider;
import com.instamic.authservice.security.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

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
    public UserSummary getCurrentUser(UserDetailsImpl userDetails) {
        return UserSummary
                .builder()
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .name(userDetails.getUserProfile().getDisplayName())
                .profilePicture(userDetails.getUserProfile().getProfilePictureUrl())
                .build();
    }

}
