package com.calculator.api.service;

import com.calculator.api.entity.CalculatorUser;
import com.calculator.api.repository.UserRepository;
import com.calculator.model.UserRegistrationRequest;
import com.calculator.model.UserRegistrationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserRegistrationResponse register(UserRegistrationRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        CalculatorUser user = new CalculatorUser();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        user.setEnabled(1);

        CalculatorUser savedUser = userRepository.save(user);

        UserRegistrationResponse response = new UserRegistrationResponse();
        response.setId(savedUser.getId());
        response.setUsername(savedUser.getUsername());
        response.setMessage("User registered successfully");
        return response;
    }
}
