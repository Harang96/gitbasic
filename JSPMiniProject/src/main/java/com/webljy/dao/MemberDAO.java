package com.webljy.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.naming.NamingException;

import com.webljy.dto.LoginDTO;
import com.webljy.etc.UploadedFile;
import com.webljy.vo.MemberVO;
import com.webljy.vo.PointlogVO;

public interface MemberDAO {
	// 유저 아이디가 중복되는지 검사
	MemberVO duplicateUserId(String tmpUserId) throws NamingException, SQLException;
	
	// 업로드 된 파일이 있는 경우 회원 가입
	int registerMemberWithFile(UploadedFile uf, MemberVO mem, String why, int howmuch) throws NamingException, SQLException;
	
	// 업로드 된 파일이 있는 경우 회원 가입
	int registerMember(MemberVO mem, String why, int howmuch) throws NamingException, SQLException;
	
	// 업로드 된 파일의 정보를 uploadedFile 테이블에 insert
	int insertUploadedFileInfo(UploadedFile uf, Connection con) throws NamingException, SQLException;
	
	// 회원 정보 insert
	int insertMember(MemberVO newMem, Connection con) throws NamingException, SQLException;
	
	// pointlog 테이블에 회원 가입 포인트 로그를 남김
	int insertPointLog(String userId, String why, Connection con) throws NamingException, SQLException;

	// 회원 가입 시 멤버 추가
	int insertMember(MemberVO newMem, Connection con, boolean userImg) throws NamingException, SQLException;

	// 로그인
	LoginDTO loginMember(String userId, String userPwd) throws NamingException, SQLException;
	
	// 아이디로 로그인 객체 찾기
	LoginDTO loginMember(String userId) throws NamingException, SQLException;
	
	// member 포인트 업데이트
	int addPointToMember(String userId, String why) throws NamingException, SQLException;
	
	// 파일이 있는 경우 수정
	int modifyUserWithFile(MemberVO mem, UploadedFile uf) throws NamingException, SQLException;
	
	// 파일이 없는 경우 수정
	int modifyUser(MemberVO mem) throws NamingException, SQLException;

	// 유저 지우기
	int deleteMember(String userId) throws NamingException, SQLException;

	// 마이페이지 - 멤버 정보 가져오기
	LoginDTO getMemberInfo(String userId) throws NamingException, SQLException;
	
	// 마이페이지 - 포인트 기록 가져오기
	List<PointlogVO> getPointInfo(String userId) throws NamingException, SQLException;
}
