# ERD (Entity Relationship Diagram)

## 테이블 목록

### 1. member (회원)
| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 회원 ID |
| email | VARCHAR(255) | UNIQUE, NOT NULL | 이메일 |
| password | VARCHAR(255) | NOT NULL | 비밀번호 (암호화) |
| name | VARCHAR(100) | NOT NULL | 이름 |
| nickname | VARCHAR(50) | UNIQUE, NOT NULL | 닉네임 |
| member_type | VARCHAR(20) | NOT NULL | 회원 유형 (STUDENT, MAJOR) |
| member_status | VARCHAR(20) | NOT NULL | 회원 상태 (ENROLLED, GRADUATED) |
| role | VARCHAR(20) | NOT NULL | 권한 (USER, ADMIN) |
| created_at | DATETIME | NOT NULL | 생성일시 |
| updated_at | DATETIME | NOT NULL | 수정일시 |

### 2. member_academic (회원 학력)
| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 학력 ID |
| member_id | BIGINT | FK, NOT NULL | 회원 ID |
| university | VARCHAR(100) | NOT NULL | 대학교 |
| major | VARCHAR(100) | NOT NULL | 전공 |
| admission_year | INT | NOT NULL | 입학년도 |
| graduation_year | INT | NULL | 졸업년도 |
| created_at | DATETIME | NOT NULL | 생성일시 |
| updated_at | DATETIME | NOT NULL | 수정일시 |

### 3. major_profile (전공자 프로필)
| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 프로필 ID |
| member_id | BIGINT | FK, UNIQUE, NOT NULL | 회원 ID |
| introduction | TEXT | NULL | 자기소개 |
| career | TEXT | NULL | 경력 |
| certificate | TEXT | NULL | 자격증 |
| created_at | DATETIME | NOT NULL | 생성일시 |
| updated_at | DATETIME | NOT NULL | 수정일시 |

### 4. major_card (전공자 카드)
| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 카드 ID |
| member_id | BIGINT | FK, NOT NULL | 회원 ID |
| profile_id | BIGINT | FK, NOT NULL | 프로필 ID |
| title | VARCHAR(200) | NOT NULL | 제목 |
| content | TEXT | NOT NULL | 내용 |
| university | VARCHAR(100) | NOT NULL | 대학교 |
| major | VARCHAR(100) | NOT NULL | 전공 |
| tags | VARCHAR(500) | NULL | 태그 (JSON) |
| like_count | INT | DEFAULT 0 | 좋아요 수 |
| view_count | INT | DEFAULT 0 | 조회수 |
| is_public | BOOLEAN | DEFAULT TRUE | 공개 여부 |
| created_at | DATETIME | NOT NULL | 생성일시 |
| updated_at | DATETIME | NOT NULL | 수정일시 |

### 5. major_card_like (카드 좋아요)
| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 좋아요 ID |
| card_id | BIGINT | FK, NOT NULL | 카드 ID |
| member_id | BIGINT | FK, NOT NULL | 회원 ID |
| created_at | DATETIME | NOT NULL | 생성일시 |

**Unique Index**: (card_id, member_id)

### 6. question (질문)
| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 질문 ID |
| card_id | BIGINT | FK, NOT NULL | 카드 ID |
| writer_id | BIGINT | FK, NOT NULL | 작성자 ID |
| title | VARCHAR(200) | NOT NULL | 제목 |
| content | TEXT | NOT NULL | 내용 |
| is_answered | BOOLEAN | DEFAULT FALSE | 답변 여부 |
| created_at | DATETIME | NOT NULL | 생성일시 |
| updated_at | DATETIME | NOT NULL | 수정일시 |

### 7. answer (답변)
| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 답변 ID |
| question_id | BIGINT | FK, UNIQUE, NOT NULL | 질문 ID |
| writer_id | BIGINT | FK, NOT NULL | 작성자 ID |
| content | TEXT | NOT NULL | 내용 |
| created_at | DATETIME | NOT NULL | 생성일시 |
| updated_at | DATETIME | NOT NULL | 수정일시 |

### 8. interview (인터뷰)
| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 인터뷰 ID |
| major_id | BIGINT | FK, NOT NULL | 전공자 ID |
| title | VARCHAR(200) | NOT NULL | 제목 |
| description | TEXT | NOT NULL | 설명 |
| interview_date | DATETIME | NOT NULL | 인터뷰 일시 |
| max_participants | INT | NOT NULL | 최대 참여자 수 |
| current_participants | INT | DEFAULT 0 | 현재 참여자 수 |
| status | VARCHAR(20) | NOT NULL | 상태 (OPEN, CLOSED, COMPLETED) |
| created_at | DATETIME | NOT NULL | 생성일시 |
| updated_at | DATETIME | NOT NULL | 수정일시 |

### 9. interview_application (인터뷰 신청)
| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 신청 ID |
| interview_id | BIGINT | FK, NOT NULL | 인터뷰 ID |
| applicant_id | BIGINT | FK, NOT NULL | 신청자 ID |
| message | TEXT | NULL | 신청 메시지 |
| status | VARCHAR(20) | NOT NULL | 상태 (PENDING, ACCEPTED, REJECTED) |
| created_at | DATETIME | NOT NULL | 생성일시 |
| updated_at | DATETIME | NOT NULL | 수정일시 |

**Unique Index**: (interview_id, applicant_id)

### 10. review (후기)
| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 후기 ID |
| interview_id | BIGINT | FK, NOT NULL | 인터뷰 ID |
| writer_id | BIGINT | FK, NOT NULL | 작성자 ID |
| major_id | BIGINT | FK, NOT NULL | 전공자 ID |
| rating | INT | NOT NULL | 평점 (1-5) |
| content | TEXT | NOT NULL | 내용 |
| created_at | DATETIME | NOT NULL | 생성일시 |
| updated_at | DATETIME | NOT NULL | 수정일시 |

### 11. major_role_request (전공자 권한 신청)
| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 신청 ID |
| member_id | BIGINT | FK, NOT NULL | 회원 ID |
| university | VARCHAR(100) | NOT NULL | 대학교 |
| major | VARCHAR(100) | NOT NULL | 전공 |
| student_id | VARCHAR(50) | NOT NULL | 학번 |
| proof_image_url | VARCHAR(500) | NOT NULL | 증명 이미지 URL |
| status | VARCHAR(20) | NOT NULL | 상태 (PENDING, APPROVED, REJECTED) |
| reject_reason | TEXT | NULL | 반려 사유 |
| reviewed_by | BIGINT | FK, NULL | 처리자 ID |
| reviewed_at | DATETIME | NULL | 처리일시 |
| created_at | DATETIME | NOT NULL | 생성일시 |
| updated_at | DATETIME | NOT NULL | 수정일시 |

### 12. major_role_request_status_history (신청 상태 이력)
| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 이력 ID |
| request_id | BIGINT | FK, NOT NULL | 신청 ID |
| from_status | VARCHAR(20) | NULL | 이전 상태 |
| to_status | VARCHAR(20) | NOT NULL | 변경 상태 |
| changed_by | BIGINT | FK, NULL | 변경자 ID |
| reason | TEXT | NULL | 사유 |
| created_at | DATETIME | NOT NULL | 생성일시 |

---

## 관계도

```
member (1) --- (N) member_academic
member (1) --- (1) major_profile
member (1) --- (N) major_card
member (1) --- (N) major_card_like
member (1) --- (N) question
member (1) --- (N) answer
member (1) --- (N) interview (as major)
member (1) --- (N) interview_application (as applicant)
member (1) --- (N) review
member (1) --- (N) major_role_request

major_profile (1) --- (N) major_card
major_card (1) --- (N) major_card_like
major_card (1) --- (N) question

question (1) --- (1) answer

interview (1) --- (N) interview_application
interview (1) --- (N) review

major_role_request (1) --- (N) major_role_request_status_history
```

---

## 인덱스 전략

### 검색 성능 최적화
- `major_card`: (university, major), (is_public, created_at)
- `question`: (card_id, created_at)
- `interview`: (status, interview_date)
- `major_role_request`: (status, created_at)

### 조회 성능 최적화
- `major_card_like`: (card_id, member_id) UNIQUE
- `interview_application`: (interview_id, applicant_id) UNIQUE
