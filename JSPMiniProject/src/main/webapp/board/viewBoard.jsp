<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시글</title>
<c:set var="contextPath" value="<%=request.getContextPath() %>"></c:set>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.
css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
<script src="https://kit.fontawesome.com/256bd7c463.js" crossorigin="anonymous"></script>
<script>
$(function() {
	$(".like").click(function() {
		// 좋아요 버튼 깜빡깜빡
		let classList = document.querySelector('.like').classList;
		if (classList.contains("bg-danger")) {
			classList.remove("bg-danger");
			classList.add("bg-light");
			$(".like").css("color", "#DC3545");
			
			// 좋아요 버튼 ajax
			$.ajax({
		        url: "unlikePost.bo",
		        type: "post",
		        data: {"no" : '${requestScope.board.no}',
		        		"userId" : '${sessionScope.loginMember.userId}'},
		        dataType: "json",
		        async: false,
		        success: function(data) {
		        	console.log(data);
		        	$(".like").html(data.likeCount);  	0	
		        },
		        error: function() {

		        },
		        complete: function() {

		        }
		   	});
		} else {
			classList.add("bg-danger");
			classList.remove("bg-light");
			$(".like").css("color", "white");
			
			$.ajax({
		        url: "likePost.bo",
		        type: "post",
		        data: {"no" : '${requestScope.board.no}',
		        		"userId" : '${sessionScope.loginMember.userId}'},
		        dataType: "json",
		        async: false,
		        success: function(data) {
		        	console.log(data);
					$(".like").html(data.likeCount);  	
		        },
		        error: function() {

		        },
		        complete: function() {

		        }
		   	});
		}
	});
	
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
});

// 삭제 모달
function deletePost(no) {
	$.ajax({
        url: "deletePost.bo",
        type: "get",
        data: {"no" : no},
        dataType: "json",
        async: false,
        success: function(data) {
        	console.log(data);
        	$("#deleteModal").hide();
        	
        	if (data.status == "success") {
				alert("삭제가 완료되었습니다.");
				location.href="${contextPath}/board/listAll.bo";
			} else {
				alert("삭제에 실패했습니다.");
			}
        	
        },
        error: function() {

        },
        complete: function() {

        }
   	}); 
	
}
	
</script>
<style>
	.badgeInfo {
/* 		border: 2px solid #0DCAF0; */
		font-size: 14px;
		padding: 3px;
		border-radius: 3px;
	}
	
	.bg-light {
		color: #DC3545;
	}
</style>
</head>
<body>
<jsp:include page="../header.jsp"></jsp:include>
<h1 id="titleH1">게시글 조회</h1>
	<div class="input-group mb-3 mt-3">
	    <span class="input-group-text">제목</span>
	    <input type="text" class="form-control" id="title" name="title" value="${requestScope.board.title}" readonly>
	</div>
	
	<div class="input-group mb-3 mt-3">
	    <span class="input-group-text">작성자</span>
	    <input type="text" class="form-control" id="writer" name="writer" value="${requestScope.board.writer }" readonly>
	    <span class="input-group-text">작성일</span>
	    <input type="text" class="form-control" id="postDate" name="postDate" value="${requestScope.board.postDate }" readonly>
	</div>
	<div>
		<span class="badgeInfo">조회수</span>
		<span class="badge bg-info" >${requestScope.board.readCount }</span>
		
		<span class="badgeInfo">좋아요</span>
		<c:choose>
			<c:when test="${sessionScope.loginMember == null}">
				<span class="badge bg-light" style="color: #DC3545; border:2px solid #DC3545;">${requestScope.board.likeCount }</span>
			</c:when>
			<c:otherwise>
				<c:choose>
					<c:when test="${requestScope.isLike == true }">
						<span class="like badge bg-danger" style="border:2px solid #DC3545;">${requestScope.board.likeCount }</span>
					</c:when>
					<c:otherwise>
						<span class="like badge bg-light" style="color: #DC3545; border:2px solid #DC3545;">${requestScope.board.likeCount }</span>
					</c:otherwise>
				</c:choose>
			</c:otherwise>
		</c:choose>
	</div>
	
	<div class="input-group mb-3 mt-3">
	    <span class="input-group-text">내용</span>
	    <div class="form-control" id="content" name="content" rows="20" style="width:100%;">${requestScope.board.content}</div>
	    <c:choose> 
	    	<c:when test="${requestScope.file.base64String != null }">	
		   		<img width="100px;" src="data:image/jpg;base64,${requestScope.file.base64String }">
		   	</c:when>
		   	<c:when test="${requestScope.file.newFileName != null }">
		   		<img src="${contextPath }/${requestScope.file.newFileName}">
		   	</c:when>
	   </c:choose>
	</div>

	<c:if test="${sessionScope.loginMember.userId == requestScope.board.writer }">	
		<button type="submit" class="btn btn-primary" onclick="location.href='${contextPath}/board/getModifyPost.bo?no=${requestScope.board.no }'">수정</button>
		<button type="button" class="btn btn-danger" id="deleteOpen">삭제</button>
	</c:if>
	
	<c:if test="${sessionScope.loginMember != null }">
		<button type="button" class="btn btn-info" onclick="location.href='${contextPath}/board/replyBoard.jsp?no=${requestScope.board.no}&ref=${requestScope.board.ref}'">답글달기</button>
	</c:if>
	<button type="button" class="btn btn-success" onclick="location.href='${contextPath}/board/listAll.bo'">목록으로</button>
	
	<div class="modal" id="deleteModal">
	  <div class="modal-dialog">
	    <div class="modal-content">
	
	      <!-- Modal Header -->
	      <div class="modal-header">
	        <h4 class="modal-title">정말 삭제하시겠습니까?</h4>
	        <button type="button" class="btn-close close" data-bs-dismiss="modal"></button>
	      </div>
	
	      <!-- Modal body -->
	      <div class="modal-body">
	        ${requestScope.board.no}번 글을 삭제합니다.
	      </div>
	
	      <!-- Modal footer -->
	      <div class="modal-footer">
	        <button type="button" class="btn btn-primary" data-bs-dismiss="modal" onclick="deletePost(${requestScope.board.no});">삭제</button>
	        <button type="button" class="btn btn-danger close" data-bs-dismiss="modal">취소</button>
	      </div>
		
	    </div>
	  </div>
	</div>
<jsp:include page="../footer.jsp"></jsp:include>
</body>
</html>