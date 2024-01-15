package com.webljy.controller;

import com.webljy.service.board.DeletePostService;
import com.webljy.service.board.GetEntireBoardService;
import com.webljy.service.board.GetModifyPostService;
import com.webljy.service.board.LikePostService;
import com.webljy.service.board.ModifyPostService;
import com.webljy.service.board.ReadPostService;
import com.webljy.service.board.ReplyPostService;
import com.webljy.service.board.UnlikePostService;
import com.webljy.service.board.WriteBoardService;
import com.webljy.service.BoardService;

public class BoardFactory {
	private boolean isRedirect; 
	private String whereToGo;
	private static BoardFactory instance;
	
	private BoardFactory() {}
	
	public static BoardFactory getInstance() {
		if (instance == null) {
			instance = new BoardFactory();
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

	public BoardService getService(String command) {
		
		BoardService result = null;
		
		if (command.equals("/board/listAll.bo")) {
			result = new GetEntireBoardService();
		} else if (command.equals("/board/writeBoard.bo")) {
			result = new WriteBoardService();
		} else if (command.equals("/board/readPost.bo")) {
			result = new ReadPostService();
		} else if (command.equals("/board/deletePost.bo")) {
			result = new DeletePostService();
		} else if (command.equals("/board/modifyPost.bo")) {
			result = new ModifyPostService();
		} else if (command.equals("/board/getModifyPost.bo")) {
			result = new GetModifyPostService();
		} else if (command.equals("/board/reply.bo")) {
			result = new ReplyPostService();
		} else if (command.equals("/board/likePost.bo")) {
			result = new LikePostService();
		} else if (command.equals("/board/unlikePost.bo")) {
			result = new UnlikePostService();
		}
			
		return result;
	}
}
