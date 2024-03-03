package com.example.bookadminapp.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class ControllerAdvice {
    @ExceptionHandler(ContentNotFoundException.class)
    public ResponseEntity<String> contentNotFoundExceptionHandler(ContentNotFoundException exception){
        log.info(" ControllerAdvice contentNotFoundExceptionHandler {0}", exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
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
