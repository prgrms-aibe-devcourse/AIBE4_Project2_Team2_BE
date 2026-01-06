INSERT INTO member (name, nickname, email, username, password, status, role, created_at, updated_at)
VALUES ('신형만', 'hyeongman', 'hyeongman.shin@example.com', 'hyeongman', '{noop}Passw0rd!1', 'ENROLLED', 'STUDENT',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('김민지', 'minji', 'minji.kim@example.com', 'minji99', '{noop}Passw0rd!2', 'ENROLLED', 'STUDENT',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('이서준', 'seojun', 'seojun.lee@example.com', 'seojun_lee', '{noop}Passw0rd!3', 'ENROLLED', 'STUDENT',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('박지훈', 'jihoon', 'jihoon.park@example.com', 'parkjh', '{noop}Passw0rd!4', 'ENROLLED', 'STUDENT',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('최유나', 'yuna', 'yuna.choi@example.com', 'yuna.choi', '{noop}Passw0rd!5', 'ENROLLED', 'STUDENT',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO member_academic (member_id, university, major, created_at, updated_at)
VALUES ((SELECT member_id FROM member WHERE username = 'hyeongman'), '서울대학교', '컴퓨터공학과', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       ((SELECT member_id FROM member WHERE username = 'minji99'), '고려대학교', '경영학과', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       ((SELECT member_id FROM member WHERE username = 'seojun_lee'), '한양대학교', '기계공학과', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       ((SELECT member_id FROM member WHERE username = 'parkjh'), '경북대학교', '전자공학과', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       ((SELECT member_id FROM member WHERE username = 'yuna.choi'), '이화여자대학교', '심리학과', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP);
