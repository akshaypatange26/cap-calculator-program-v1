package com.calculator.api.config;

import com.calculator.api.utility.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final HeaderValidationInterceptor headerValidationInterceptor;

    @Override
    public void addInterceptors(
            InterceptorRegistry registry) {
        registry.addInterceptor(headerValidationInterceptor).addPathPatterns(Constants.PATH_PATTERNS);
    }
}