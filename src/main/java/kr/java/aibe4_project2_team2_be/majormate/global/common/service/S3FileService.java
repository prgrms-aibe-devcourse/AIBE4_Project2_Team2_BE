package kr.java.aibe4_project2_team2_be.majormate.global.common.service;

import io.awspring.cloud.s3.S3Exception;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.BusinessException;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@ConditionalOnProperty(name = "file.storage.type", havingValue = "s3")
@RequiredArgsConstructor
public class S3FileService implements FileService {

    // 1. 허용할 MIME 타입 (중복 제거 및 명확화)
    private static final List<String> ALLOWED_MIME_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "application/pdf"
    );

    // 2. 허용할 확장자 (파일명 위변조 방지)
    private static final List<String> ALLOWED_EXTENSIONS = List.of(
            "jpg", "jpeg", "png", "pdf"
    );

    private final S3Client s3Client;
    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${file.storage.public-base-url:}")
    private String publicBaseUrl;

    @Override
    public String upload(MultipartFile file) {
        validateFile(file); // 강화된 검증 로직 실행

        String extension = extractExtension(file.getOriginalFilename());
        String key = UUID.randomUUID() + extension;

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType()) // 미리보기를 위해 필수
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

    //  MIME 타입과 확장자를 모두 체크
    private void validateFile(MultipartFile file) {
        // 1. 빈 파일 체크
        if (file == null || file.isEmpty() || file.getSize() == 0) {
            throw new BusinessException(ErrorCode.FILE_400_EMPTY_FILE);
        }

        // 2. MIME Type 체크
        String contentType = file.getContentType();
        if (!StringUtils.hasText(contentType) || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new BusinessException(ErrorCode.FILE_400_UNSUPPORTED_CONTENT_TYPE);
        }

        // 3. 확장자 체크
        String filename = file.getOriginalFilename();
        String extension = StringUtils.getFilenameExtension(filename);
        if (!StringUtils.hasText(extension) || !ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
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
        if (StringUtils.hasText(publicBaseUrl)) {
            String base = publicBaseUrl.trim();
            if (base.endsWith("/")) {
                base = base.substring(0, base.length() - 1);
            }
            return base + "/" + bucket + "/" + key;
        }

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

            int publicIdx = decodedPath.indexOf("/object/public/");
            if (publicIdx != -1) {
                String after = decodedPath.substring(publicIdx + "/object/public/".length());
                return stripBucketPrefix(after);
            }

            int s3Idx = decodedPath.indexOf("/s3/");
            if (s3Idx != -1) {
                String after = decodedPath.substring(s3Idx + "/s3/".length());
                return stripBucketPrefix(after);
            }

            String path = decodedPath.startsWith("/") ? decodedPath.substring(1) : decodedPath;
            return stripBucketPrefix(path);

        } catch (Exception e) {
            log.error("URL 파싱 실패: {}", fileUrl, e);
            return fileUrl;
        }
    }

    private String stripBucketPrefix(String path) {
        if (!StringUtils.hasText(path)) {
            return path;
        }
        String p = path.startsWith("/") ? path.substring(1) : path;
        String prefix = bucket + "/";
        if (p.startsWith(prefix)) {
            return p.substring(prefix.length());
        }
        return p;
    }
    public String transferFile(String sourceUrl, String newKey) {
        log.info("파일 이동 시작 (Stream Transfer): Source={}, Dest={}", sourceUrl, newKey);

        try {
            // 1. 원본 URL 연결
            URL url = new URL(sourceUrl);
            URLConnection connection = url.openConnection();

            // 2. 메타데이터 읽기
            long contentLength = connection.getContentLengthLong();
            String contentType = connection.getContentType();

            // 3. 스트림으로 읽으면서 동시에 S3 업로드 (메모리 효율적)
            try (InputStream inputStream = connection.getInputStream()) {
                PutObjectRequest.Builder putRequestBuilder = PutObjectRequest.builder()
                        .bucket(this.bucket)
                        .key(newKey);

                if (contentType != null) {
                    putRequestBuilder.contentType(contentType);
                }

                // 길이가 명확하면 스트림 전송, 모르면 바이트 배열 버퍼링
                RequestBody requestBody;
                if (contentLength > 0) {
                    putRequestBuilder.contentLength(contentLength);
                    requestBody = RequestBody.fromInputStream(inputStream, contentLength);
                } else {
                    byte[] bytes = inputStream.readAllBytes();
                    putRequestBuilder.contentLength((long) bytes.length);
                    requestBody = RequestBody.fromBytes(bytes);
                }

                s3Client.putObject(putRequestBuilder.build(), requestBody);
            }

            log.info("파일 이동 성공: {}", newKey);
            return buildPublicUrl(this.bucket, newKey);

        } catch (Exception e) {
            log.error("파일 이동 실패: {}", e.getMessage());
            // 원본 파일에 접근 불가하거나 업로드 실패 시 예외 처리
            throw new BusinessException(ErrorCode.FILE_500_UPLOAD_FAILED);
        }
    }
}