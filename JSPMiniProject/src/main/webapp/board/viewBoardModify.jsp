<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시글 수정</title>
<c:set var="contextPath" value="<%=request.getContextPath() %>"></c:set>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.
css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
<script src="https://kit.fontawesome.com/256bd7c463.js" crossorigin="anonymous"></script>
<script>
$(function() {
	let dateOri = $("#postDate").val();
	
	let time = Date.parse(dateOri);
	
	// postDate 시간 포메팅
	let postDate = new Date(time);
	let year = postDate.getFullYear();
	let month = postDate.getMonth() + 1;
	let date = postDate.getDate();
	
	let hours = postDate.getHours();
	let minutes = postDate.getMinutes();
	let seconds = postDate.getSeconds();
	
	let postDateFmt = year + "년 " + month + "월 " + date + "일 " + hours + ":" + minutes + ":" + seconds;
	$("#postDate").val(postDateFmt);
	
	// 삭제 모달
	$(".close").click(function() {
		$("#deleteModal").hide();
	});
	
	$("#deleteOpen").click(function() {
		$("#deleteModal").show();
	});
	
	document.getElementById('userImgFile').addEventListener('change', function(e) {
	    const file = e.target.files[0]; // 선택한 파일 가져오기
	
	    if (file) {
	        const reader = new FileReader(); // 파일을 읽기 위한 FileReader 객체 생성
	
	        reader.onload = function(ev) {
	            const previewImage = document.getElementById('userImg');
	            previewImage.src = ev.target.result; // 이미지 데이터를 img 태그의 src에 할당하여 미리보기
	        };
	
	        reader.readAsDataURL(file); // 파일을 읽어 data URL 형태로 가져옴
	    }
	});
});
</script>
</head>
<body>
	<jsp:include page="../header.jsp"></jsp:include>
<h1 id="titleH1">게시글 수정</h1>
	<form action="modifyPost.bo" method="post" enctype="multipart/form-data" >
		<div class="input-group mb-3 mt-3">
		    <span class="input-group-text">제목</span>
		    <input type="hidden" name="no" value="${requestScope.board.no }">
		    <input type="text" class="form-control" id="title" name="title" value="${requestScope.board.title}">
		</div>
		
		<div class="input-group mb-3 mt-3">
		    <span class="input-group-text">작성자</span>
		    <input type="text" class="form-control" id="writer" name="writer" value="${requestScope.board.writer }" readonly>
		    <span class="input-group-text">작성일</span>
		    <input type="text" class="form-control" id="postDate" value="${requestScope.board.postDate }" readonly>
		</div>
		
		<div class="input-group mb-3 mt-3">
		    <span class="input-group-text">내용</span>
		    <textArea class="form-control" id="content" name="content" rows="20" style="width:100%;">${requestScope.board.content}</textArea>
		    <div>
		    	<input type="hidden" value="${requestScope.file.newFileName}" name="newFileName">
		    	<input type="hidden" class="form-control" id="originalImg" name="originalImg">
		    	<input type="file" class="form-control" id="userImgFile" name="userImg">
	    	</div>
		    
		    <c:choose> 
		    	<c:when test="${requestScope.file.base64String != null }">
			   		<img id="userImg" width="100px;" src="data:image/jpg;base64,${requestScope.file.base64String }">
			   	</c:when>
<%-- 			   	<c:when test="${requestScope.file.newFileName != null }"> --%>
<%-- 			   		<img id="userImg" width="100px;" src="${contextPath }/${requestScope.file.newFileName}"> --%>
<%-- 			   		<input type="hidden" value="${requestScope.file.newFileName}" name="newFileName"> --%>
<%-- 			   	</c:when> --%>
		   </c:choose>
		</div>
		
		<button type="submit" class="btn btn-primary">수정</button>
		<button type="button" class="btn btn-danger">취소</button>
	</form>

<jsp:include page="../footer.jsp"></jsp:include>
</body>
</html>