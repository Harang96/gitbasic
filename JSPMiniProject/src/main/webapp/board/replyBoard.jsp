<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>답글 작성</title>
<c:set var="contextPath" value="<%=request.getContextPath() %>"></c:set>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.
css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
<script src="https://kit.fontawesome.com/256bd7c463.js" crossorigin="anonymous"></script>
</head>
<body>
<jsp:include page="../header.jsp"></jsp:include>

<c:if test="${sessionScope.loginMember == null }">
	<c:redirect url="../member/login.jsp"></c:redirect>	
</c:if>


<h1 id="titleH1">답글 작성</h1>
	<form action="reply.bo" method="post">
		<div class="input-group mb-3 mt-3">
		    <span class="input-group-text">작성자</span>
		    <input type="text" class="form-control" id="writer" name="writer" value="${sessionScope.loginMember.userId }" readonly>
		    <input type="hidden" value="${param.no }" name="no">
		    <input type="hidden" value="${param.ref }" name="ref">
		</div>
		
		<div class="input-group mb-3 mt-3">
		    <span class="input-group-text">제목</span>
		    <input type="text" class="form-control" id="title" name="title">
		</div>
		
		<div class="input-group mb-3 mt-3">
		    <span class="input-group-text">내용</span>
		    <textArea class="form-control" id="content" name="content" rows="20" style="width:100%;"></textArea>
		</div>
		
		<button type="submit" class="btn btn-primary">저장</button>
		<button type="reset" class="btn btn-danger" onclick="location.href='listAll.bo'">취소</button>
	</form>
<jsp:include page="../footer.jsp"></jsp:include>
</body>
</html>