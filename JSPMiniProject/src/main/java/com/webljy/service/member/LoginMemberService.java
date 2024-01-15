package com.webljy.service.member;

import java.io.IOException;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.webljy.controller.MemberFactory;
import com.webljy.dao.MemberCRUD;
import com.webljy.dao.MemberDAO;
import com.webljy.dto.LoginDTO;
import com.webljy.service.MemberService;

public class LoginMemberService implements MemberService {

	@Override
	public MemberFactory executeService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("로그인 요청");
		
		String userId = request.getParameter("userId");
		String userPwd = request.getParameter("userPwd");
		
		MemberDAO dao = MemberCRUD.getInstance();
		MemberFactory mf = MemberFactory.getInstance();
		
		int result = -1;
		try {
			LoginDTO loginMember = dao.loginMember(userId, userPwd);
			
			if (loginMember != null) { // 로그인 성공
				System.out.println(loginMember.toString());
				
				// member 테이블에 포인트를 업데이트 하고, pointlog에 기록 남기기
				result = dao.addPointToMember(userId, "로그인");
				System.out.println("로그인 트랜잭션 : " + result);
				
				// 원래는 유저를 다시 select 해서 가져와야 함
				loginMember.setUserPoint(loginMember.getUserPoint() + 5);
				HttpSession ses = request.getSession();
				ses.setAttribute("loginMember", loginMember); // 세션에 로그인 유저 정보 바인딩
				
//				request.getRequestDispatcher("../index.jsp").forward(request, response);
				mf.setRedirect(true);
				mf.setWhereToGo(request.getContextPath() + "/index.jsp?status=success");
			}  else { // 로그인 실패
				mf.setRedirect(true);
				mf.setWhereToGo(request.getContextPath() + "/member/login.jsp?status=fail");
			}
		} catch (NamingException | SQLException e) {
			e.printStackTrace();
		}
		
		return mf;
	}

}
