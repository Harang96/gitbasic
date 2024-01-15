package com.webljy.service.member;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.webljy.controller.MemberFactory;
import com.webljy.dao.MemberCRUD;
import com.webljy.dao.MemberDAO;
import com.webljy.service.MemberService;

public class DeleteUserInfoService implements MemberService {

	@Override
	public MemberFactory executeService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userId = request.getParameter("userId");
		String userImg = request.getParameter("userImg");
		String uploadDir = "\\memberImg";
		String realPath = request.getSession().getServletContext().getRealPath(uploadDir);
		
		int result = -1;
		
		MemberFactory mf = MemberFactory.getInstance();
		MemberDAO dao = MemberCRUD.getInstance();
		try {
			result = dao.deleteMember(userId);
			
			if (result == 1) {
				File deleteFile = new File(realPath + File.separator + userImg);
				
				deleteFile.delete(); // 파일 삭제
				
				HttpSession ses = request.getSession();
				ses.removeAttribute("loginMember");
				ses.invalidate();
				
				mf.setRedirect(true);
				mf.setWhereToGo(request.getContextPath() + "/index.jsp?status=success");
			} else {
				mf.setRedirect(true);
				mf.setWhereToGo(request.getContextPath() + "/member/memberInfo.jsp?status=fail");
			}
		} catch (NamingException | SQLException e) {
			e.printStackTrace();
			mf.setRedirect(true);
			mf.setWhereToGo(request.getContextPath() + "/member/memberInfo.jsp?status=fail");
		}
		
		return mf;
	}

}
