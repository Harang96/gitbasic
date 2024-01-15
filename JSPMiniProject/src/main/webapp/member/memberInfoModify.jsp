<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="EUC-KR">
<title>회원 정보 수정</title>
<c:set var="contextPath" value="<%=request.getContextPath() %>"></c:set>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
<script src="https://kit.fontawesome.com/256bd7c463.js" crossorigin="anonymous"></script>
<script type="text/javascript" src="memberInfoModify.js"></script>
</head>
<body>
<jsp:include page="../header.jsp"></jsp:include>
	<div class="container">
		<h1>마이 페이지</h1>
		<form action="modifyUser.mem" method="post" enctype="multipart/form-data" >
			<div class="input-group mb-3 mt-3">
			    <span class="input-group-text">회원 이미지</span>
			    <input type="file" class="form-control" id="userImgFile" name="userImg">
			    <input type="hidden" class="form-control" id="originalImg" name="originalImg">
			</div>
		    <div><img id="userImg" name="userImg" src="${contextPath }/${sessionScope.loginMember.memberImg}"></div>
			
			<div class="input-group mb-3 mt-3">
			    <span class="input-group-text">아이디</span>
			    <input type="text" name="userId" class="form-control" readonly value="${sessionScope.loginMember.userId }">
			</div>
			
			<div class="input-group mb-3 mt-3">
			    <span class="input-group-text">비밀번호</span>
			    <input id="userPwd" type="password" name="userPwd" class="form-control" placeholder="변경할 비밀번호를 입력해주세요.">
			    <div id="errorPwd2"></div>
			</div>
			
			<div class="input-group mb-3 mt-3">
			    <span class="input-group-text">비밀번호 확인</span>
			    <input id="userPwd2" type="password" class="form-control" placeholder="비밀번호를 다시 입력해주세요." onchange="validUserPwd2();">
			</div>
			
			<div class="input-group mb-3 mt-3">
			    <span class="input-group-text">이메일</span>
			    <input type="text" name="userEmail" class="form-control" value="${sessionScope.loginMember.userEmail }" onchange="changeEmail(this, '${sessionScope.loginMember.userEmail }');">
			</div>
			
			<button type="submit" class="btn btn-primary" onclick="return valid();">회원 정보 수정</button>
			<a href="${contextPath}/member/memberInfo.jsp"><button type="button" class="btn btn-danger">취소</button></a>
		</form>
	</div>
<jsp:include page="../footer.jsp"></jsp:include>
</body>
</html>