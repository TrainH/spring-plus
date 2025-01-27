# SPRING PLUS

## 13. 대용량 데이터 처리

### 테스트 방법

1. **commit 이동**: `ddea18efc8788c5eea1a0d37313ddb207d07dc97`
2. **AuthController 실행**:
    - 경로: `org.example.expert.domain.auth.controller`
    - 실행 시 100만 건의 user가 생성됨.
3. **POSTMAN으로 로그인**:
    - 계정 정보:
        - 이메일: `user1@example.com`
        - 비밀번호: `password1`
    - 로그인 후 `bearerToken` 획득.
4. **bearerToken 입력**:
    - `org.example.expert.domain.user.controller`의 `headers.setBearerAuth()`에 `bearerToken`을 입력.
5. **UserControllerTest 실행**:
    - 결과 값을 확인.
6. **commit 이동**: `ea5a35371d59562d859c304ba71aa3cfb6c12edc`
7. **AuthController 다시 실행**:
    - 경로: `org.example.expert.domain.auth.controller`
    - 실행 시 100만 건의 user가 생성됨.
8. **POSTMAN으로 로그인**:
    - 계정 정보:
        - 이메일: `user1@example.com`
        - 비밀번호: `password1`
    - 로그인 후 `bearerToken` 획득.
9. **bearerToken 입력**:
    - `org.example.expert.domain.user.controller`의 `headers.setBearerAuth()`에 `bearerToken`을 입력.
10. **UserControllerTest 실행**:
    - 결과 값을 확인.

---

### 테스트 결과

- **닉네임 인덱싱 적용**:
  유저 엔티티에 아래 어노테이션으로 `nickname`을 인덱싱.
  ```java
  indexes = {
      @Index(name = "idx_user_nickname", columnList = "nickname")
  }
  ```

- **유저 데이터 생성**:
    - 10만 건의 유저를 생성 (약 1시간 소요).
    - 시간 관계상 100만 건 생성 대신 10만 건으로 테스트 진행.

- **조회 성능 비교**:
    - **인덱싱 미적용**: 닉네임으로 100번 조회 시 평균 조회시간: **420.09 ms**.
    - **인덱싱 적용**: 닉네임으로 100번 조회 시 평균 조회시간: **127.54 ms**.
    - **성능 향상**: 인덱싱 적용 시 조회 시간이 약 **70% 감소**됨.

-
