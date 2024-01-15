package com.webljy.dao;

import java.sql.SQLException;
import java.util.List;

import javax.naming.NamingException;

import com.webljy.dto.BoardNoDTO;
import com.webljy.dto.ReplyDTO;
import com.webljy.dto.SearchCriteria;
import com.webljy.dto.WriteBoardDTO;
import com.webljy.etc.PagingInfo;
import com.webljy.etc.UploadedFile;
import com.webljy.vo.Board;

public interface BoardDAO {
	// --------------------- 게시판 띄우기 -------------------------
	// 전체 게시판 가져오기
	List<Board> selectAllBoard() throws NamingException, SQLException;
	
	// --------------------- 게시판 글 쓰기 --------------------------
	// 게시판 글 저장(업로드 파일이 있는 경우)
	int insertBoardWithFileTransaction(WriteBoardDTO brd, UploadedFile uf) throws NamingException, SQLException;

	// 게시판 글 저장(업로드 파일이 없는 경우)
	int insertBoardTransaction(WriteBoardDTO brd) throws NamingException, SQLException;

	// --------------------- 조회수 처리 ------------------------
	// readcountprocess 테이블에 ip 주소와 글 번호 no가 있는지 없는지 확인
	boolean selectReadCountProcess(BoardNoDTO bNo) throws NamingException, SQLException;

	// 글을 조회한지 얼마나 됐는지 확인(24시간이 지났는지)
	int selectHourDiff(BoardNoDTO bNo) throws NamingException, SQLException;

	// readcountprocess 테이블에 아이피 주소와 글 번호와 읽은 시간을 넣기
	int readCountProcessWithCntInc(BoardNoDTO bNo, String how) throws NamingException, SQLException;

	// ---------------------- 게시글 가져오기 ----------------------
	// 해당 no의 글 가져오기
	Board selectBoardByNo(BoardNoDTO bNo) throws NamingException, SQLException;

	// 해당 글의 파일 가져오기
	UploadedFile getFile(BoardNoDTO bNo) throws NamingException, SQLException;

	// ------------------------ 게시글 삭제 ------------------------
	// 게시글 삭제
	int deletePost(int no) throws NamingException, SQLException;

	// ------------------------ 게시글 수정 ------------------------
	// 게시글 수정(파일)
	int modifyPostWithFile(WriteBoardDTO brd, UploadedFile uf, int no) throws NamingException, SQLException;
	
	// 게시글 수정
	int modifyPost(WriteBoardDTO brd, int no) throws NamingException, SQLException;


	// ------------------------- 답글 달기 ------------------------
	// 답글 처리
	boolean insertReplyTransaction(ReplyDTO rep) throws NamingException, SQLException;

	// ------------------------- 페이징 처리 -------------------------
	// 총 게시글 수 가져오기
	int getTotalPostCnt() throws NamingException, SQLException;
	
	// 페이징에 따라 글을 가져오기
	List<Board> selectAllBoard(PagingInfo pi) throws NamingException, SQLException;

	// ------------------------- 좋아요 처리 -------------------------
	// 좋아요를 눌렀는지 판단
	Boolean validateLike(BoardNoDTO bNo) throws NamingException, SQLException;

	// 좋아요를 눌렀을 떄(좋아요)
	int likePost(BoardNoDTO bNo) throws NamingException, SQLException;

	// 좋아요를 눌렀을 떄(좋아요 해제)
	int unlikePost(BoardNoDTO bNo) throws NamingException, SQLException;
	
	// ---------------------------- 검색 ----------------------------
	// 검색한 게시글 수 가져오기
	int getTotalPostCnt(SearchCriteria sc) throws NamingException, SQLException;

	// 검색어에 따라 글 가져오기
	List<Board> selectSearchBoard(SearchCriteria sc, PagingInfo pi) throws NamingException, SQLException;

}
