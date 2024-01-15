package com.webljy.service.member;

import java.io.IOException;

import javax.mail.Session;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.webljy.controller.MemberFactory;
import com.webljy.service.MemberService;

public class LogoutMemeberService implements MemberService {

	@Override
	public MemberFactory executeService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession ses = request.getSession();
		
		ses.removeAttribute("loginMember");
		ses.invalidate();
		
		MemberFactory mf = MemberFactory.getInstance();
		
		mf.setRedirect(true);
		mf.setWhereToGo(request.getContextPath() + "/index.jsp?status=success");
//		request.getRequestDispatcher("../index.jsp").forward(request, response);
		return mf;
	}

}
