package com.webljy.service.board;

import java.io.IOException;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webljy.controller.BoardFactory;
import com.webljy.dao.BoardCRUD;
import com.webljy.dao.BoardDAO;
import com.webljy.dto.BoardNoDTO;
import com.webljy.etc.UploadedFile;
import com.webljy.service.BoardService;
import com.webljy.vo.Board;

public class ReadPostService implements BoardService {

	@Override
	public BoardFactory doAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("게시글 읽기");
		
		String userId = request.getParameter("userId");
		int no = Integer.parseInt(request.getParameter("no"));
		
		System.out.println("글 읽기 userId : " + userId);
		System.out.println("".equals(userId));
		
		// 클라이언트 ip 주소 얻어오기
		String ip = getIp(request);

		BoardNoDTO bNo = new BoardNoDTO(no, ip, userId);
		
		
		BoardDAO dao = BoardCRUD.getInstance();
		int readResult = -1;
		try {
			if (dao.selectReadCountProcess(bNo)) { // 해당 아이피 주소와 글 번호가 같은 것이 있을 때 
				if (dao.selectHourDiff(bNo) > 23) { // 24시간이 지난 경우
					readResult = dao.readCountProcessWithCntInc(bNo, "update");
				} else { // 24시간이 지나지 않은 경우
					readResult = 1;
				}
			} else { // 해당 아이피 주소와 글 번호가 같은 것이 없을 때(최초 조회)
				readResult = dao.readCountProcessWithCntInc(bNo, "insert");
			}

			// 글을 가져옴
			Board board = dao.selectBoardByNo(bNo);
			UploadedFile attachedFile = dao.getFile(bNo);
			Boolean isLike = dao.validateLike(bNo);
			
			if (board != null) {
				// viewBoard.jsp
				board.setContent(board.getContent().replaceAll("\r\n", "<br>"));
				request.setAttribute("isLike", isLike);
				request.setAttribute("board", board);
				request.setAttribute("file", attachedFile);
				request.getRequestDispatcher("viewBoard.jsp").forward(request, response);
			}
		} catch (NamingException | SQLException e) {
			e.printStackTrace();
			
			e.getMessage(); // String으로 반환		
			e.getStackTrace(); // Array로 반환
			
			request.setAttribute("errorMsg", e.getMessage());
			request.setAttribute("errorStack", e.getStackTrace());
			
			request.getRequestDispatcher("../commonError.jsp").forward(request, response);
			
		}
		
		return null;
	}

	private String getIp(HttpServletRequest request) {
		
		String ip = request.getHeader("X-Forwarded-For");
		
		System.out.println(">>>> X-FORWARDED-FOR : " + ip);
		
		if (ip == null) {
			ip = request.getHeader("Proxy-Client-IP");
			System.out.println(">>>> Proxy-Client-IP : " + ip);
		}
		if (ip == null) {
			ip = request.getHeader("WL-Proxy-Client-IP");
			System.out.println(">>>> WL-Proxy-Client-IP : " + ip);
		}
		if (ip == null) {
			ip = request.getHeader("HTTP_CLIENT_IP");
			System.out.println(">>>> HTTP_CLIENT_IP : " + ip);
		}
		if (ip == null) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
			System.out.println(">>>> HTTP_X_FORWARDED_FOR : " + ip);
		}
		if (ip == null) {
			ip = request.getRemoteAddr();
		}
		
		System.out.println(">>>> Result : IP Address : " + ip);
		
		return ip;
		
	}

}
