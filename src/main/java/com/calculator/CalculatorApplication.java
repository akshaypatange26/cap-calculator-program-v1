package com.calculator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;

@SpringBootApplication(exclude = {ErrorMvcAutoConfiguration.class})
@EnableWebMvc
@ComponentScan(basePackages = {"com.calculator"}, excludeFilters = @ComponentScan.Filter(type = org.springframework.context.annotation.FilterType.REGEX, pattern = "com\\.calculator\\.api\\.CalculatorApiController"))
public class CalculatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(CalculatorApplication.class, args);
    }
}

