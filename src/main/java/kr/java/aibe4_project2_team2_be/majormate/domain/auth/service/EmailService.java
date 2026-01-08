package kr.java.aibe4_project2_team2_be.majormate.domain.auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.entity.EmailVerification;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.entity.EmailVerification.VerificationType;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.repository.EmailVerificationRepository;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCode;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.custom.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailVerificationRepository emailVerificationRepository;

    private static final int CODE_LENGTH = 6;
    private static final int EXPIRATION_MINUTES = 5;

    /**
     * 인증 코드 생성 및 이메일 발송
     */
    @Transactional
    public void sendVerificationCode(String email, VerificationType type) {
        // 기존 인증 코드 삭제
        emailVerificationRepository.deleteByEmailAndType(email, type);

        // 인증 코드 생성
        String code = generateVerificationCode();

        // 인증 정보 저장
        EmailVerification verification = EmailVerification.builder()
                .email(email)
                .code(code)
                .type(type)
                .expiresAt(LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES))
                .build();

        emailVerificationRepository.save(verification);

        // 이메일 발송 (비동기)
        sendEmail(email, code, type);

        log.info("인증 코드 발송 완료 - Email: {}, Type: {}", email, type);
    }

    /**
     * 인증 코드 검증
     */
    @Transactional
    public void verifyCode(String email, String code, VerificationType type) {
        EmailVerification verification = emailVerificationRepository
                .findTopByEmailAndTypeOrderByCreatedAtDesc(email, type)
                .orElseThrow(() -> new BadRequestException(ErrorCode.VERIFICATION_CODE_NOT_FOUND));

        // 만료 확인
        if (verification.isExpired()) {
            throw new BadRequestException(ErrorCode.VERIFICATION_CODE_EXPIRED);
        }

        // 이미 인증됨
        if (verification.isVerified()) {
            throw new BadRequestException(ErrorCode.ALREADY_VERIFIED);
        }

        // 코드 일치 확인
        if (!verification.getCode().equals(code)) {
            throw new BadRequestException(ErrorCode.INVALID_VERIFICATION_CODE);
        }

        // 인증 처리
        verification.verify();
        log.info("이메일 인증 완료 - Email: {}, Type: {}", email, type);
    }

    /**
     * 인증 여부 확인
     */
    @Transactional(readOnly = true)
    public boolean isVerified(String email, VerificationType type) {
        return emailVerificationRepository
                .findTopByEmailAndTypeOrderByCreatedAtDesc(email, type)
                .map(EmailVerification::isVerified)
                .orElse(false);
    }

    /**
     * 6자리 랜덤 인증 코드 생성
     */
    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(1000000); // 0 ~ 999999
        return String.format("%06d", code); // 6자리로 포맷
    }

    /**
     * 이메일 발송 (비동기)
     */
    @Async
    public void sendEmail(String to, String code, VerificationType type) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(getEmailSubject(type));
            helper.setText(buildEmailContent(code, type), true);

            mailSender.send(message);
            log.info("이메일 발송 성공 - To: {}", to);
        } catch (MessagingException e) {
            log.error("이메일 발송 실패 - To: {}, Error: {}", to, e.getMessage());
            throw new BadRequestException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }

    /**
     * 이메일 제목 생성
     */
    private String getEmailSubject(VerificationType type) {
        return switch (type) {
            case SIGNUP -> "[MajorMate] 회원가입 이메일 인증";
            case FIND_USERNAME -> "[MajorMate] 아이디 찾기 인증";
            case RESET_PASSWORD -> "[MajorMate] 비밀번호 재설정 인증";
        };
    }

    /**
     * 이메일 본문 생성 (HTML)
     */
    private String buildEmailContent(String code, VerificationType type) {
        String purpose = switch (type) {
            case SIGNUP -> "회원가입";
            case FIND_USERNAME -> "아이디 찾기";
            case RESET_PASSWORD -> "비밀번호 재설정";
        };

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }
                        .content { background-color: #f9f9f9; padding: 30px; border-radius: 5px; margin-top: 20px; }
                        .code { font-size: 32px; font-weight: bold; color: #4CAF50; text-align: center; letter-spacing: 5px; margin: 20px 0; }
                        .footer { text-align: center; margin-top: 20px; font-size: 12px; color: #666; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>MajorMate</h1>
                        </div>
                        <div class="content">
                            <h2>%s 인증 코드</h2>
                            <p>안녕하세요, MajorMate입니다.</p>
                            <p>아래의 인증 코드를 입력하여 %s를 완료해주세요.</p>
                            <div class="code">%s</div>
                            <p>이 인증 코드는 <strong>%d분</strong> 동안 유효합니다.</p>
                            <p>본인이 요청하지 않은 경우, 이 이메일을 무시하셔도 됩니다.</p>
                        </div>
                        <div class="footer">
                            <p>본 메일은 발신 전용입니다.</p>
                            <p>&copy; 2026 MajorMate. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(purpose, purpose, code, EXPIRATION_MINUTES);
    }
}
