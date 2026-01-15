package kr.java.aibe4_project2_team2_be.majormate.domain.admin.service;

import kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.response.AdminReviewDetailDto;
import kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.response.AdminReviewDto;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.InterviewForm;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.repository.InterviewFormRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberProfile;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.repository.MemberProfileRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.review.entity.Review;
import kr.java.aibe4_project2_team2_be.majormate.domain.review.repository.ReviewRepository;
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
public class AdminReviewService {

    private final ReviewRepository reviewRepository;
    private final InterviewFormRepository interviewFormRepository;
    private final MemberProfileRepository memberProfileRepository;

    // 1. 목록 조회 (DTO 변환 포함)
    public Page<AdminReviewDto> findAllReviews(Pageable pageable) {
        return reviewRepository.findAll(pageable)
                .map(review -> {
                    // 이름 찾기 로직 (중복되지만 일단 이해하기 쉽게 내부에 둠)
                    InterviewForm interview = interviewFormRepository.findById(review.getInterviewId()).orElse(null);
                    String sName = "알수없음";
                    String mName = "알수없음";

                    if (interview != null) {
                        sName = memberProfileRepository.findById(interview.getStudentMemberId())
                                .map(MemberProfile::getName).orElse("(탈퇴)");
                        mName = memberProfileRepository.findById(interview.getMajorMemberId())
                                .map(m -> m.getAcademic() != null ? m.getAcademic().getMajor() : "전공정보 없음")
                                .orElse("(탈퇴)");
                    }
                    return new AdminReviewDto(review, sName, mName);
                });
    }

    // 2. 상세 조회 (DTO 변환 포함)
    public AdminReviewDetailDto findReviewDetail(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.REVIEW_404));

        InterviewForm interview = interviewFormRepository.findById(review.getInterviewId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.INTERVIEW_404));

        String sName = memberProfileRepository.findById(interview.getStudentMemberId())
                .map(MemberProfile::getName).orElse("(탈퇴)");

        String mName = memberProfileRepository.findById(interview.getMajorMemberId())
                .map(m -> m.getAcademic() != null ? m.getAcademic().getMajor() : "전공정보 없음")
                .orElse("(탈퇴)");

        return new AdminReviewDetailDto(review, sName, mName);
    }

    // 3. 삭제
    @Transactional
    public void deleteReview(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new NotFoundException(ErrorCode.REVIEW_404);
        }
        reviewRepository.deleteById(reviewId);
    }
}