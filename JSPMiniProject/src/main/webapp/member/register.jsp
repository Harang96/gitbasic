<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원가입</title>
<c:set var="contextPath" value="<%=request.getContextPath() %>"></c:set>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.
css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
<script src="https://kit.fontawesome.com/256bd7c463.js" crossorigin="anonymous"></script>
<script src="${contextPath}/member/register.js"></script>
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
		<h1 id="titleH1">회원가입</h1>
		<form action="registerMember.mem" method="post" enctype="multipart/form-data" >
			<div class="mb-3 mt-3">
			    <label for="userId" class="form-label">아이디 : </label>
			    <input type="text" class="form-control" id="userId" placeholder="아이디를 입력해주세요." name="userId">
			    <div id="errorId"></div>
			</div>
			
			<div class="mb-3 mt-3">
			    <label for="userPwd" class="form-label">비밀번호 : </label>
			    <input type="password" class="form-control" id="userPwd" placeholder="비밀번호를 입력해주세요." name="userPwd">
			    <div id="errorPwd"></div>
			</div>

			<div class="mb-3 mt-3">
			    <label for="userPwd2" class="form-label">비밀번호 확인 : </label>
			    <input type="password" class="form-control" id="userPwd2" placeholder="입력한 비밀번호를 다시 입력해주세요.">
			    <div id="errorPwd2"></div>
			</div>
			
			<div class="mb-3 mt-3">
			    <label for="userEmail" class="form-label">이메일 : </label>
			    <div class="input-group mb-3">
		    		<input type="text" class="form-control" id="userEmail" placeholder="이메일을 입력해주세요." name="userEmail">
					<button type="button" id="sendCode" class="input-group-text btn btn-secondary">이메일 인증</button>
				</div>
				<div id="errorEmail"></div>
				<div class="codeDiv input-group mb-3" style="display:none;">
					<input type="text" class="form-control" id="mailCode" placeholder="인증 코드를 입력해주세요.">
					<button type="button" id="confirmCode" class="input-group-text btn btn-secondary">인증 확인</button>
				</div>
			</div>
			
			<div class="mb-3 mt-3">
			    <label for="userImg" class="form-label">회원 이미지 : </label>
			    <input type="file" class="form-control" id="userImg" name="userImg">
			</div>
			
			<div class="mb-3 mt-3">
			    <input type="checkbox" class="form-check-input" id="agree" name="agree" value="Y">
			    <label class="form-check-label" for="check1">가입조항에 동의합니다.</label>
			    <div id="errorAgree"></div>
			</div>
			
			<button type="submit" class="btn btn-primary" onclick="return validCheck();">회원가입</button>
			<button type="reset" class="btn btn-danger" onclick="clearMsg();">취소</button>
		</form>
	</div>
	<jsp:include page="../footer.jsp"></jsp:include>
</body>
</html>