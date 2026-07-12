package com.calculator.api.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class InvalidHeaderException extends RuntimeException {

    private final List<String> errors;

    public InvalidHeaderException(List<String> errors) {
        this.errors = errors;
    }
}