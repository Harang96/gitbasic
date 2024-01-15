package com.webljy.dto;

public class BoardNoDTO {
	private int no;
	private String ip;
	private String userId;
	
	public BoardNoDTO(int no, String ip, String userId) {
		super();
		this.no = no;
		this.ip = ip;
		this.userId = userId;
	}

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "BoardNoDTO [no=" + no + ", ip=" + ip + ", userId=" + userId + "]";
	}

	
}
