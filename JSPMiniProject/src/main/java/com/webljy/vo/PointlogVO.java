package com.webljy.vo;

import java.sql.Date;

public class PointlogVO {
	private int no;
	private Date when;
	private String why;
	private int howmuch;
	private String who;
	
	public PointlogVO(int no, Date when, String why, int howmuch, String who) {
		this.no = no;
		this.when = when;
		this.why = why;
		this.howmuch = howmuch;
		this.who = who;
	}

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public Date getWhen() {
		return when;
	}

	public void setWhen(Date when) {
		this.when = when;
	}

	public String getWhy() {
		return why;
	}

	public void setWhy(String why) {
		this.why = why;
	}

	public int getHowmuch() {
		return howmuch;
	}

	public void setHowmuch(int howmuch) {
		this.howmuch = howmuch;
	}

	public String getWho() {
		return who;
	}

	public void setWho(String who) {
		this.who = who;
	}

	@Override
	public String toString() {
		return "[no=" + no + ", when=" + when + ", why=" + why + ", howmuch=" + howmuch + ", who=" + who
				+ "]";
	}
	
	
	
	
}
