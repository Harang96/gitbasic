<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="EUC-KR">
<title>마이 페이지</title>
<c:set var="contextPath" value="<%=request.getContextPath() %>"></c:set>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
<script src="https://kit.fontawesome.com/256bd7c463.js" crossorigin="anonymous"></script>
<script type="text/javascript" src="memberInfo.js"></script>
</head>
<script>
$(function() {
	console.log($("#userImg").attr("src"));
});
</script>
<body>
	<jsp:include page="../header.jsp"></jsp:include>
	<div class="container">
		<h1>마이 페이지 - 강사님</h1>
		<div id="infoBox">
			<div class="input-group mb-3 mt-3">
			    <span class="input-group-text">회원 이미지</span>
			</div>
		    <div><img id="userImg" src="${contextPath }/${requestScope.member.memberImg}"></div>
			
			<div class="input-group mb-3 mt-3">
			    <span class="input-group-text">아이디</span>
			    <input type="text" id="userId" class="form-control" readonly value="${requestScope.member.userId }">
			</div>
			
			<div class="input-group mb-3 mt-3">
			    <span class="input-group-text">이메일</span>
			    <input type="text" class="form-control" readonly value="${requestScope.member.userEmail }">
			</div>
			
			<div class="input-group mb-3 mt-3">
			    <span class="input-group-text">가입일</span>
			    <input type="text" class="form-control" readonly value="${requestScope.member.registerDate }">
			</div>
			
			<div class="input-group mb-3 mt-3">
			    <span class="input-group-text">포인트</span>
			    <input type="text" class="form-control" readonly value="${requestScope.member.userPoint }점">
				<div class="input-group mb-3 mt-3">
					<div class="input-group-text">적립금 내역</div>
					<span class="input-group-text">총 적립금 ${requestScope.member.userPoint }포인트</span>
					<table id="pointlogOut" class="table">
						<thead>
					      <tr>
					        <th>적립 일시</th>
					        <th>적립 사유</th>
					        <th>적립 포인트</th>
					      </tr>
					    </thead>
					    <tbody>
					      <c:forEach var="point" items="${requestScope.pointlog }" begin="0" end="9">
					      	  <tr>
						        <td>${point.when }</td>
						        <td>${point.why }</td>
						        <td>${point.howmuch }</td>
						      </tr>
					      </c:forEach>
					    </tbody>
					</table>
				</div>
			</div>
			
		
			
			<a href="${contextPath}/member/memberInfoModify.jsp"><button type="submit" class="btn btn-primary">회원 정보 수정</button></a>
			<button type="reset" class="btn btn-danger" id="deleteOpen">회원 탈퇴</button>
		</div>
	</div>
	
	<div class="modal" id="deleteModal">
	  <div class="modal-dialog">
	    <div class="modal-content">
	
	      <!-- Modal Header -->
	      <div class="modal-header">
	        <h4 class="modal-title">정말 탈퇴하시겠습니까?</h4>
	        <button type="button" class="btn-close close" data-bs-dismiss="modal"></button>
	      </div>
	
	      <!-- Modal body -->
	      <div class="modal-body">
	        회원 탈퇴를 진행하실 경우 어쩌구 저쩌구 
	      </div>
	
	      <!-- Modal footer -->
	      <div class="modal-footer">
	        <button type="button" class="btn btn-primary" data-bs-dismiss="modal" onclick="deleteUser();">탈퇴</button>
	        <button type="button" class="btn btn-danger close" data-bs-dismiss="modal">취소</button>
	      </div>
	
	    </div>
	  </div>
	</div>
	
	<jsp:include page="../footer.jsp"></jsp:include>
</body>
</html>