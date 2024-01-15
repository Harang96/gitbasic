package com.webljy.service.board;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webljy.controller.BoardFactory;
import com.webljy.dao.BoardCRUD;
import com.webljy.dao.BoardDAO;
import com.webljy.dto.SearchCriteria;
import com.webljy.etc.PagingInfo;
import com.webljy.service.BoardService;
import com.webljy.vo.Board;

public class GetEntireBoardService implements BoardService {

	@Override
	public BoardFactory doAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<Board> lst = new ArrayList<Board>();

		// 만약 페이지에 대한 정보가 없으면 1
		int pageNo = 1;
		// 정보가 있으면 그 페이지 번호를 대입
		if (request.getParameter("pageNo") != null && !request.getParameter("pageNo").equals("")) {
			pageNo = Integer.parseInt(request.getParameter("pageNo"));
		}
		System.out.println(pageNo + "페이지의 글 목록을 불러옴");
		
		// 검색 유형과 검색어 추가
		String searchType = request.getParameter("searchType");
		String searchWords = "";
		
		if (request.getParameter("searchWords") != null && !request.getParameter("searchWords").equals("")) { // 검색어가 있는 경우
			searchWords = request.getParameter("searchWords").toLowerCase();
		}
		
		SearchCriteria sc = new SearchCriteria(searchWords, searchType);
		System.out.println(sc);
		
		BoardDAO dao = BoardCRUD.getInstance();
		
		try {
			PagingInfo pi = pagingProcess(pageNo, sc);
			
			if (sc.getSearchWords().equals("")) { 
				lst = dao.selectAllBoard(pi);
			} else {
				lst = dao.selectSearchBoard(sc, pi);
			}
//			System.out.println(lst);
			
			if (lst.size() == 0) {
				request.setAttribute("boardList", null);
				request.setAttribute("pagingInfo", pi);
			} else {
				request.setAttribute("boardList", lst);
				request.setAttribute("pagingInfo", pi);
			}
			
			request.getRequestDispatcher("listAll.jsp").forward(request, response);
		} catch (NamingException | SQLException e) {
			e.printStackTrace();
		}
		
		
		return null;
	}

	// 페이지 정보 객체
	private PagingInfo pagingProcess(int pageNo, SearchCriteria sc) throws NamingException, SQLException {
		PagingInfo pi = new PagingInfo();
		
		BoardDAO dao = BoardCRUD.getInstance();
		
		
		if (sc.getSearchWords().equals("")) {
			// 검색어가 없는 경우
			int totalPostCnt = dao.getTotalPostCnt();
			pi.setAttribute(totalPostCnt, pageNo);
		} else if (!sc.getSearchWords().equals("") && !sc.getSearchType().equals("")) {
			// 검색어가 있는 경우
			pi.setAttribute(dao.getTotalPostCnt(sc), pageNo);
		}
	
		System.out.println("pi : " + pi);
		
		return pi;
	}

}
