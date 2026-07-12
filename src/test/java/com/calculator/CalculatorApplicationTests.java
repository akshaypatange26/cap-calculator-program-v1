package com.calculator;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CalculatorApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void testMainMethod() {
        try (var mocked = Mockito.mockStatic(SpringApplication.class)) {
            mocked.when(() -> SpringApplication.run(CalculatorApplication.class, new String[]{}))
                  .thenReturn(null);

            CalculatorApplication.main(new String[]{});

            mocked.verify(() -> SpringApplication.run(CalculatorApplication.class, new String[]{}));
        }
    }
}