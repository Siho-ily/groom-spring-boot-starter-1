# Profile & Tech Stack API

## 프로젝트 개요

개발자 프로필과 기술 스택을 관리하는 REST API 서비스입니다. Spring Boot 기초 학습 내용을 종합하여 Layered Architecture와 DAO 패턴을 활용한 CRUD API를 구현합니다.

### 주요 기능
- 개발자 프로필 관리 (생성, 조회, 수정, 삭제)
- 기술 스택 관리 (프로필별 기술 스택 추가, 조회, 수정, 삭제)
- 페이징 처리 및 검색 기능
- 공통 응답 형식과 예외 처리

## 기술 스택

- **Language**: Java 17
- **Framework**: Spring Boot 4.0.3
- **Database**: H2 (개발), MySQL (운영)
- **Data Access**: JdbcTemplate
- **Build Tool**: Gradle
- **Others**: Lombok

## 프로젝트 구조

```
src/main/java/com/study/profile_stack_api/
├── ProfileStackApiApplication.java
├── domain/
│   ├── profile/
│   │   ├── controller/
│   │   │   └── ProfileController.java
│   │   ├── service/
│   │   │   └── ProfileService.java
│   │   ├── repository/
│   │   │   └── ProfileRepository.java
│   │   ├── dao/
│   │   │   ├── ProfileDao.java              (인터페이스)
│   │   │   └── ProfileDaoImpl.java          (JdbcTemplate 구현체)
│   │   ├── entity/
│   │   │   ├── Profile.java
│   │   │   └── Position.java                (Enum)
│   │   └── dto/
│   │       ├── request/
│   │       │   ├── ProfileCreateRequest.java
│   │       │   └── ProfileUpdateRequest.java
│   │       └── response/
│   │           └── ProfileResponse.java
│   └── techstack/
│       ├── controller/
│       │   └── TechStackController.java
│       ├── service/
│       │   └── TechStackService.java
│       ├── repository/
│       │   └── TechStackRepository.java
│       ├── dao/
│       │   ├── TechStackDao.java            (인터페이스)
│       │   └── TechStackDaoImpl.java        (JdbcTemplate 구현체)
│       ├── entity/
│       │   ├── TechStack.java
│       │   ├── TechCategory.java            (Enum)
│       │   └── Proficiency.java             (Enum)
│       └── dto/
│           ├── request/
│           │   ├── TechStackCreateRequest.java
│           │   └── TechStackUpdateRequest.java
│           └── response/
│               └── TechStackResponse.java
└── global/
    ├── common/
    │   ├── ApiResponse.java                 (공통 응답 객체)
    │   └── Page.java                        (페이징 응답 객체)
    ├── exception/
    │   ├── BusinessException.java
    │   ├── ProfileNotFoundException.java
    │   ├── TechStackNotFoundException.java
    │   ├── DuplicateEmailException.java
    │   └── GlobalExceptionHandler.java
    └── config/
        └── AppConfig.java
```

## 데이터베이스 스키마

### Profile 테이블
| 컬럼명 | 타입 | 설명 | 제약조건 |
|--------|------|------|----------|
| id | BIGINT | 프로필 ID | PK, AUTO_INCREMENT |
| name | VARCHAR(50) | 이름 | NOT NULL |
| email | VARCHAR(100) | 이메일 | NOT NULL, UNIQUE |
| bio | VARCHAR(500) | 자기소개 | - |
| position | VARCHAR(20) | 직무 | NOT NULL |
| career_years | INT | 경력 연차 | NOT NULL, DEFAULT 0 |
| github_url | VARCHAR(200) | GitHub URL | - |
| blog_url | VARCHAR(200) | 블로그 URL | - |
| created_at | TIMESTAMP | 생성일시 | DEFAULT CURRENT_TIMESTAMP |
| updated_at | TIMESTAMP | 수정일시 | DEFAULT CURRENT_TIMESTAMP |

### TechStack 테이블
| 컬럼명 | 타입 | 설명 | 제약조건 |
|--------|------|------|----------|
| id | BIGINT | 기술스택 ID | PK, AUTO_INCREMENT |
| profile_id | BIGINT | 프로필 ID | NOT NULL, FK |
| name | VARCHAR(50) | 기술명 | NOT NULL |
| category | VARCHAR(20) | 카테고리 | NOT NULL |
| proficiency | VARCHAR(20) | 숙련도 | NOT NULL |
| years_of_exp | INT | 경험 연수 | NOT NULL, DEFAULT 0 |
| created_at | TIMESTAMP | 생성일시 | DEFAULT CURRENT_TIMESTAMP |
| updated_at | TIMESTAMP | 수정일시 | DEFAULT CURRENT_TIMESTAMP |

## API 엔드포인트

### Profile API

| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/api/v1/profiles` | 프로필 생성 |
| GET | `/api/v1/profiles` | 프로필 목록 조회 (페이징) |
| GET | `/api/v1/profiles/{id}` | 프로필 단건 조회 |
| GET | `/api/v1/profiles/position/{position}` | 직무별 프로필 조회 |
| PUT | `/api/v1/profiles/{id}` | 프로필 수정 |
| DELETE | `/api/v1/profiles/{id}` | 프로필 삭제 |

### TechStack API

| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/api/v1/profiles/{profileId}/tech-stacks` | 기술 스택 추가 |
| GET | `/api/v1/profiles/{profileId}/tech-stacks` | 기술 스택 목록 조회 (페이징) |
| GET | `/api/v1/profiles/{profileId}/tech-stacks/{id}` | 기술 스택 단건 조회 |
| PUT | `/api/v1/profiles/{profileId}/tech-stacks/{id}` | 기술 스택 수정 |
| DELETE | `/api/v1/profiles/{profileId}/tech-stacks/{id}` | 기술 스택 삭제 |

## Enum 정의

### Position (직무)
- `BACKEND` - 백엔드 개발자 ⚙️
- `FRONTEND` - 프론트엔드 개발자 🎨
- `FULLSTACK` - 풀스택 개발자 🔄
- `MOBILE` - 모바일 개발자 📱
- `DEVOPS` - DevOps 엔지니어 🚀
- `DATA` - 데이터 엔지니어 📊
- `AI` - AI/ML 엔지니어 🤖
- `ETC` - 기타 💻

### TechCategory (기술 카테고리)
- `LANGUAGE` - 프로그래밍 언어 📝
- `FRAMEWORK` - 프레임워크 🏗️
- `DATABASE` - 데이터베이스 💾
- `DEVOPS` - DevOps/인프라 ☁️
- `TOOL` - 개발 도구 🔧
- `ETC` - 기타 📦

### Proficiency (숙련도)
- `BEGINNER` - 입문 🌱
- `INTERMEDIATE` - 중급 🌿
- `ADVANCED` - 고급 🌳
- `EXPERT` - 전문가 🏆

## 실행 방법

### 1. 프로젝트 클론
```bash
git clone https://github.com/jinho-yoo-jack/groom-spring-boot-starter-1.git
cd groom-spring-boot-starter-1
```

### 2. 애플리케이션 실행
```bash
./gradlew bootRun
```

### 3. H2 Console 접속
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (빈 값)

### 4. API 테스트
```bash
# 프로필 생성
curl -X POST http://localhost:8080/api/v1/profiles \
  -H "Content-Type: application/json" \
  -d '{
    "name": "김자바",
    "email": "java.kim@example.com",
    "bio": "Spring Boot를 사랑하는 백엔드 개발자입니다.",
    "position": "BACKEND",
    "careerYears": 3,
    "githubUrl": "https://github.com/javakim"
  }'

# 프로필 목록 조회
curl "http://localhost:8080/api/v1/profiles?page=0&size=10"

# 기술 스택 추가
curl -X POST http://localhost:8080/api/v1/profiles/1/tech-stacks \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Java",
    "category": "LANGUAGE",
    "proficiency": "ADVANCED",
    "yearsOfExp": 3
  }'
```

## 초급 과정 구현 체크리스트

### Phase 1: 기본 설정

- [x]  Spring Boot 프로젝트 생성 (spring-boot-starter-web, spring-boot-starter-jdbc, h2, lombok)
- [x]  application.yml 작성 (H2 설정, SQL 초기화 설정)
- [x]  schema.sql, data.sql 작성
- [x]  애플리케이션 실행 후 H2 Console에서 테이블 확인

### Phase 2: 공통 모듈

- [x]  ApiResponse 클래스 구현
- [x]  Page 제네릭 클래스 구현
- [x]  ErrorCode Enum 정의
- [x]  BusinessException 및 하위 예외 클래스 구현
- [x]  GlobalExceptionHandler 구현

### Phase 3: Profile CRUD

- [x]  Entity 클래스 작성 (Profile, Position Enum)
- [x]  DTO 클래스 작성 (CreateRequest, UpdateRequest, Response)
- [x]  ProfileDao 인터페이스 정의
- [x]  ProfileDaoImpl 구현 (JdbcTemplate + RowMapper)
- [x]  ProfileRepository 구현
- [x]  ProfileService 구현
- [x]  ProfileController 구현
- [x]  cURL 또는 Postman으로 CRUD 테스트

### Phase 4: TechStack CRUD

- [x]  Entity 클래스 작성 (TechStack, TechCategory Enum, Proficiency Enum)
- [x]  DTO 클래스 작성 (CreateRequest, UpdateRequest, Response)
- [x]  TechStackDao 인터페이스 정의
- [x]  TechStackDaoImpl 구현 (profileId를 활용한 쿼리 주의)
- [x]  TechStackRepository 구현
- [x]  TechStackService 구현 (프로필 존재 여부 검증 포함)
- [x]  TechStackController 구현
- [x]  cURL 또는 Postman으로 CRUD 테스트

### Phase 5: 페이징 & 검색

- [x]  프로필 목록 페이징 구현 (findAllWithPaging)
- [x]  프로필 검색 구현 (이름 검색, 직무 필터링)
- [x]  기술 스택 목록 페이징 구현 (findByProfileIdWithPaging)
- [x]  기술 스택 검색 구현 (카테고리, 숙련도 필터링)

### Phase 6: 테스트 코드 (보너스)

- [ ]  ProfileDao 통합 테스트
- [ ]  TechStackDao 통합 테스트
- [ ]  페이징 경계 조건 테스트 (첫 페이지, 마지막 페이지, 빈 페이지)

## 고급 과정 구현 체크리스트

### Phase 1: Lombok 적용

- [x]  `build.gradle`에 Lombok 의존성 추가
- [x]  모든 Entity 클래스에 `@Getter`, `@Setter`, `@NoArgsConstructor`, `@Builder` 적용
- [x]  모든 Request DTO에 `@Getter`, `@NoArgsConstructor` 적용
- [ ]  모든 Response DTO에 `@Getter`, `@Builder` (또는 `@AllArgsConstructor`) 적용
- [ ]  모든 Service, Controller에 `@RequiredArgsConstructor` 적용하여 생성자 주입 코드 제거
- [ ]  필요한 곳에 `@Slf4j` 적용하여 로깅 추가
- [ ]  수동으로 작성한 Getter/Setter/생성자/Builder 코드 모두 삭제
- [ ]  애플리케이션 실행 후 기존 API 동작 확인

### Phase 2: Bean Validation 적용

- [ ]  `build.gradle`에 Validation 의존성 추가
- [ ]  `ProfileCreateRequest`에 검증 어노테이션 추가 (`@NotBlank`, `@Size`, `@Email`, `@NotNull`, `@Min`)
- [ ]  `ProfileUpdateRequest`에 검증 어노테이션 추가 (`@Size`, `@Min`)
- [ ]  `TechStackCreateRequest`에 검증 어노테이션 추가
- [ ]  `TechStackUpdateRequest`에 검증 어노테이션 추가
- [ ]  모든 Controller의 `@RequestBody` 앞에 `@Valid` 추가
- [ ]  `GlobalExceptionHandler`에 `MethodArgumentNotValidException` 처리 추가
- [ ]  **Service에서 if문 기반 유효성 검증 코드 모두 제거** (비즈니스 로직만 남기기)
- [ ]  Postman으로 유효성 검증 실패 케이스 테스트

### Phase 3: MapStruct 적용

- [ ]  `build.gradle`에 MapStruct 의존성 추가 (annotationProcessor 순서 주의: Lombok → MapStruct)
- [ ]  `ProfileMapper` 인터페이스 작성 (`toEntity`, `toResponse`, `toResponseList`, `updateEntity`)
- [ ]  `TechStackMapper` 인터페이스 작성 (`toEntity`, `toResponse`, `toResponseList`, `updateEntity`)
- [ ]  `ProfileService`에서 수동 매핑 코드를 `ProfileMapper` 호출로 교체
- [ ]  `TechStackService`에서 수동 매핑 코드를 `TechStackMapper` 호출로 교체
- [ ]  빌드 후 `build/generated/sources/annotationProcessor`에 Mapper 구현체 생성 확인
- [ ]  Postman으로 CRUD API 동작 확인

### Phase 4: Spring Security — 회원 도메인 및 JWT 기반 구현

- [ ]  `build.gradle`에 Spring Security, JJWT 의존성 추가
- [ ]  `application.yml`에 JWT 설정 추가 (`secret`, `access-token-expiration`, `refresh-token-expiration`)
- [ ]  `Member` Entity 작성 (Lombok 적용)
- [ ]  `Role` Enum 작성 (`USER`, `ADMIN`)
- [ ]  `RefreshToken` Entity 작성
- [ ]  `MemberDao` 인터페이스 및 구현체 작성
- [ ]  `RefreshTokenDao` 인터페이스 및 구현체 작성 (`save`, `findByMemberId`, `deleteByMemberId`)
- [ ]  `CustomUserDetailsService` 구현 (`UserDetailsService` 구현체)
- [ ]  `PasswordEncoder` Bean 등록 (`BCryptPasswordEncoder`)
- [ ]  `SignupRequest` 작성 (Bean Validation 적용)
- [ ]  `LoginRequest`, `TokenRefreshRequest` 작성
- [ ]  `LoginResponse` (accessToken + refreshToken), `TokenRefreshResponse` (accessToken) 작성

### Phase 5: Spring Security — JWT 필터 및 설정

- [ ]  `JwtTokenProvider` 구현 (`createAccessToken`, `createRefreshToken`, `getUsername`, `validateToken`)
- [ ]  `JwtAuthenticationFilter` 구현 (`OncePerRequestFilter` 상속, `shouldNotFilter()` 오버라이드)
- [ ]  `JwtAuthenticationEntryPoint` 구현 (토큰 에러 분기: `TOKEN_EXPIRED`, `INVALID_TOKEN`, `UNAUTHORIZED`)
- [ ]  `SecurityConfig` 작성
  - [ ]  CSRF 비활성화
  - [ ]  세션 정책 `STATELESS` 설정
  - [ ]  인가 규칙 설정 (GET은 허용, CUD는 인증 필요)
  - [ ]  `addFilterBefore`로 `JwtAuthenticationFilter` 등록
  - [ ]  `JwtAuthenticationEntryPoint` 등록
- [ ]  `AuthService` 구현 (회원가입, 로그인, 토큰 재발급, 로그아웃)
- [ ]  `AuthController` 구현 (signup, login, refresh, logout 엔드포인트)
- [ ]  Postman으로 회원가입 → 로그인 → Access Token으로 API 호출 테스트

### Phase 6: 소유권 검증 적용

- [ ]  `profile` 테이블에 `member_id` 컬럼 추가 (schema.sql 수정)
- [ ]  `Profile` Entity에 `memberId` 필드 추가
- [ ]  `ProfileDao`에 `member_id` 관련 쿼리 추가 (`findByMemberId`, INSERT 시 `member_id` 포함)
- [ ]  `ProfileService`에 소유권 검증 로직 추가 (수정/삭제 시 `memberId` 확인)
- [ ]  `TechStackService`에 소유권 검증 로직 추가 (해당 프로필의 소유자인지 확인)
- [ ]  `ProfileController`에 `@AuthenticationPrincipal UserDetails` 파라미터 추가
- [ ]  `TechStackController`에 `@AuthenticationPrincipal UserDetails` 파라미터 추가
- [ ]  `UnauthorizedException` 작성 + `GlobalExceptionHandler`에 처리 추가
- [ ]  Postman으로 소유권 검증 테스트 (A 사용자로 B의 프로필 수정 시도 → 거부 확인)

## 아키텍처

### Layered Architecture
```
Controller (REST API 엔드포인트)
    ↓
Service (비즈니스 로직)
    ↓
Repository (데이터 접근 추상화)
    ↓
DAO (JdbcTemplate 구현)
    ↓
Database (H2/MySQL)
```

### 패키지 구조
- `domain`: 도메인별 기능 구현
    - `profile`: 프로필 관련 기능
    - `techstack`: 기술 스택 관련 기능
- `global`: 공통 기능
    - `common`: 공통 응답 객체
    - `exception`: 예외 처리
    - `config`: 설정

## 주요 구현 포인트

### 1. DAO 패턴
- 인터페이스와 구현체를 분리하여 데이터 접근 계층 추상화
- JdbcTemplate을 활용한 SQL 쿼리 직접 작성
- RowMapper를 통한 객체 매핑

### 2. 페이징 처리
- Page 객체를 활용한 페이징 응답 구현
- LIMIT, OFFSET을 활용한 쿼리 최적화

### 3. 예외 처리
- BusinessException을 상속한 커스텀 예외 정의
- GlobalExceptionHandler를 통한 전역 예외 처리
- 일관된 에러 응답 형식

### 4. FK 관계 처리
- TechStack의 profileId를 통한 연관 관계 관리
- CASCADE DELETE를 통한 참조 무결성 유지
- 프로필 존재 여부 검증 로직

## 테스트

### 단위 테스트
```bash
./gradlew test
```

### 통합 테스트
- H2 인메모리 데이터베이스를 활용한 통합 테스트
- @DataJdbcTest를 활용한 DAO 계층 테스트

## 참고 자료

- [Spring Boot 공식 문서](https://spring.io/projects/spring-boot)
- [Spring Data JDBC](https://spring.io/projects/spring-data-jdbc)
- [H2 Database](http://www.h2database.com)

## 라이센스

MIT License

## 기여하기

### Fork를 통한 협업 방법

#### 1. Fork 및 Clone
```bash
# 1. GitHub에서 프로젝트를 Fork (웹에서 Fork 버튼 클릭)

# 2. Fork한 저장소를 로컬에 Clone
git clone https://github.com/YOUR_GITHUB_USERNAME/groom-spring-boot-starter-1.git
cd groom-spring-boot-starter-1
```

#### 2. Upstream 저장소 설정
```bash
# 원본 저장소를 upstream으로 추가
git remote add upstream https://github.com/jinho-yoo-jack/groom-spring-boot-starter-1.git

# remote 저장소 확인
git remote -v
# origin: 본인의 Fork 저장소
# upstream: 원본 저장소
```

#### 3. 브랜치 생성 및 작업
```bash
# main 브랜치에서 새 브랜치 생성 (본인 이름 사용)
git checkout -b spring1/YOUR_NAME

# 예시
git checkout -b spring1/jinho
```

#### 4. 변경사항 Commit
```bash
# 변경사항 확인
git status

# 변경사항 추가
git add .

# Commit 작성
git commit -m "feat: Profile API 구현 완료"
```

#### 5. Upstream 동기화 (최신 상태 유지)
```bash
# upstream의 최신 변경사항 가져오기
git fetch upstream

# 현재 브랜치에 upstream/master 병합
git merge upstream/master

# 충돌이 있다면 해결 후 commit
```

#### 6. Fork 저장소에 Push
```bash
# 본인의 Fork 저장소에 Push
git push origin spring1/YOUR_NAME
```

#### 7. Pull Request 생성
1. GitHub에서 본인의 Fork 저장소로 이동
2. "Compare & pull request" 버튼 클릭
3. PR 제목과 설명 작성
4. Create Pull Request 클릭

### 주의사항
- 작업 전 항상 upstream과 동기화
- 의미있는 commit 메시지 작성
- 하나의 PR에는 하나의 기능만 포함
- 코드 리뷰 피드백은 적극적으로 반영

## 문의사항

- GitHub Issues: [https://github.com/jinho-yoo-jack/groom-spring-boot-starter-1/issues](https://github.com/jinho-yoo-jack/groom-spring-boot-starter-1/issues)
- Email: jhy7342@gmail.com

---

**Made with ❤️ by Profile & Stack API Team**