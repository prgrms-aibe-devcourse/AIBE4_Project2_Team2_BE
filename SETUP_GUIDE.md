# MajorMate Backend 설정 가이드

## 환경 설정

### application-dev.yml 수정

**데이터베이스, AWS, JWT 설정은 이미 되어 있습니다.**

OAuth2 소셜 로그인을 사용하려면 `src/main/resources/application-dev.yml` 파일에서 다음 부분만 수정하세요:

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: your-google-client-id        # ← Google Client ID 입력
            client-secret: your-google-client-secret # ← Google Client Secret 입력

          github:
            client-id: your-github-client-id        # ← GitHub Client ID 입력
            client-secret: your-github-client-secret # ← GitHub Client Secret 입력
```

**참고:**
- `application-dev.yml`은 `.gitignore`에 포함되어 있어 Git에 커밋되지 않습니다
- OAuth2를 사용하지 않으면 수정하지 않아도 됩니다

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

## OAuth2 소셜 로그인 설정 (선택사항)

### Google OAuth2 설정
1. [Google Cloud Console](https://console.cloud.google.com) 접속
2. 프로젝트 생성 또는 선택
3. "API 및 서비스" → "사용자 인증 정보" 이동
4. "사용자 인증 정보 만들기" → "OAuth 클라이언트 ID" 선택
5. 애플리케이션 유형: 웹 애플리케이션
6. 승인된 리디렉션 URI 추가:
   - `http://localhost:8080/login/oauth2/code/google`
7. 클라이언트 ID와 클라이언트 보안 비밀을 `.env`에 추가

### GitHub OAuth2 설정
1. [GitHub Settings](https://github.com/settings/developers) 접속
2. "OAuth Apps" → "New OAuth App" 클릭
3. 다음 정보 입력:
   - Application name: MajorMate
   - Homepage URL: `http://localhost:8080`
   - Authorization callback URL: `http://localhost:8080/login/oauth2/code/github`
4. 클라이언트 ID와 클라이언트 시크릿을 `.env`에 추가

### 소셜 로그인 사용
- **Google 로그인**: `http://localhost:8080/oauth2/authorization/google`
- **GitHub 로그인**: `http://localhost:8080/oauth2/authorization/github`

로그인 성공 시 프론트엔드로 리다이렉트:
```
http://localhost:3000/oauth2/redirect?accessToken=xxx&refreshToken=xxx&expiresIn=3600
```

## 주의사항

- `application-dev.yml`은 이미 `.gitignore`에 포함되어 있어 Git에 커밋되지 않습니다
- 팀원들과 공유 시 `application-dev.yml` 파일을 별도로 공유하세요
- OAuth2 클라이언트 ID/Secret은 팀 내에서만 공유하고 외부에 노출하지 마세요
- 프로덕션 환경에서는 `application-prod.yml`을 사용하고 환경변수로 관리하세요
