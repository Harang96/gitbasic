package com.webljy.service.member;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.google.gson.Gson;
import com.webljy.controller.MemberFactory;
import com.webljy.dao.MemberCRUD;
import com.webljy.dao.MemberDAO;
import com.webljy.service.MemberService;
import com.webljy.vo.MemberVO;

public class DuplicateUserIdService implements MemberService {

	@Override
	public MemberFactory executeService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String tmpUserId = request.getParameter("tmpUserId");
		response.setContentType("apllication/json; charset=utf-8");
		PrintWriter out = response.getWriter();
		
		MemberDAO mDao = MemberCRUD.getInstance();
		
		Map<String, String> map = new HashMap<String, String>();
		try {
			MemberVO mem = mDao.duplicateUserId(tmpUserId);
			
			if (mem != null) {
				// 아이디가 중복("isDuplicate" : "true")
				map.put("isDuplicate", "true");
			} else {
				// 중복이 아님
				map.put("isDuplicate", "false");
			}
			
			map.put("responseCode", "00");
			
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초");
			String outputDate = fmt.format(Calendar.getInstance().getTime());
			map.put("outputDate", outputDate);
			
		} catch (NamingException | SQLException e) {
			map.put("responseCode", "err");
			map.put("errMsg", e.getMessage());
			
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초");
			String outputDate = fmt.format(Calendar.getInstance().getTime());
			map.put("outputDate", outputDate);
		}
		
		JSONObject json = new JSONObject(map);
		
		out.print(json.toJSONString());
		
		out.flush();
		out.close();
		
		return null;
	}

}
