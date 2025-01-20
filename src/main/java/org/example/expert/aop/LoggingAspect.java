package org.example.expert.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.log.entity.Log;
import org.example.expert.domain.log.repository.LogRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {
    private final LogRepository logRepository;

    @AfterReturning(value = "@annotation(org.springframework.web.bind.annotation.PostMapping)",
            returning = "response")
    public void logPostRequests(JoinPoint joinPoint, Object response) {
//        Object[] args = joinPoint.getArgs();
//        AuthUser authUser = (AuthUser) args[0];
//        Long userId = authUser.getId();
//        String description = Arrays.toString(joinPoint.getArgs());
//        LocalDateTime requestTime = LocalDateTime.now();
//
//        // logRepository.save(new Log(action,description));
//        log.info("Admin Access Log - User ID: {}, Request Time: {}, Method: {}",
//                userId, requestTime, joinPoint.getSignature().getName());
        if (response != null) {
            log.info("Response: {}", response.toString());
        } else {
            log.info("No response returned.");
        }
    }

}
