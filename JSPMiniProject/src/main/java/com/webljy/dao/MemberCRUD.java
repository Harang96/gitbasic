package com.webljy.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import com.webljy.dto.LoginDTO;
import com.webljy.etc.UploadedFile;
import com.webljy.vo.MemberVO;
import com.webljy.vo.PointlogVO;

public class MemberCRUD implements MemberDAO {
	private static MemberCRUD instance = null;
	
	private MemberCRUD() {}
	
	public static MemberCRUD getInstance() {
		if (instance == null) {
			instance = new MemberCRUD();
		}
		
		return instance;
	}
	
	@Override
	public MemberVO duplicateUserId(String tmpUserId) throws NamingException, SQLException {
		
		MemberVO result = null;
		
		Connection con = DBConnection.getInstance().connectDB();
		
		if (con != null) {
			String query = "select * from member where userId = ?";
			
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, tmpUserId);
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				result = new MemberVO(rs.getString("userId"), rs.getString("userPwd"), rs.getString("userEmail"), rs.getDate("registerDate"), rs.getInt("userImg"), rs.getInt("userPoint"));
			}
        			
			DBConnection.getInstance().dbClose(rs, ps, con);
		}
		
		return result;
	}

	@Override
	public int registerMemberWithFile(UploadedFile uf, MemberVO mem, String why, int howmuch) throws NamingException, SQLException {
		Connection con = DBConnection.getInstance().connectDB();
		con.setAutoCommit(false);
		int result = -1;
		
		System.out.println("회원가입 CRUD : " + mem.toString());
		if (con != null) {
			// (1) 업로드 된 파일이 있다면, 업로드 된 파일의 정보를 uploadedfile 테이블에 insert
			int no = -1;
			int insertCnt = -1;

			no = insertUploadedFileInfo(uf, con);
	
			// (2) 회원 가입(순수한 회원의 데이터를 저장 + (1)에서 저장된 파일의 no(pk)를 userImg에 저장 + 회원가입 (100포인트) 포함
			if (no != -1) {
				mem.setUserImg(no);
				mem.setUserPoint(howmuch);
				insertCnt = insertMember(mem, con);
			}
			
			// (3) pointlog 테이블에 회원 가입 포인트 로그를 남겨야 함
			int logCnt = -1;
			
			if (insertCnt == 1) {
				logCnt = insertPointLog(mem.getUserId(), why, con);
			}
			
			if (no != -1 && insertCnt == 1 && logCnt == 1) {
				con.commit();
				result = 1;
			} else {
				con.rollback();
				con.commit();
			}
			
			con.setAutoCommit(true);
			con.close();
		}
		
		return result;
	}

	@Override
	public int registerMember(MemberVO mem, String why, int howmuch) throws NamingException, SQLException {
		Connection con = DBConnection.getInstance().connectDB();
		con.setAutoCommit(false);
		int result = -1;
		
		if (con != null) {
//   	업로드 된 파일이 없는 경우
//		(1) 회원 가입(순수한 회원의 데이터를 저장 + userImg에 default(1)이 저장) + 회원가입 (100포인트) 포함
			mem.setUserPoint(howmuch);
			int insertCnt = insertMember(mem, con, false);

//		(2) 회원 가입이 완료된 경우 => pointlog 테이블에 회원 가입 포인트 로그를 남겨야 함
			int logCnt = -1;
			
			if (insertCnt == 1) {
				logCnt = insertPointLog(mem.getUserId(), why, con);
			}
			
			if (insertCnt == 1 && logCnt == 1) {
				con.commit();
				result = 1;
			} else {
				con.rollback();
			}
			
			con.setAutoCommit(true);
			con.close();
			
			
		}
		
		return result;
	}

	@Override
	public int insertUploadedFileInfo(UploadedFile uf, Connection con) throws NamingException, SQLException {
		String query = "insert into uploadedfile (`originalFileName`, `ext`, `newFileName`, `fileSize`) VALUES (?, ?, ?, ?)";
		int result = -1;
		
		PreparedStatement ps = con.prepareStatement(query);
		ps.setString(1, uf.getOriginalFileName());
		ps.setString(2, uf.getExt());
		ps.setString(3, uf.getNewFileName());
		ps.setLong(4, uf.getSize());
		
		ps.executeUpdate();
		
		result = getUploadedFileNo(con, uf); // 현재 업로드 된 파일의 저장 번호(no)
		
		ps.close();
		
		return result;
	}

	private int getUploadedFileNo(Connection con, UploadedFile uf) throws SQLException {
		String query = "select no from uploadedfile where newfilename = ?";
		int no = -1;
		
		PreparedStatement ps = con.prepareStatement(query);
		ps.setString(1, uf.getNewFileName());
		
		ResultSet rs = ps.executeQuery();
		
		while (rs.next()) {
			no = rs.getInt("no");
		}
		
		rs.close();
		ps.close();
		
		return no;
	}

	@Override
	public int insertMember(MemberVO newMem, Connection con) throws NamingException, SQLException {
		int result = -1;
		String query = "insert into member (userId, userPwd, userEmail, userImg, userPoint) values (?, sha1(md5(?)), ?, ?, ?)";
		
		PreparedStatement ps = con.prepareStatement(query);
		ps.setString(1, newMem.getUserId());
		ps.setString(2, newMem.getUserPwd());
		ps.setString(3, newMem.getUserEmail());
		ps.setInt(4, newMem.getUserImg());
		ps.setInt(5, newMem.getUserPoint());
		
		result = ps.executeUpdate();
		
		ps.close();
		
		return result;
	}

	@Override
	public int insertPointLog(String userId, String why, Connection con) throws NamingException, SQLException {
		String query = "insert into pointlog (why, howmuch, who) values (?, (select howmuch from pointpolicy where why = ?), ?)";
		int result = -1;
		
		PreparedStatement ps = con.prepareStatement(query);
		ps.setString(1, why);
		ps.setString(2, why);
		ps.setString(3, userId);
		
		result = ps.executeUpdate();
		
		ps.close();
		
		return result;
	}
	
	@Override
	public int insertMember(MemberVO newMem, Connection con, boolean userImg) throws NamingException, SQLException {
		// 오버로딩
		int result = -1;
		String query = "insert into member (userId, userPwd, userEmail, userPoint) values (?, sha1(md5(?)), ?, ?)";
		
		PreparedStatement ps = con.prepareStatement(query);
		ps.setString(1, newMem.getUserId());
		ps.setString(2, newMem.getUserPwd());
		ps.setString(3, newMem.getUserEmail());
		ps.setInt(4, newMem.getUserPoint());
		
		result = ps.executeUpdate();
		
		ps.close();
		
		return result;
	}

	@Override
	public LoginDTO loginMember(String userId, String userPwd) throws NamingException, SQLException {
		Connection con = DBConnection.getInstance().connectDB();
		
		LoginDTO loginMember = null;
		
		if (con != null) {
			String query = "select m.*, u.newFileName from member m inner join uploadedfile u on m.userImg = u.no where m.userId = ? and m.userPwd = sha1(md5(?))";
			
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, userId);
			ps.setString(2, userPwd);
			
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				loginMember = new LoginDTO(rs.getString("userId"), 
										rs.getString("userPwd"), 
										rs.getString("userEmail"), 
										rs.getDate("registerDate"), 
										rs.getInt("userImg"), 
										rs.getInt("userPoint"), 
										rs.getString("newFileName"),
										rs.getString("isAdmin"));
			}
			
			DBConnection.getInstance().dbClose(rs, ps, con);
		}
		
		return loginMember;
	}

	@Override
	public LoginDTO loginMember(String userId) throws NamingException, SQLException {
		Connection con = DBConnection.getInstance().connectDB();
		
		LoginDTO loginMember = null;
		
		if (con != null) {
			String query = "select m.*, u.newFileName from member m inner join uploadedfile u on m.userImg = u.no where m.userId = ?";
			
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, userId);
			
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				loginMember = new LoginDTO(rs.getString("userId"), 
										rs.getString("userPwd"), 
										rs.getString("userEmail"), 
										rs.getDate("registerDate"), 
										rs.getInt("userImg"), 
										rs.getInt("userPoint"), 
										rs.getString("newFileName"),
										rs.getString("isAdmin"));
			}
			
			DBConnection.getInstance().dbClose(rs, ps, con);
		}
		
		return loginMember;
	}
	
	@Override
	public int addPointToMember(String userId, String why) throws NamingException, SQLException {
		int result = -1;
		Connection con = DBConnection.getInstance().connectDB();
		con.setAutoCommit(false);
		
		if (con != null) {
			String query = "update member set userPoint = userPoint + (select howmuch from pointpolicy where why = ?) where userId = ?";
			
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, why);
			ps.setString(2, userId);
			
			result = ps.executeUpdate() + 1;
			
			ps.close();
			
			if (result == 2) {
				int afterPointLog = insertPointLog(userId, why, con);
				
				if (afterPointLog == 1) {
					con.commit();
					result = 1;
				} else {
					con.rollback();
					con.commit();
				}
			} else {
				con.rollback();
				con.commit();
			}
			 
			con.setAutoCommit(true);
			con.close();
		}
		
		return result;
	}

	@Override
	public int modifyUserWithFile(MemberVO mem, UploadedFile uf) throws NamingException, SQLException {
		System.out.println("파일을 갖고 수정");
		Connection con = DBConnection.getInstance().connectDB();
		con.setAutoCommit(false);
		int result = -1;
		
		System.out.println("회원 수정용 : " + mem.toString());
		if (con != null) {
			// (1) 회원 이미지 번호를 가져옴 // 여기
			int no = -1;

			no = selectUserImg(mem, uf, con);;
			System.out.println("수정용 no : " + no);
			int upCnt = -1;
			int fNo = -1;
			int memCnt = -1;
			
			// (2) 업로드 된 파일이 있다면, 업로드 된 파일의 정보를 uploadedfile 테이블에 update
			// (3) member 테이블 수정
			if (no == 1) {
				fNo = insertUploadedFileInfo(uf, con);
				if (fNo != -1) {
					upCnt = 1;
				}
				memCnt = updateMemberWithFile(mem, fNo, con);
			} else if (no != -1) {
				upCnt = updateUserImg(uf, no, con);
				memCnt = updateMember(mem, con);
			}
			
			System.out.println(no);
			System.out.println(memCnt);
			System.out.println(upCnt);

			if (no != -1 && memCnt == 1 && upCnt == 1) {
				con.commit();
				result = 1;
			} else {
				con.rollback();
				con.commit();
			}
			
			con.setAutoCommit(true);
			con.close();
		}
		
		return result;
	}

	@Override
	public int modifyUser(MemberVO mem) throws NamingException, SQLException {
		Connection con = DBConnection.getInstance().connectDB();
		con.setAutoCommit(false);
		int result = -1;
		
		System.out.println("회원 수정용 : " + mem.toString());
		if (con != null) {
			result = updateMember(mem, con);

			if (result == 1) {
				con.commit();
			} else {
				con.rollback();
				con.commit();
			}
		}
		
		con.setAutoCommit(true);
		con.close();
		
		return result;
	}

	private int selectUserImg(MemberVO mem, UploadedFile uf, Connection con) throws SQLException {
		int no = -1;
		String query = "select userImg from member where userId = ?";
		
		PreparedStatement ps = con.prepareStatement(query);
		ps.setString(1, mem.getUserId());
		
		ResultSet rs = ps.executeQuery();
		
		while (rs.next()) {
			no = rs.getInt("userImg");
		}
		
		rs.close();
		ps.close();
		
		return no;
	}
	
	private int selectUserImg(String userId, Connection con) throws SQLException {
		int no = -1;
		String query = "select userImg from member where userId = ?";
		
		PreparedStatement ps = con.prepareStatement(query);
		ps.setString(1, userId);
		
		ResultSet rs = ps.executeQuery();
		
		while (rs.next()) {
			no = rs.getInt("userImg");
		}
		
		rs.close();
		ps.close();
		
		return no;
	}
	
	private int updateUserImg(UploadedFile uf, int no, Connection con) throws SQLException {
		String query = "update uploadedfile set originalFileName = ?, ext = ?, newFileName = ?, filesize = ? where no = ?";
		int result = -1;
		
		System.out.println(uf.toString());
		
		PreparedStatement ps = con.prepareStatement(query);
		ps.setString(1, uf.getOriginalFileName());
		ps.setString(2, uf.getExt());
		ps.setString(3, uf.getNewFileName());
		ps.setLong(4, uf.getSize());
		ps.setInt(5, no);
		
		System.out.println(query);
		
		result = ps.executeUpdate();
		
		ps.close();
		
		return result;
	}
	
	private int updateMember(MemberVO mem, Connection con) throws SQLException {
		int result = -1;
		String query = "update member set ";
		if (!mem.getUserPwd().equals("")) {
			query += "userPwd = sha1(md5(?)), ";
		}
		query += "userEmail = ? where userId = ?";
		
		System.out.println("수정 쿼리문 : " + query);
		
		PreparedStatement ps = con.prepareStatement(query);
		if (!mem.getUserPwd().equals("")) {
			ps.setString(1, mem.getUserPwd());
			ps.setString(2, mem.getUserEmail());
			ps.setString(3, mem.getUserId());
		} else {
			ps.setString(1, mem.getUserEmail());
			ps.setString(2, mem.getUserId());
		}
		
		result = ps.executeUpdate();
		
		ps.close();
		
		return result;
	}

	private int updateMemberWithFile(MemberVO mem, int fNo, Connection con) throws SQLException { // 저기
		System.out.println("이동 후 fNo : " + fNo);
		int result = -1;
		String query = "update member set ";
		if (!mem.getUserPwd().equals("")) {
			query += "userPwd = sha1(md5(?)), ";
		}
		query += "userEmail = ?, userImg = ? where userId = ?";
		
		System.out.println("수정 쿼리문 : " + query);
		
		PreparedStatement ps = con.prepareStatement(query);
		if (mem.getUserPwd().equals("") == false) {
			ps.setString(1, mem.getUserPwd());
			ps.setString(2, mem.getUserEmail());
			ps.setInt(3, fNo);
			ps.setString(4, mem.getUserId());
		} else {
			ps.setString(1, mem.getUserEmail());
			ps.setInt(2, fNo);
			ps.setString(3, mem.getUserId());
		}
		
		result = ps.executeUpdate();
		System.out.println("결과 : " + result);
		ps.close();
		
		return result;
	}

	@Override
	public int deleteMember(String userId) throws NamingException, SQLException {
		Connection con = DBConnection.getInstance().connectDB();
		con.setAutoCommit(false);
		
		int result = -1;
		
		if (con != null) {
			// 일단 no 가져오기
			int no = -1;
			
			no = selectUserImg(userId, con);
			
			int dMem = -1;
			dMem = deleteMem(userId, con);
			
			int dImg = -1;
			if (no != -1) {
				dImg = deleteImg(no, con);
			}
			
			if (dImg == 1 && dMem == 1) {
				con.commit();
				result = 1;
			} else {
				con.rollback();
				con.commit();
			}
			
			con.setAutoCommit(true);
			con.close();
		}
		
		return result;
		
	}

	private int deleteMem(String userId, Connection con) throws SQLException {
		String query = "delete from member where userId = ?";
		int result = -1;
		
		PreparedStatement ps = con.prepareStatement(query);
		ps.setString(1, userId);
		
		result = ps.executeUpdate();
		
		ps.close();
		return result;
	}

	private int deleteImg(int no, Connection con) throws SQLException {
		String query = "delete from uploadedfile where no = ?";
		int result = -1;
		
		PreparedStatement ps = con.prepareStatement(query);
		ps.setInt(1, no);
		
		result = ps.executeUpdate();
		
		ps.close();
		
		return result;
	}

	@Override
	public LoginDTO getMemberInfo(String userId) throws NamingException, SQLException {
		Connection con = DBConnection.getInstance().connectDB();
		LoginDTO mem = null;
		
		if (con != null) {
			String query = "select m.*, u.newFileName from member m inner join uploadedfile u on m.userImg = u.no where m.userId = ?";
			
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, userId);
			
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				mem = new LoginDTO(rs.getString("userId"), 
						rs.getString("userPwd"), 
						rs.getString("userEmail"), 
						rs.getDate("registerDate"), 
						rs.getInt("userImg"), 
						rs.getInt("userPoint"), 
						rs.getString("newFileName"),
						rs.getString("isAdmin"));
			}
			
			DBConnection.getInstance().dbClose(rs, ps, con);
		}
		
		return mem;
	}

	@Override
	public List<PointlogVO> getPointInfo(String userId) throws NamingException, SQLException {
		Connection con = DBConnection.getInstance().connectDB();
		List<PointlogVO> lst = new ArrayList<PointlogVO>();
		
		if (con != null) {
			String query = "select * from pointlog where who = ? order by no desc";
			
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, userId);
			
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				PointlogVO log = new PointlogVO(rs.getInt("no"),
												rs.getDate("when"),
												rs.getString("why"),
												rs.getInt("howmuch"),
												rs.getString("who"));
				
				lst.add(log);
			}
			
			DBConnection.getInstance().dbClose(rs, ps, con);
		}
		
		return lst;
	}


}
