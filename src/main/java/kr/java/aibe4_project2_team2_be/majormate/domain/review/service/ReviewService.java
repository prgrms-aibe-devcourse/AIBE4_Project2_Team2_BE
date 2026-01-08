package kr.java.aibe4_project2_team2_be.majormate.domain.review.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.InterviewForm;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.InterviewMajorSnapshot;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.InterviewStudentSnapshot;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.repository.InterviewFormRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.repository.InterviewMajorSnapshotRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.repository.InterviewStudentSnapshotRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.service.MemberInfoReader;
import kr.java.aibe4_project2_team2_be.majormate.domain.review.dto.request.ReviewRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.review.dto.response.ReceivedReviewResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.review.dto.response.WrittenReviewResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.review.entity.Review;
import kr.java.aibe4_project2_team2_be.majormate.domain.review.repository.ReviewRepository;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.InterviewFormStatus;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.BusinessException;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCode;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.custom.NotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final InterviewFormRepository interviewFormRepository;
	private final InterviewStudentSnapshotRepository interviewStudentSnapshotRepository;
	private final InterviewMajorSnapshotRepository interviewMajorSnapshotRepository;

	private final MemberInfoReader memberInfoReader;

	public List<WrittenReviewResponse> getWrittenReviews(Long studentId) {
		List<Long> interviewIds = interviewFormRepository.findInterviewIdsByStudentMemberId(studentId);
		if (interviewIds.isEmpty()) {
			return List.of();
		}

		List<Review> reviews = reviewRepository.findByInterviewIdInOrderByCreatedAtDesc(interviewIds);
		if (reviews.isEmpty()) {
			return List.of();
		}

		List<Long> reviewInterviewIds = extractInterviewIds(reviews);

		Map<Long, InterviewForm> formMap = indexByInterviewId(
			interviewFormRepository.findAllById(reviewInterviewIds),
			InterviewForm::getInterviewId
		);

		Map<Long, InterviewMajorSnapshot> majorSnapshotMap = indexByInterviewId(
			interviewMajorSnapshotRepository.findByInterviewIdIn(reviewInterviewIds),
			InterviewMajorSnapshot::getInterviewId
		);

		return reviews.stream()
			.map(review -> {
				Long interviewId = review.getInterviewId();
				InterviewForm form = getOrInternalError(formMap, interviewId);
				InterviewMajorSnapshot snapshot = getOrInternalError(majorSnapshotMap, interviewId);
				return WrittenReviewResponse.from(review, form, snapshot);
			})
			.toList();
	}

	public List<ReceivedReviewResponse> getReceivedReviews(Long majorId) {
		memberInfoReader.validateMajorRoleOrThrow(majorId);

		List<Long> interviewIds = interviewFormRepository.findInterviewIdsByMajorMemberId(majorId);
		if (interviewIds.isEmpty()) {
			return List.of();
		}

		List<Review> reviews = reviewRepository.findByInterviewIdInOrderByCreatedAtDesc(interviewIds);
		if (reviews.isEmpty()) {
			return List.of();
		}

		List<Long> reviewInterviewIds = extractInterviewIds(reviews);

		Map<Long, InterviewStudentSnapshot> studentSnapshotMap = indexByInterviewId(
			interviewStudentSnapshotRepository.findByInterviewIdIn(reviewInterviewIds),
			InterviewStudentSnapshot::getInterviewId
		);

		return reviews.stream()
			.map(review -> ReceivedReviewResponse.from(
				review,
				getOrInternalError(studentSnapshotMap, review.getInterviewId())
			))
			.toList();
	}

	@Transactional
	public WrittenReviewResponse createReview(Long memberId, Long interviewId, ReviewRequest request) {
		InterviewForm interviewForm = getInterviewFormOrThrow(interviewId);

		validateOwnerOrThrow(memberId, interviewForm);
		validateInterviewCompletedOrThrow(interviewForm);
		validateReviewNotExistsOrThrow(interviewId);

		Review saved = reviewRepository.save(Review.create(interviewId, request.rating(), request.content()));

		InterviewMajorSnapshot majorSnapshot = getMajorSnapshotOrInternalError(interviewId);

		return WrittenReviewResponse.from(saved, interviewForm, majorSnapshot);
	}

	@Transactional
	public WrittenReviewResponse updateReview(Long memberId, Long interviewId, ReviewRequest request) {
		InterviewForm interviewForm = getInterviewFormOrThrow(interviewId);

		validateOwnerOrThrow(memberId, interviewForm);
		validateInterviewCompletedOrThrow(interviewForm);

		Review review = getReviewByInterviewIdOrThrow(interviewId);
		review.update(request.rating(), request.content());

		InterviewMajorSnapshot majorSnapshot = getMajorSnapshotOrInternalError(interviewId);

		return WrittenReviewResponse.from(review, interviewForm, majorSnapshot);
	}

	private InterviewForm getInterviewFormOrThrow(Long interviewId) {
		return interviewFormRepository.findById(interviewId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.INTERVIEW_NOT_FOUND));
	}

	private Review getReviewByInterviewIdOrThrow(Long interviewId) {
		return reviewRepository.findByInterviewId(interviewId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.REVIEW_NOT_FOUND));
	}

	private void validateOwnerOrThrow(Long memberId, InterviewForm interviewForm) {
		if (interviewForm.getStudentMemberId() == null || !memberId.equals(interviewForm.getStudentMemberId())) {
			throw new BusinessException(ErrorCode.UNAUTHORIZED_REVIEW_ACCESS);
		}
	}

	private void validateInterviewCompletedOrThrow(InterviewForm interviewForm) {
		if (interviewForm.getStatus() != InterviewFormStatus.COMPLETED) {
			throw new BusinessException(ErrorCode.INTERVIEW_CLOSED);
		}
	}

	private void validateReviewNotExistsOrThrow(Long interviewId) {
		if (reviewRepository.existsByInterviewId(interviewId)) {
			throw new BusinessException(ErrorCode.REVIEW_ALREADY_EXISTS);
		}
	}

	private InterviewMajorSnapshot getMajorSnapshotOrInternalError(Long interviewId) {
		return interviewMajorSnapshotRepository.findById(interviewId)
			.orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR));
	}

	private List<Long> extractInterviewIds(List<Review> reviews) {
		return reviews.stream()
			.map(Review::getInterviewId)
			.distinct()
			.toList();
	}

	private <S> Map<Long, S> indexByInterviewId(List<S> snapshots, Function<S, Long> idExtractor) {
		return snapshots.stream()
			.collect(Collectors.toMap(
				idExtractor,
				Function.identity(),
				(a, b) -> a
			));
	}

	private <S> S getOrInternalError(Map<Long, S> map, Long key) {
		S value = map.get(key);
		if (value == null) {
			throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
		return value;
	}
}
