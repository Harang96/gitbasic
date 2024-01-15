package com.webljy.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webljy.service.MemberService;

@WebServlet("*.mem")
public class MemberServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public MemberServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doService(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doService(request, response);
	}
	
	private void doService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("요청한 페이지 : " + request.getRequestURL());
		System.out.println("요청한 URI : " + request.getRequestURI());
		System.out.println("요청한 통신 방식 : " + request.getMethod());
		System.out.println("요청한 contextPath : " + request.getContextPath());
		
		// 요청된 서블릿 매핑 주소를 통해서 기능을 분휴
		String requestURI = request.getRequestURI();
		String contextPath = request.getContextPath();
		
		String command = requestURI.substring(contextPath.length());
//		String command = requestURI.replaceFirst(contextPath, "");;
		System.out.println("최종 요청된 서비스 : " + command);
		
		MemberFactory mf = MemberFactory.getInstance();
		MemberService service = mf.getService(command);
		if (service != null) {
			mf = service.executeService(request, response);
		}
		
		if (mf != null && mf.isRedirect()) {
			response.sendRedirect(mf.getWhereToGo());
		}
		
		return;
	}

}
