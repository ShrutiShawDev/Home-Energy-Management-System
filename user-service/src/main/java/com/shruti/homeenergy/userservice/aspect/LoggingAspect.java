package com.shruti.homeenergy.userservice.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
public class LoggingAspect {

    @Pointcut("execution(* com.shruti.homeenergy.userservice.service.*.*(..))")
    public void serviceMethods(){

    }

    @Before("serviceMethods()")
    public void logBefore(JoinPoint joinPoint){
        log.info("Called service method: {} with arguments: {}", joinPoint.getSignature().getName(), joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "serviceMehtods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result){
        log.info("Service method: {}, returned: {}",
                joinPoint.getSignature().getName(), result);
    }
}
