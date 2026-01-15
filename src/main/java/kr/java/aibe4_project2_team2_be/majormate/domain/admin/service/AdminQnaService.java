package kr.java.aibe4_project2_team2_be.majormate.domain.admin.service;

import kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.response.AdminQuestionDetailDto;
import kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.response.AdminQuestionDto;
import kr.java.aibe4_project2_team2_be.majormate.domain.qna.entity.Question;
import kr.java.aibe4_project2_team2_be.majormate.domain.qna.repository.QuestionRepository;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCode;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.custom.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminQnaService {

    private final QuestionRepository questionRepository;
    // 1. 목록 조회
    public Page<AdminQuestionDto> findAllQuestions(Pageable pageable) {
        return questionRepository.findAll(pageable)
                .map(question -> {
                    // Question 엔티티 안에 이미 student, major 객체가 있음 (Lazy Loading 주의)
                    String studentName = (question.getStudent() != null) ? question.getStudent().getName() : "(탈퇴)";
                    String majorName = (question.getMajor() != null) ? question.getMajor().getName() : "(탈퇴)";

                    return new AdminQuestionDto(question, studentName, majorName);
                });
    }

    // 2. 상세 조회
    public AdminQuestionDetailDto findQuestionDetail(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.COMMON_404));

        String studentName = (question.getStudent() != null) ? question.getStudent().getName() : "(탈퇴)";
        String majorName = (question.getMajor() != null) ? question.getMajor().getName() : "(탈퇴)";

        return new AdminQuestionDetailDto(question, studentName, majorName);
    }

    // 3. 질문 삭제 (답변은 CascadeType.ALL 덕분에 자동 삭제됨)
    @Transactional
    public void deleteQuestion(Long questionId) {
        if (!questionRepository.existsById(questionId)) {
            throw new NotFoundException(ErrorCode.COMMON_404);
        }
        questionRepository.deleteById(questionId);
    }
}