package com.example.bookadminapp.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(ContentNotFoundException.class)
    public ResponseEntity<? extends RuntimeException> contentNotFoundExceptionHandler(ContentNotFoundException exception){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> notValidExceptionHandler(MethodArgumentNotValidException exception){
        BindingResult bindingResult = exception.getBindingResult();
        List<String> errorMessages = bindingResult.getAllErrors()
                .stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
        String errorMessage = String.join(", ", errorMessages);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }


}
