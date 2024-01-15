package com.webljy.service.board;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;

import com.webljy.controller.BoardFactory;
import com.webljy.controller.MemberFactory;
import com.webljy.dao.BoardCRUD;
import com.webljy.dao.BoardDAO;
import com.webljy.dto.WriteBoardDTO;
import com.webljy.etc.UploadedFile;
import com.webljy.service.BoardService;

public class ModifyPostService implements BoardService {

	private static final int MEMORY_THRESHOLD = 1024 * 1024 * 5; // 하나의 파일 블럭의 버퍼 사이즈 5MB
	private static final int MAX_FILE_SIZE = 1024 * 1024 * 10; // 최대 파일 사이즈 10MB
	private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 15; // 최대 request 사이즈 15MB
	
	@Override
	public BoardFactory doAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		BoardFactory bf = BoardFactory.getInstance();
		
		String uploadDir = "\\boardImg";
		String realPath = request.getSession().getServletContext().getRealPath(uploadDir);
		System.out.println("리얼 패스 : " + realPath);
		
		File saveFileDir = new File(realPath);
		
		int no = -1;
		String writer = "";
		String title = "";
		String content = "";
		String upFile = "";
		String newFileName = ""; // 원래 있던 파일의 이름
		String encoding = "utf-8";
		
		DiskFileItemFactory factory = new DiskFileItemFactory(MEMORY_THRESHOLD, saveFileDir);
		
		ServletFileUpload sfu = new ServletFileUpload(factory);
		UploadedFile uf = null;
		
		try {
			List<FileItem> lst = sfu.parseRequest(request);
			
			for (FileItem f : lst) {
				if (f.isFormField()) { // 파일이 아닌 데이터
					if (f.getFieldName().equals("writer")) {
						writer = f.getString(encoding);
					} else if (f.getFieldName().equals("title")) {
						title = f.getString(encoding);
					} else if (f.getFieldName().equals("content")) {
						content = f.getString(encoding);
					} else if (f.getFieldName().equals("no")) {
						no = Integer.parseInt(f.getString(encoding));
					} 
					
					if (f.getFieldName().equals("newFileName")) {
						newFileName = f.getString(encoding);
					}

					System.out.println(writer);
					System.out.println(title);
					System.out.println(content);
					System.out.println(newFileName);
				} else if (f.isFormField() == false && f.getName() != "") { // 업로드 된 파일인 경우
					if (newFileName != "") {
	//					uf = makeFileName(f, realPath, writer); // 업로드 파일
						uf = makeFileName(f, realPath, newFileName); // 업로드 파일
					} else {
						uf = makeFileNameByUUID(f, realPath, writer);
					}
					File fileToSave = new File(realPath + File.separator + uf.getNewFileName());

					try { 
						f.write(fileToSave);
						uf.setBase64String(makeImgToBase64String(realPath + File.separator + uf.getNewFileName()));
//						System.out.println(uf);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (FileUploadException e) {
			e.printStackTrace();
		}
		
		int result = -1;
		// --- 글수정 진행 ---
		BoardDAO dao = BoardCRUD.getInstance();
		WriteBoardDTO brd = new WriteBoardDTO(writer, title, content);
		
		// 여기부터 수정
		try {
			if (uf != null) { // 업로드 된 파일이 있는 경우
				uf.setNewFileName("boardImg/" + uf.getNewFileName());

				result = dao.modifyPostWithFile(brd, uf, no);
				System.out.println("업로드 된 파일이 있는 경우의 글수정 : " + result);
			} else { // 업로드 된 파일이 없는 경우
				result = dao.modifyPost(brd, no);
			}

			if (result == 1) {
				System.out.println("글 수정 성공");
				System.out.println(newFileName);
//				String without = newFileName.substring("boardImg/".length());
//				File deleteFile = new File(realPath + File.separator + without);
//				
//				deleteFile.delete();
			}  
		} catch (NamingException | SQLException e) {
			// DB에 저장할 때 나오는 예외
			e.printStackTrace();

			// 업로드 된 파일이 있으면 삭제해야 함
			
			if (uf != null) {
				String without = uf.getNewFileName().substring("boardImg/".length());
				File deleteFile = new File(realPath + File.separator + without);
				
				deleteFile.delete(); // 파일 삭제
			}

			// 회원 가입 시 예외 발생 => 회원 가입 페이지로 이동
			bf.setRedirect(true);
			bf.setWhereToGo(request.getContextPath() + "/board/getModifyPost.bo?no=" + no);
			
			return bf;
		}
		
		// 수정 성공 => 게시글로 보낸다.
		bf.setRedirect(true);
		bf.setWhereToGo(request.getContextPath() + "/board/readPost.bo?no=" + no);
		
		return bf;
	}

	private UploadedFile makeFileName(FileItem f, String realPath, String file) {
		UploadedFile uf = null;
		
		String newFileName = "";
		String originalFileName = f.getName();
		String ext = originalFileName.substring(originalFileName.lastIndexOf("."));
		
		newFileName = file.substring("boardImg/".length());
		
		uf = new UploadedFile(originalFileName, ext, newFileName, f.getSize()); 
		
		return uf;
	}
	
	private UploadedFile makeFileNameByUUID(FileItem f, String realPath, String writer) {
		UploadedFile uf = null;
		
		UUID uuid = UUID.randomUUID();
		String originalFileName = f.getName();
		String ext = originalFileName.substring(originalFileName.lastIndexOf("."));
		String newFileName = "";

		if (f.getSize() > 0) {
			newFileName += writer + "_" + uuid + ext;
		}
		
		uf = new UploadedFile(originalFileName, ext, newFileName, f.getSize()); 
		
		return uf;
	}
	
	// 파일 만들기(Base64)
	private String makeImgToBase64String(String uploadedFile) {
		// 인코딩(파일 -> 문자열)
		String result = null;
		
		byte[] file;
		try {
			File upfile = new File(uploadedFile);
			
			file = FileUtils.readFileToByteArray(upfile);
			result = Base64.getEncoder().encodeToString(file);
//			System.out.println("base64 인코딩 : " + result);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
