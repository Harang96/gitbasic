let EmailCheck = true;
let UserImg = "";
$(function() {
	let tmpSrc = $("#userImg").attr("src").substr();
	UserImg = tmpSrc.substring(tmpSrc.lastIndexOf("/") + 1);
	console.log(UserImg);
	
	$("#originalImg").val(UserImg);
	
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
//----------------------- 온로드의 끝 ----------------------------

function changeEmail(obj, val) {
	EmailCheck = false;
	$(obj).parent().html(`<span class="input-group-text">이메일</span>
			    <div class="input-group mb-3">
		    		<input type="text" class="form-control" id="userEmail" name="userEmail" value="${obj.value}" onchange="changeEmailIf('${val}', this);">
					<button type="button" id="sendCode" class="input-group-text btn btn-secondary" onclick="emailCheck();">이메일 인증</button>
				</div>
				<div id="errorEmail"></div>
				<div class="codeDiv input-group mb-3" style="display:none;">
					<input type="text" class="form-control" id="mailCode" placeholder="인증 코드를 입력해주세요.">
					<button type="button" id="confirmCode" class="input-group-text btn btn-secondary" onclick="codeCheck();">인증 확인</button>
				</div>`);
}

function changeEmailIf(val, obj) {
	EmailCheck = true;
	console.log(val, obj.value);
	if (obj.value == val) {
		$(obj).parent().parent().html(`<span class="input-group-text">이메일</span>
			<input type="text" class="form-control" value="${val }" onchange="changeEmail(this, '${val}');">`);
	}
}

// 이메일 인증 클릭시
function emailCheck() {
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
}
	
// 코드 확인 버튼 클릭시
function codeCheck() {
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
				EmailCheck = true;				
			} else {
				alert("인증 실패");
			}
        },
        error: function() {

        },
        complete: function() {

        }
   	}); 
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

// 수정 버튼을 누른다
function valid() {
	let isValid = false;
	let isPwd = false;
	
	if (validUserPwd2) {
		isPwd = true;	
	}
	
	if (EmailCheck && isPwd) {
		isValid = true;
	}
	
	return isValid;
}

