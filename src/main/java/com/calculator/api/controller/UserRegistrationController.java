package com.calculator.api.controller;

import com.calculator.api.UsersApi;
import com.calculator.api.service.UserService;
import com.calculator.model.UserRegistrationRequest;
import com.calculator.model.UserRegistrationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserRegistrationController implements UsersApi {

    private final UserService userService;

    @Override
    public ResponseEntity<UserRegistrationResponse> registerUser(UserRegistrationRequest userRegistrationRequest) {
        return ResponseEntity.ok(userService.register(userRegistrationRequest));
    }
}
