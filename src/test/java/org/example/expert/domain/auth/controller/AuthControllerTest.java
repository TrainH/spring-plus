package org.example.expert.domain.auth.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.auth.service.AuthService;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthControllerTest {

    @DynamicPropertySource
    static void configureTestDatabase(DynamicPropertyRegistry registry) {
        // 데이터베이스 연결 설정
        registry.add("spring.datasource.url", () -> "jdbc:mysql://localhost:3307/expert");
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
        registry.add("spring.datasource.username", () -> "root");
        registry.add("spring.datasource.password", () -> "password");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");

        // Hibernate 쿼리 로깅 비활성화
        registry.add("spring.jpa.show-sql", () -> "false");  // SQL 출력 끄기
        registry.add("logging.level.org.hibernate.SQL", () -> "off");  // Hibernate SQL 로깅 끄기
        registry.add("logging.level.org.hibernate.type.descriptor.sql", () -> "off");  // SQL 타입 변환 로깅 끄기
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    public void testMillionUserSignup() {
        int totalUsers = 1_000_000; // 100만 명
        Random random = new Random();

        List<String> roles = Arrays.asList("ADMIN", "USER");
        ExecutorService executorService = Executors.newFixedThreadPool(8); // 멀티스레드 처리

        long startTime = System.currentTimeMillis();

        try {
            for (int i = 0; i < totalUsers; i++) {
                final int index = i;
                executorService.submit(() -> {
                    SignupRequest signupRequest = new SignupRequest(
                            "user" + index + "@example.com", // 이메일
                            "password" + index,             // 패스워드
                            roles.get(random.nextInt(roles.size())), // 랜덤 유저 역할
                            "nickname"+index                  // 닉네임
                    );

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);

                    HttpEntity<SignupRequest> request = new HttpEntity<>(signupRequest, headers);
                    ResponseEntity<SignupResponse> response = restTemplate.postForEntity(
                            "/auth/signup",
                            request,
                            SignupResponse.class
                    );

                    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
                });
            }
        } finally {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(30, TimeUnit.MINUTES)) {
                    executorService.shutdownNow(); // awaitTermination(1, TimeUnit.HOURS)
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Time taken: " + (endTime - startTime) + " ms");
        System.out.println("Test Total Users: " + totalUsers);
        System.out.println("Saved Total Users: " + userRepository.count());
        // 최종적으로 데이터가 100만 건 생성되었는지 검증
        Assertions.assertEquals(totalUsers, userRepository.count());
    }
}