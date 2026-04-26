package com.convergence.cms.exception;

public class Handlerpackage com.convergence.cms.exception;

import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handle(Exception ex) {
        return ex.getMessage();
    }
}{
}
