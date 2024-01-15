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

public class GetModifyPostService implements BoardService {

	@Override
	public BoardFactory doAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("게시글 수정 페이지 띄우기");
		
		int no = Integer.parseInt(request.getParameter("no"));
		BoardNoDTO bNo = new BoardNoDTO(no, null, null);
		BoardDAO dao = BoardCRUD.getInstance();
		
		Board result = null;
		
		try {
			Board board = dao.selectBoardByNo(bNo);
			UploadedFile attachedFile = dao.getFile(bNo);
			
			if (board != null) {
				// viewBoard.jsp
				request.setAttribute("board", board);
				request.setAttribute("file", attachedFile);
				request.getRequestDispatcher("viewBoardModify.jsp").forward(request, response);
			}
		} catch (NamingException | SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
