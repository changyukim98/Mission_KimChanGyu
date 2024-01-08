-- 게시판 더미 데이터
INSERT INTO board(name)
VALUES ('자유 게시판');
INSERT INTO board(name)
VALUES ('개발 게시판');
INSERT INTO board(name)
VALUES ('일상 게시판');
INSERT INTO board(name)
VALUES ('사건사고 게시판');


-- 게시글 더미 데이터
INSERT INTO post(board_id, content, password, title, writer)
VALUES (1, '자유 게시판 내용입니다.', '1234', '자유 게시판 제목입니다.', 'ㅇㅇ');
INSERT INTO post(board_id, content, password, title, writer)
VALUES (2, '개발 게시판 내용입니다.', '1234', '개발 게시판 제목입니다.', 'ㅇㅇ');
INSERT INTO post(board_id, content, password, title, writer)
VALUES (3, '일상 게시판 내용입니다.', '1234', '일상 게시판 제목입니다.', 'ㅇㅇ');
INSERT INTO post(board_id, content, password, title, writer)
VALUES (4, '사건사고 게시판 내용입니다.', '1234', '사건사고 게시판 제목입니다.', 'ㅇㅇ');