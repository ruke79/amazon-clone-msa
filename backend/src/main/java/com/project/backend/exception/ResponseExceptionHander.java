package com.project.backend.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.project.backend.security.response.GenericResponse;

@RestControllerAdvice
public class ResponseExceptionHander extends ResponseEntityExceptionHandler {
    
    @ExceptionHandler(InsufficientAuthenticationException.class)
    public final ResponseEntity<Object> handleInsufficientAuthenticationExceptions(Exception ex, WebRequest request) {
        
        GenericResponse error = new GenericResponse(ex.getLocalizedMessage(), "Insufficient Authentication");
        return new ResponseEntity(error, HttpStatus.UNAUTHORIZED);
    }
}
