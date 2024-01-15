package com.webljy.service.member;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webljy.controller.MemberFactory;
import com.webljy.dao.MemberCRUD;
import com.webljy.dao.MemberDAO;
import com.webljy.dto.LoginDTO;
import com.webljy.service.MemberService;
import com.webljy.vo.MemberVO;
import com.webljy.vo.PointlogVO;

public class MyPageService implements MemberService {

	@Override
	public MemberFactory executeService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 멤버 정보 + pointlog 정보
		String userId = request.getParameter("userId");
		
		System.out.println("조회할 멤버 아이디 : " + userId);
		MemberDAO dao = MemberCRUD.getInstance();
		
		try {
			LoginDTO mem = dao.getMemberInfo(userId);
			List<PointlogVO> lst = dao.getPointInfo(userId);
			
			request.setAttribute("member", mem);
			request.setAttribute("pointlog", lst);
			
			request.getRequestDispatcher("myPage.jsp").forward(request, response);
		} catch (NamingException | SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
