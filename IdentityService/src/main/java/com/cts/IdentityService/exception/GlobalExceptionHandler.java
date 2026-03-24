package com.cts.IdentityService.exception;

import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public String handleRuntime(RuntimeException ex) {
        return ex.getMessage();
    }
}