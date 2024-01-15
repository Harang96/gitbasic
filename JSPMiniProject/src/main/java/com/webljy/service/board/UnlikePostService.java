package com.webljy.service.board;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.webljy.controller.BoardFactory;
import com.webljy.dao.BoardCRUD;
import com.webljy.dao.BoardDAO;
import com.webljy.dto.BoardNoDTO;
import com.webljy.service.BoardService;

public class UnlikePostService implements BoardService {

	@Override
	public BoardFactory doAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int no = Integer.parseInt(request.getParameter("no"));
		String userId = request.getParameter("userId");
		
		System.out.println("좋아요 해제 : " + no);
		System.out.println("좋아요 해제 : " + userId);
		
		BoardNoDTO bNo = new BoardNoDTO(no, null, userId);
		
		BoardDAO dao = BoardCRUD.getInstance();
		PrintWriter out = response.getWriter();
		JSONObject json = new JSONObject();
		
		try {
			int like = dao.unlikePost(bNo);
			
			if (like != -1) {
				json.put("likeCount", like);
				out.print(json.toJSONString());
			}
		} catch (NamingException | SQLException e) {
			e.printStackTrace();
		}
		
		out.flush();
		out.close();
		
		return null;
	}

}
