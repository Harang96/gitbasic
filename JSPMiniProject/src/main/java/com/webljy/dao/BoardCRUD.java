package com.webljy.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import com.webljy.dto.BoardNoDTO;
import com.webljy.dto.ReplyDTO;
import com.webljy.dto.SearchCriteria;
import com.webljy.dto.WriteBoardDTO;
import com.webljy.etc.PagingInfo;
import com.webljy.etc.UploadedFile;
import com.webljy.vo.Board;

public class BoardCRUD implements BoardDAO {
	private static BoardCRUD instance;
	
	private BoardCRUD() {}
	
	public static BoardCRUD getInstance() {
		if (instance == null) {
			instance = new BoardCRUD();
		}
		
		return instance;
	}

	// ------------------------ 게시판 불러오기 -----------------------------
	@Override
	public List<Board> selectAllBoard() throws NamingException, SQLException {
		Connection con = DBConnection.getInstance().connectDB();
		List<Board> lst = new ArrayList<Board>();
		
		if (con != null) {
			String query = "select * from board order by ref desc, reforder asc";
			
			PreparedStatement ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				lst.add(new Board(rs.getInt("no"), 
								rs.getString("writer"), 
								rs.getString("title"), 
								rs.getTimestamp("postDate"), 
								rs.getString("content"), 
								rs.getInt("readCount"), 
								rs.getInt("likeCount"),
								rs.getInt("ref"),
								rs.getInt("step"),
								rs.getInt("reforder"),
								rs.getString("isDelete")));
			}
			
			DBConnection.getInstance().dbClose(rs, ps, con);
		}

		return lst;
	}
	
	// ------------------------ 게시판 글 작성 -----------------------------
	@Override
	public int insertBoardWithFileTransaction(WriteBoardDTO brd, UploadedFile uf) throws NamingException, SQLException {
		Connection con = DBConnection.getInstance().connectDB();
		con.setAutoCommit(false);
		int result = -1;
		
		if (con != null) {
			int no = -1;
			no = insertBoardTable(brd, con);
			
			int fileRe = -1;
			if (no != 1) {
				fileRe = insertFileTable(uf, no, con);
			}
			
			int pointRe = -1;
			if (fileRe == 1) {
				pointRe = insertPointTable(brd.getWriter(), "게시물작성", con);
//				pointRe = MemberCRUD.getInstance().addPointToMember(brd.getWriter(), "게시물작성");
				// 안에서 다시 con을 열어서 그런가 longTime 예외가 발생
			}
			
			if (no != -1 && fileRe == 1 && pointRe == 1) {
				con.commit();
				result = 1;
			} else {
				con.rollback();
//				String query = "ALTER TABLE board AUTO_INCREMENT = (select max(b.no) + 1 from board as b)";
//				PreparedStatement ps = con.prepareStatement(query);
//				ps.executeUpdate();
				con.commit();
			}
			
			con.setAutoCommit(true);
			con.close();
		}
		
		return result;
	}

	@Override
	public int insertBoardTransaction(WriteBoardDTO brd) throws NamingException, SQLException {
		Connection con = DBConnection.getInstance().connectDB();
		con.setAutoCommit(false);
		int result = -1;
		
		if (con != null) {
			int no = -1;
			no = insertBoardTable(brd, con);
			
			int pointRe = -1;
			if (no != 1) {
				pointRe = insertPointTable(brd.getWriter(), "게시물작성", con);
			}
			
			int memRe = -1;
			if (pointRe == 1) {
				memRe = updateMemberPoint("게시물작성", brd.getWriter(), con);
			}
			
			if (no != -1 && pointRe == 1 && memRe == 1) {
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

	// Board 테이블에 작성
	private int insertBoardTable(WriteBoardDTO brd, Connection con) throws NamingException, SQLException {
		String query = "insert into board (writer, title, content, ref) value(?, ?, ?, ((select max(b.no) + 1 as ref from board as b)))";
		int result = -1;
		int tmpResult = -1;
		
		System.out.println("작성자 : " + brd.getWriter());
		
		PreparedStatement ps = con.prepareStatement(query);
		ps.setString(1, brd.getWriter());
		ps.setString(2, brd.getTitle());
		ps.setString(3, brd.getContent());
		
		tmpResult = ps.executeUpdate();
		
		if (tmpResult == 1) {
			result = selectBoardNo(con);
		}
		
		return result;
	}

	// 가장 마지막 번호의 글의 no
	private int selectBoardNo(Connection con) throws SQLException {
		String query = "select max(no) as no from board";
		int no = -1;
		
		PreparedStatement ps = con.prepareStatement(query);
		
		ResultSet rs = ps.executeQuery();
		
		while (rs.next()) {
			no = rs.getInt("no");
		}
		
		rs.close();
		ps.close();
		
		return no;
	}

	// 파일 테이블에 인서트
	private int insertFileTable(UploadedFile uf, int no, Connection con) throws SQLException {
		String query = "insert into uploadedfile (originalFileName, ext, newFileName, fileSize, boardNo, base64String) values (?, ?, ?, ?, ?, ?)";
		int result = -1;
		
		PreparedStatement ps = con.prepareStatement(query);
		ps.setString(1, uf.getOriginalFileName());
		ps.setString(2, uf.getExt());
		ps.setString(3, uf.getNewFileName());
		ps.setLong(4, uf.getSize());
		ps.setInt(5, no);
		ps.setString(6, uf.getBase64String());
		
		result = ps.executeUpdate();
		
		ps.close();
		
		return result;
	}
	
	// 포인트로그 테이블에 인서트
	private int insertPointTable(String writer, String why, Connection con) throws SQLException {
		String query = "insert into pointlog (why, howmuch, who) values (?, (select howmuch from pointpolicy where why = ?), ?)";
		int result = -1;
		
		PreparedStatement ps = con.prepareStatement(query);
		ps.setString(1, why);
		ps.setString(2, why);
		ps.setString(3, writer);
		
		result = ps.executeUpdate();
		
		ps.close();
		
		return result;
	}

	// member 테이블에 포인트 적립
	private int updateMemberPoint(String why, String writer, Connection con) throws SQLException {
		String query = "update member set userPoint = userPoint + (select howmuch from pointpolicy where why = ?) where userId = ?";
		int result = -1;
		
		PreparedStatement ps = con.prepareStatement(query);
		ps.setString(1, why);
		ps.setString(2, writer);
		
		result = ps.executeUpdate();
		
		ps.close();
		
		return result;
	}

	// ------------------------------------------- 조회수 관련 ----------------------------------------------
	// readcountprocess 테이블에 ip 주소와 글 번호 no가 있는지 없는지 확인
	@Override
	public boolean selectReadCountProcess(BoardNoDTO bNo) throws NamingException, SQLException {
		Connection con = DBConnection.getInstance().connectDB();
		boolean result = false;
		
		if (con != null) {
			String query = "select * from readcountprocess where ipAddr = ? and boardNo = ?";
			
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, bNo.getIp());
			ps.setInt(2, bNo.getNo());
			
			ResultSet rs = ps.executeQuery();
			
			if (rs.next()) {
				result = true;
			}
			
			DBConnection.getInstance().dbClose(rs, ps, con);
		}
		
		System.out.println(result);
		return result;
	}

	// 글을 조회한지 얼마나 됐는지 확인(24시간이 지났는지)
	@Override
	public int selectHourDiff(BoardNoDTO bNo) throws NamingException, SQLException {
		Connection con = DBConnection.getInstance().connectDB();
		int result = -1;
	
		if (con != null) {
			String query = "select timestampdiff(HOUR, readTime, now()) as hourDiff from readcountprocess where ipAddr = ? and boardNo = ?";
			
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, bNo.getIp());
			ps.setInt(2, bNo.getNo());
			
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				result = rs.getInt("hourDiff");
			}
			
			DBConnection.getInstance().dbClose(rs, ps, con);
		}
		
		System.out.println("시간 차이 : " + result);
		
		return result;
	}

	// readcountprocess 테이블에 아이피 주소와 글 번호와 읽은 시간을 넣기
	@Override
	public int readCountProcessWithCntInc(BoardNoDTO bNo, String how) throws NamingException, SQLException {
		int result = -1;
		Connection con = DBConnection.getInstance().connectDB();
		con.setAutoCommit(false);
		
		String query = "";
		
		if (con != null) {
			if (how.equals("insert")) {
				query = "insert into readcountprocess (ipAddr, boardNo) values (?, ?)";
			} else if (how.equals("update")) {
				query = "update readcountprocess set readTime = now() where ipAddr = ? and boardNo = ?";
			}
			
			System.out.println(query);
			
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, bNo.getIp());
			ps.setInt(2, bNo.getNo());
			
			int fResult = ps.executeUpdate();
			ps.close();
			System.out.println(fResult);
			
			if (fResult == 1) {
				result = updateReadCount(bNo, con);
				if (result == 1) {
					con.commit();
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

	// 조회수 증가
	private int updateReadCount(BoardNoDTO bNo, Connection con) throws SQLException {
		String bQuery = "update board set readcount = readcount + 1 where no = ?";
		int result = -1;
		
		PreparedStatement ps = con.prepareStatement(bQuery);
		ps.setInt(1, bNo.getNo());
		
		result = ps.executeUpdate();
		System.out.println("조회수 늘리기 : " + result);
		
		ps.close();
		
		return result;
	}

	// ---------------------- 게시글 가져오기 ----------------------
	// 게시글 가져오기(no로)
		@Override
		public Board selectBoardByNo(BoardNoDTO bNo) throws NamingException, SQLException {
			Board board = null;
			Connection con = DBConnection.getInstance().connectDB();
			
			if (con != null) {
				String query = "select * from board where no = ?";
				
				PreparedStatement ps = con.prepareStatement(query);
				ps.setInt(1, bNo.getNo());
				
				ResultSet rs = ps.executeQuery();
				
				while (rs.next()) {
					board = new Board(rs.getInt("no"), 
							rs.getString("writer"), 
							rs.getString("title"), 
							rs.getTimestamp("postDate"), 
							rs.getString("content"), 
							rs.getInt("readCount"), 
							rs.getInt("likeCount"),
							rs.getInt("ref"),
							rs.getInt("step"),
							rs.getInt("reforder"),
							rs.getString("isDelete"));
				}
				
				DBConnection.getInstance().dbClose(rs, ps, con);
			}
			
			System.out.println(bNo.getNo() + "번의 보드 : " + board.toString());
			
			return board;
		}

		// 게시글의 파일 가져오기
		@Override
		public UploadedFile getFile(BoardNoDTO bNo) throws NamingException, SQLException {
			UploadedFile uf = null;
			
			Connection con = DBConnection.getInstance().connectDB();
			
			if (con != null) {
				String query = "select * from uploadedfile where boardNo = ?";
				
				PreparedStatement ps = con.prepareStatement(query);
				ps.setInt(1, bNo.getNo());
				
				ResultSet rs = ps.executeQuery();
				
				while (rs.next()) {
					uf = new UploadedFile(rs.getString("originalFileName"),
										rs.getString("ext"), 
										rs.getString("newFileName"), 
										rs.getLong("fileSize"),
										rs.getInt("boardNo"),
										rs.getString("base64String"));
				}
				
				DBConnection.getInstance().dbClose(rs, ps, con);
			}
			
			return uf;
		}

		// ------------------------ 게시글 삭제 ------------------------
		// 게시글 삭제
		@Override
		public int deletePost(int no) throws NamingException, SQLException {
			int result = -1;
			
			Connection con = DBConnection.getInstance().connectDB();
			
			if (con != null) {
				String query = "update board set isDelete = 'Y' where no = ?";
				
				PreparedStatement ps = con.prepareStatement(query);
				ps.setInt(1, no);
				
				result = ps.executeUpdate();

				DBConnection.getInstance().dbClose(ps, con);
			}
			
			return result;
		}

		// ------------------------ 게시글 수정 ------------------------
		// 게시글 수정
		@Override
		public int modifyPostWithFile(WriteBoardDTO brd, UploadedFile uf, int no) throws NamingException, SQLException {
			int result = -1;
			
			Connection con = DBConnection.getInstance().connectDB();
			con.setAutoCommit(false);
			
			if (con != null) {
				if (modifyBoardTable(brd, no, con)) {
					result = modifyFileTable(uf, no, con);
				}
			}
			
			if (result == 1) {
				con.commit();
			} else {
				con.rollback();
				con.commit();
			} 
			
			con.setAutoCommit(true);
			con.close();
			
			return result;
		}

		// 게시글 수정
		@Override
		public int modifyPost(WriteBoardDTO brd, int no) throws NamingException, SQLException {
			int result = -1;
			
			Connection con = DBConnection.getInstance().connectDB();
			con.setAutoCommit(false);
			
			if (con != null) {
				boolean tmp = modifyBoardTable(brd, no, con);
				if (tmp) {
					result = 1;
				}
			}
			
			if (result == 1) {
				con.commit();
			} else {
				con.rollback();
				con.commit();
			} 
			
			con.setAutoCommit(true);
			con.close();
			
			return result;
		}
		
		// board 테이블의 수정
		private boolean modifyBoardTable(WriteBoardDTO brd, int no, Connection con) throws SQLException {
			boolean result = false;
			
			String query = "update board set title = ?, content = ? where no = ?";
			System.out.println("보드 수정의 쿼리 : " + query);
			
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, brd.getTitle());
			ps.setString(2, brd.getContent());
			ps.setInt(3, no);
			
			if (ps.executeUpdate() == 1) {
				result = true;
			}
			
			ps.close();
			
			System.out.println("보드 수정의 결과 : " + result);
			return result;
		}

		// uploadedfile 테이블의 수정
		private int modifyFileTable(UploadedFile uf, int no, Connection con) throws SQLException {
			int result = -1;
			
			String query = "update uploadedfile set originalFileName = ?, ext =?, fileSize = ?, base64String = ? where boardNo = ?";
			System.out.println("파일 수정의 쿼리 : " + query);
			
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, uf.getOriginalFileName());
			ps.setString(2, uf.getExt());
			ps.setLong(3, uf.getSize());
			ps.setString(4, uf.getBase64String());
			ps.setInt(5, no);
			
			result = ps.executeUpdate();
			
			ps.close();
			
			System.out.println("파일 수정의 결과 : " + result);
			
			return result;
		}

		// ------------------------- 답글 달기 ------------------------
		// 답글 처리
		@Override
		public boolean insertReplyTransaction(ReplyDTO rep) throws NamingException, SQLException {
			boolean result = false;
			
			Connection con = DBConnection.getInstance().connectDB();
			con.setAutoCommit(false);
			
			if (con != null) {
				int updateResult = updateRefOrder(rep, con);
				
				if (updateResult >= 0) {
					if (insertReply(rep, con)) {
						int pointResult = insertPointTable(rep.getWriter(), "답글작성", con);
						if (pointResult == 1) {
							int poResult = updateMemberPoint("답글작성", rep.getWriter(), con);
							if (poResult == 1) {
								result = true;
								con.commit();
							} else {
								con.rollback();
							}
						} else {
							con.rollback();
						}
					} else {
						con.rollback();
					}
				} else {
					con.rollback();
				}
				
				con.setAutoCommit(true);
				con.close();
			}
			
			return result;
		}

		// ref 업데이트
		private int updateRefOrder(ReplyDTO rep, Connection con) throws SQLException {
			int result = -1;
			
			String query = "update board set reforder = reforder + 1 where ref = ? and reforder > (SELECT reforder FROM (SELECT reforder FROM board WHERE no = ?) AS b)";
			
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, rep.getRef());
			ps.setInt(2, rep.getNo());
			
			result = ps.executeUpdate();
			
			ps.close();

			System.out.println("ref 업데이트 결과 : " + result);
			
			return result;
		}

		// 답글 board에 추가
		private boolean insertReply(ReplyDTO rep, Connection con) throws SQLException {
			boolean result = false;
			
			String query = "insert into board (writer, title, content, ref, step, reforder) "
					+ "value(?, ?, ?, ?, (select b.step from board b where no = ?) + 1, "
					+ "(select b.reforder from board b where no = ?) + 1)";
			
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, rep.getWriter());
			ps.setString(2, rep.getTitle());
			ps.setString(3, rep.getContent());
			ps.setInt(4, rep.getRef());
			ps.setInt(5, rep.getNo());
			ps.setInt(6, rep.getNo());
			
			int reRe = ps.executeUpdate();
			
			if (reRe == 1) {
				result = true;
			}
			
			ps.close();
			
			System.out.println("답글 넣기 결과 : " + result);
			
			return result;
		}

		// ------------------------- 페이징 처리 -------------------------
		// 총 게시글 수 가져오기
		@Override
		public int getTotalPostCnt() throws NamingException, SQLException {
			int result = -1;
			
			Connection con = DBConnection.getInstance().connectDB();
			
			if (con != null) {
				String query = "select count(*) as totalPostCnt from board;";
				
				PreparedStatement ps = con.prepareStatement(query);
				
				ResultSet rs = ps.executeQuery();
				
				while (rs.next()) {
					result = rs.getInt("totalPostCnt");
				}
				System.out.println("총 게시글의 수 : " + result);
				DBConnection.getInstance().dbClose(rs, ps, con);
			}
			
			return result;
		}

		// 페이징에 따라 게시글 가져오기
		@Override
		public List<Board> selectAllBoard(PagingInfo pi) throws NamingException, SQLException {
			Connection con = DBConnection.getInstance().connectDB();
			List<Board> lst = new ArrayList<Board>();
			
			if (con != null) {
				String query = "select * from board order by ref desc, reforder asc limit ?, ?";
				
				PreparedStatement ps = con.prepareStatement(query);
				ps.setInt(1, pi.getStartRowIndex());
				ps.setInt(2, pi.getViewPostCntPerPage());
				
				ResultSet rs = ps.executeQuery();
				
				while (rs.next()) {
					lst.add(new Board(rs.getInt("no"), 
									rs.getString("writer"), 
									rs.getString("title"), 
									rs.getTimestamp("postDate"), 
									rs.getString("content"), 
									rs.getInt("readCount"), 
									rs.getInt("likeCount"),
									rs.getInt("ref"),
									rs.getInt("step"),
									rs.getInt("reforder"),
									rs.getString("isDelete")));
				}
				
				DBConnection.getInstance().dbClose(rs, ps, con);
			}

			return lst;
		}

		// ------------------------- 좋아요 처리 -------------------------
		// 좋아요를 눌렀는지 판단
		@Override
		public Boolean validateLike(BoardNoDTO bNo) throws NamingException, SQLException {
			Boolean result = false;
			
			Connection con = DBConnection.getInstance().connectDB();
			
			if (con != null) {
				String query = "select * from likelog where userId = ? and boardNo = ?";
				
				PreparedStatement ps = con.prepareStatement(query);
				ps.setString(1, bNo.getUserId());
				ps.setInt(2, bNo.getNo());
				
				ResultSet rs = ps.executeQuery();
				
				if (rs.next()) {
					result = true;
				}
				
				DBConnection.getInstance().dbClose(rs, ps, con);
			}
			
			return result;
		}

		// 좋아요를 눌렀을 떄(좋아요)
		@Override
		public int likePost(BoardNoDTO bNo) throws NamingException, SQLException {
			int like = -1;
			
			Connection con = DBConnection.getInstance().connectDB();
			con.setAutoCommit(false);
			
			if (con != null) {
				if (insertLike(bNo, con)) {
					if (plusLikeCount(bNo, con)) {
						like = getLikeCount(bNo, con);
					} else {
						con.rollback();
					}
				} else {
					con.rollback();
				}
				
				if (like != -1) {
					con.commit();
				} else {
					con.rollback();
				}
				
				con.setAutoCommit(true);
				con.close();
			}
			
			return like;
		}

		// 좋아요를 눌렀을 떄(좋아요 해제)
		@Override
		public int unlikePost(BoardNoDTO bNo) throws NamingException, SQLException {
			int like = -1;
			
			Connection con = DBConnection.getInstance().connectDB();
			con.setAutoCommit(false);
			
			if (con != null) {
				if (deleteLike(bNo, con)) {
					if (minusLikeCount(bNo, con)) {
						like = getLikeCount(bNo, con);
					} else {
						con.rollback();
					}
				} else {
					con.rollback();
				}
				
				if (like != -1) {
					con.commit();
				} else {
					con.rollback();
				}
				
				con.setAutoCommit(true);
				con.close();
			}
			
			return like;
		}
		
		// likelog에 좋아요 insert
		private boolean insertLike(BoardNoDTO bNo, Connection con) throws SQLException {
			boolean result = false;
			
			String query = "insert into likelog (userId, boardNo) values (?, ?)";
			
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, bNo.getUserId());
			ps.setInt(2, bNo.getNo());
			
			if (ps.executeUpdate() == 1) {
				result = true;
			}
			
			ps.close();
			
			return result;
		}
		
		// likelig에 좋아요 delete
		private boolean deleteLike(BoardNoDTO bNo, Connection con) throws SQLException {
			boolean result = false;
			
			String query = "delete from likelog where userId = ? and boardNo = ?";
			
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, bNo.getUserId());
			ps.setInt(2, bNo.getNo());
			
			if (ps.executeUpdate() == 1) {
				result = true;
			}
			
			ps.close();
			
			return result;
		}
		
		// board 테이블 좋아요 늘리기
		private boolean plusLikeCount(BoardNoDTO bNo, Connection con) throws SQLException {
			boolean result = false;
			
			String query = "update board set likeCount = likeCount + 1 where no = ?";
			
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, bNo.getNo());
			
			if (ps.executeUpdate() == 1) {
				result = true;
			}
			
			ps.close();
			
			return result;
		}
		
		// board 테이블 좋아요 줄이기
		private boolean minusLikeCount(BoardNoDTO bNo, Connection con) throws SQLException {
			boolean result = false;
			
			String query = "update board set likeCount = likeCount - 1 where no = ?";
			
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, bNo.getNo());
			
			if (ps.executeUpdate() == 1) {
				result = true;
			}
			
			ps.close();
			
			return result;
		}

		// 좋아요 수 가져오기
		private int getLikeCount(BoardNoDTO bNo, Connection con) throws SQLException {
			int like = -1;
			
			String query = "select likeCount from board where no = ?";
			
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, bNo.getNo());
			
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				like = rs.getInt("likeCount");
			}
			
			rs.close();
			ps.close();
			
			return like;
		}
		
		// ------------------------------------- 검색 -------------------------------------
		// 검색한 게시글 수 가져오기
		@Override
		public int getTotalPostCnt(SearchCriteria sc) throws NamingException, SQLException {
			int result = -1;
			
			Connection con = DBConnection.getInstance().connectDB();
			
			if (con != null) {
				String query = "select count(*) as totalPostCnt from board ";
				if (sc.getSearchType().equals("title")) {
					query += "where lower(title) like ? and isDelete = 'N'";
				} else if (sc.getSearchType().equals("writer")) {
					query += "where lower(writer) like ? and isDelete = 'N'";
				} else {
					query += "where lower(title) like ? or lower(content) like ? and isDelete = 'N'";
				}
				
				PreparedStatement ps = con.prepareStatement(query);
				if (sc.getSearchType().equals("content")) {
					ps.setString(1, "%" + sc.getSearchWords() + "%");
					ps.setString(2, "%" + sc.getSearchWords() + "%");
				} else {
					ps.setString(1, "%" + sc.getSearchWords() + "%");
				}
				
				ResultSet rs = ps.executeQuery();
				
				while (rs.next()) {
					result = rs.getInt("totalPostCnt");
				}
				System.out.println("게시글의 수 : " + result);
				DBConnection.getInstance().dbClose(rs, ps, con);
			}
			
			return result;
		}

		// 검색어에 따라 글 가져오기
		@Override
		public List<Board> selectSearchBoard(SearchCriteria sc, PagingInfo pi) throws NamingException, SQLException {
			Connection con = DBConnection.getInstance().connectDB();
			List<Board> lst = new ArrayList<Board>();
			
			if (con != null) {
				String query = "select * from board "; 
				if (sc.getSearchType().equals("title")) {
					query += "where lower(title) like ?";
				} else if (sc.getSearchType().equals("writer")) {
					query += "where lower(writer) like ?";
				} else {
					query += "where lower(title) like ? or lower(content) like ?";
				}
				query += " and isDelete = 'N' order by ref desc, reforder asc limit ?, ?";
				
				PreparedStatement ps = con.prepareStatement(query);
				if (sc.getSearchType().equals("content")) {
					ps.setString(1, "%" + sc.getSearchWords() + "%");
					ps.setString(2, "%" + sc.getSearchWords() + "%");
					ps.setInt(3, pi.getStartRowIndex());	
					ps.setInt(4, pi.getViewPostCntPerPage());
				} else {
					ps.setString(1, "%" + sc.getSearchWords() + "%");
					ps.setInt(2, pi.getStartRowIndex());
					ps.setInt(3, pi.getViewPostCntPerPage());
				}
				
				ResultSet rs = ps.executeQuery();
				
				while (rs.next()) {
					lst.add(new Board(rs.getInt("no"), 
									rs.getString("writer"), 
									rs.getString("title"), 
									rs.getTimestamp("postDate"), 
									rs.getString("content"), 
									rs.getInt("readCount"), 
									rs.getInt("likeCount"),
									rs.getInt("ref"),
									rs.getInt("step"),
									rs.getInt("reforder"),
									rs.getString("isDelete")));
				}
				
				DBConnection.getInstance().dbClose(rs, ps, con);
			}

			return lst;
		}

}

