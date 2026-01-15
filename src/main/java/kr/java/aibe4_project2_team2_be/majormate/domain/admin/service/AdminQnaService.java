package kr.java.aibe4_project2_team2_be.majormate.domain.admin.service;

import kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.response.AdminQuestionDetailDto;
import kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.response.AdminQuestionDto;
import kr.java.aibe4_project2_team2_be.majormate.domain.qna.entity.Answer;
import kr.java.aibe4_project2_team2_be.majormate.domain.qna.entity.Question;
import kr.java.aibe4_project2_team2_be.majormate.domain.qna.repository.AnswerRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.qna.repository.QuestionRepository;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.BusinessException;
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
    private final AnswerRepository answerRepository;

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

    // 답변 등록
    @Transactional
    public void createAnswer(Long questionId, String content) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QNA_404_QUESTION));

        // Answer 엔티티의 팩토리 메서드 사용 (검증 로직 및 연관관계 설정 포함됨)
        Answer answer = Answer.create(question, content);

        // Question에 CascadeType.ALL이 설정되어 있어 question을 저장해도 되지만,
        // 명시적으로 Answer를 저장합니다.
        answerRepository.save(answer);
    }
}