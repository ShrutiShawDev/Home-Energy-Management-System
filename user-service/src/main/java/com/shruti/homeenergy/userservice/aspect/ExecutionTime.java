package com.shruti.homeenergy.userservice.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
public class ExecutionTime {
    @Pointcut("execution(* com.shruti.homeenergy.userservice.controller.*.*(..))")
    public void controllerMethods(){

    }

    @Around("controllerMethods()")
    public Object measureExecutionTime(ProceedingJoinPoint pjp) throws Throwable{
        long start = System.nanoTime();
        try{
            return pjp.proceed();
        }
        finally {
            long end = System.nanoTime();
            long elapseNs = end- start;
            long elapseMs = TimeUnit.NANOSECONDS.toMillis(elapseNs);
            String signature = pjp.getSignature().toShortString();
            log.info("Controller method {} executed in {} ms", signature, elapseMs);
        }
    }
}
