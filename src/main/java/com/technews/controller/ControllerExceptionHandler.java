package com.technews.controller;

import com.technews.exception.NoEmailException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(value = {NoEmailException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> noEmailException(NoEmailException e) {
        String error = e.getMessage();

        ResponseEntity<Object> responseEntity = new ResponseEntity<>(error, HttpStatus.NOT_FOUND);

        return responseEntity;
    }
}
