use ljy;
----------------------------------------------------------------------------------------
------------------------------ member --------------------------------------------------
----------------------------------------------------------------------------------------
-- member 테이블 생성
CREATE TABLE `ljy`.`member` (
  `userId` VARCHAR(8) NOT NULL,
  `userPwd` VARCHAR(500) NOT NULL,
  `userEmail` VARCHAR(50) NULL DEFAULT 'null',
  `registerDate` DATETIME NULL DEFAULT now(),
  `userImg` INT(11) NULL DEFAULT 1,
  `userPoint` INT NULL DEFAULT 0,
  `isAdmin` VARCHAR(1) DEFAULT 'N',
  PRIMARY KEY (`userId`),
  UNIQUE INDEX `userEmail_UNIQUE` (`userEmail` ASC) VISIBLE,
  INDEX `member_userImg_fk_idx` (`userImg` ASC) VISIBLE,
  CONSTRAINT `member_userImg_fk`
    FOREIGN KEY (`userImg`)
    REFERENCES `ljy`.`uploadedfile` (`no`)
    ON DELETE NO ACTION
    ON UPDATE CASCADE);
  
-- 유저 아이디 중복 검사
select * from member where userId = ?;
-- member에 회원가입 insert
insert into member (userId, userPwd, userEmail, userImg, userPoint) values (?, sha1(md5(?)), ?, ?, ?);

select sha1(md5('1234'));
  
-- uploadedfile 테이블 생성
CREATE TABLE `ljy`.`uploadedfile` (
  `no` INT NOT NULL AUTO_INCREMENT,
  `originalFileName` VARCHAR(100) NULL,
  `ext` VARCHAR(5) NULL,
  `newFileName` VARCHAR(100) NULL,
  `fileSize` INT NULL,
  PRIMARY KEY (`no`));
  
-- 수정한 uploadedfile 테이블
CREATE TABLE `uploadedfile` (
  `no` int(11) NOT NULL AUTO_INCREMENT,
  `originalFileName` varchar(100) DEFAULT NULL,
  `ext` varchar(5) DEFAULT NULL,
  `newFileName` varchar(100) DEFAULT NULL,
  `fileSize` int(11) DEFAULT NULL,
  `boardNo` int(11) DEFAULT NULL,
  `base64String` longtext,
  PRIMARY KEY (`no`),
  UNIQUE KEY `newFileName_UNIQUE` (`newFileName`),
  KEY `uploadedfile_boardNo_fk_idx` (`boardNo`),
  CONSTRAINT `uploadedfile_boardNo_fk` FOREIGN KEY (`boardNo`) REFERENCES `board` (`no`)
);

-- fk 적용
ALTER TABLE `ljy`.`uploadedfile` 
ADD COLUMN `boardNo` INT(11) NULL DEFAULT NULL AFTER `fileSize`,
ADD INDEX `uploadedfile_boardNo_fk_idx` (`boardNo` ASC) VISIBLE;
;
ALTER TABLE `ljy`.`uploadedfile` 
ADD CONSTRAINT `uploadedfile_boardNo_fk`
  FOREIGN KEY (`boardNo`)
  REFERENCES `ljy`.`board` (`no`)
  ON DELETE RESTRICT
  ON UPDATE RESTRICT;
  
ALTER TABLE `ljy`.`uploadedfile` 
ADD COLUMN `base64String` LONGTEXT NULL AFTER `boardNo`;

-- insert 첫 이미지(default)
INSERT INTO `ljy`.`uploadedfile` (`no`, `originalFileName`, `ext`, `newFileName`, `fileSize`) VALUES ('1', 'user.png', '.png', 'memberImg/user.png', '9600');
-- 파일 업로드
insert into uploadedfile (`originalFileName`, `ext`, `newFileName`, `fileSize`) VALUES (?, ?, ?, ?);
-- 업로드 된 파일의 저장 번호를 얻어오기
select no from uploadedfile where newfilename = ?;

-- pointpolicy 테이블 생성
CREATE TABLE `ljy`.`pointpolicy` (
  `why` VARCHAR(50) NOT NULL,
  `howmuch` INT NULL,
  PRIMARY KEY (`why`));
  
-- pointpolicy 데이터 작성
INSERT INTO `ljy`.`pointpolicy` (`why`, `howmuch`) VALUES ('회원가입', '100');
INSERT INTO `ljy`.`pointpolicy` (`why`, `howmuch`) VALUES ('로그인', '5');
INSERT INTO `ljy`.`pointpolicy` (`why`, `howmuch`) VALUES ('게시물작성', '2');
INSERT INTO `ljy`.`pointpolicy` (`why`, `howmuch`) VALUES ('답글작성', '1');
  
  -- pointlog 테이블 생성
  CREATE TABLE `ljy`.`pointlog` (
  `no` INT NOT NULL AUTO_INCREMENT,
  `when` DATETIME NULL DEFAULT now(),
  `why` VARCHAR(50) NULL,
  `howmuch` INT NULL,
  `who` VARCHAR(8) NULL,
  PRIMARY KEY (`no`),
  INDEX `pointlog_why_fk_idx` (`why` ASC) VISIBLE,
  INDEX `pointlog_who_fk_idx` (`who` ASC) VISIBLE,
  CONSTRAINT `pointlog_why_fk`
    FOREIGN KEY (`why`)
    REFERENCES `ljy`.`pointpolicy` (`why`)
    ON DELETE NO ACTION
    ON UPDATE CASCADE,
  CONSTRAINT `pointlog_who_fk`
    FOREIGN KEY (`who`)
    REFERENCES `ljy`.`member` (`userId`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION);
    
-- pointlog 테이블에 내역 insert
insert into pointlog (why, howmuch, who) values (?, ?, ?);

----------------------------- 로그인 -------------------------------------------------
-- 로그인 검사(dooly, 1234)
select * from member where userId = 'dooly' and userPwd = sha1(md5('1234'));
select * from member where userId = ? and userPwd = sha1(md5(?));

-- 회원 정보 + memberImg
select m.*, u.newFileName from member m inner join uploadedfile u on m.userImg = u.no where m.userId = 'admin' and m.userPwd = sha1(md5('1234a'));
select m.*, u.newFileName from member m inner join uploadedfile u on m.userImg = u.no where m.userId = ? and m.userPwd = sha1(md5(?));

-- 로그인 성공 시 member 테이블 포인트 update
update member set userPoint = userPoint + (select howmuch from pointpolicy where why = ?) where userId = ?;
select * from member where userId = 'admin';

---------------------------- 회원 수정 -----------------------------------------------
-- 회원의 Img no를 가져오기
select userImg from member where userId = ?;

-- uploadedfile 테이블에 유저 이미지 수정
update uploadedfile set (originalFileName, ext, newFileName, filesize) values (?, ?, ?, ?) where no = ?;

-- member 테이블 회원 수정
update member set userPwd = ?, userEmail = ? where userId = ?;
update member set userPwd = ?, userEmail = ?, userImg = ? where userId = ?;
update member set userImg = 1, userEmail = 'asd1@asd.asd' where userId = 'asdf1';

-- 새 파일 이름으로 no 가져오기
select no from uploadedfile where newFileName = ?;

--------------------------- 회원 탈퇴 -----------------------------------------
-- 회원 탈퇴
delete from member where userId = ?;

-- 이미지 삭제
delete from uploadedfile where no = ?;

commit;

--------------------------- 회원 페이지 ---------------------------------------
-- 멤버 + 포인트 정보
select m.*, p.* from member m inner join pointlog p on m.userId = p.who where m.userId = ?;

-- 해당 아이디 회원의 정보
select m.*, u.newFileName from member m inner join uploadedfile u on m.userImg = u.no where m.userId = ?;

-- 해당 아이디 회원의 포인트 로그
select * from pointlog where who = ? order by no desc;







----------------------------------------------------------------------------------------
------------------------------ board --------------------------------------------------
----------------------------------------------------------------------------------------
-- board 테이블 생성
CREATE TABLE `ljy`.`board` (
  `no` INT NOT NULL AUTO_INCREMENT,
  `writer` VARCHAR(8) NULL,
  `title` VARCHAR(100) NOT NULL,
  `postDate` DATETIME NULL DEFAULT now(),
  `content` VARCHAR(1000) NOT NULL,
  `readCount` INT NULL DEFAULT 0,
  `likeCount` INT NULL DEFAULT 0,
  `ref` INT NULL DEFAULT NULL,
  `step` INT NULL DEFAULT 0,
  `reforder` INT NULL DEFAULT 0,
  `isDelete` VARCHAR(1) NULL DEFAULT 'N',
  PRIMARY KEY (`no`),
  INDEX `board_writer_fk_idx` (`writer` ASC) VISIBLE,
  CONSTRAINT `board_writer_fk`
    FOREIGN KEY (`writer`)
    REFERENCES `ljy`.`member` (`userId`)
    ON DELETE SET NULL
    ON UPDATE NO ACTION);

-- 첫 글 남기기    
insert into board (writer, title, content, ref) values ('admin', '게시판이 생성되었습니다.', '자유롭게 글을 남겨주세요.', 1);

insert into board (writer, title, content, ref) values ('admin', '페이징 실험용 글', '페이징 실험용
 글', (select max(no) + 1 from (select * from board) as b));

-- 게시판의 전체 글 가져오기
select * from board order by no desc;

-- 게시판의 글 지우기
delete from board where no = 1;


---------------------------------------- 게시판의 글 저장 -------------------------------------------
-- 게시판의 글 저장
insert into board (writer, title, content, ref) value('admin', '초반에 글 남깁니다.', '사람 많아지기 전에 게시판에 글 남깁니다.', 1);
insert into board (writer, title, content, ref) value(?, ?, ?, (select max(b.no) + 1 as ref from board as b));
select max(b.no) + 1 as ref from board as b;
-- select auto_increament as nextref from information_schema.tables where table_schema = 'key' and table_name = 'board'; 

-- 글 저장 후 가장 마지막의 글의 번호 가져오기
select max(no) as no from board;

-- 파일 저장
insert into uploadedfile (originalFileName, ext, newFileName, fileSize, boardNo, base64String) values (?, ?, ?, ?, ?, ?);

-- 포인트 로그에 저장
insert into pointlog (why, howmuch, who) values (?, (select howmuch from pointpolicy where why = ?), ?);

-- AI 조정
ALTER TABLE board AUTO_INCREMENT = (select max(b.no) + 1 from board as b); -- 실패
ALTER TABLE board AUTO_INCREMENT = 28;

delete from board;

commit;

---------------------------------------- 게시판의 글 조회 ------------------------------------------
-- 게시판의 글을 조회한 기록에 대한 테이블 생성
CREATE TABLE `ljy`.`readcountprocess` (
  `no` INT NOT NULL AUTO_INCREMENT,
  `ipAddr` VARCHAR(50) NULL,
  `boardNo` INT NULL,
  `readTime` DATETIME NULL DEFAULT now(),
  PRIMARY KEY (`no`),
  INDEX `rcp_boardNo_fk_idx` (`boardNo` ASC) VISIBLE,
  CONSTRAINT `rcp_boardNo_fk`
    FOREIGN KEY (`boardNo`)
    REFERENCES `ljy`.`board` (`no`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);
    
-- readcountprocess 테이블에 ip 주소와 글 번호 no가 있는지 없는지 확인
select * from readcountprocess where ipAddr = ? and boardNo = ?;

-- 글을 조회한지 얼마나 됐는지 확인(시간 차이를 계산)
select timestampdiff(HOUR, readTime, now()) from readcountprocess as hourDiff where ipAddr = ? and boardNo = ?; -- 한줄
select timestampdiff(HOUR, (select readTime from readcountprocess as hourDiff where ipAddr = ? and boardNo = ?), now()); -- 서브쿼리
select timestampdiff(HOUR, readTime, '2024-01-10 12:22:13') as hourDiff from readcountprocess where ipAddr = '127.0.0.1' and boardNo = 1;

-- readcountprocess 테이블에 아이피 주소와 글 번호와 읽은 시간을 넣기
insert into readcountprocess (ipAddr, boardNo) values (?, ?);

-- readcountprocess 테이블에 아이피 주소와 글 번호와 읽은 시간을 수정하기
update readcountprocess set readTime = now() where ipAddr = ? and boardNo = ?;

-- board 테이블의 readCount 증가시키는 함수
update board set readcount = (SELECT readcount + 1 FROM (SELECT readcount FROM board WHERE no = 1) as B) where no = 1;
update board set readcount = readcount + 1 where no = ?;

commit;

-- no번 글 가져오기
select * from board where no = ?;

-- no번 글의 이미지 가져오기
select * from uploadedfile where boardNo = ?;

------------------------------------ 게시글 삭제 --------------------------------------------
-- 게시글 삭제
update board set isDelete = 'Y' where no = ?;


------------------------------------ 게시글 수정 --------------------------------------------
-- 게시글 수정
update board set title = ?, content = ? where no = ?;

-- 게시글의 이미지 수정
update uploadedfile set originalFileName = ?, ext =?, fileSize = ?, base64String = ? where boardNo = ?;


------------------------------------- 답글 달기 ---------------------------------------------
-- 테스트
CREATE TABLE test_board AS
SELECT * FROM board WHERE 1 = 0;
INSERT INTO test_board
SELECT * FROM board;
insert into test_board (no, writer, title, content, ref, step, reforder) value(9, 'admin', '답글 실험', '답글 실험', 8, (select b.step from test_board b where no = 8) + 1, (select b.reforder from test_board b where no = 8) + 1);
insert into test_board (no, writer, title, content, ref, step, reforder) value(10, 'admin', '답글 실험2', '답글 실험2', 8, (select b.step from test_board b where no = 9) + 1, (select b.reforder from test_board b where no = 9) + 1);
delete from test_board where no = 10;
update test_board set reforder = reforder + 1 where ref = 8 and reforder > (SELECT reforder FROM (SELECT reforder FROM board WHERE no = 8) AS subquery);
SELECT reforder FROM (SELECT reforder FROM test_board WHERE no = 9) as sub;

-- 게시글 리스트 출력할 때 정렬 기준
select * from board order by ref desc, reforder asc;

-- pRef == ref and pReforder < reforder인 행을 찾아서 reforder = reforder + 1 업데이트
update board set reforder = reforder + 1 where ref = ? and reforder > (SELECT reforder FROM (SELECT reforder FROM board WHERE no = ?) AS b);	

-- 답글을 board 테이블에 등록
insert into board (writer, title, content, ref, step, reforder) value(?, ?, ?, ?, (select b.step from board b where no = ?) + 1, (select b.reforder from board b where no = ?) + 1);


------------------------------------- 페이징 처리 ---------------------------------------------
-- 조회수가 많은 글
select * from board order by readcount desc limit 5;

-- 한 페이지당 보여줄 글의 개수 = 5
-- 전체 글의 개수
select count(*) as totalPostCnt from board;
-- => 총 페이지 수 = 전체 글의 개수 / 한 페이지 당 보여줄 글의 개수
-- 20 / 5 = 4 (나누어 떨어지면 4페이지)
-- 23 / 5 = 4.6 (나누어 떨어지지 않으면 올림)

-- 보여주기 시작할 row index 번호
-- limit [보여주기 시작할 row index 번호], 보여줄 row의 개수
-- 1페이지
select * from board order by ref desc, reforder asc limit 0, 5;

-- 2페이지
select * from board order by ref desc, reforder asc limit 5, 5;

-- 3페이지
select * from board order by ref desc, reforder asc limit 10, 5;

-- 가져오는 쿼리 (? = (pageNo - 1) * 한 페이지당 보여줄 글의 개수)
select * from board order by ref desc, reforder asc limit ?, ?;

-- 페이징 블럭 처리
-- 1 2 / 3 4 / 5 6 / 7

-- 1) 1개의 블럭에 몇 개 페이지를 보여줄 것인지 (pageCntPerBlock) : 2

-- 2) 현재 페이지가 속한 페이징 블럭 번호 :
-- ==> 전체 페이징 블럭 개수 = 전체 페이지 수(7) / 블럭 당 페이지 수(2) => 나누어 떨어지지 않으면 올림
-- 현재 페이지가 2 -> 1번 블럭
-- pageNo / pageCntPerBlock
-- 현재 페이지가 5 -> 3번 블럭
-- pageNo / pageCntPerBlock

-- 3) 현재 페이징 블럭 시작 페이지 번호 :
-- 페이지 블럭의 번호
-- (블럭 번호 - 1) * pageCntPerBlock + 1
-- 페이지 블럭의 시작 번호
-- pageNo - ((pageNo - 1) % viewPostCntPerPage)

-- 4) 현재 페이징 블럭 끝 페이지 번호 :
-- 블럭 번호 * pageCntPerBlock



----------------------------------------------------------------------------------------
-------------------------------- like --------------------------------------------------
----------------------------------------------------------------------------------------
-- likelog 테이블 생성
CREATE TABLE `ljy`.`likelog` (
  `no` INT NOT NULL AUTO_INCREMENT,
  `userId` VARCHAR(8) NULL,
  `boardNo` INT NULL,
  PRIMARY KEY (`no`),
  INDEX `likelog_userId_fk_idx` (`userId` ASC) VISIBLE,
  INDEX `likelog_boardNo_fk_idx` (`boardNo` ASC) VISIBLE,
  CONSTRAINT `likelog_userId_fk`
    FOREIGN KEY (`userId`)
    REFERENCES `ljy`.`member` (`userId`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `likelog_boardNo_fk`
    FOREIGN KEY (`boardNo`)
    REFERENCES `ljy`.`board` (`no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);

-- 좋아요 누른 글인지 판단
select * from likelog where userId = ? and boardNo = ?;

-- 좋아요 누르기
insert into likelog (userId, boardNo) values (?, ?);
update board set likeCount = likeCount + 1 where no = ?;

-- 좋아요 삭제
delete from likelog where userId = ? and boardNo = ?;
update board set likeCount = likeCount - 1 where no = ?;

-- 이후 좋아요 읽어오기
select likeCount from board where no = ?;

----------------------------------------------------------------------------------------
--------------------------------- 검색 --------------------------------------------------
----------------------------------------------------------------------------------------
-- 검색어에 따라 글의 수 가져오기
select count(*) as totalPostCnt from board;

-- 1. 검색 유형 = title
select count(*) as totalPostCnt from board where lower(title) like ? and isDelete = 'N';
-- 2. 검색 유형 = writer
select count(*) as totalPostCnt from board where lower(writer) like ? and isDelete = 'N';
-- 3. 검색 유형 content(제목 + 내용)
select count(*) as totalPostCnt from board where lower(title) like ? or lower(content) like ? and isDelete = 'N';

-- 검색어에 따라 게시글 가져오기
select * from board where ? like ? and isDelete = 'N' order by ref desc, reforder asc limit ?, ?;

-- 1. 검색 유형 = title
select * from board where lower(title) like ? and isDelete = 'N' order by ref desc, reforder asc limit ?, ?;
-- 2. 검색 유형 = writer
select * from board where lower(writer) like ? and isDelete = 'N' order by ref desc, reforder asc limit ?, ?;
-- 3. 검색 유형 content(제목 + 내용)
select * from board where lower(title) like ? or lower(content) like ? and isDelete = 'N' order by ref desc, reforder asc limit ?, ?;



