package com.vuong.vmess.aop.aspect;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Aspect
@Configuration
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RepositoryAspect {
    @Value("${application.repository.query-limit-warning-ms}")
    int executionLimitMs;

    /**
     * This class will detect every method in package repository
     * which have executed time up to 60ms
     * @param joinPoint point of method
     */
    @Around("execution(* com.vuong.vmess.repository.*.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;
        String message = joinPoint.getSignature() + " exec in " + executionTime + " ms";
        if (executionTime >= executionLimitMs) {
            log.warn(message + " : SLOW QUERY");
        }
        return proceed;
    }
}
