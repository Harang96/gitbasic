<%@page import="com.webljy.etc.PagingInfo"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시판</title>
<c:set var="contextPath" value="<%=request.getContextPath() %>"></c:set>
<c:set var="begin" value="${pagingInfo.pageNo - ((pagingInfo.pageNo - 1) % pagingInfo.viewPostCntPerPage)}"></c:set>
<c:set var="end" value="${pagingInfo.totalPageCnt - ((pagingInfo.totalPageCnt - 1) % pagingInfo.viewPostCntPerPage)}"></c:set>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.
css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
<script src="https://kit.fontawesome.com/256bd7c463.js" crossorigin="anonymous"></script>
<script>
$(function() {
	$(".board").each(function(item, i) {
		if ($(this).length != 0) {
			let dateOri = $(this).children().eq(3).html();
			
			let time = Date.parse(dateOri);
			let now = Date.now();
			
			let timeDiff = Math.floor((now - time) / (1000 * 60 * 60));
			
			if (timeDiff < 5) {
				$(this).children().eq(1).children().eq(1).attr("src", "../boardImg/new.png");
			}
			
			// postDate 시간 포메팅
			let postDate = new Date(time);
			let year = postDate.getFullYear();
			let month = postDate.getMonth() + 1;
			let date = postDate.getDate();

			let hours = postDate.getHours();
			let minutes = postDate.getMinutes();
			let seconds = postDate.getSeconds();
			
			let postDateFmt = year + "년 " + month + "월 " + date + "일 " + hours + ":" + minutes + ":" + seconds;
			$(this).children().eq(3).html(postDateFmt);
		}
	});
	

	$.each($(".replyMark"), function(e, i) {
		console.log(i);
		console.log($(i).attr("value"));
		
		let count = $(i).attr("value");
		if (count == 1) {
			$(i).html("ㄴ");
		} else if (count > 1) {
			let output = "";
			for (let i = 0; i < count - 1; i++) {
				output += "&nbsp;&nbsp;&nbsp;";
			}
			output += "ㄴ";
			
			$(i).html(output);
		}
		
	});
});

</script>
<style>
	.newImg {
		width: 30px;
	}
	
	#searchType {
		width: 150px;
		border: 1px solid #DEE2E6;
		border-radius: 3px; 
	}
	
	#searchType:focus {
		outline: 5px solid rgba(135, 207, 235, 0.4);
	}
</style>
</head>
<body>
<jsp:include page="../header.jsp"></jsp:include>
<div class=container>
	<h1>게시판 전체 목록</h1>
<!-- 	작성 시간이 5시간 전이면 new.png를 제목 옆에 붙여준다. -->
<%-- 	<div>${boardList }</div> --%>
	<div class="boardList">
 	<form action="listAll.bo" method="get">
		 <div class="input-group mt-3 mb-3">
		 	<select id="searchType" name="searchType">
		      <option value="title">제목</option>
		      <option value="writer">작성자</option>
		      <option value="content">제목+내용</option>
		    </select>
		    <input type="text" class="form-control" id="searchWords" name="searchWords" placeholder="검색어를 입력해주세요.">
		  	<button type="submit" class="btn btn-primary" id="searchBtn">검색</button>
		  </div>
	  </form>
	</div>
		<c:choose>
			<c:when test="${boardList != null}">
				<div class="input-group mt-3">
					<table id="tablelogOut" class="table table-hover">
						<thead>
					      <tr>
					        <th>글 번호</th>
					        <th>제목</th>
					        <th>작성자</th>
					        <th>작성일</th>
					        <th>조회수</th>
					      </tr>
					    </thead>
					    <tbody>
					      <c:forEach var="boardList" items="${boardList}" begin="0" end="19">
					      	<c:choose>
						      	<c:when test="${boardList.isDelete != 'Y' }">
							      	<tr id="board${boardList.no}" class="board" onclick="location.href='readPost.bo?no=${boardList.no}&userId=${sessionScope.loginMember.userId }'">
								        <td>${boardList.no}</td>
<%-- 										<c:forEach var="i" begin="1" end="${boardList.step }" varStatus="status"> --%>
<%-- 											<c:if test="${status.last }"> --%>
<%-- 												<span style="margin-left: calc(20px * ${i})">ㄴ</span> --%>
<%-- 											</c:if>	 --%>
<%-- 										</c:forEach> --%>
								        <td><span class="replyMark" value="${boardList.step}"></span>${boardList.title} <img id="outputImg${boardList.no }" class="newImg" src=""></td>
								        <td>${boardList.writer}</td>
						       			<td>${boardList.postDate}</td>
						        		<td>${boardList.readCount}</td>
						      		</tr>
							    </c:when>
							    <c:otherwise>
							    	<tr id="board${boardList.no}" class="board">
								        <td>${boardList.no}</td>
								        <td style="color: grey;"><span class="replyMark" value="${boardList.step}"></span><strike>삭제된 글입니다.</strike></td>
								        <td>${boardList.writer}</td>
						       			<td>${boardList.postDate}</td>
						        		<td>${boardList.readCount}</td>
						        	</tr>
							    </c:otherwise>
						    </c:choose>
					      </c:forEach>
					    </tbody>
					</table>
				</div>
			</c:when>
			<c:otherwise>
				<div>게시글이 존재하지 않습니다.</div>
			</c:otherwise>
		</c:choose>
		
		<div class="btns" style="text-align: right;"><button type="button" class="btn btn-primary" onclick="location.href='writeBoard.jsp'">글쓰기</button></div>
		<div class="container">
			<ul class="pagination justify-content-center">
					<c:choose>
						<c:when test="${pagingInfo.pageNo == 1}">
				   			<li class="page-item disabled"><a class="page-link" href="listAll.bo?pageNo=1&searchType=${param.searchType}&searchWords=${param.searchWords}">처음으로</a></li>
				    		<li class="page-item disabled"><a class="page-link" href="listAll.bo?pageNo=${pagingInfo.pageNo - 1}&searchType=${param.searchType}&searchWords=${param.searchWords}">이전</a></li>
				    	</c:when>
				    	<c:when test="${pagingIngo.pageNo != 1 && pagingInfo.startNumOfCurrentPagingBlock == 1}">
				    		<li class="page-item disabled"><a class="page-link" href="listAll.bo?pageNo=1&searchType=${param.searchType}&searchWords=${param.searchWords}">처음으로</a></li>
				    		<li class="page-item"><a class="page-link" href="listAll.bo?pageNo=${pagingInfo.pageNo - 1}&searchType=${param.searchType}&searchWords=${param.searchWords}">이전</a></li>
				    	</c:when>
				    	<c:otherwise>
				    		<li class="page-item"><a class="page-link" href="listAll.bo?pageNo=1&searchType=${param.searchType}&searchWords=${param.searchWords}">처음으로</a></li>
				    		<li class="page-item"><a class="page-link" href="listAll.bo?pageNo=${pagingInfo.pageNo - 1}&searchType=${param.searchType}&searchWords=${param.searchWords}">이전</a></li>
				    	</c:otherwise>
				    </c:choose>
				    <c:forEach var="i" begin="${begin }" end="${begin + pagingInfo.viewPostCntPerPage - 1}">
				    	<c:if test="${i <= pagingInfo.totalPageCnt }">
				    		<c:choose>
				    			<c:when test="${i == pagingInfo.pageNo}">
				    				<li class="page-item active"><a class="page-link" href="listAll.bo?pageNo=${i }&searchType=${param.searchType}&searchWords=${param.searchWords}">${i }</a></li>
				    			</c:when>
				    			<c:otherwise>
				    				<li class="page-item"><a class="page-link" href="listAll.bo?pageNo=${i }&searchType=${param.searchType}&searchWords=${param.searchWords}">${i }</a></li>
				    			</c:otherwise>
				    		</c:choose>
				    	</c:if>
				    </c:forEach>
					<c:choose>
				    	<c:when test="${pagingInfo.totalPageCnt <= pagingInfo.pageNo }">
				    		<li class="page-item disabled"><a class="page-link" href="listAll.bo?pageNo=${pagingInfo.pageNo + 1}&searchType=${param.searchType}&searchWords=${param.searchWords}">다음</a></li>
				    		<li class="page-item disabled"><a class="page-link" href="listAll.bo?pageNo=${pagingInfo.totalPageCnt }&searchType=${param.searchType}&searchWords=${param.searchWords}">끝으로</a></li>
				    	</c:when>
				    	<c:when test="${pagingInfo.totalPageCnt != pagingInfo.pageNo && begin == end }">
			   				<li class="page-item"><a class="page-link" href="listAll.bo?pageNo=${pagingInfo.pageNo + 1}&searchType=${param.searchType}&searchWords=${param.searchWords}">다음</a></li>
				    		<li class="page-item disabled"><a class="page-link" href="listAll.bo?pageNo=${pagingInfo.totalPageCnt }&searchType=${param.searchType}&searchWords=${param.searchWords}">끝으로</a></li>
				    	</c:when>
				    	<c:otherwise>
			   				<li class="page-item"><a class="page-link" href="listAll.bo?pageNo=${pagingInfo.pageNo + 1}&searchType=${param.searchType}&searchWords=${param.searchWords}">다음</a></li>
				    		<li class="page-item"><a class="page-link" href="listAll.bo?pageNo=${pagingInfo.totalPageCnt }&searchType=${param.searchType}&searchWords=${param.searchWords}">끝으로</a></li>
				    	</c:otherwise>
				    </c:choose>
			</ul>
		</div>
		
	</div>
	<div>${requestScope.pagingInfo }</div>
</div>
<jsp:include page="../footer.jsp"></jsp:include>
</body>
</html>