# API 명세서

## 개요
MajorMate API 명세서입니다.

## Base URL
```
http://localhost:8080
```

## 인증
- JWT Bearer Token 사용
- Header: `Authorization: Bearer {token}`

---

## 1. 인증 (Auth)

### 1.1 회원가입
```
POST /api/auth/signup
```

**Request**
```json
{
  "email": "user@example.com",
  "password": "password123",
  "name": "홍길동",
  "nickname": "길동이",
  "memberType": "STUDENT"
}
```

**Response**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "email": "user@example.com",
    "name": "홍길동",
    "nickname": "길동이"
  },
  "message": "회원가입이 완료되었습니다."
}
```

### 1.2 로그인
```
POST /api/auth/login
```

**Request**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600
  },
  "message": "로그인 성공"
}
```

---

## 2. 회원 (Member)

### 2.1 내 정보 조회
```
GET /api/members/me
```

**Response**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "email": "user@example.com",
    "name": "홍길동",
    "nickname": "길동이",
    "memberType": "STUDENT",
    "memberStatus": "ENROLLED"
  }
}
```

---

## 3. 전공자 카드 (Major Card)

### 3.1 카드 목록 조회
```
GET /api/cards?page=0&size=10&major=컴퓨터공학
```

**Response**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "title": "컴퓨터공학 전공자입니다",
        "university": "서울대학교",
        "major": "컴퓨터공학",
        "likeCount": 10,
        "isLiked": false
      }
    ],
    "totalPages": 5,
    "totalElements": 50,
    "currentPage": 0
  }
}
```

---

## 4. Q&A

### 4.1 질문 작성
```
POST /api/questions
```

**Request**
```json
{
  "cardId": 1,
  "title": "전공 선택에 대해 질문드립니다",
  "content": "컴퓨터공학과 소프트웨어학과 중 어떤 것이 더 좋을까요?"
}
```

---

## 5. 인터뷰

### 5.1 인터뷰 신청
```
POST /api/interviews/{interviewId}/applications
```

---

## 에러 코드

| 코드 | HTTP Status | 설명 |
|------|-------------|------|
| AUTH_001 | 401 | 인증 실패 |
| AUTH_002 | 403 | 권한 없음 |
| MEMBER_001 | 404 | 회원을 찾을 수 없음 |
| MEMBER_002 | 409 | 이미 존재하는 이메일 |
| CARD_001 | 404 | 카드를 찾을 수 없음 |

---

## 공통 응답 형식

### 성공 응답
```json
{
  "success": true,
  "data": {},
  "message": "성공 메시지"
}
```

### 에러 응답
```json
{
  "success": false,
  "error": {
    "code": "ERROR_CODE",
    "message": "에러 메시지",
    "details": []
  }
}
```
