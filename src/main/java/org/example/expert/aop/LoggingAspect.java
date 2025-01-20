package org.example.expert.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.log.entity.Log;
import org.example.expert.domain.log.repository.LogRepository;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {
    private final LogRepository logRepository;
    private final ObjectMapper objectMapper;

    @Before(value = "@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void logPostRequestData(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();

        // 요청 데이터를 JSON으로 변환
        String requestBody = extractRequestBody(joinPoint.getArgs());

        logRepository.save(new Log("POST", methodName, requestBody, LocalDateTime.now()));
    }

    private String extractRequestBody(Object[] args) {
        StringBuilder requestBody = new StringBuilder();

        for (Object arg : args) {
            try {
                // 객체 이름과 JSON 직렬화 결과를 추가
                if (arg != null) {
                    String className = arg.getClass().getSimpleName();  // 객체 클래스 이름 추출
                    String json = objectMapper.writeValueAsString(arg); // 객체를 JSON 문자열로 변환
                    requestBody.append(className).append(": ").append(json).append(" ");  // 이름과 JSON 추가
                }
            } catch (Exception e) {
                // 직렬화 실패 시 예외 처리
                requestBody.append("Failed to serialize object: ").append(e.getMessage()).append(" ");
            }
        }

        return requestBody.toString().trim(); // 모든 객체의 정보를 합친 결과 반환
    }
}
