# MajorMate Backend 설정 가이드

## 환경변수 설정

### 1. .env 파일 생성
```bash
cp .env.example .env
```

### 2. .env 파일 수정
실제 데이터베이스 및 JWT 시크릿 값으로 수정:
```env
DATABASE_URL=jdbc:mysql://your-database-host:port/database-name?sslMode=REQUIRED
DATABASE_USERNAME=your-username
DATABASE_PASSWORD=your-password
JWT_SECRET=your-super-secret-jwt-key-min-256-bits-required
```

### 3. IntelliJ 환경변수 설정

**방법 1: EnvFile 플러그인 사용 (추천)**
1. File → Settings → Plugins
2. "EnvFile" 검색 및 설치
3. Run → Edit Configurations
4. EnvFile 탭에서 `.env` 파일 추가

**방법 2: 수동 설정**
1. Run → Edit Configurations
2. Environment variables 클릭
3. 각 변수 수동 입력

## 애플리케이션 실행

```bash
./gradlew bootRun
```

또는 IntelliJ에서 `MajorMateApplication` 실행

## API 문서

실행 후 Swagger UI 접속:
```
http://localhost:8080/swagger-ui.html
```

## 프론트엔드 연동

### CORS 허용 포트
- `http://localhost:3000` (React 기본 포트)
- `http://localhost:5173` (Vite 기본 포트)

### API Base URL
```
http://localhost:8080
```

### 인증 흐름
1. 회원가입: POST /api/auth/signup
2. 로그인: POST /api/auth/login → accessToken, refreshToken 받기
3. API 호출 시 헤더에 포함: `Authorization: Bearer {accessToken}`
4. 토큰 만료 시: POST /api/auth/refresh → 새 accessToken 받기

### 토큰 유효기간
- Access Token: 1시간
- Refresh Token: 7일

## 주의사항

- `.env` 파일은 절대 git에 커밋하지 마세요 (`.gitignore`에 포함됨)
- `application.yml`도 git에 커밋하지 마세요 (`.gitignore`에 포함됨)
- JWT_SECRET은 256비트 이상의 랜덤 문자열 사용
- 프로덕션 환경에서는 반드시 환경변수를 별도로 관리
