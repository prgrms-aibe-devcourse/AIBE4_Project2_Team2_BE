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
import kr.java.aibe4_project2_team2_be.majormate.global.exception.BusinessException;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@Service
@ConditionalOnProperty(name = "file.storage.type", havingValue = "s3")
@RequiredArgsConstructor
public class S3FileService implements FileService {

	private static final List<String> ALLOWED_TYPES = List.of(
		"image/jpeg",
		"image/png"
	);

	private final S3Client s3Client;

	@Value("${aws.s3.bucket}")
	private String bucket;

	@Value("${file.storage.public-base-url:}")
	private String publicBaseUrl;

	@Override
	public String upload(MultipartFile file) {
		validateFile(file);

		String extension = extractExtension(file.getOriginalFilename());
		String key = UUID.randomUUID() + extension;

		try {
			PutObjectRequest request = PutObjectRequest.builder()
				.bucket(bucket)
				.key(key)
				.contentType(file.getContentType())
				.contentLength(file.getSize())
				.build();

			s3Client.putObject(
				request,
				RequestBody.fromInputStream(file.getInputStream(), file.getSize())
			);

			String url = buildPublicUrl(bucket, key);

			log.info("S3 업로드 성공. Key: {}, URL: {}", key, url);
			return url;

		} catch (IOException | S3Exception e) {
			throw new BusinessException(ErrorCode.FILE_500_UPLOAD_FAILED);
		}
	}

	@Override
	public void delete(String fileUrl) {
		if (!StringUtils.hasText(fileUrl)) {
			throw new BusinessException(ErrorCode.FILE_400_INVALID_FILE_URL);
		}

		String key = "[UNRESOLVED]";

		try {
			key = extractKeyFromUrl(fileUrl);
			if (!StringUtils.hasText(key) || key.equals(fileUrl)) {
				throw new BusinessException(ErrorCode.FILE_400_CANNOT_EXTRACT_FILE_KEY);
			}

			log.info("S3 삭제 요청. url={}, key={}", fileUrl, key);

			DeleteObjectRequest request = DeleteObjectRequest.builder()
				.bucket(bucket)
				.key(key)
				.build();

			s3Client.deleteObject(request);

			log.info("S3 삭제 성공. key={}", key);

		} catch (BusinessException e) {
			throw e;

		} catch (S3Exception e) {
			log.error("S3 삭제 실패. bucket={}, url={}, key={}", bucket, fileUrl, key, e);
			throw new BusinessException(ErrorCode.FILE_500_DELETE_FAILED);

		} catch (Exception e) {
			log.error("파일 삭제 중 오류. bucket={}, url={}, key={}", bucket, fileUrl, key, e);
			throw new BusinessException(ErrorCode.FILE_500_DELETE_FAILED);
		}
	}

	private void validateFile(MultipartFile file) {
		if (file == null || file.isEmpty() || file.getSize() == 0) {
			throw new BusinessException(ErrorCode.FILE_400_EMPTY_FILE);
		}

		String contentType = file.getContentType();
		if (!StringUtils.hasText(contentType) || !ALLOWED_TYPES.contains(contentType)) {
			throw new BusinessException(ErrorCode.FILE_400_UNSUPPORTED_CONTENT_TYPE);
		}
	}

	private String extractExtension(String filename) {
		if (!StringUtils.hasText(filename) || !filename.contains(".")) {
			return "";
		}
		return filename.substring(filename.lastIndexOf("."));
	}

	private String buildPublicUrl(String bucket, String key) {
		// 1) publicBaseUrl이 설정돼 있으면 그걸 우선 사용
		if (StringUtils.hasText(publicBaseUrl)) {
			String base = publicBaseUrl.trim();
			if (base.endsWith("/")) {
				base = base.substring(0, base.length() - 1);
			}
			return base + "/" + bucket + "/" + key;
		}

		// 2) fallback: SDK 유틸 URL(퍼블릭 버킷/정상 public endpoint일 때만 브라우저에서 바로 뜸)
		try {
			URL url = s3Client.utilities().getUrl(GetUrlRequest.builder()
				.bucket(bucket)
				.key(key)
				.build());
			return url.toString();
		} catch (Exception e) {
			log.warn("Public URL 생성 실패. bucket={}, key={}", bucket, key, e);
			return key;
		}
	}

	private String extractKeyFromUrl(String fileUrl) {
		try {
			URL url = new URL(fileUrl);
			String decodedPath = URLDecoder.decode(url.getPath(), StandardCharsets.UTF_8);

			// 예: /storage/v1/object/public/{bucket}/{key}
			int publicIdx = decodedPath.indexOf("/object/public/");
			if (publicIdx != -1) {
				String after = decodedPath.substring(publicIdx + "/object/public/".length());
				return stripBucketPrefix(after);
			}

			// 예: /storage/v1/s3/{bucket}/{key} 또는 /storage/v1/s3/file/{key}
			int s3Idx = decodedPath.indexOf("/s3/");
			if (s3Idx != -1) {
				String after = decodedPath.substring(s3Idx + "/s3/".length());
				return stripBucketPrefix(after);
			}

			// 일반적인 /{key}
			String path = decodedPath.startsWith("/") ? decodedPath.substring(1) : decodedPath;
			return stripBucketPrefix(path);

		} catch (Exception e) {
			log.error("URL 파싱 실패: {}", fileUrl, e);
			// 최후의 수단: 그냥 원문 반환(삭제 실패 가능)
			return fileUrl;
		}
	}

	private String stripBucketPrefix(String path) {
		if (!StringUtils.hasText(path)) {
			return path;
		}
		String p = path.startsWith("/") ? path.substring(1) : path;

		// {bucket}/{key} 형태면 key만 추출
		String prefix = bucket + "/";
		if (p.startsWith(prefix)) {
			return p.substring(prefix.length());
		}
		return p;
	}
}
