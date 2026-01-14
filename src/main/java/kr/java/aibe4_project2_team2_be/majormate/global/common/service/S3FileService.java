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
import java.net.URL;
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
    public String moveFile(String sourceUrl, String newKey) {
        log.info("파일 이동 시작: sourceUrl={}, destKey={}", sourceUrl, newKey);

        // 1. URL 파싱: 원본 버킷명과 파일 Key 분리
        // 예: .../public/request/file.jpg -> [request, file.jpg]
        String[] sourceInfo = parseSupabaseUrl(sourceUrl);
        String sourceBucket = sourceInfo[0];
        String sourceKey = sourceInfo[1];

        if (sourceBucket == null || sourceKey == null) {
            log.error("URL 파싱 실패 (Supabase 형식이 아님): {}", sourceUrl);
            throw new BusinessException(ErrorCode.FILE_400_INVALID_FILE_URL);
        }

        byte[] fileData;
        String contentType;

        // 2. [다운로드] 원본 파일 가져오기 (메모리에 로드)
        try {
            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(sourceBucket) // URL에서 추출한 원본 버킷
                    .key(sourceKey)       // URL에서 추출한 원본 키
                    .build();

            ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getRequest);
            fileData = s3Object.readAllBytes(); // 바이트 배열로 변환 (안정성 확보)
            contentType = s3Object.response().contentType();

            log.info("파일 다운로드 성공: bucket={}, key={}, size={}", sourceBucket, sourceKey, fileData.length);

        } catch (Exception e) {
            log.error("원본 파일 찾기 실패: bucket={}, key={}, error={}", sourceBucket, sourceKey, e.getMessage());
            throw new BusinessException(ErrorCode.FILE_400_INVALID_FILE_URL);
        }

        // 3. [업로드] 타겟 버킷(this.bucket = Major)으로 저장
        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(this.bucket) // application.yml에 설정된 타겟 버킷
                    .key(newKey)
                    .contentType(contentType) // 기존 Content-Type 유지 (미리보기 필수)
                    .contentLength((long) fileData.length)
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromBytes(fileData));
            log.info("파일 업로드 성공: bucket={}, key={}", this.bucket, newKey);

        } catch (Exception e) {
            log.error("새 위치 업로드 실패: {}", e.getMessage());
            throw new BusinessException(ErrorCode.FILE_500_UPLOAD_FAILED);
        }

        // 4. [삭제] 원본 파일 제거
        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(sourceBucket)
                    .key(sourceKey)
                    .build();
            s3Client.deleteObject(deleteRequest);
            log.info("원본 파일 삭제 완료");
        } catch (Exception e) {
            log.warn("원본 삭제 실패 (이동은 성공함): {}", sourceUrl);
        }

        // 5. 새 URL 반환
        return buildPublicUrl(this.bucket, newKey);
    }

    /**
     * Supabase Public URL 파싱 헬퍼
     * 반환값: String[] {버킷명, 파일경로}
     */
    private String[] parseSupabaseUrl(String url) {
        try {
            String decoded = URLDecoder.decode(url, StandardCharsets.UTF_8);

            // Supabase 패턴: .../storage/v1/object/public/{bucketName}/{filePath}
            String marker = "/object/public/";
            int idx = decoded.indexOf(marker);

            if (idx != -1) {
                // marker 이후의 문자열: "{bucketName}/{filePath}"
                String path = decoded.substring(idx + marker.length());

                int slashIdx = path.indexOf("/");
                if (slashIdx != -1) {
                    String bucketName = path.substring(0, slashIdx);
                    String fileKey = path.substring(slashIdx + 1);
                    return new String[]{bucketName, fileKey};
                }
            }

            // 패턴이 안 맞으면 기본 로직 시도 (현재 버킷이라고 가정)
            return new String[]{this.bucket, extractKeyFromUrl(url)};

        } catch (Exception e) {
            log.error("URL 파싱 에러: {}", e.getMessage());
            return new String[]{null, null};
        }
    }
}