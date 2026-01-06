package kr.java.aibe4_project2_team2_be.majormate.global.common.service;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import io.awspring.cloud.s3.S3Exception;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@ConditionalOnProperty(name = "file.storage.type", havingValue = "s3")
@RequiredArgsConstructor
@Slf4j
public class S3FileService implements FileService{

	private static final List<String> ALLOWED_TYPES = List.of(
		"image/jpeg",
		"image/png"
	);

	private void validateFile(MultipartFile file) {
		if (file == null || file.isEmpty() || file.getSize() == 0) {
			throw new IllegalArgumentException("빈 파일은 업로드할 수 없습니다");
		}

		String contentType = file.getContentType();
		if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
			throw new IllegalArgumentException("허용되지 않는 파일 형식입니다. (허용: jpg, png)");
		}
	}

	private String extractExtension(String filename) {
		if (filename == null || !filename.contains(".")) {
			return "";
		}
		return filename.substring(filename.lastIndexOf("."));
	}

	// S3Client
	private final S3Client s3Client;


	@Value("${aws.s3.bucket}")
	private String bucket;

	@Override
	public String upload(MultipartFile file) {
		validateFile(file); // 파일을 검증
		String extension = extractExtension(file.getOriginalFilename());
		// 파일 확장자 추출
		String key = UUID.randomUUID() + extension;

		try {
			PutObjectRequest request = PutObjectRequest.builder()
				.bucket(bucket)
				.key(key)
				.contentType(file.getContentType())
				.contentLength(file.getSize())
				.build();

			s3Client.putObject(request,
				RequestBody.fromInputStream(
					file.getInputStream(),
					file.getSize()
				));

			// 전체 URL 반환
			URL url = s3Client.utilities().getUrl(GetUrlRequest.builder()
				.bucket(bucket)
				.key(key)
				.build());
			
			log.info("S3 업로드 성공. Key: {}, URL: {}", key, url.toString());
			return url.toString();

		} catch (IOException | S3Exception e) {
			throw new RuntimeException("S3 업로드 실패: " + e.getMessage(), e);
		}
	}

	@Override
	public void delete(String fileUrl) {
		if (!StringUtils.hasText(fileUrl)) {
			return;
		}
		try {
			// URL에서 Key 추출
			String key = extractKeyFromUrl(fileUrl);
			
			log.info("S3 삭제 요청. URL: {}, Extracted Key: {}", fileUrl, key);

			DeleteObjectRequest request = DeleteObjectRequest.builder()
				.bucket(bucket)
				.key(key)
				.build();
			s3Client.deleteObject(request);
			log.info("S3 삭제 성공. Key: {}", key);
		} catch (S3Exception e) {
			log.error("S3 삭제 실패: {}", e.getMessage());
		} catch (Exception e) {
			log.error("파일 삭제 중 오류 발생: {}", e.getMessage());
		}
	}

	private String extractKeyFromUrl(String fileUrl) {
		try {
			URL url = new URL(fileUrl);
			String path = url.getPath();
			// URL path는 보통 "/key" 형태이므로 앞의 "/" 제거
			String key = path.startsWith("/") ? path.substring(1) : path;
			
			// URL 디코딩 (한글 파일명 등 처리)
			String decodedKey = URLDecoder.decode(key, StandardCharsets.UTF_8);
			
			// Supabase 등 호환 스토리지 경로 처리
			// 예: storage/v1/s3/{bucket}/{key} -> {key}
			// 또는 /{bucket}/{key} -> {key}
			
			// 버킷 이름이 경로에 포함되어 있다면 그 뒤를 Key로 간주
			int bucketIndex = decodedKey.indexOf(bucket + "/");
			if (bucketIndex != -1) {
				return decodedKey.substring(bucketIndex + bucket.length() + 1);
			}
			
			return decodedKey;
		} catch (Exception e) {
			log.error("URL 파싱 실패: {}", fileUrl);
			// 파싱 실패 시 원본 문자열을 그대로 key로 사용 시도 (혹은 예외 던짐)
			return fileUrl;
		}
	}
}
