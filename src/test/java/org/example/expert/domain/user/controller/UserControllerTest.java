package org.example.expert.domain.user.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void searchUsersAverageSpeed() {
        int totalRequests = 100;
        long totalExecutionTime = 0;

        for (int i = 0; i < totalRequests; i++) {
            // 테스트 시작 시간
            long startTime = System.currentTimeMillis();

            // API 호출
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI0IiwiZW1haWwiOiJ1c2VyMUBleGFtcGxlLmNvbSIsInVzZXJSb2xlIjoiQURNSU4iLCJuaWNrbmFtZSI6Im5pY2tuYW1lMSIsImV4cCI6MTczNzQ0NDQ5NCwiaWF0IjoxNzM3NDQwODk0fQ.Wluvs9ywP8nXK2UKeEj_Vb_bmyjs02_3W0YZEW4pR-Y"); // 여기에 실제 JWT 토큰 값을 넣으세요

            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    "/users?nickname=" + "nickname" + i,
                    HttpMethod.GET,
                    requestEntity,
                    String.class
            );
            // 테스트 종료 시간
            long endTime = System.currentTimeMillis();

            // 개별 실행 시간 기록
            long executionTime = endTime - startTime;
            totalExecutionTime += executionTime;

            // 예상 응답 상태 확인
            assertEquals(HttpStatus.OK, response.getStatusCode());

        }

        // 평균 속도 계산
        double averageExecutionTime = (double) totalExecutionTime / totalRequests;
        System.out.println("Average Execution Time: " + averageExecutionTime + " ms");
    }
}