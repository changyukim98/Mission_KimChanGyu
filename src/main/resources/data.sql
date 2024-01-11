-- 게시판 초기 데이터
INSERT INTO board(name)
VALUES ('자유 게시판');
INSERT INTO board(name)
VALUES ('개발 게시판');
INSERT INTO board(name)
VALUES ('일상 게시판');
INSERT INTO board(name)
VALUES ('사건사고 게시판');


-- 게시글 초기 데이터
INSERT INTO Article(board_id, content, password, title, writer)
VALUES (1, '비밀번호 1234', '1234', '자유 게시판 test', '김찬규');
INSERT INTO article(board_id, content, password, title, writer)
VALUES (2, '비밀번호 1234', '1234', '개발 게시판 test', '김찬규');
INSERT INTO article(board_id, content, password, title, writer)
VALUES (3, '비밀번호 1234', '1234', '일상 게시판 test', '김찬규');
INSERT INTO article(board_id, content, password, title, writer)
VALUES (4, '비밀번호 1234', '1234', '사건사고 게시판 test', '김찬규');

