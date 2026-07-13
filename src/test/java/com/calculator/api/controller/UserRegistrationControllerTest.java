package com.calculator.api.controller;

import com.calculator.api.service.UserService;
import com.calculator.model.UserRegistrationRequest;
import com.calculator.model.UserRegistrationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserRegistrationControllerTest {

    private UserService userService;
    private UserRegistrationController controller;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        controller = new UserRegistrationController(userService);
    }

    @Test
    void shouldRegisterUser() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("newUser");
        request.setPassword("password123");

        UserRegistrationResponse expectedResponse = new UserRegistrationResponse();
        expectedResponse.setId(1L);
        expectedResponse.setUsername("newUser");
        expectedResponse.setMessage("User registered successfully");

        when(userService.register(request)).thenReturn(expectedResponse);

        ResponseEntity<UserRegistrationResponse> response = controller.registerUser(request);

        assertEquals(expectedResponse, response.getBody());
        verify(userService).register(request);
    }
}
