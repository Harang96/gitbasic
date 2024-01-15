let MailValid = false; 

// ------------------------------- 온로드의 시작 ---------------------------------------
$(function() {
	// 아이디 작성을 마쳤을 때 
	$("#userId").on("blur", function() {
		validUserId();
	});
	
	// 비밀번호 작성을 마쳤을 때
	$("#userPwd").on("blur", function() {
		validUserPwd();
	});
	
	// 비밀번호 확인 작성을 마쳤을 때
	$("#userPwd2").on("blur", function() {
		validUserPwd2();
		$("#userPwd").on("blur", function() {
			validUserPwd();
			validUserPwd2();
		});
	});
	
	// 이메일 인증 클릭시
	$("#sendCode").click(function() {
		if ($("#userEmail").val() != "") {
			printErrMsg("userEmail", "errorEmail", "", false, "error");
			// 이메일 보내기
			$.ajax({
		        url: "sendMail.mem",
		        type: "get",
		        data: {"tmpUserEmail" : $("#userEmail").val()},
		        dataType: "json",
		        async: false,
		        success: function(data) {
		        	console.log(data);
		        	if (data.status == "success") {
						alert("메일을 발송했습니다.");
					} else {
						alert("메일을 발송하지 못했습니다.");
					}
		        },
		        error: function() {
		
		        },
		        complete: function() {
		
		        }
		   	}); 

			$(".codeDiv").show();
		} else {
			printErrMsg("userEmail", "errorEmail", "이메일을 입력해주세요.", false, "error");
		}
	});
	
	// 코드 확인 버튼 클릭시
	$("#confirmCode").click(function() {
		let tmpCode = $("#mailCode").val();
		
		$.ajax({
	        url: "confirmCode.mem",
	        type: "get",
	        data: {"tmpCode" : tmpCode},
	        dataType: "json",
	        async: false,
	        success: function(data) {
	        	console.log(data);
	        	if (data.activation == "success") {
					alert("인증 성공");
					MailValid = true;
				} else {
					alert("인증 실패");
					MailValid = false;
				}
	        },
	        error: function() {
	
	        },
	        complete: function() {
	
	        }
	   	}); 
	});
	
})
// ------------------------------- 온로드의 끝 ---------------------------------------


// 아이디 유효성 검사
function validUserId() {
	// 3자 이상 8자 이하
	let tmpUserId = $("#userId").val();
	
	let isValid = false;
	
	if (tmpUserId.length >= 3 && tmpUserId.length <= 8) {
		printErrMsg("userId", "errorId", "", false, "error");
		// 아이디 중복 검사
		$.ajax({
	        url: "duplicateUserId.mem",
	        type: "get",
	        data: {"tmpUserId" : tmpUserId},
	        dataType: "json",
	        async: false,
	        success: function(data) {
	        	console.log(data);
	        	if (data.responseCode != "00") {
					alert("DB에 문제가 있습니다. 다시 시도해주세요.");
				} else {
		        	if (data.isDuplicate == "true") {
						// 아이디 중복
						printErrMsg("userId", "errorId", "중복된 아이디입니다. 다시 입력해주세요.", false, "error");
					} else if (data.isDuplicate == "false") {
						// 사용 가능한 아이디
						printErrMsg("userId", "errorId", "사용 가능한 아이디입니다.", false, "success");
						isValid = true;
						
					}
				}
	        	
	        },
	        error: function() {
	
	        },
	        complete: function() {
	
	        }
	   	}); 
	} else {
		printErrMsg("userId", "errorId", "아이디는 3자 이상 8자 이하로 입력해야 합니다.", false, "error");
	}
	
	
	return isValid;
}

// 비밀번호 유효성 검사
function validUserPwd() {
	let isValid = false;
	
	let reg = /^(?=.*[a-zA-Z])(?=.*[0-9]).{4,25}$/; // 영문 숫자 4자 이상 
	
	let pwd = $("#userPwd").val();
	
	
	if (reg.test(pwd)) {
		// 유효성 통과
		printErrMsg("userPwd", "errorPwd", "사용 가능한 비밀번호입니다.", false, "success");
		isValid = true;
	} else {
		// 유효성 불통과
		printErrMsg("userPwd", "errorPwd", "비밀번호는 숫자, 영문 포함 4자 이상 입력해주세요.", false, "error");
	}
	
	return isValid;
}

// 비밀번호 일치 검사
function validUserPwd2() {
	let isValid = false;
	
	let pwd = $("#userPwd").val();
	let pwd2 = $("#userPwd2").val();
	
	if (pwd.length > 3) {
		if (pwd == pwd2) {
			printErrMsg("userPwd2", "errorPwd2", "비밀번호가 일치합니다.", false, "success");
			isValid = true;
		} else {
			printErrMsg("userPwd2", "errorPwd2", "비밀번호가 일치하지 않습니다.", false, "error");
		}
	}
	
	return isValid;
}

// 회원가입 버튼을 눌렀을 때
function validCheck() {
	let isValid = false;
	
	let userIdValid = validUserId();
	let userPwdValid = validUserPwd();
	let userPwd2Valid = validUserPwd2();
	let agreeValid = false;
	
	console.log(userIdValid, userPwdValid, userPwd2Valid);
	
	let agree = $("#agree").prop("checked");
	if (agree) {
		agreeValid = true;
		printErrMsg("agree", "errorAgree", "", false, "error");
	} else {
		printErrMsg("agree", "errorAgree", "가입 조항에 동의해주세요.", false, "error");
	}
	
	if (userIdValid && userPwdValid && userPwd2Valid && agreeValid && MailValid) {
		isValid = true;
	}
	
	return isValid;
}

// 에러 메세지 출력
function printErrMsg(id, errId, msg, isFocus, success) {
	let output = "";
	if (success == "success") {
		output = `<div class="sucMsg">${msg}</div>`;
	} else {
		output = `<div class="errMsg">${msg}</div>`;
	}
	
	$(`#${errId}`).html(output);
	if (isFocus == true) {
		$(`#${id}`).focus();
	}
}

// 취소 버튼 눌렀을 때 에러 메세지 초기화
function clearMsg() {
	$("#errorId").html("");
	$("#errorPwd").html("");
	$("#errorPwd2").html("");
	$("#errorAgree").html("");
}
