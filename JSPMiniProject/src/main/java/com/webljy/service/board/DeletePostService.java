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
import com.webljy.service.BoardService;

public class DeletePostService implements BoardService {

	@Override
	public BoardFactory doAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int no = Integer.parseInt(request.getParameter("no"));
		System.out.println("게시글 삭제 : " + no);
		
		BoardDAO dao = BoardCRUD.getInstance();
		
		int result = -1;
		
		JSONObject json = new JSONObject();
		response.setContentType("utf-8");
		PrintWriter out = response.getWriter();
		
		try {
			result = dao.deletePost(no);
			json.put("status", "success");
		} catch (NamingException | SQLException e) {
			e.printStackTrace();
			json.put("status", "fail");
		}
		
		out.print(json.toJSONString());
		out.flush();
		out.close();
		
		return null;
	}

}
