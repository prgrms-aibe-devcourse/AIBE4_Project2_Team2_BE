-- 모든 계정 비밀번호는 test12345! (BCrypt, strength=10) 로 통일
-- BCrypt 해시는 salt 포함이라 원문은 같아도 해시 문자열은 매번 달라질 수 있음
-- 아래 해시 문자열을 전 계정에 동일 적용
-- $2b$10$mn3ahFtDZzHoO6xLa8.uweMPvYVqRXfEcC80vl4TXiCab2xez.rF6

-- ------------------------------------------------------------
-- member_profile 더미데이터 (20개, 명시적 PK)
-- role: STUDENT(1~10), MAJOR(11~20)
-- ------------------------------------------------------------
INSERT INTO member_profile (member_id, name, nickname, email, username, password, status, role, profile_image_url,
                            created_at, updated_at)
VALUES (1, '신형만', 'hyeongman', 'hyeongman.shin@example.com', 'hyeongman',
        '$2b$10$mn3ahFtDZzHoO6xLa8.uweMPvYVqRXfEcC80vl4TXiCab2xez.rF6', 'ENROLLED', 'STUDENT', NULL,
        DATE_SUB(NOW(), INTERVAL 25 DAY), DATE_SUB(NOW(), INTERVAL 25 DAY)),
       (2, '김민지', 'minji', 'minji.kim@example.com', 'minji99',
        '$2b$10$mn3ahFtDZzHoO6xLa8.uweMPvYVqRXfEcC80vl4TXiCab2xez.rF6', 'ENROLLED', 'STUDENT',
        'https://cdn.example.com/profiles/minji99.png', DATE_SUB(NOW(), INTERVAL 24 DAY),
        DATE_SUB(NOW(), INTERVAL 24 DAY)),
       (3, '정하늘', 'haneul', 'haneul.jeong@example.com', 'haneul.j',
        '$2b$10$mn3ahFtDZzHoO6xLa8.uweMPvYVqRXfEcC80vl4TXiCab2xez.rF6', 'ENROLLED', 'STUDENT',
        'https://cdn.example.com/profiles/haneul.j.png', DATE_SUB(NOW(), INTERVAL 23 DAY),
        DATE_SUB(NOW(), INTERVAL 23 DAY)),
       (4, '오지수', 'jisoo', 'jisoo.oh@example.com', 'jisoo_oh',
        '$2b$10$mn3ahFtDZzHoO6xLa8.uweMPvYVqRXfEcC80vl4TXiCab2xez.rF6', 'ENROLLED', 'STUDENT', NULL,
        DATE_SUB(NOW(), INTERVAL 22 DAY), DATE_SUB(NOW(), INTERVAL 22 DAY)),
       (5, '윤도현', 'dohyun', 'dohyun.yoon@example.com', 'dohyun_y',
        '$2b$10$mn3ahFtDZzHoO6xLa8.uweMPvYVqRXfEcC80vl4TXiCab2xez.rF6', 'ENROLLED', 'STUDENT',
        'https://cdn.example.com/profiles/dohyun_y.png', DATE_SUB(NOW(), INTERVAL 21 DAY),
        DATE_SUB(NOW(), INTERVAL 21 DAY)),
       (6, '한서연', 'seoyeon', 'seoyeon.han@example.com', 'seoyeon_h',
        '$2b$10$mn3ahFtDZzHoO6xLa8.uweMPvYVqRXfEcC80vl4TXiCab2xez.rF6', 'ENROLLED', 'STUDENT',
        'https://cdn.example.com/profiles/seoyeon_h.png', DATE_SUB(NOW(), INTERVAL 20 DAY),
        DATE_SUB(NOW(), INTERVAL 20 DAY)),
       (7, '임준호', 'junho', 'junho.lim@example.com', 'junho_lim',
        '$2b$10$mn3ahFtDZzHoO6xLa8.uweMPvYVqRXfEcC80vl4TXiCab2xez.rF6', 'ENROLLED', 'STUDENT', NULL,
        DATE_SUB(NOW(), INTERVAL 19 DAY), DATE_SUB(NOW(), INTERVAL 19 DAY)),
       (8, '서지민', 'jimin', 'jimin.seo@example.com', 'jimin_seo',
        '$2b$10$mn3ahFtDZzHoO6xLa8.uweMPvYVqRXfEcC80vl4TXiCab2xez.rF6', 'ENROLLED', 'STUDENT',
        'https://cdn.example.com/profiles/jimin_seo.png', DATE_SUB(NOW(), INTERVAL 18 DAY),
        DATE_SUB(NOW(), INTERVAL 18 DAY)),
       (9, '배현우', 'hyunwoo', 'hyunwoo.bae@example.com', 'hyunwoo_b',
        '$2b$10$mn3ahFtDZzHoO6xLa8.uweMPvYVqRXfEcC80vl4TXiCab2xez.rF6', 'ENROLLED', 'STUDENT',
        'https://cdn.example.com/profiles/hyunwoo_b.png', DATE_SUB(NOW(), INTERVAL 17 DAY),
        DATE_SUB(NOW(), INTERVAL 17 DAY)),
       (10, '최예린', 'yerin', 'yerin.choi@example.com', 'yerin_c',
        '$2b$10$mn3ahFtDZzHoO6xLa8.uweMPvYVqRXfEcC80vl4TXiCab2xez.rF6', 'ENROLLED', 'STUDENT', NULL,
        DATE_SUB(NOW(), INTERVAL 16 DAY), DATE_SUB(NOW(), INTERVAL 16 DAY)),

       (11, '이서준', 'seojun', 'seojun.lee@example.com', 'seojun_lee',
        '$2b$10$mn3ahFtDZzHoO6xLa8.uweMPvYVqRXfEcC80vl4TXiCab2xez.rF6', 'ENROLLED', 'MAJOR',
        'https://cdn.example.com/profiles/seojun_lee.png', DATE_SUB(NOW(), INTERVAL 40 DAY),
        DATE_SUB(NOW(), INTERVAL 5 DAY)),
       (12, '박지훈', 'jihoon', 'jihoon.park@example.com', 'parkjh',
        '$2b$10$mn3ahFtDZzHoO6xLa8.uweMPvYVqRXfEcC80vl4TXiCab2xez.rF6', 'ENROLLED', 'MAJOR', NULL,
        DATE_SUB(NOW(), INTERVAL 38 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
       (13, '최유나', 'yuna', 'yuna.choi@example.com', 'yuna.choi',
        '$2b$10$mn3ahFtDZzHoO6xLa8.uweMPvYVqRXfEcC80vl4TXiCab2xez.rF6', 'ENROLLED', 'MAJOR',
        'https://cdn.example.com/profiles/yuna.choi.png', DATE_SUB(NOW(), INTERVAL 36 DAY),
        DATE_SUB(NOW(), INTERVAL 3 DAY)),
       (14, '강태현', 'taehyun', 'taehyun.kang@example.com', 'taehyun_k',
        '$2b$10$mn3ahFtDZzHoO6xLa8.uweMPvYVqRXfEcC80vl4TXiCab2xez.rF6', 'ENROLLED', 'MAJOR',
        'https://cdn.example.com/profiles/taehyun_k.png', DATE_SUB(NOW(), INTERVAL 34 DAY),
        DATE_SUB(NOW(), INTERVAL 2 DAY)),
       (15, '조은비', 'eunbi', 'eunbi.jo@example.com', 'eunbi_jo',
        '$2b$10$mn3ahFtDZzHoO6xLa8.uweMPvYVqRXfEcC80vl4TXiCab2xez.rF6', 'ENROLLED', 'MAJOR',
        'https://cdn.example.com/profiles/eunbi_jo.png', DATE_SUB(NOW(), INTERVAL 32 DAY),
        DATE_SUB(NOW(), INTERVAL 1 DAY)),
       (16, '문건우', 'geonwoo', 'geonwoo.moon@example.com', 'geonwoo_m',
        '$2b$10$mn3ahFtDZzHoO6xLa8.uweMPvYVqRXfEcC80vl4TXiCab2xez.rF6', 'ENROLLED', 'MAJOR',
        'https://cdn.example.com/profiles/geonwoo_m.png', DATE_SUB(NOW(), INTERVAL 30 DAY),
        DATE_SUB(NOW(), INTERVAL 5 DAY)),
       (17, '송다은', 'daeun', 'daeun.song@example.com', 'daeun_s',
        '$2b$10$mn3ahFtDZzHoO6xLa8.uweMPvYVqRXfEcC80vl4TXiCab2xez.rF6', 'ENROLLED', 'MAJOR', NULL,
        DATE_SUB(NOW(), INTERVAL 28 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
       (18, '장민석', 'minseok', 'minseok.jang@example.com', 'minseok_j',
        '$2b$10$mn3ahFtDZzHoO6xLa8.uweMPvYVqRXfEcC80vl4TXiCab2xez.rF6', 'ENROLLED', 'MAJOR',
        'https://cdn.example.com/profiles/minseok_j.png', DATE_SUB(NOW(), INTERVAL 26 DAY),
        DATE_SUB(NOW(), INTERVAL 3 DAY)),
       (19, '유지안', 'jian', 'jian.yu@example.com', 'jian_y',
        '$2b$10$mn3ahFtDZzHoO6xLa8.uweMPvYVqRXfEcC80vl4TXiCab2xez.rF6', 'ENROLLED', 'MAJOR',
        'https://cdn.example.com/profiles/jian_y.png', DATE_SUB(NOW(), INTERVAL 24 DAY),
        DATE_SUB(NOW(), INTERVAL 2 DAY)),
       (20, '홍서아', 'seoa', 'seoa.hong@example.com', 'seoa_h',
        '$2b$10$mn3ahFtDZzHoO6xLa8.uweMPvYVqRXfEcC80vl4TXiCab2xez.rF6', 'ENROLLED', 'MAJOR',
        'https://cdn.example.com/profiles/seoa_h.png', DATE_SUB(NOW(), INTERVAL 22 DAY),
        DATE_SUB(NOW(), INTERVAL 1 DAY));



-- ------------------------------------------------------------
-- member_academic 더미데이터 (20개)
-- ------------------------------------------------------------
INSERT INTO member_academic (member_id, university, major, created_at, updated_at)
VALUES (1, '서울대학교', '컴퓨터공학과', DATE_SUB(NOW(), INTERVAL 25 DAY), DATE_SUB(NOW(), INTERVAL 25 DAY)),
       (2, '고려대학교', '경영학과', DATE_SUB(NOW(), INTERVAL 24 DAY), DATE_SUB(NOW(), INTERVAL 24 DAY)),
       (3, '연세대학교', '경제학과', DATE_SUB(NOW(), INTERVAL 23 DAY), DATE_SUB(NOW(), INTERVAL 23 DAY)),
       (4, '성균관대학교', '통계학과', DATE_SUB(NOW(), INTERVAL 22 DAY), DATE_SUB(NOW(), INTERVAL 22 DAY)),
       (5, '중앙대학교', '심리학과', DATE_SUB(NOW(), INTERVAL 21 DAY), DATE_SUB(NOW(), INTERVAL 21 DAY)),
       (6, '서강대학교', '산업공학과', DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY)),
       (7, '한양대학교', '전자공학과', DATE_SUB(NOW(), INTERVAL 19 DAY), DATE_SUB(NOW(), INTERVAL 19 DAY)),
       (8, '경희대학교', '화학공학과', DATE_SUB(NOW(), INTERVAL 18 DAY), DATE_SUB(NOW(), INTERVAL 18 DAY)),
       (9, '부산대학교', '국제학부', DATE_SUB(NOW(), INTERVAL 17 DAY), DATE_SUB(NOW(), INTERVAL 17 DAY)),
       (10, '전남대학교', '미디어커뮤니케이션학과', DATE_SUB(NOW(), INTERVAL 16 DAY), DATE_SUB(NOW(), INTERVAL 16 DAY)),

       (11, '한양대학교', '기계공학과', DATE_SUB(NOW(), INTERVAL 40 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
       (12, '경북대학교', '전자공학과', DATE_SUB(NOW(), INTERVAL 38 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
       (13, '이화여자대학교', '심리학과', DATE_SUB(NOW(), INTERVAL 36 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
       (14, '서울시립대학교', '소프트웨어학과', DATE_SUB(NOW(), INTERVAL 34 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
       (15, '부산대학교', '화학과', DATE_SUB(NOW(), INTERVAL 32 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
       (16, 'KAIST', '산업디자인학과', DATE_SUB(NOW(), INTERVAL 30 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
       (17, 'POSTECH', '데이터사이언스학과', DATE_SUB(NOW(), INTERVAL 28 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
       (18, '전북대학교', '건축학과', DATE_SUB(NOW(), INTERVAL 26 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
       (19, '인하대학교', '생명공학과', DATE_SUB(NOW(), INTERVAL 24 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
       (20, '동국대학교', '항공우주공학과', DATE_SUB(NOW(), INTERVAL 22 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY));



-- ------------------------------------------------------------
-- interview_form 더미데이터 (20개, 상태 다양)
-- status: PENDING, ACCEPTED, REJECTED, COMPLETED
-- ------------------------------------------------------------
INSERT INTO interview_form (interview_id, student_member_id, major_member_id, title, content, interview_method,
                            preferred_datetime, extra_description, status, major_message, created_at, updated_at)
VALUES (1001, 1, 11, '전공 생활 루틴과 학점 관리 팁', '전공 수업 적응과 학점 관리, 시험 기간 루틴과 과제 관리 노하우가 궁금하다.', 'ONLINE',
        '2026-01-10 19:30:00', NULL, 'PENDING', NULL, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
       (1002, 1, 12, '전자공학 진로 선택 상담', '전력·통신·반도체 중 진로 선택 기준과 추천 과목, 프로젝트 경험을 듣고 싶다.', 'CHAT', '2026-01-12 21:00:00',
        '채팅으로 먼저 방향을 잡고 싶다.', 'ACCEPTED', '좋다. 1/12 21:00에 채팅으로 진행하자. 질문을 3~5개로 정리해주면 좋다.',
        DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
       (1003, 2, 13, '심리학과 대학원 준비', '통계·연구방법 과목 준비와 대학원 진학을 고려할 때 학부에서 할 활동이 궁금하다.', 'ONLINE', '2026-01-11 20:00:00',
        NULL, 'REJECTED', '해당 주차 일정이 어렵다. 2주 뒤 다시 신청해달라.', DATE_SUB(NOW(), INTERVAL 7 DAY),
        DATE_SUB(NOW(), INTERVAL 7 DAY)),
       (1004, 2, 11, '기계공학 프로젝트 경험', '학부 프로젝트 주제 선정 기준과 포트폴리오 정리 방식, 취업 준비에 도움 된 활동을 듣고 싶다.', 'ONLINE',
        '2026-01-05 19:00:00', '30분 정도로 핵심만 듣고 싶다.', 'COMPLETED', '진행 완료했다. 추가 자료는 메시지로 공유하겠다.',
        DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
       (1005, 3, 14, '소프트웨어 직무 준비 로드맵', '백엔드 개발자로 취업 준비를 시작하려고 한다. 학부에서 어떤 순서로 공부하면 좋을지 듣고 싶다.', 'ONLINE',
        '2026-01-15 20:30:00', NULL, 'PENDING', NULL, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
       (1006, 3, 15, '산업디자인 포트폴리오 피드백', '포트폴리오 구성과 프로젝트 스토리텔링을 어떻게 잡았는지 궁금하다.', 'ONLINE', '2026-01-07 18:30:00',
        '포트폴리오 링크를 사전에 전달하겠다.', 'COMPLETED', '진행 완료했다. 수정 포인트를 정리해 전달했다.', DATE_SUB(NOW(), INTERVAL 13 DAY),
        DATE_SUB(NOW(), INTERVAL 1 DAY)),
       (1007, 4, 16, '데이터사이언스 커리어 전환', '비전공자 관점에서 데이터 분야로 전환할 때 필요한 역량과 추천 프로젝트가 궁금하다.', 'CHAT', '2026-01-13 22:00:00',
        NULL, 'ACCEPTED', '가능하다. 먼저 질문 목록을 공유해달라.', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
       (1008, 4, 17, '건축학과 스튜디오 과제 관리', '스튜디오 과제를 시간 내에 끝내는 방법과 피드백 반영 루틴이 궁금하다.', 'ONLINE', '2026-01-03 19:00:00',
        '과제 이미지 일부를 공유하겠다.', 'COMPLETED', '진행 완료했다. 작업 루틴을 체크리스트로 전달했다.', DATE_SUB(NOW(), INTERVAL 16 DAY),
        DATE_SUB(NOW(), INTERVAL 8 DAY)),
       (1009, 5, 18, '생명공학 연구실 인턴 준비', '연구실 인턴 지원 준비와 논문 읽는 루틴, 면접에서 자주 묻는 질문이 궁금하다.', 'ONLINE', '2026-01-18 19:00:00',
        NULL, 'PENDING', NULL, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
       (1010, 5, 19, '항공우주공학 캡스톤 주제 선정', '캡스톤 주제 선정 과정과 팀 역할 분담, 결과물 정리 방법을 듣고 싶다.', 'ONLINE', '2026-01-04 18:00:00',
        '가능하면 사례 중심으로 듣고 싶다.', 'COMPLETED', '진행 완료했다. 참고 자료 링크를 공유했다.', DATE_SUB(NOW(), INTERVAL 15 DAY),
        DATE_SUB(NOW(), INTERVAL 6 DAY)),
       (1011, 6, 20, '전공 수업 선택과 시간표 구성', '전공 수업 난이도와 추천 조합, 시간표 짜는 기준이 궁금하다.', 'ONLINE', '2026-01-16 20:00:00', NULL,
        'PENDING', NULL, DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
       (1012, 6, 12, '전자공학 취업 준비', '학부에서 어떤 프로젝트를 하면 좋을지, 포트폴리오 정리 방법을 듣고 싶다.', 'CHAT', '2026-01-06 21:30:00', NULL,
        'COMPLETED', '진행 완료했다. 포트폴리오 구성 예시를 전달했다.', DATE_SUB(NOW(), INTERVAL 11 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
       (1013, 7, 13, '심리학 상담 트랙 선택', '상담·임상·산업심리 중 선택 기준과 학부에서 추천 활동이 궁금하다.', 'ONLINE', '2026-01-14 20:30:00', NULL,
        'ACCEPTED', '가능하다. 희망 트랙과 관심 분야를 알려달라.', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
       (1014, 7, 11, '기계공학 복수전공 고민', '복수전공을 고민 중이다. 전공 난이도와 시간 관리, 추천 과목 흐름을 듣고 싶다.', 'CHAT', '2026-01-02 22:00:00',
        NULL, 'REJECTED', '당분간 일정이 어려워 진행이 어렵다.', DATE_SUB(NOW(), INTERVAL 17 DAY), DATE_SUB(NOW(), INTERVAL 17 DAY)),
       (1015, 8, 14, '소프트웨어 학습 계획 점검', '학습 계획을 세웠는데 현실적인지 점검받고 싶다. 프로젝트 주제도 추천받고 싶다.', 'ONLINE', '2026-01-08 19:00:00',
        '현재 계획표를 공유하겠다.', 'COMPLETED', '진행 완료했다. 우선순위 조정안을 전달했다.', DATE_SUB(NOW(), INTERVAL 10 DAY),
        DATE_SUB(NOW(), INTERVAL 1 DAY)),
       (1016, 8, 15, '산업디자인 취업 포지션 선택', 'BX/UX/제품디자인 중 어떤 기준으로 포지션을 선택했는지 듣고 싶다.', 'ONLINE', '2026-01-17 21:00:00',
        NULL, 'PENDING', NULL, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
       (1017, 9, 16, '데이터사이언스 포트폴리오 구성', '프로젝트를 어떤 순서로 배치했고, 결과를 어떻게 서술했는지 궁금하다.', 'CHAT', '2026-01-09 21:00:00', NULL,
        'COMPLETED', '진행 완료했다. 개선 포인트를 코멘트로 정리했다.', DATE_SUB(NOW(), INTERVAL 9 DAY), DATE_SUB(NOW(), INTERVAL 0 DAY)),
       (1018, 9, 18, '생명공학 전공 수업 로드맵', '학부에서 어떤 과목을 우선 수강하면 좋은지, 연구 주제 선택 팁이 궁금하다.', 'ONLINE', '2026-01-19 20:00:00',
        NULL, 'ACCEPTED', '가능하다. 관심 연구 분야를 알려달라.', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
       (1019, 10, 20, '전공 생활 적응 상담', '전공 수업이 갑자기 어려워졌다. 학습 습관과 과제 관리 방식을 조언받고 싶다.', 'ONLINE', '2026-01-20 19:30:00',
        NULL, 'PENDING', NULL, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
       (1020, 10, 19, '항공우주공학 대학원 진학', '대학원 진학을 고민 중이다. 준비해야 할 역량과 연구실 선택 기준이 궁금하다.', 'ONLINE', '2026-01-01 20:00:00',
        NULL, 'REJECTED', '현재 연구 일정으로는 어렵다. 다음 학기 초에 다시 요청해달라.', DATE_SUB(NOW(), INTERVAL 18 DAY),
        DATE_SUB(NOW(), INTERVAL 18 DAY));



-- ------------------------------------------------------------
-- interview_student_snapshot 더미데이터 (20개, interview_id 1:1)
-- ------------------------------------------------------------
INSERT INTO interview_student_snapshot (interview_id, profile_image_url, nickname, status, university, major)
VALUES (1001, NULL, 'hyeongman', 'ENROLLED', '서울대학교', '컴퓨터공학과'),
       (1002, NULL, 'hyeongman', 'ENROLLED', '서울대학교', '컴퓨터공학과'),
       (1003, 'https://cdn.example.com/profiles/minji99.png', 'minji', 'ENROLLED', '고려대학교', '경영학과'),
       (1004, 'https://cdn.example.com/profiles/minji99.png', 'minji', 'ENROLLED', '고려대학교', '경영학과'),
       (1005, 'https://cdn.example.com/profiles/haneul.j.png', 'haneul', 'ENROLLED', '연세대학교', '경제학과'),
       (1006, 'https://cdn.example.com/profiles/haneul.j.png', 'haneul', 'ENROLLED', '연세대학교', '경제학과'),
       (1007, NULL, 'jisoo', 'ENROLLED', '성균관대학교', '통계학과'),
       (1008, NULL, 'jisoo', 'ENROLLED', '성균관대학교', '통계학과'),
       (1009, 'https://cdn.example.com/profiles/dohyun_y.png', 'dohyun', 'ENROLLED', '중앙대학교', '심리학과'),
       (1010, 'https://cdn.example.com/profiles/dohyun_y.png', 'dohyun', 'ENROLLED', '중앙대학교', '심리학과'),
       (1011, 'https://cdn.example.com/profiles/seoyeon_h.png', 'seoyeon', 'ENROLLED', '서강대학교', '산업공학과'),
       (1012, 'https://cdn.example.com/profiles/seoyeon_h.png', 'seoyeon', 'ENROLLED', '서강대학교', '산업공학과'),
       (1013, NULL, 'junho', 'ENROLLED', '한양대학교', '전자공학과'),
       (1014, NULL, 'junho', 'ENROLLED', '한양대학교', '전자공학과'),
       (1015, 'https://cdn.example.com/profiles/jimin_seo.png', 'jimin', 'ENROLLED', '경희대학교', '화학공학과'),
       (1016, 'https://cdn.example.com/profiles/jimin_seo.png', 'jimin', 'ENROLLED', '경희대학교', '화학공학과'),
       (1017, 'https://cdn.example.com/profiles/hyunwoo_b.png', 'hyunwoo', 'ENROLLED', '부산대학교', '국제학부'),
       (1018, 'https://cdn.example.com/profiles/hyunwoo_b.png', 'hyunwoo', 'ENROLLED', '부산대학교', '국제학부'),
       (1019, NULL, 'yerin', 'ENROLLED', '전남대학교', '미디어커뮤니케이션학과'),
       (1020, NULL, 'yerin', 'ENROLLED', '전남대학교', '미디어커뮤니케이션학과');



-- ------------------------------------------------------------
-- interview_major_snapshot 더미데이터 (20개, interview_id 1:1)
-- ------------------------------------------------------------
INSERT INTO interview_major_snapshot (interview_id, profile_image_url, nickname, status, university, major)
VALUES (1001, 'https://cdn.example.com/profiles/seojun_lee.png', 'seojun', 'ENROLLED', '한양대학교', '기계공학과'),
       (1002, NULL, 'jihoon', 'ENROLLED', '경북대학교', '전자공학과'),
       (1003, 'https://cdn.example.com/profiles/yuna.choi.png', 'yuna', 'ENROLLED', '이화여자대학교', '심리학과'),
       (1004, 'https://cdn.example.com/profiles/seojun_lee.png', 'seojun', 'ENROLLED', '한양대학교', '기계공학과'),
       (1005, 'https://cdn.example.com/profiles/taehyun_k.png', 'taehyun', 'ENROLLED', '서울시립대학교', '소프트웨어학과'),
       (1006, 'https://cdn.example.com/profiles/eunbi_jo.png', 'eunbi', 'ENROLLED', '부산대학교', '화학과'),
       (1007, 'https://cdn.example.com/profiles/geonwoo_m.png', 'geonwoo', 'ENROLLED', 'KAIST', '산업디자인학과'),
       (1008, NULL, 'daeun', 'ENROLLED', 'POSTECH', '데이터사이언스학과'),
       (1009, 'https://cdn.example.com/profiles/minseok_j.png', 'minseok', 'ENROLLED', '전북대학교', '건축학과'),
       (1010, 'https://cdn.example.com/profiles/jian_y.png', 'jian', 'ENROLLED', '인하대학교', '생명공학과'),
       (1011, 'https://cdn.example.com/profiles/seoa_h.png', 'seoa', 'ENROLLED', '동국대학교', '항공우주공학과'),
       (1012, NULL, 'jihoon', 'ENROLLED', '경북대학교', '전자공학과'),
       (1013, 'https://cdn.example.com/profiles/yuna.choi.png', 'yuna', 'ENROLLED', '이화여자대학교', '심리학과'),
       (1014, 'https://cdn.example.com/profiles/seojun_lee.png', 'seojun', 'ENROLLED', '한양대학교', '기계공학과'),
       (1015, 'https://cdn.example.com/profiles/taehyun_k.png', 'taehyun', 'ENROLLED', '서울시립대학교', '소프트웨어학과'),
       (1016, 'https://cdn.example.com/profiles/eunbi_jo.png', 'eunbi', 'ENROLLED', '부산대학교', '화학과'),
       (1017, 'https://cdn.example.com/profiles/geonwoo_m.png', 'geonwoo', 'ENROLLED', 'KAIST', '산업디자인학과'),
       (1018, 'https://cdn.example.com/profiles/minseok_j.png', 'minseok', 'ENROLLED', '전북대학교', '건축학과'),
       (1019, 'https://cdn.example.com/profiles/seoa_h.png', 'seoa', 'ENROLLED', '동국대학교', '항공우주공학과'),
       (1020, 'https://cdn.example.com/profiles/jian_y.png', 'jian', 'ENROLLED', '인하대학교', '생명공학과');



-- ------------------------------------------------------------
-- review 더미데이터 (COMPLETED 인터뷰에 대해서만 생성)
-- interview_id unique 전제에 맞춰 COMPLETED(7건)만 삽입
-- ------------------------------------------------------------
INSERT INTO review (interview_id, rating, content, created_at, updated_at)
VALUES (1004, 5, '답변이 구체적이라 바로 적용할 수 있었다. 추천해준 과제 관리 방식이 특히 도움이 됐다.', DATE_SUB(NOW(), INTERVAL 4 DAY),
        DATE_SUB(NOW(), INTERVAL 3 DAY)),
       (1006, 4, '포트폴리오 구조를 어떻게 잡아야 하는지 명확해졌다. 피드백이 현실적이었다.', DATE_SUB(NOW(), INTERVAL 1 DAY),
        DATE_SUB(NOW(), INTERVAL 0 DAY)),
       (1008, 3, '체크리스트 형태로 정리해줘서 좋았다. 다음 스튜디오에 바로 적용하겠다.', DATE_SUB(NOW(), INTERVAL 8 DAY),
        DATE_SUB(NOW(), INTERVAL 7 DAY)),
       (1010, 5, '캡스톤 주제 선정 기준을 사례로 설명해줘서 이해가 쉬웠다.', DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
       (1012, 4, '포트폴리오 구성 예시가 유용했다. 준비 방향이 선명해졌다.', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
       (1015, 3, '학습 계획의 우선순위를 잡아줘서 시간 낭비를 줄일 수 있었다.', DATE_SUB(NOW(), INTERVAL 1 DAY),
        DATE_SUB(NOW(), INTERVAL 0 DAY)),
       (1017, 5, '프로젝트 결과 서술 방식이 특히 도움이 됐다. 문장 예시까지 제공해줘서 좋았다.', DATE_SUB(NOW(), INTERVAL 0 DAY),
        DATE_SUB(NOW(), INTERVAL 0 DAY));
