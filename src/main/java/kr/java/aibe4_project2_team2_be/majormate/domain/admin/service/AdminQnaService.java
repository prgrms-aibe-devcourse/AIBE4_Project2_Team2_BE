package kr.java.aibe4_project2_team2_be.majormate.domain.admin.service;

import kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.response.AdminQuestionDetailDto;
import kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.response.AdminQuestionDto;
import kr.java.aibe4_project2_team2_be.majormate.domain.qna.entity.Question;
import kr.java.aibe4_project2_team2_be.majormate.domain.qna.repository.QuestionRepository;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.BusinessException;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCode;
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

    // 문의 목록 조회
    public Page<AdminQuestionDto> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable)
                .map(AdminQuestionDto::from);
    }

    // 문의 상세 조회
    public AdminQuestionDetailDto findById(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QNA_404_QUESTION));
        return AdminQuestionDetailDto.from(question);
    }

    // 문의 삭제 (관리자 권한)
    @Transactional
    public void deleteQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QNA_404_QUESTION));

        // Question 엔티티에 CascadeType.ALL이 걸려있으므로, 질문 삭제 시 답변도 함께 삭제됩니다.
        questionRepository.delete(question);
    }
}