package com.calculator.api.Controller;

import com.calculator.api.utility.Constants;
import com.calculator.model.HealthCheckResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/healthCheck")
    public ResponseEntity<HealthCheckResponse> healthCheck() {
        HealthCheckResponse response = new HealthCheckResponse();
        response.setStatus(HealthCheckResponse.StatusEnum.UP);
        response.setMessage(Constants.HEALTH_STATUS_MESSAGE);
        return ResponseEntity.ok(response);
    }
}