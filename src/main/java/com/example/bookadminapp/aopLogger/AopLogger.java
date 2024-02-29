package com.example.bookadminapp.aopLogger;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class AopLogger {

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void loggingAllControllers() {
    }

    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void loggingAllService() {
    }


    @Pointcut("within(@org.springframework.web.bind.annotation.RestControllerAdvice *)")
    public void loggingControllerAdvice() {
    }


    @Before("loggingAllControllers()")
    public void beforeAdviceFromAllControllers(JoinPoint joinPoint){
        Arrays.stream(joinPoint.getArgs()).forEach(f->   log.info( "log controller" +f));



    }

    @Before("loggingAllService()")
    public void beforeAdviceFromAllService(JoinPoint joinPoint){
        Arrays.stream(joinPoint.getArgs()).forEach(f->   log.info( "log Service" +f));

    }


    @AfterThrowing(value = "loggingControllerAdvice()", throwing = "exception")
    public void afterAdviceFromLoggingControllerAdvice(JoinPoint joinPoint, Exception exception){

    }

}
