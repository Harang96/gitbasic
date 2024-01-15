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
import com.webljy.dto.ReplyDTO;
import com.webljy.service.BoardService;

public class ReplyPostService implements BoardService {

	@Override
	public BoardFactory doAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int no = Integer.parseInt(request.getParameter("no"));
		int ref = Integer.parseInt(request.getParameter("ref"));
		String writer = request.getParameter("writer");
		String title = request.getParameter("title");
		String content = request.getParameter("content");
		
		boolean result = false;
		
		System.out.println("no : " + no + ", ref : " + ref + ", writer : " + writer + ", title : " + title + ", content : " + content);
		ReplyDTO rep = new ReplyDTO(no, ref, writer, title, content);
		
		BoardDAO dao = BoardCRUD.getInstance();
		BoardFactory bf = BoardFactory.getInstance();
		
		try {
			result = dao.insertReplyTransaction(rep);
			
			if (result == true) {
				bf.setRedirect(true);
				bf.setWhereToGo(request.getContextPath() + "/board/listAll.bo?status=success");
			} else {
				bf.setRedirect(true);
				bf.setWhereToGo(request.getContextPath() + "/board/replyBoard.jsp?no=" + no + "&ref=" + ref);
			}
		} catch (NamingException | SQLException e) {
			e.printStackTrace();
			bf.setRedirect(true);
			bf.setWhereToGo(request.getContextPath() + "/board/replyBoard.jsp?no=" + no + "&ref=" + ref);
		}
		
		return bf;
	}

}
