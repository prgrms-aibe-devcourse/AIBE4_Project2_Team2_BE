INSERT INTO member (name, nickname, email, username, password, status, role, created_at, updated_at)
VALUES ('신형만', 'hyeongman', 'hyeongman.shin@example.com',
        'hyeongman', '$2a$10$y3dmZoSBhQgrkYFb7Mrh6eSvfn5DMTmEyptjoajINn3xTbNS5bZfW',
        'ENROLLED', 'STUDENT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

       ('김민지', 'minji', 'minji.kim@example.com',
        'minji99', '$2a$10$K8pdBN//CMXimYAkDc93zeIyrPqgQGa1CVhsyPXF/ec1zSEkUjf8a',
        'ENROLLED', 'STUDENT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

       ('이서준', 'seojun', 'seojun.lee@example.com',
        'seojun_lee', '$2a$10$2gyYE7Y7FmjoIpmnVNw3Je21sUrmfo.wH2AiDKdvWeXxddeORtuiW',
        'ENROLLED', 'MAJOR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

       ('박지훈', 'jihoon', 'jihoon.park@example.com',
        'parkjh', '$2a$10$Ph3198sbmwMPBvuXwsn10OnCvm7T6S7agOXPE6KN85Yu3cCthWo2a',
        'ENROLLED', 'MAJOR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

       ('최유나', 'yuna', 'yuna.choi@example.com',
        'yuna.choi', '$2a$10$G5HsHPdTKDxDnhWx2BlhEOeAGHBajKkzf.x07Py68vtcoa72X2RLe',
        'ENROLLED', 'MAJOR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO member_academic (member_id, university, major, created_at, updated_at)
VALUES ((SELECT member_id FROM member WHERE username = 'hyeongman'), '서울대학교', '컴퓨터공학과',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ((SELECT member_id FROM member WHERE username = 'minji99'), '고려대학교', '경영학과',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ((SELECT member_id FROM member WHERE username = 'seojun_lee'), '한양대학교', '기계공학과',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ((SELECT member_id FROM member WHERE username = 'parkjh'), '경북대학교', '전자공학과',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ((SELECT member_id FROM member WHERE username = 'yuna.choi'), '이화여자대학교', '심리학과',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ------------------------------------------------------------
-- IDs 캐시
-- ------------------------------------------------------------
SET @student1 := (SELECT member_id
                  FROM member
                  WHERE
                      username = 'hyeongman');
SET @student2 := (SELECT member_id
                  FROM member
                  WHERE
                      username = 'minji99');

SET @major1 := (SELECT member_id
                FROM member
                WHERE
                    username = 'seojun_lee');
SET @major2 := (SELECT member_id
                FROM member
                WHERE
                    username = 'parkjh');
SET @major3 := (SELECT member_id
                FROM member
                WHERE
                    username = 'yuna.choi');

-- ------------------------------------------------------------
-- 1) hyeongman -> seojun_lee (PENDING)
-- ------------------------------------------------------------
INSERT INTO interview (student_member_id, major_member_id,
                       title, content, interview_method, preferred_datetime, extra_description,
                       status, major_message,
                       created_at, updated_at)
VALUES (@student1, @major1,
        '전공 생활 루틴과 학점 관리 팁 질문',
        '안녕하세요. 전공 수업 난이도 적응과 학점 관리를 어떻게 했는지, 시험 기간 루틴과 과제 관리 방법을 듣고 싶습니다.',
        'ONLINE',
        '2026-01-10 19:30:00',
        NULL,
        'PENDING',
        NULL,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

SET @interview1 := LAST_INSERT_ID();

INSERT INTO interview_student_snapshot (interview_id,
                                        student_profile_image_url, student_nickname, student_status, student_university,
                                        student_major)
SELECT
    @interview1,
    m.profile_image_url,
    m.nickname,
    m.status,
    a.university,
    a.major
FROM
    member m
        JOIN member_academic a ON a.member_id = m.member_id
WHERE
    m.member_id = @student1;

INSERT INTO interview_major_snapshot (interview_id,
                                      major_profile_image_url, major_nickname, major_status, major_university,
                                      major_major)
SELECT
    @interview1,
    m.profile_image_url,
    m.nickname,
    m.status,
    a.university,
    a.major
FROM
    member m
        JOIN member_academic a ON a.member_id = m.member_id
WHERE
    m.member_id = @major1;

-- ------------------------------------------------------------
-- 2) hyeongman -> parkjh (ACCEPTED)
-- ------------------------------------------------------------
INSERT INTO interview (student_member_id, major_member_id,
                       title, content, interview_method, preferred_datetime, extra_description,
                       status, major_message,
                       created_at, updated_at)
VALUES (@student1, @major2,
        '전자공학과 진로 선택 상담 요청',
        '전자공학과에서 전력/통신/반도체 중 어떤 기준으로 진로를 정했는지 궁금합니다. 학부 때 추천 과목과 프로젝트도 듣고 싶습니다.',
        'CHAT',
        '2026-01-12 21:00:00',
        '채팅으로 먼저 간단히 방향을 잡고, 필요하면 온라인으로 진행해도 좋습니다.',
        'ACCEPTED',
        '좋습니다. 1/12(일) 21:00에 채팅으로 진행하겠습니다. 미리 질문 3~5개 정리해서 보내주면 더 도움이 됩니다.',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

SET @interview2 := LAST_INSERT_ID();

INSERT INTO interview_student_snapshot (interview_id,
                                        student_profile_image_url, student_nickname, student_status, student_university,
                                        student_major)
SELECT
    @interview2,
    m.profile_image_url,
    m.nickname,
    m.status,
    a.university,
    a.major
FROM
    member m
        JOIN member_academic a ON a.member_id = m.member_id
WHERE
    m.member_id = @student1;

INSERT INTO interview_major_snapshot (interview_id,
                                      major_profile_image_url, major_nickname, major_status, major_university,
                                      major_major)
SELECT
    @interview2,
    m.profile_image_url,
    m.nickname,
    m.status,
    a.university,
    a.major
FROM
    member m
        JOIN member_academic a ON a.member_id = m.member_id
WHERE
    m.member_id = @major2;

-- ------------------------------------------------------------
-- 3) minji99 -> yuna.choi (REJECTED)
-- ------------------------------------------------------------
INSERT INTO interview (student_member_id, major_member_id,
                       title, content, interview_method, preferred_datetime, extra_description,
                       status, major_message,
                       created_at, updated_at)
VALUES (@student2, @major3,
        '심리학과 전공 수업 구성과 대학원 준비 질문',
        '심리학과 커리큘럼에서 통계/연구방법 과목을 어떻게 준비하면 좋은지, 대학원 진학을 고려할 때 학부에서 어떤 활동을 하면 좋은지 듣고 싶습니다.',
        'ONLINE',
        '2026-01-11 20:00:00',
        NULL,
        'REJECTED',
        '죄송합니다. 해당 주차에 일정이 꽉 차서 진행이 어렵습니다. 다음 달에 다시 신청해주면 일정 확인 후 답변드리겠습니다.',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

SET @interview3 := LAST_INSERT_ID();

INSERT INTO interview_student_snapshot (interview_id,
                                        student_profile_image_url, student_nickname, student_status, student_university,
                                        student_major)
SELECT
    @interview3,
    m.profile_image_url,
    m.nickname,
    m.status,
    a.university,
    a.major
FROM
    member m
        JOIN member_academic a ON a.member_id = m.member_id
WHERE
    m.member_id = @student2;

INSERT INTO interview_major_snapshot (interview_id,
                                      major_profile_image_url, major_nickname, major_status, major_university,
                                      major_major)
SELECT
    @interview3,
    m.profile_image_url,
    m.nickname,
    m.status,
    a.university,
    a.major
FROM
    member m
        JOIN member_academic a ON a.member_id = m.member_id
WHERE
    m.member_id = @major3;

-- ------------------------------------------------------------
-- 4) minji99 -> seojun_lee (COMPLETED) + review 생성
-- ------------------------------------------------------------
INSERT INTO interview (student_member_id, major_member_id,
                       title, content, interview_method, preferred_datetime, extra_description,
                       status, major_message,
                       created_at, updated_at)
VALUES (@student2, @major1,
        '기계공학과 프로젝트 경험과 취업 준비 질문',
        '학부 프로젝트에서 어떤 주제를 선택했고, 포트폴리오로 어떻게 정리했는지 궁금합니다. 취업 준비 과정에서 도움이 된 활동도 듣고 싶습니다.',
        'ONLINE',
        '2026-01-05 19:00:00',
        '가능하면 30분 정도로 핵심만 듣고 싶습니다.',
        'COMPLETED',
        '1/5 19:00에 진행했고, 질문 답변은 정리해서 채팅으로 추가 전달하겠습니다.',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

SET @interview4 := LAST_INSERT_ID();

INSERT INTO interview_student_snapshot (interview_id,
                                        student_profile_image_url, student_nickname, student_status, student_university,
                                        student_major)
SELECT
    @interview4,
    m.profile_image_url,
    m.nickname,
    m.status,
    a.university,
    a.major
FROM
    member m
        JOIN member_academic a ON a.member_id = m.member_id
WHERE
    m.member_id = @student2;

INSERT INTO interview_major_snapshot (interview_id,
                                      major_profile_image_url, major_nickname, major_status, major_university,
                                      major_major)
SELECT
    @interview4,
    m.profile_image_url,
    m.nickname,
    m.status,
    a.university,
    a.major
FROM
    member m
        JOIN member_academic a ON a.member_id = m.member_id
WHERE
    m.member_id = @major1;

INSERT INTO review (interview_id, rating, content, created_at)
VALUES (@interview4,
        5,
        '질문에 대해 구체적인 사례 중심으로 답변해줘서 도움이 많이 됐습니다. 특히 프로젝트 주제 선정 기준과 정리 방식이 명확해서 바로 적용할 수 있었습니다.',
        CURRENT_TIMESTAMP);

SELECT interview_id, student_member_id, major_member_id, status, title
FROM interview
ORDER BY
    interview_id;

