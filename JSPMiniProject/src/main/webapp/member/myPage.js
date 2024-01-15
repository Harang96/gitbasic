let UserImg = "";

$(function() {
	$(".close").click(function() {
		$("#deleteModal").hide();
	});
	
	$("#deleteOpen").click(function() {
		$("#deleteModal").show();
	});
	
	let tmpSrc = $("#userImg").attr("src").substr();
	UserImg = tmpSrc.substring(tmpSrc.lastIndexOf("/") + 1);
	console.log(UserImg);
	
});
// ---------------------------온로드의 끝-----------------------------

// 유저 삭제
function deleteUser() {
	let userId = $("#userId").val();
	
	location.href = "deleteUser.mem?userId=" + userId + "&userImg=" + UserImg;
	
}