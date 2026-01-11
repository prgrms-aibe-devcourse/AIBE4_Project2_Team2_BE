package kr.java.aibe4_project2_team2_be.majormate.domain.review.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
import kr.java.aibe4_project2_team2_be.majormate.global.exception.BusinessExceptionNew;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCodeNew;
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

	public Page<WrittenReviewResponse> getWrittenReviews(Long memberId, Pageable pageable) {
		Page<Review> page = reviewRepository.findWrittenByStudent(memberId, pageable);
		List<Review> reviews = page.getContent();
		if (reviews.isEmpty()) {
			return Page.empty(pageable);
		}

		List<Long> interviewIds = extractInterviewIds(reviews);

		Map<Long, InterviewForm> formMap = indexByInterviewId(
			interviewFormRepository.findAllById(interviewIds),
			InterviewForm::getInterviewId
		);

		Map<Long, InterviewMajorSnapshot> majorSnapshotMap = indexByInterviewId(
			interviewMajorSnapshotRepository.findByInterviewIdIn(interviewIds),
			InterviewMajorSnapshot::getInterviewId
		);

		List<WrittenReviewResponse> content = reviews.stream()
			.map(r -> {
				Long interviewId = r.getInterviewId();
				InterviewForm form = getOrInternalSnapshotMissing(formMap, interviewId);
				InterviewMajorSnapshot snap = getOrInternalSnapshotMissing(majorSnapshotMap, interviewId);
				return WrittenReviewResponse.fromSummary(r, form, snap);
			})
			.toList();

		return new PageImpl<>(content, pageable, page.getTotalElements());
	}

	public WrittenReviewResponse getWrittenReviewDetail(Long memberId, Long interviewId) {
		InterviewForm form = getInterviewFormOrThrow(interviewId);
		validateWrittenOwnerOrThrow(memberId, form);

		Review review = getReviewByInterviewIdOrThrow(interviewId);
		InterviewMajorSnapshot majorSnapshot = getMajorSnapshotOrInternalError(interviewId);

		return WrittenReviewResponse.fromDetail(review, form, majorSnapshot);
	}

	public Page<ReceivedReviewResponse> getReceivedReviews(Long memberId, Pageable pageable) {
		memberInfoReader.validateMajorRoleOrThrow(memberId);

		Page<Review> page = reviewRepository.findReceivedByMajor(memberId, pageable);
		List<Review> reviews = page.getContent();
		if (reviews.isEmpty()) {
			return Page.empty(pageable);
		}

		List<Long> interviewIds = extractInterviewIds(reviews);

		Map<Long, InterviewForm> formMap = indexByInterviewId(
			interviewFormRepository.findAllById(interviewIds),
			InterviewForm::getInterviewId
		);

		Map<Long, InterviewStudentSnapshot> studentSnapshotMap = indexByInterviewId(
			interviewStudentSnapshotRepository.findByInterviewIdIn(interviewIds),
			InterviewStudentSnapshot::getInterviewId
		);

		List<ReceivedReviewResponse> content = reviews.stream()
			.map(r -> {
				Long interviewId = r.getInterviewId();
				InterviewForm form = getOrInternalSnapshotMissing(formMap, interviewId);
				InterviewStudentSnapshot snap = getOrInternalSnapshotMissing(studentSnapshotMap, interviewId);
				return ReceivedReviewResponse.fromSummary(r, form, snap);
			})
			.toList();

		return new PageImpl<>(content, pageable, page.getTotalElements());
	}

	public ReceivedReviewResponse getReceivedReviewDetail(Long memberId, Long interviewId) {
		memberInfoReader.validateMajorRoleOrThrow(memberId);

		InterviewForm form = getInterviewFormOrThrow(interviewId);
		validateReceivedOwnerOrThrow(memberId, form);

		Review review = getReviewByInterviewIdOrThrow(interviewId);
		InterviewStudentSnapshot studentSnapshot = getStudentSnapshotOrInternalError(interviewId);

		return ReceivedReviewResponse.fromDetail(review, form, studentSnapshot);
	}

	@Transactional
	public WrittenReviewResponse createReview(Long memberId, Long interviewId, ReviewRequest request) {
		InterviewForm form = getInterviewFormOrThrow(interviewId);

		validateWrittenOwnerOrThrow(memberId, form);
		validateInterviewCompletedOrThrow(form);
		validateReviewNotExistsOrThrow(interviewId);

		Review saved = reviewRepository.save(Review.create(interviewId, request.rating(), request.content()));
		InterviewMajorSnapshot majorSnapshot = getMajorSnapshotOrInternalError(interviewId);

		return WrittenReviewResponse.fromDetail(saved, form, majorSnapshot);
	}

	@Transactional
	public WrittenReviewResponse updateReview(Long memberId, Long interviewId, ReviewRequest request) {
		InterviewForm form = getInterviewFormOrThrow(interviewId);

		validateWrittenOwnerOrThrow(memberId, form);
		validateInterviewCompletedOrThrow(form);

		Review review = getReviewByInterviewIdOrThrow(interviewId);
		review.update(request.rating(), request.content());

		InterviewMajorSnapshot majorSnapshot = getMajorSnapshotOrInternalError(interviewId);

		return WrittenReviewResponse.fromDetail(review, form, majorSnapshot);
	}

	private InterviewForm getInterviewFormOrThrow(Long interviewId) {
		return interviewFormRepository.findById(interviewId)
			.orElseThrow(() -> new BusinessExceptionNew(ErrorCodeNew.INTERVIEW_404));
	}

	private Review getReviewByInterviewIdOrThrow(Long interviewId) {
		return reviewRepository.findByInterviewId(interviewId)
			.orElseThrow(() -> new BusinessExceptionNew(ErrorCodeNew.REVIEW_404));
	}

	private void validateWrittenOwnerOrThrow(Long memberId, InterviewForm form) {
		if (!Objects.equals(form.getStudentMemberId(), memberId)) {
			throw new BusinessExceptionNew(ErrorCodeNew.REVIEW_403_NOT_OWNER);
		}
	}

	private void validateReceivedOwnerOrThrow(Long memberId, InterviewForm form) {
		if (!Objects.equals(form.getMajorMemberId(), memberId)) {
			throw new BusinessExceptionNew(ErrorCodeNew.REVIEW_403_NOT_RECEIVER);
		}
	}

	private void validateInterviewCompletedOrThrow(InterviewForm form) {
		if (form.getStatus() != InterviewFormStatus.COMPLETED) {
			throw new BusinessExceptionNew(ErrorCodeNew.REVIEW_400_INTERVIEW_NOT_COMPLETED);
		}
	}

	private void validateReviewNotExistsOrThrow(Long interviewId) {
		if (reviewRepository.existsByInterviewId(interviewId)) {
			throw new BusinessExceptionNew(ErrorCodeNew.REVIEW_409_ALREADY_EXISTS);
		}
	}

	private InterviewMajorSnapshot getMajorSnapshotOrInternalError(Long interviewId) {
		return interviewMajorSnapshotRepository.findById(interviewId)
			.orElseThrow(() -> new BusinessExceptionNew(ErrorCodeNew.COMMON_500_SNAPSHOT_MISSING));
	}

	private InterviewStudentSnapshot getStudentSnapshotOrInternalError(Long interviewId) {
		return interviewStudentSnapshotRepository.findById(interviewId)
			.orElseThrow(() -> new BusinessExceptionNew(ErrorCodeNew.COMMON_500_SNAPSHOT_MISSING));
	}

	private List<Long> extractInterviewIds(List<Review> reviews) {
		return reviews.stream()
			.map(Review::getInterviewId)
			.distinct()
			.toList();
	}

	private <S> Map<Long, S> indexByInterviewId(List<S> items, Function<S, Long> idExtractor) {
		return items.stream()
			.collect(Collectors.toMap(
				idExtractor,
				Function.identity(),
				(a, b) -> a
			));
	}

	private <S> S getOrInternalSnapshotMissing(Map<Long, S> map, Long key) {
		S value = map.get(key);
		if (value == null) {
			throw new BusinessExceptionNew(ErrorCodeNew.COMMON_500_SNAPSHOT_MISSING);
		}
		return value;
	}
}
