package kr.java.aibe4_project2_team2_be.majormate.global.common.controller;

import kr.java.aibe4_project2_team2_be.majormate.global.common.service.S3FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/test/s3")
@RequiredArgsConstructor
public class S3TestController { // s3 테스트용 컨트롤러 추후에 삭제 예정

    private final S3FileService s3FileService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestPart("file") MultipartFile file) {
        String fileUrl = s3FileService.upload(file);
        return ResponseEntity.ok(fileUrl);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFile(@RequestParam("url") String fileUrl) {
        s3FileService.delete(fileUrl);
        return ResponseEntity.ok("파일이 삭제되었습니다: " + fileUrl);
    }
}
