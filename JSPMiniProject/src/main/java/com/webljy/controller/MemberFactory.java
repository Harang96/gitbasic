package com.webljy.controller;

import com.webljy.service.MemberService;
import com.webljy.service.member.ConfirmMailCodeService;
import com.webljy.service.member.DeleteUserInfoService;
import com.webljy.service.member.DuplicateUserIdService;
import com.webljy.service.member.LoginMemberService;
import com.webljy.service.member.LogoutMemeberService;
import com.webljy.service.member.ModifyUserInfoService;
import com.webljy.service.member.MyPageService;
import com.webljy.service.member.RegisterMemberService;
import com.webljy.service.member.SendMailService;

public class MemberFactory {
	private boolean isRedirect; // redirect를 할 것인지 말 것인지
	private String whereToGo; // 어느 view단으로 이동할지
	private static MemberFactory instance;
	
	private MemberFactory() {}
	
	public static MemberFactory getInstance() {
		if (instance == null) {
			instance = new MemberFactory();
		}
		
		return instance;
	}
	
	public boolean isRedirect() {
		return isRedirect;
	}

	public void setRedirect(boolean isRedirect) {
		this.isRedirect = isRedirect;
	}

	public String getWhereToGo() {
		return whereToGo;
	}

	public void setWhereToGo(String whereToGo) {
		this.whereToGo = whereToGo;
	}

	public MemberService getService(String command) {
		
		MemberService result = null;
		
		if (command.equals("/member/duplicateUserId.mem")) {
			result = new DuplicateUserIdService();
		} else if (command.equals("/member/registerMember.mem")) {
			result = new RegisterMemberService();
		} else if (command.equals("/member/sendMail.mem")) {
			result = new SendMailService();
		} else if (command.equals("/member/confirmCode.mem")) {
			result = new ConfirmMailCodeService();
		} else if (command.equals("/member/login.mem")) {
			result = new LoginMemberService();
		} else if (command.equals("/member/logout.mem")) {
			result = new LogoutMemeberService();
		} else if (command.equals("/member/modifyUser.mem")) {
			result = new ModifyUserInfoService();
		} else if (command.equals("/member/deleteUser.mem")) {
			result = new DeleteUserInfoService();
		} else if (command.equals("/member/myPage.mem")) {
			result = new MyPageService();
		}
		
		return result;
	}
}
