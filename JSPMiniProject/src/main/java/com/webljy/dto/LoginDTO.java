package com.webljy.dto;

import java.sql.Date;

public class LoginDTO {
	private String userId;
	private String userPwd;
	private String userEmail;
	private Date registerDate;
	private int userImg;
	private int userPoint;
	private String memberImg;
	private String isAdmin;
	
	public LoginDTO(String userId, String userPwd, String userEmail, Date registerDate, int userImg, int userPoint,
			String memberImg, String isAdmin) {
		super();
		this.userId = userId;
		this.userPwd = userPwd;
		this.userEmail = userEmail;
		this.registerDate = registerDate;
		this.userImg = userImg;
		this.userPoint = userPoint;
		this.memberImg = memberImg;
		this.isAdmin = isAdmin;
	}
	 
	
	public String getUserId() {
		return userId;
	}


	public String getUserPwd() {
		return userPwd;
	}


	public String getUserEmail() {
		return userEmail;
	}


	public Date getRegisterDate() {
		return registerDate;
	}


	public int getUserImg() {
		return userImg;
	}


	public int getUserPoint() {
		return userPoint;
	}


	public String getMemberImg() {
		return memberImg;
	}


	public String getIsAdmin() {
		return isAdmin;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}
	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}
	public void setUserImg(int userImg) {
		this.userImg = userImg;
	}
	public void setUserPoint(int userPoint) {
		this.userPoint = userPoint;
	}
	public void setMemberImg(String memberImg) {
		this.memberImg = memberImg;
	}
	public void setIsAdmin(String isAdmin) {
		this.isAdmin = isAdmin;
	}

	@Override
	public String toString() {
		return "LoginDTO [userId=" + userId + ", userPwd=" + userPwd + ", userEmail=" + userEmail + ", registerDate="
				+ registerDate + ", userImg=" + userImg + ", userPoint=" + userPoint + ", memberImg=" + memberImg
				+ ", isAdmin=" + isAdmin + "]";
	}
	
	
}
