<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<c:set var="contextPath" value="<%=request.getContextPath() %>"></c:set>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
<script src="https://kit.fontawesome.com/256bd7c463.js" crossorigin="anonymous"></script>
<script type="text/javascript" src="${contextPath}/header.js"></script>
<style>
	#header_title {
		background-image : url('${contextPath}/image/title_sky.jpg'); 
		background-size : cover;
		text-shadow: 1px 1px 3px black, -1px 1px 3px black, 1px -1px 3px black, -1px -1px 3px black;
	}

	
</style>
</head>
<body>
	<div id="header_title" class="p-5 bg-primary text-white text-center">
	  <h1>JSP Mini Project</h1>
	  <p>2024 Jan</p> 
	</div>
	
	<nav class="navbar navbar-expand-sm bg-dark navbar-dark">
	  <div class="container-fluid">
	    <ul class="navbar-nav">
	      <li class="nav-item">
	        <a class="nav-link active" href="${contextPath}/index.jsp">홈</a>
	      </li>
	      <li class="nav-item">
	        <a class="nav-link" href="${contextPath}/board/listAll.bo">게시판</a>
	      </li>
	      
	      <c:choose>
	      	<c:when test="${sessionScope.loginMember == null}">
		      <li class="nav-item">
		        <a class="nav-link" href="${contextPath}/member/register.jsp">회원가입</a>
		      </li>
		      <li class="nav-item">
		        <a class="nav-link" href="${contextPath}/member/login.jsp">로그인</a>
		      </li>
	     	 </c:when>
	     	 <c:otherwise>
	     	 	<li class="nav-item">
<%-- 		        <a class="nav-link" href="${contextPath}/member/memberInfo.jsp"><img src="${contextPath }/${sessionScope.loginMember.memberImg }" width="20px" height="20px"> ${sessionScope.loginMember.userId }</a> --%>
		        <a class="nav-link" href="${contextPath}/member/myPage.mem?userId=${sessionScope.loginMember.userId}"><img src="${contextPath }/${sessionScope.loginMember.memberImg }" width="20px" height="20px"> ${sessionScope.loginMember.userId }</a>
		      </li>
		      <li class="nav-item">
		        <a class="nav-link" href="${contextPath}/member/logout.mem">로그아웃</a>
		      </li>
	     	 </c:otherwise>
	      </c:choose>
	       <c:if test="${sessionScope.loginMember.isAdmin == 'Y'}">
	     	 	<li class="nav-item">
			        <a class="nav-link" href="${contextPath}/admin/admin.jsp">관리자페이지</a>
			      </li>	
	     	 </c:if>
	    </ul>
	  </div>
	</nav>
</body>
</html>