package com.webljy.dto;

public class ReplyDTO {
	private int no;
	private int ref;
	private String writer;
	private String title;
	private String content;
	
	public ReplyDTO(int no, int ref, String writer, String title, String content) {
		super();
		this.no = no;
		this.ref = ref;
		this.writer = writer;
		this.title = title;
		this.content = content;
	}

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public int getRef() {
		return ref;
	}

	public void setRef(int ref) {
		this.ref = ref;
	}

	public String getWriter() {
		return writer;
	}

	public void setWriter(String writer) {
		this.writer = writer;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "ReplyDTO [no=" + no + ", ref=" + ref + ", writer=" + writer + ", title=" + title + ", content="
				+ content + "]";
	}
	
}
