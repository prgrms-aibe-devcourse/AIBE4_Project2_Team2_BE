# 프로젝트 설정 가이드

## 개발 환경 요구사항

### 필수 설치 항목
- Java 17 이상
- Gradle 8.x
- MySQL 8.0 이상
- Git

### 권장 IDE
- IntelliJ IDEA (Ultimate 또는 Community)
- VS Code + Java Extension Pack

---

## 프로젝트 설정

### 1. 저장소 클론
```bash
git clone https://github.com/your-team/MajorMate.git
cd MajorMate
```

### 2. MySQL 데이터베이스 생성
```sql
-- 개발 환경 DB
CREATE DATABASE majormate_dev CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 운영 환경 DB (선택)
CREATE DATABASE majormate CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 사용자 생성 (선택)
CREATE USER 'majormate'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON majormate_dev.* TO 'majormate'@'localhost';
GRANT ALL PRIVILEGES ON majormate.* TO 'majormate'@'localhost';
FLUSH PRIVILEGES;
```

### 3. 환경 변수 설정

프로젝트 루트에 `.env` 파일 생성 (선택):
```properties
JWT_SECRET=your-secret-key-at-least-256-bits-long-for-production-use
DATABASE_URL=jdbc:mysql://localhost:3306/majormate
DATABASE_USERNAME=root
DATABASE_PASSWORD=password
```

또는 application-dev.yml에서 직접 설정

### 4. 의존성 설치 및 빌드
```bash
# Windows
gradlew clean build

# macOS/Linux
./gradlew clean build
```

### 5. 애플리케이션 실행
```bash
# Windows
gradlew bootRun

# macOS/Linux
./gradlew bootRun
```

또는 IDE에서 `MajorMateApplication.java` 실행

---

## IntelliJ IDEA 설정

### 1. 프로젝트 Import
1. `File` → `Open` → 프로젝트 폴더 선택
2. `Import project from external model` → `Gradle` 선택
3. JDK 17 설정 확인

### 2. Lombok 플러그인 설치
1. `File` → `Settings` → `Plugins`
2. `Lombok` 검색 후 설치
3. `File` → `Settings` → `Build, Execution, Deployment` → `Compiler` → `Annotation Processors`
4. `Enable annotation processing` 체크

### 3. 코드 스타일 설정
1. `File` → `Settings` → `Editor` → `Code Style`
2. `Scheme` → `Import Scheme` → `IntelliJ IDEA code style XML`
3. 프로젝트에서 제공하는 code style 파일 import (선택)

---

## 데이터베이스 스키마 초기화

### 방법 1: DDL Auto (개발 환경)
`application-dev.yml`에서 `ddl-auto: update` 설정으로 자동 생성

### 방법 2: SQL 스크립트 (운영 환경)
```bash
mysql -u root -p majormate < src/main/resources/schema.sql
```

---

## API 문서 확인

애플리케이션 실행 후:
```
http://localhost:8080/swagger-ui.html
```

---

## 테스트 실행

### 전체 테스트
```bash
gradlew test
```

### 특정 테스트
```bash
gradlew test --tests MemberServiceTest
```

---

## 프로필별 실행

### 개발 환경
```bash
gradlew bootRun --args='--spring.profiles.active=dev'
```

### 운영 환경
```bash
gradlew bootRun --args='--spring.profiles.active=prod'
```

### 테스트 환경
```bash
gradlew test -Pspring.profiles.active=test
```

---

## 트러블슈팅

### 1. Port 이미 사용 중
```yaml
# application.yml에 추가
server:
  port: 8081
```

### 2. MySQL 연결 실패
- MySQL 서비스 실행 확인
- 데이터베이스 이름, 사용자, 비밀번호 확인
- 방화벽 설정 확인

### 3. Lombok 작동 안 함
- Lombok 플러그인 설치 확인
- Annotation Processing 활성화 확인
- IDE 재시작

### 4. 빌드 실패
```bash
# 캐시 클리어 후 재빌드
gradlew clean build --refresh-dependencies
```

---

## 패키지 구조

```
src/main/java/kr/java/majormate/
├── MajorMateApplication.java          ← 메인 클래스
├── domain/                            ← 도메인 패키지
│   ├── member/                        ← 회원
│   ├── auth/                          ← 인증
│   ├── profile/                       ← 전공자 프로필
│   ├── card/                          ← 전공자 카드
│   ├── qna/                           ← Q&A
│   ├── interview/                     ← 인터뷰
│   ├── review/                        ← 후기
│   └── request/                       ← 전공자 권한 신청
└── global/                            ← 공통 기능
    ├── config/                        ← 설정
    ├── security/                      ← 보안
    ├── exception/                     ← 예외 처리
    ├── common/                        ← 공통
    └── util/                          ← 유틸리티
```

---

## 추가 도구

### API 테스트
- Postman
- IntelliJ HTTP Client
- curl

### 데이터베이스 관리
- MySQL Workbench
- DBeaver
- IntelliJ Database Tools

---

## 참고 자료

- [Spring Boot 공식 문서](https://spring.io/projects/spring-boot)
- [Spring Security 공식 문서](https://spring.io/projects/spring-security)
- [JWT 공식 사이트](https://jwt.io/)
- [MySQL 공식 문서](https://dev.mysql.com/doc/)

---

## 팀원별 담당 영역

```
팀원 A: member, auth
팀원 B: profile, request
팀원 C: card, qna
팀원 D: interview
팀원 E: review, global
```

---

## 문의

문제가 발생하면 Issues에 등록하거나 팀 채팅방에 문의하세요.
