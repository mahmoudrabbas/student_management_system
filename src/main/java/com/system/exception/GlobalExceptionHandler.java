package com.system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> ResourceNotFoundExceptionHandler(ResourceNotFoundException ex){
        Map<String, String> errors = new HashMap<>();
        errors.put("Status", HttpStatus.NOT_FOUND.toString());
        errors.put("Timestamp", new Date().toString());
        errors.put("Message", ex.getMessage());

        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        Map<String, String > errors = new HashMap<>();
        errors.put("Status", HttpStatus.BAD_REQUEST.toString());
        errors.put("Timestamp", new Date().toString());
        for (FieldError err: ex.getBindingResult().getFieldErrors()){
            errors.put(err.getField(), err.getDefaultMessage());
        };
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex){
        Map<String, String> errors = new HashMap<>();
        errors.put("Status", HttpStatus.UNAUTHORIZED.toString());
        errors.put("Timestamp", new Date().toString());
        errors.put("Message", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
