package kr.java.aibe4_project2_team2_be.majormate.domain.review.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import kr.java.aibe4_project2_team2_be.majormate.domain.review.dto.response.PublicReviewResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.review.dto.response.ReviewResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.review.entity.Review;
import kr.java.aibe4_project2_team2_be.majormate.domain.review.repository.ReviewRepository;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.InterviewFormStatus;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.PageSort;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.BusinessException;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final InterviewFormRepository interviewFormRepository;
	private final InterviewStudentSnapshotRepository interviewStudentSnapshotRepository;
	private final InterviewMajorSnapshotRepository interviewMajorSnapshotRepository;

	private final MemberInfoReader memberInfoReader;

	@Transactional(readOnly = true)
	public Page<ReviewResponse> getMyReviews(
		Long memberId, ReviewResponse.ViewType type, PageSort sort, Pageable pageable
	) {
		Pageable sortedPageable = applySort(pageable, sort);

		if (type == ReviewResponse.ViewType.RECEIVED) {
			memberInfoReader.validateMajorRoleOrThrow(memberId);
			return getReceivedReviewsInternal(memberId, sortedPageable);
		}

		return getWrittenReviewsInternal(memberId, sortedPageable);
	}

	@Transactional(readOnly = true)
	public ReviewResponse getMyReviewDetail(Long memberId, Long reviewId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_404));

		Long interviewId = review.getInterviewId();
		InterviewForm form = getInterviewFormOrThrow(interviewId);

		boolean isWriter = Objects.equals(form.getStudentMemberId(), memberId);
		boolean isReceiver = Objects.equals(form.getMajorMemberId(), memberId);

		if (!isWriter && !isReceiver) {
			throw new BusinessException(ErrorCode.REVIEW_403_NOT_OWNER);
		}

		if (isWriter) {
			InterviewMajorSnapshot major = getMajorSnapshotOrInternalError(interviewId);
			return ReviewResponse.writtenDetail(review, form, major);
		}

		memberInfoReader.validateMajorRoleOrThrow(memberId);
		InterviewStudentSnapshot student = getStudentSnapshotOrInternalError(interviewId);
		return ReviewResponse.receivedDetail(review, form, student);
	}

	@Transactional(readOnly = true)
	public Page<PublicReviewResponse> getMajorPublicReceivedReviews(
		Long majorId, PageSort sort, Pageable pageable
	) {
		// 공개 프로필은 기본적으로 "전공자가 받은 후기(RECEIVED)"만 허용
		memberInfoReader.validateMajorRoleOrThrow(majorId);

		Pageable sortedPageable = applySort(pageable, sort);

		Page<Review> page = reviewRepository.findReceivedByMajor(majorId, sortedPageable);
		List<Review> reviews = page.getContent();
		if (reviews.isEmpty()) {
			return Page.empty(sortedPageable);
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

		List<PublicReviewResponse> content = reviews.stream()
			.map(r -> {
				Long interviewId = r.getInterviewId();
				InterviewForm form = getOrInternalSnapshotMissing(formMap, interviewId);
				InterviewStudentSnapshot student = getOrInternalSnapshotMissing(studentSnapshotMap, interviewId);
				return PublicReviewResponse.receivedSummary(r, form, student);
			})
			.toList();

		return new PageImpl<>(content, sortedPageable, page.getTotalElements());
	}

	@Transactional
	public ReviewResponse createReview(Long memberId, Long interviewId, ReviewRequest request) {
		InterviewForm form = getInterviewFormOrThrow(interviewId);

		validateWrittenOwnerOrThrow(memberId, form);
		validateInterviewCompletedOrThrow(form);
		validateReviewNotExistsOrThrow(interviewId);

		Review saved = reviewRepository.save(Review.create(interviewId, request.rating(), request.content()));
		InterviewMajorSnapshot major = getMajorSnapshotOrInternalError(interviewId);

		return ReviewResponse.writtenDetail(saved, form, major);
	}

	@Transactional
	public ReviewResponse updateReview(Long memberId, Long interviewId, ReviewRequest request) {
		InterviewForm form = getInterviewFormOrThrow(interviewId);

		validateWrittenOwnerOrThrow(memberId, form);
		validateInterviewCompletedOrThrow(form);

		Review review = getReviewByInterviewIdOrThrow(interviewId);
		review.update(request.rating(), request.content());

		InterviewMajorSnapshot major = getMajorSnapshotOrInternalError(interviewId);

		return ReviewResponse.writtenDetail(review, form, major);
	}

	private Pageable applySort(Pageable pageable, PageSort sort) {
		Sort s = switch (sort) {
			case CREATED_AT_ASC -> Sort.by(Sort.Direction.ASC, "createdAt");
			case CREATED_AT_DESC -> Sort.by(Sort.Direction.DESC, "createdAt");
		};
		return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), s);
	}

	private Page<ReviewResponse> getWrittenReviewsInternal(Long memberId, Pageable pageable) {
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

		List<ReviewResponse> content = reviews.stream()
			.map(r -> {
				Long interviewId = r.getInterviewId();
				InterviewForm form = getOrInternalSnapshotMissing(formMap, interviewId);
				InterviewMajorSnapshot major = getOrInternalSnapshotMissing(majorSnapshotMap, interviewId);
				return ReviewResponse.writtenSummary(r, form, major);
			})
			.toList();

		return new PageImpl<>(content, pageable, page.getTotalElements());
	}

	private Page<ReviewResponse> getReceivedReviewsInternal(Long memberId, Pageable pageable) {
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

		List<ReviewResponse> content = reviews.stream()
			.map(r -> {
				Long interviewId = r.getInterviewId();
				InterviewForm form = getOrInternalSnapshotMissing(formMap, interviewId);
				InterviewStudentSnapshot student = getOrInternalSnapshotMissing(studentSnapshotMap, interviewId);
				return ReviewResponse.receivedSummary(r, form, student);
			})
			.toList();

		return new PageImpl<>(content, pageable, page.getTotalElements());
	}

	private InterviewForm getInterviewFormOrThrow(Long interviewId) {
		return interviewFormRepository.findById(interviewId)
			.orElseThrow(() -> new BusinessException(ErrorCode.INTERVIEW_404));
	}

	private Review getReviewByInterviewIdOrThrow(Long interviewId) {
		return reviewRepository.findByInterviewId(interviewId)
			.orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_404));
	}

	private void validateWrittenOwnerOrThrow(Long memberId, InterviewForm form) {
		if (!Objects.equals(form.getStudentMemberId(), memberId)) {
			throw new BusinessException(ErrorCode.REVIEW_403_NOT_OWNER);
		}
	}

	private void validateInterviewCompletedOrThrow(InterviewForm form) {
		if (form.getStatus() != InterviewFormStatus.COMPLETED) {
			throw new BusinessException(ErrorCode.REVIEW_400_INTERVIEW_NOT_COMPLETED);
		}
	}

	private void validateReviewNotExistsOrThrow(Long interviewId) {
		if (reviewRepository.existsByInterviewId(interviewId)) {
			throw new BusinessException(ErrorCode.REVIEW_409_ALREADY_EXISTS);
		}
	}

	private InterviewMajorSnapshot getMajorSnapshotOrInternalError(Long interviewId) {
		return interviewMajorSnapshotRepository.findById(interviewId)
			.orElseThrow(() -> new BusinessException(ErrorCode.COMMON_500_SNAPSHOT_MISSING));
	}

	private InterviewStudentSnapshot getStudentSnapshotOrInternalError(Long interviewId) {
		return interviewStudentSnapshotRepository.findById(interviewId)
			.orElseThrow(() -> new BusinessException(ErrorCode.COMMON_500_SNAPSHOT_MISSING));
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
			throw new BusinessException(ErrorCode.COMMON_500_SNAPSHOT_MISSING);
		}
		return value;
	}
}
