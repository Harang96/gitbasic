package com.webljy.service.member;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.TreeSet;
import java.util.UUID;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.webljy.controller.MemberFactory;
import com.webljy.dao.MemberCRUD;
import com.webljy.dao.MemberDAO;
import com.webljy.dto.LoginDTO;
import com.webljy.etc.UploadedFile;
import com.webljy.service.MemberService;
import com.webljy.vo.MemberVO;

public class ModifyUserInfoService implements MemberService {

	private static final int MEMORY_THRESHOLD = 1024 * 1024 * 5; // 하나의 파일 블럭의 버퍼 사이즈 5MB
	private static final int MAX_FILE_SIZE = 1024 * 1024 * 10; // 최대 파일 사이즈 10MB
	private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 15; // 최대 request 사이즈 15MB
	
	@Override
	public MemberFactory executeService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("정보수정 실행");
		
		MemberFactory mf = MemberFactory.getInstance();
		
		// 파일 업로드할 디렉토리를 생성
		String uploadDir = "\\memberImg";
		// 실제 파일이 저장될 물리적 경로
		String realPath = request.getSession().getServletContext().getRealPath(uploadDir);
		System.out.println(realPath);

		// 파일 객체 만들기
		File saveFileDir = new File(realPath);

		String userId = "";
		String userPwd = "";
		String email = "";
		String originalImg = "";
		String encoding = "utf-8";
		// 파일이 저장될 공간의 경로, 사이즈 등의 환경설정 정보를 가지고 있는 객체
		DiskFileItemFactory factory = new DiskFileItemFactory(MEMORY_THRESHOLD, saveFileDir);
				
		ServletFileUpload sfu = new ServletFileUpload(factory);
		UploadedFile uf = null;
		
		try {
			List<FileItem> lst = sfu.parseRequest(request);

			for (FileItem f : lst) {
//				System.out.println(f.toString());
				if (f.isFormField()) { // 파일이 아닌 데이터
					if (f.getFieldName().equals("userId")) {
						userId = f.getString(encoding);
					} else if (f.getFieldName().equals("userPwd")) {
						userPwd = f.getString(encoding);
					} else if (f.getFieldName().equals("userEmail")) {
						email = f.getString(encoding);
					} else if (f.getFieldName().equals("originalImg")) {
						originalImg = f.getString(encoding);
					}

					System.out.println(userId);
					System.out.println(userPwd);
					System.out.println(email);

				} else if (f.isFormField() == false && f.getName() != "") { // 업로드 된 파일인 경우
					uf = getNewFileName(f, realPath, userId);
					
					File fileToSave = new File(realPath + File.separator + uf.getNewFileName());

					try {
						f.write(fileToSave);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (FileUploadException e) {
			e.printStackTrace();
		}
		
		int result = -1;
		// --- 회원 정보 수정 ---
		MemberDAO mDao = MemberCRUD.getInstance();
		MemberVO mem = new MemberVO(userId, userPwd, email, null, -1, -1);
		
		try {
			if (uf != null) { // 업로드 된 파일이 있는 경우
				uf.setNewFileName("memberImg/" + uf.getNewFileName());

				result = mDao.modifyUserWithFile(mem, uf);
			} else { // 업로드 된 파일이 없는 경우
				result = mDao.modifyUser(mem);
			}

			if (result == 1) {
				System.out.println("변경 성공");
			}
		} catch (NamingException | SQLException e) {
			// DB에 저장할 때 나오는 예외
			e.printStackTrace();

			if (uf != null) {
				String without = uf.getNewFileName().substring("memberImg/".length());
				File deleteFile = new File(realPath + File.separator + without);
				
				deleteFile.delete(); // 파일 삭제
			}

			// 회원 가입 시 예외 발생 => 회원 가입 페이지로 이동
			mf.setRedirect(true);
			mf.setWhereToGo(request.getContextPath() + "/member/memberInfoModify.jsp?status=fail");
			
			return mf;
		}
		
		// 수정 성공 => memberInfo.jsp로 보낸다.
		MemberDAO dao = MemberCRUD.getInstance();
		try {
			LoginDTO loginMember = dao.loginMember(userId);
			HttpSession ses = request.getSession();
			ses.setAttribute("loginMember", loginMember);
			
			// 기존 파일 삭제
			String without = originalImg;
			File deleteFile = new File(realPath + File.separator + without);
			
			deleteFile.delete(); // 파일 삭제
			
			mf.setRedirect(true);
			mf.setWhereToGo(request.getContextPath() + "/member/memberInfo.jsp?status=success");
		} catch (NamingException | SQLException e) {
			e.printStackTrace();
		}
		
		return mf;
	}

	private UploadedFile getNewFileName(FileItem f, String realPath, String userId) {
		// userId_UUID로 새 파일 이름 만들기
		String uuid = UUID.randomUUID().toString();
		String originalFileName = f.getName(); // 업로드 된 원본 파일 이름
		String ext = originalFileName.substring(originalFileName.lastIndexOf(".")); // .jpg

		String newFileName = "";

		if (f.getSize() > 0) { // 실제 파일이 저장되는 경우
			newFileName += userId + "_" + uuid + ext;
		}

		UploadedFile uf = new UploadedFile(originalFileName, ext, newFileName, f.getSize());

		return uf;

	}
	
}
