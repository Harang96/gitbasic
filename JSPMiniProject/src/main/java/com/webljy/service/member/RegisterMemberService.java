package com.webljy.service.member;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.webljy.controller.MemberFactory;
import com.webljy.dao.MemberCRUD;
import com.webljy.dao.MemberDAO;
import com.webljy.etc.UploadedFile;
import com.webljy.service.MemberService;
import com.webljy.vo.MemberVO;

public class RegisterMemberService implements MemberService {

	private static final int MEMORY_THRESHOLD = 1024 * 1024 * 5; // 하나의 파일 블럭의 버퍼 사이즈 5MB
	private static final int MAX_FILE_SIZE = 1024 * 1024 * 10; // 최대 파일 사이즈 10MB
	private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 15; // 최대 request 사이즈 15MB

	@Override
	public MemberFactory executeService(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("회원가입 실행");

		MemberFactory mf = MemberFactory.getInstance();
		// 파일과 함께 데이터를 받았다면, request.getParameter()로 데이터를 수집할 수 없다.
//		System.out.println(request.getParameter("userId"));

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
		String userImg = "";
		String encoding = "utf-8";
		String test = "";
		// 파일이 저장될 공간의 경로, 사이즈 등의 환경설정 정보를 가지고 있는 객체
		DiskFileItemFactory factory = new DiskFileItemFactory(MEMORY_THRESHOLD, saveFileDir);

//		DiskFileItemFactory factory1 = new DiskFileItemFactory();
//		factory1.setSizeThreshold(MEMORY_THRESHOLD);
//		factory1.setRepository(saveFileDir);

		// 실제 request로 넘겨져 온 매개 변수를 통해 파일을 업로드 처리할 객체
		ServletFileUpload sfu = new ServletFileUpload(factory);
		UploadedFile uf = null;

		try {
			List<FileItem> lst = sfu.parseRequest(request);

//		 FileItem 속성
//		 1) name 값이 null이 아니면 파일(name 값이 파일 이름 : 확장자 포함)
//		 2) isFormField의 값이 true이면 파일이 아닌 데이터
//		    isFormField의 값이 false이면 파일
//		 3) FieldName의 값이 보내온 데이터의 input 태그의 name 속성 값

			for (FileItem f : lst) {
//				System.out.println(f.toString());
				if (f.isFormField()) { // 파일이 아닌 데이터
					if (f.getFieldName().equals("userId")) {
						userId = f.getString(encoding);
					} else if (f.getFieldName().equals("userPwd")) {
						userPwd = f.getString(encoding);
					} else if (f.getFieldName().equals("userEmail")) {
						email = f.getString(encoding);
					}

				} else if (f.isFormField() == false && f.getName() != "") { // 업로드 된 파일인 경우
					// 파일 이름 중복 제거
					// 1) 중복되지 않을 새 이름으로 파일명을 변경 : UUID 이용
					// userId_uuid
					uf = getNewFileName(f, realPath, userId);
					
//					System.out.println(uf);
					// 2) 파일명(순서번호).확장자
//					uf = getNewFileNameNo(f, realPath, userId);
//					uf = makeNewFileNameWithNumbering(f, realPath);

					// 파일 하드디스크에 저장
					File fileToSave = new File(realPath + File.separator + uf.getNewFileName());

					try {
						f.write(fileToSave);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (FileUploadException e) {
			// 파일 업로드 될 때의 예외
			e.printStackTrace();
		}

		int result = -1;
		// --- 회원 가입 진행 ---
		MemberDAO mDao = MemberCRUD.getInstance();
		MemberVO mem = new MemberVO(userId, userPwd, email, null, -1, -1);
		try {
			if (uf != null) { // 업로드 된 파일이 있는 경우
				uf.setNewFileName("memberImg/" + uf.getNewFileName());

				result = mDao.registerMemberWithFile(uf, mem, "회원가입", 100);
				System.out.println("업로드 된 파일이 있는 경우의 회원 가입 : " + result);
			} else { // 업로드 된 파일이 없는 경우
				result = mDao.registerMember(mem, "회원가입", 100);
			}

			if (result == 1) {
				System.out.println("회원가입 성공");
			}
		} catch (NamingException | SQLException e) {
			// DB에 저장할 때 나오는 예외
			e.printStackTrace();

			// 업로드 된 파일이 있으면 삭제해야 함
//			System.out.println("삭제할 이미지 : " + uf.getNewFileName());
			
			if (uf != null) {
				String without = uf.getNewFileName().substring("memberImg/".length());
				File deleteFile = new File(realPath + File.separator + without);
				
				deleteFile.delete(); // 파일 삭제
			}

			// 회원 가입 시 예외 발생 => 회원 가입 페이지로 이동
			mf.setRedirect(true);
			mf.setWhereToGo(request.getContextPath() + "/member/register.jsp?status=fail");
			
			return mf;
		}

		// 회원 가입 성공 => index.jsp로 보낸다.
		mf.setRedirect(true);
		mf.setWhereToGo(request.getContextPath() + "/index.jsp?status=success");
		
		return mf;
	}

	private UploadedFile getNewFileNameNo(FileItem f, String realPath, String userId) {
		String originalFileName = f.getName();
		File dir = new File(realPath);
		String ext = originalFileName.substring(originalFileName.lastIndexOf("."));
		String originalName = originalFileName.substring(0, originalFileName.lastIndexOf(ext));
		String newFileName = "";
		int count = -1;
		boolean isFind = false;

		String[] files = dir.list();

		TreeSet<Integer> countSet = new TreeSet<Integer>();

		for (String file : files) {

			System.out.println("ori : " + file + "// file : " + file);
			String extFile = file.substring(file.lastIndexOf("."));
			String oriFile = "";
			String newFile = "";
			if (file.lastIndexOf("(") != -1) {
				oriFile = file.substring(0, file.lastIndexOf("("));
				newFile = oriFile + extFile;
			} else {
				newFile = file;
			}

			if (file.equals(originalFileName)) { // 정확히 이름이 같은 파일이 있는지 => 없으면 그 파일명으로 생성
				isFind = true;
			}

			if (newFile.equals(file)) {
				countSet.add(0);
			} else {
				if (newFile.equals(originalFileName)) {

					if (file.lastIndexOf("(") != -1) {
						int openParenIndex = file.lastIndexOf("(");
						int closeParenIndex = file.lastIndexOf(")");
						if (openParenIndex != -1 && closeParenIndex != -1 && closeParenIndex > openParenIndex) {
							String countStr = file.substring(openParenIndex + 1, closeParenIndex);
							System.out.println("fileCnt: " + countStr);
							int tmpCount = Integer.parseInt(countStr);
							countSet.add(tmpCount);
						}
					}
				}
			}

		}

		if (!isFind) {
			newFileName = originalFileName;
		} else {
			if (countSet.size() > 0) { // 새로운 기준을 정립해야할 듯
				count = countSet.last() + 1;
				newFileName = originalName + "(" + count + ")" + ext;
			}
		}

		UploadedFile uf = null;

		uf = new UploadedFile(originalFileName, ext, newFileName, f.getSize());

		return uf;
	}

	private UploadedFile getNewFileName(FileItem f, String realPath, String userId) {
		// userId_UUID로 새 파일 이름 만들기
		String uuid = UUID.randomUUID().toString();
		String originalFileName = f.getName(); // 업로드 된 원본 파일 이름
		String ext = originalFileName.substring(originalFileName.lastIndexOf(".")); // .jpg

//		System.out.println("ext : " + ext);

		String newFileName = "";

		if (f.getSize() > 0) { // 실제 파일이 저장되는 경우
			newFileName += userId + "_" + uuid + ext;
		}

//		System.out.println("새 파일 이름 : " + newFileName);
		UploadedFile uf = new UploadedFile(originalFileName, ext, newFileName, f.getSize());

		return uf;

	}

	private UploadedFile makeNewFileNameWithNumbering(FileItem f, String realPath) { // 선생님 메서드
		// ex> 파일명(번호).확장자 -> 새 파일 이름을 만들기
		int cnt = 0;
		String tmpFileName = f.getName(); // 업로드 된 원본 이름
		String newFileName = ""; // 실제 저장될 새 파일명
		String ext = tmpFileName.substring(tmpFileName.lastIndexOf("."));

		while (duplicateFileName(tmpFileName, realPath)) { // 파일이 중복되면
			// 새 파일 이름 만들기
			cnt++;
			tmpFileName = makeNewFileName(tmpFileName, cnt);
		}

		newFileName = tmpFileName;

		UploadedFile uf = new UploadedFile(f.getName(), ext, newFileName, f.getSize());

		return uf;
	}

	private boolean duplicateFileName(String tmpFileName, String realPath) {
		boolean result = false;
		File tmpFileNamePath = new File(realPath);
		File[] files = tmpFileNamePath.listFiles();

//		for (File f : files) {
//			if (f.getName().equals(tmpFileName)) {
//				System.out.println(tmpFileName + "이 중복됩니다.");
//				result = true;
//			}
//		}

		File tmpFile = new File(realPath + File.separator + tmpFileName);
		if (tmpFile.exists()) {
			System.out.println(tmpFileName + "이 중복됩니다.");
			result = true;
		}

		return result;
	}

	private String makeNewFileName(String tmpFileName, int cnt) {
		// ex) 파일명(번호).확장자
		String newFileName = "";
		String ext = tmpFileName.substring(tmpFileName.lastIndexOf("."));
		String oldFileNameWithoutExt = tmpFileName.substring(0, tmpFileName.lastIndexOf("."));

		int openPos = oldFileNameWithoutExt.indexOf("(");

		if (openPos == -1) { // 괄호가 없다면 -> 처음 중복
			newFileName = oldFileNameWithoutExt + "(" + cnt + ")" + "_cp" + ext;
		} else {
			newFileName = oldFileNameWithoutExt.substring(0, openPos) + "(" + cnt + ")" + "_cp" + ext;
		}

		return newFileName;
	}

}
