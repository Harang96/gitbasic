package com.webljy.dto;

public class WriteBoardDTO {

	private String writer;
	private String title;
	private String content;
	
	public WriteBoardDTO(String writer, String title, String content) {
		super();
		this.writer = writer;
		this.title = title;
		this.content = content;
	}

	public String getWriter() {
		return writer;
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}

	@Override
	public String toString() {
		return "WriteBoardDTO [writer=" + writer + ", title=" + title + ", content=" + content + "]";
	}
}
