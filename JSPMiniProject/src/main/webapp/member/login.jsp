<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>로그인</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.
css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
<script src="https://kit.fontawesome.com/256bd7c463.js" crossorigin="anonymous"></script>
<script src="login.js"></script>
<style>
	.errMsg {
		color: lightCoral;
	}
	
	.sucMsg {
		color: skyBlue;
	}
	
	#titleH1 {
		font-size: 22px;
		font-weight: 600;
	}
	
	.container {
		margin-top: 20px;
	}
	
	
</style>
</head>
<body>
	<jsp:include page="../header.jsp"></jsp:include>
	<div class="container">
		<h1 id="titleH1">로그인</h1>
		<form action="login.mem" method="post">
			<div class="mb-3 mt-3">
			    <label for="userId" class="form-label">아이디 : </label>
			    <input type="text" class="form-control" id="userId" placeholder="아이디를 입력해주세요." name="userId">
			</div>
			
			<div class="mb-3 mt-3">
			    <label for="userPwd" class="form-label">비밀번호 : </label>
			    <input type="password" class="form-control" id="userPwd" placeholder="비밀번호를 입력해주세요." name="userPwd">
			</div>

			<button type="submit" class="btn btn-primary">로그인</button>
			<button type="reset" class="btn btn-danger">취소</button>
		</form>
	</div>
	<jsp:include page="../footer.jsp"></jsp:include>
</body>
</html>