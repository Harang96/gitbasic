package com.webljy.service.member;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.catalina.Session;
import org.json.simple.JSONObject;

import com.webljy.controller.MemberFactory;
import com.webljy.service.MemberService;

public class ConfirmMailCodeService implements MemberService {

	@Override
	public MemberFactory executeService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String tmpCode = request.getParameter("tmpCode"); // 유저가 입력한 인증 코드
		
		HttpSession session = request.getSession();
		String authCode = (String) session.getAttribute("authCode"); // 세션에 저장해놓은 인증 코드
		
		PrintWriter out = response.getWriter();
		JSONObject json = new JSONObject();
		
		if (tmpCode.equals(authCode)) {
			json.put("activation", "success");
		} else {
			json.put("activation", "fail");
		}
		
		out.print(json.toJSONString());
		
		out.flush();
		out.close();
		
		return null;
	}

}
