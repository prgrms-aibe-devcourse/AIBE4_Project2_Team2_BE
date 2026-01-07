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
