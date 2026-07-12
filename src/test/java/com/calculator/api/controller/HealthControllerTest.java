package com.calculator.api.controller;

import com.calculator.api.utility.Constants;
import com.calculator.model.HealthCheckResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HealthControllerTest {

    private final HealthController controller = new HealthController();

    @Test
    void shouldReturnHealthCheckStatusUp() {
        ResponseEntity<HealthCheckResponse> response = controller.healthCheck();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HealthCheckResponse.StatusEnum.UP, response.getBody().getStatus());
        assertEquals(Constants.HEALTH_STATUS_MESSAGE, response.getBody().getMessage());
    }
}
