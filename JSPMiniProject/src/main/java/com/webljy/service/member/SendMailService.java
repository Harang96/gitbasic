package com.webljy.service.member;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.webljy.controller.MemberFactory;
import com.webljy.dao.MemberCRUD;
import com.webljy.dao.MemberDAO;
import com.webljy.etc.SendMail;
import com.webljy.service.MemberService;

public class SendMailService implements MemberService {

	@Override
	public MemberFactory executeService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String tmpUserEmail = request.getParameter("tmpUserEmail");
		response.setContentType("apllication/json; charset=utf-8");
		PrintWriter out = response.getWriter();
		
		// 인증 코드를 만들고 코드를 세션에 저장
		String code = UUID.randomUUID().toString();
		System.out.println(code);
		
		request.getSession().setAttribute("authCode", code);
		
		Map<String, String> jsonMap = new HashMap<String, String>();
		// 유저 이메일로 인증 코드를 발송
		try {
			SendMail.sendMail(tmpUserEmail, code);
			
			jsonMap.put("status", "success");
		} catch (MessagingException e) {
			e.printStackTrace();
			jsonMap.put("status", "fail");
		}
		
		JSONObject json = new JSONObject(jsonMap);
		
		out.print(json.toJSONString());
		
		out.flush();
		out.close();
		
		return null;
	}

}
