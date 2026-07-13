package com.calculator.api.service;

import com.calculator.api.entity.CalculatorUser;
import com.calculator.api.repository.UserRepository;
import com.calculator.model.UserRegistrationRequest;
import com.calculator.model.UserRegistrationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void shouldRegisterNewUser() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("newUser");
        request.setPassword("plainPassword");

        CalculatorUser savedUser = new CalculatorUser();
        savedUser.setId(123L);
        savedUser.setUsername("newUser");
        savedUser.setPasswordHash("hashedPassword");
        savedUser.setRole("USER");
        savedUser.setEnabled(1);

        when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plainPassword")).thenReturn("hashedPassword");
        when(userRepository.save(any(CalculatorUser.class))).thenReturn(savedUser);

        UserRegistrationResponse response = userService.register(request);

        assertNotNull(response);
        assertEquals(123L, response.getId());
        assertEquals("newUser", response.getUsername());
        assertEquals("User registered successfully", response.getMessage());

        verify(userRepository).findByUsername("newUser");
        verify(passwordEncoder).encode("plainPassword");
        verify(userRepository).save(any(CalculatorUser.class));
    }

    @Test
    void shouldThrowExceptionWhenUsernameExists() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("existingUser");
        request.setPassword("password");

        when(userRepository.findByUsername("existingUser")).thenReturn(Optional.of(new CalculatorUser()));

        assertThrows(IllegalArgumentException.class, () -> userService.register(request));

        verify(userRepository).findByUsername("existingUser");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(CalculatorUser.class));
    }
}
