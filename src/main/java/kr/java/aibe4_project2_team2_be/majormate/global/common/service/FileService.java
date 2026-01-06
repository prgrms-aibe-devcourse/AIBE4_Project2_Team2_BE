package kr.java.aibe4_project2_team2_be.majormate.global.common.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
	String upload(MultipartFile file);
	void delete(String fileUrl);
}
