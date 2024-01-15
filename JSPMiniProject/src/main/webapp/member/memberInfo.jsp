<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="EUC-KR">
<title>���� ������</title>
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
		<h1>���� ������</h1>
		<div id="infoBox">
			<div class="input-group mb-3 mt-3">
			    <span class="input-group-text">ȸ�� �̹���</span>
			</div>
		    <div><img id="userImg" src="${contextPath }/${sessionScope.loginMember.memberImg}"></div>
			
			<div class="input-group mb-3 mt-3">
			    <span class="input-group-text">���̵�</span>
			    <input type="text" id="userId" class="form-control" readonly value="${sessionScope.loginMember.userId }">
			</div>
			
			<div class="input-group mb-3 mt-3">
			    <span class="input-group-text">�̸���</span>
			    <input type="text" class="form-control" readonly value="${sessionScope.loginMember.userEmail }">
			</div>
			
			<div class="input-group mb-3 mt-3">
			    <span class="input-group-text">������</span>
			    <input type="text" class="form-control" readonly value="${sessionScope.loginMember.registerDate }">
			</div>
			
			<div class="input-group mb-3 mt-3">
			    <span class="input-group-text">����Ʈ</span>
			    <input type="text" class="form-control" readonly value="${sessionScope.loginMember.userPoint }��">
			</div>
			
			<a href="${contextPath}/member/memberInfoModify.jsp"><button type="submit" class="btn btn-primary">ȸ�� ���� ����</button></a>
			<button type="reset" class="btn btn-danger" id="deleteOpen">ȸ�� Ż��</button>
		</div>
	</div>
	
	<div class="modal" id="deleteModal">
	  <div class="modal-dialog">
	    <div class="modal-content">
	
	      <!-- Modal Header -->
	      <div class="modal-header">
	        <h4 class="modal-title">���� Ż���Ͻðڽ��ϱ�?</h4>
	        <button type="button" class="btn-close close" data-bs-dismiss="modal"></button>
	      </div>
	
	      <!-- Modal body -->
	      <div class="modal-body">
	        ȸ�� Ż�� �����Ͻ� ��� ��¼�� ��¼�� 
	      </div>
	
	      <!-- Modal footer -->
	      <div class="modal-footer">
	        <button type="button" class="btn btn-primary" data-bs-dismiss="modal" onclick="deleteUser();">Ż��</button>
	        <button type="button" class="btn btn-danger close" data-bs-dismiss="modal">���</button>
	      </div>
	
	    </div>
	  </div>
	</div>
	
	<jsp:include page="../footer.jsp"></jsp:include>
</body>
</html>