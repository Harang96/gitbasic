package com.webljy.vo;

import java.sql.Timestamp;

public class Board {
	private int no; 
	private String writer;
	private String title;
	private Timestamp postDate; 
	private String content;
	private int readCount;
	private int likeCount;
	private int ref;
	private int step;
	private int reforder; 
	private String isDelete;
	
	public Board(int no, String writer, String title, Timestamp postDate, String content, int readCount, int likeCount,
			int ref, int step, int reforder, String isDelete) {
		super();
		this.no = no;
		this.writer = writer;
		this.title = title;
		this.postDate = postDate;
		this.content = content;
		this.readCount = readCount;
		this.likeCount = likeCount;
		this.ref = ref;
		this.step = step;
		this.reforder = reforder;
		this.isDelete = isDelete;
	}

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
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

	public Timestamp getPostDate() {
		return postDate;
	}

	public void setPostDate(Timestamp postDate) {
		this.postDate = postDate;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getReadCount() {
		return readCount;
	}

	public void setReadCount(int readCount) {
		this.readCount = readCount;
	}

	public int getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}

	public int getRef() {
		return ref;
	}

	public void setRef(int ref) {
		this.ref = ref;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public int getReforder() {
		return reforder;
	}

	public void setReforder(int reforder) {
		this.reforder = reforder;
	}

	public String getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(String isDelete) {
		this.isDelete = isDelete;
	}

	@Override
	public String toString() {
		return "Board [no=" + no + ", writer=" + writer + ", title=" + title + ", postDate=" + postDate + ", content="
				+ content + ", readCount=" + readCount + ", likeCount=" + likeCount + ", ref=" + ref + ", step=" + step
				+ ", reforder=" + reforder + ", isDelete=" + isDelete + "]";
	}

	
}
