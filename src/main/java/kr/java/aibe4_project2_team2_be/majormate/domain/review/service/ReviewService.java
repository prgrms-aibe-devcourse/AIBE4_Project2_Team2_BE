package kr.java.aibe4_project2_team2_be.majormate.domain.review.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.Interview;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.InterviewMajorSnapshot;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.repository.InterviewMajorSnapshotRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.repository.InterviewRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.review.dto.ReviewCreateRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.review.dto.ReviewResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.review.entity.Review;
import kr.java.aibe4_project2_team2_be.majormate.domain.review.repository.ReviewRepository;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.InterviewStatus;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.BusinessException;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCode;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.custom.NotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final InterviewRepository interviewRepository;
	private final InterviewMajorSnapshotRepository interviewMajorSnapshotRepository;

	public List<ReviewResponse> getReviews(Long memberId) {
		validateMemberIdOrThrow(memberId);

		List<Long> interviewIds = findMyInterviewIdsOrThrow(memberId);
		List<Review> reviews = findReviewsOrThrow(interviewIds);

		List<Long> reviewInterviewIds = extractInterviewIds(reviews);

		Map<Long, Interview> interviewMap = loadInterviewMap(reviewInterviewIds);
		Map<Long, InterviewMajorSnapshot> majorSnapshotMap = loadMajorSnapshotMap(reviewInterviewIds);

		return toResponses(reviews, memberId, interviewMap, majorSnapshotMap);
	}

	@Transactional
	public void createReview(Long memberId, Long interviewId, ReviewCreateRequest request) {
		validateMemberIdOrThrow(memberId);

		Interview interview = interviewRepository.findById(interviewId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.INTERVIEW_NOT_FOUND));

		validateOwnerOrThrow(memberId, interview);
		validateInterviewCompletedOrThrow(interview);
		validateReviewNotExistsOrThrow(interviewId);

		Review review = Review.builder()
			.interviewId(interviewId)
			.rating(request.rating())
			.content(request.content())
			.build();

		reviewRepository.save(review);
	}

	private void validateMemberIdOrThrow(Long memberId) {
		if (memberId == null) {
			throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
		}
	}

	private List<Long> findMyInterviewIdsOrThrow(Long memberId) {
		List<Long> interviewIds = interviewRepository.findInterviewIdsByStudentMemberId(memberId);
		if (interviewIds.isEmpty()) {
			throw new NotFoundException(ErrorCode.REVIEW_NOT_FOUND);
		}
		return interviewIds;
	}

	private List<Review> findReviewsOrThrow(List<Long> interviewIds) {
		List<Review> reviews = reviewRepository.findByInterviewIdInOrderByCreatedAtDesc(interviewIds);
		if (reviews.isEmpty()) {
			throw new NotFoundException(ErrorCode.REVIEW_NOT_FOUND);
		}
		return reviews;
	}

	private List<Long> extractInterviewIds(List<Review> reviews) {
		return reviews.stream()
			.map(Review::getInterviewId)
			.distinct()
			.toList();
	}

	private Map<Long, Interview> loadInterviewMap(List<Long> interviewIds) {
		return interviewRepository.findAllById(interviewIds).stream()
			.collect(Collectors.toMap(Interview::getInterviewId, Function.identity()));
	}

	private Map<Long, InterviewMajorSnapshot> loadMajorSnapshotMap(List<Long> interviewIds) {
		return interviewMajorSnapshotRepository.findByInterviewIdIn(interviewIds).stream()
			.collect(Collectors.toMap(InterviewMajorSnapshot::getInterviewId, Function.identity()));
	}

	private List<ReviewResponse> toResponses(
		List<Review> reviews,
		Long memberId,
		Map<Long, Interview> interviewMap,
		Map<Long, InterviewMajorSnapshot> majorSnapshotMap
	) {
		return reviews.stream()
			.map(review -> toResponse(review, memberId, interviewMap, majorSnapshotMap))
			.toList();
	}

	private ReviewResponse toResponse(
		Review review,
		Long memberId,
		Map<Long, Interview> interviewMap,
		Map<Long, InterviewMajorSnapshot> majorSnapshotMap
	) {
		Long interviewId = review.getInterviewId();

		Interview interview = getInterviewOrThrow(interviewMap, interviewId);
		validateOwnerOrThrow(memberId, interview);

		InterviewMajorSnapshot majorSnapshot = getMajorSnapshotOrThrow(majorSnapshotMap, interviewId);

		return new ReviewResponse(
			review.getReviewId(),
			interviewId,
			new ReviewResponse.MajorSummary(
				majorSnapshot.getMajorProfileImageUrl(),
				majorSnapshot.getMajorNickname(),
				majorSnapshot.getMajorUniversity(),
				majorSnapshot.getMajorMajor()
			),
			review.getRating(),
			review.getContent(),
			review.getCreatedAt()
		);
	}

	private Interview getInterviewOrThrow(Map<Long, Interview> interviewMap, Long interviewId) {
		Interview interview = interviewMap.get(interviewId);
		if (interview == null) {
			throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
		return interview;
	}

	private InterviewMajorSnapshot getMajorSnapshotOrThrow(
		Map<Long, InterviewMajorSnapshot> majorSnapshotMap,
		Long interviewId
	) {
		InterviewMajorSnapshot snapshot = majorSnapshotMap.get(interviewId);
		if (snapshot == null) {
			throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
		return snapshot;
	}

	private void validateOwnerOrThrow(Long memberId, Interview interview) {
		if (interview.getStudentMemberId() == null || !memberId.equals(interview.getStudentMemberId())) {
			throw new BusinessException(ErrorCode.UNAUTHORIZED_REVIEW_ACCESS);
		}
	}

	private void validateInterviewCompletedOrThrow(Interview interview) {
		if (interview.getStatus() != InterviewStatus.COMPLETED) {
			throw new BusinessException(ErrorCode.INTERVIEW_CLOSED);
		}
	}

	private void validateReviewNotExistsOrThrow(Long interviewId) {
		if (reviewRepository.existsByInterviewId(interviewId)) {
			throw new BusinessException(ErrorCode.REVIEW_ALREADY_EXISTS);
		}
	}
}
