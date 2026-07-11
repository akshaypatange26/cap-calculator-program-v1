package com.calculator.api;

import com.calculator.model.HealthResponse;
import com.calculator.utility.Constants;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<HealthResponse> healthCheck() {

        HealthResponse response = new HealthResponse();
        response.setStatus(HealthResponse.StatusEnum.UP);
        response.setMessage(Constants.HEALTH_STATUS_MESSAGE);

        return ResponseEntity.ok(response);
    }
}