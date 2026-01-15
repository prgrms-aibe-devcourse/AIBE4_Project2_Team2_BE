package kr.java.aibe4_project2_team2_be.majormate.domain.admin.service;

import kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.response.AdminInterviewDetailDto;
import kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.response.AdminInterviewDto;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.InterviewForm;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.repository.InterviewFormRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberProfile;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.repository.MemberProfileRepository;
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
public class AdminInterviewService {

    private final InterviewFormRepository interviewFormRepository;
    private final MemberProfileRepository memberProfileRepository;

    // 1. 목록 조회
    public Page<AdminInterviewDto> findAllInterviews(Pageable pageable) {
        return interviewFormRepository.findAll(pageable)
                .map(interview -> {
                    String sName = getMemberName(interview.getStudentMemberId());
                    String mName = getMemberName(interview.getMajorMemberId());
                    return new AdminInterviewDto(interview, sName, mName);
                });
    }

    // 2. 상세 조회
    public AdminInterviewDetailDto findInterviewDetail(Long interviewId) {
        InterviewForm interview = interviewFormRepository.findById(interviewId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.INTERVIEW_404));

        String sName = getMemberName(interview.getStudentMemberId());
        String mName = getMemberName(interview.getMajorMemberId());

        return new AdminInterviewDetailDto(interview, sName, mName);
    }

    // 3. 삭제 (관리자 권한 강제 삭제)
    @Transactional
    public void deleteInterview(Long interviewId) {
        if (!interviewFormRepository.existsById(interviewId)) {
            throw new NotFoundException(ErrorCode.INTERVIEW_404);
        }
        interviewFormRepository.deleteById(interviewId);
    }

    // [헬퍼 메서드] 이름 가져오기 (중복 코드 제거)
    private String getMemberName(Long memberId) {
        return memberProfileRepository.findById(memberId)
                .map(MemberProfile::getName)
                .orElse("(탈퇴/알수없음)");
    }
}