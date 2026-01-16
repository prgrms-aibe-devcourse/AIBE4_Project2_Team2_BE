package kr.java.aibe4_project2_team2_be.majormate.domain.interview.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import kr.java.aibe4_project2_team2_be.majormate.domain.notification.dto.event.NotificationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.java.aibe4_project2_team2_be.majormate.domain.interview.dto.request.InterviewFormCreateRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.dto.request.InterviewStatusUpdateRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.dto.response.InterviewFormResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.InterviewForm;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.InterviewMajorSnapshot;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.InterviewStudentSnapshot;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.repository.InterviewFormRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.repository.InterviewMajorSnapshotRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.repository.InterviewStudentSnapshotRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberAcademic;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberProfile;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.service.MemberInfoReader;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.InterviewFormStatus;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberRole;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.PageSort;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.BusinessException;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InterviewFormService {

	private final InterviewFormRepository interviewFormRepository;
	private final InterviewStudentSnapshotRepository interviewStudentSnapshotRepository;
	private final InterviewMajorSnapshotRepository interviewMajorSnapshotRepository;

	private final MemberInfoReader memberInfoReader;
    private final ApplicationEventPublisher eventPublisher; // 알림기능 추가를 위한 이벤트 퍼블리셔 주입

	@Transactional(readOnly = true)
	public Page<InterviewFormResponse> getMyInterviewForms(
		Long memberId,
		InterviewFormResponse.ViewType type,
		InterviewFormStatus status,
		Boolean reviewed,
		PageSort sort,
		Pageable pageable
	) {
		Pageable sortedPageable = applySort(pageable, sort);

		if (type == InterviewFormResponse.ViewType.RECEIVED) {
			memberInfoReader.validateMajorRoleOrThrow(memberId);
			return getReceived(memberId, status, sortedPageable);
		}

		// APPLIED
		// reviewed=false는 "COMPLETED인데 리뷰 없음" 필터로만 사용하도록 제약
		if (reviewed != null) {
			if (!reviewed) {
				if (status != InterviewFormStatus.COMPLETED) {
					throw new BusinessException(ErrorCode.COMMON_400);
				}
				return getCompletedWithoutReview(memberId, sortedPageable);
			}
			throw new BusinessException(ErrorCode.COMMON_400);
		}

		return getApplied(memberId, status, sortedPageable);
	}

	@Transactional(readOnly = true)
	public InterviewFormResponse getMyInterviewFormDetail(Long memberId, Long interviewId) {
		InterviewForm form = interviewFormRepository.findById(interviewId)
			.orElseThrow(() -> new BusinessException(ErrorCode.INTERVIEW_404));

		if (Objects.equals(form.getStudentMemberId(), memberId)) {
			InterviewMajorSnapshot major = interviewMajorSnapshotRepository.findById(interviewId)
				.orElseThrow(() -> new BusinessException(ErrorCode.INTERVIEW_500_SNAPSHOT_MISSING));
			return InterviewFormResponse.appliedDetail(major, form);
		}

		if (Objects.equals(form.getMajorMemberId(), memberId)) {
			memberInfoReader.validateMajorRoleOrThrow(memberId);

			InterviewStudentSnapshot student = interviewStudentSnapshotRepository.findById(interviewId)
				.orElseThrow(() -> new BusinessException(ErrorCode.INTERVIEW_500_SNAPSHOT_MISSING));
			return InterviewFormResponse.receivedDetail(student, form);
		}

		throw new BusinessException(ErrorCode.AUTH_403);
	}

	@Transactional
	public InterviewFormResponse createInterviewForm(
		Long requesterId,
		Long targetMajorId,
		InterviewFormCreateRequest request
	) {
		validateCreateRequestOrThrow(requesterId, targetMajorId);

		MemberProfile requester = memberInfoReader.getProfileOrThrow(requesterId);
		MemberProfile targetMajor = memberInfoReader.getProfileOrThrow(targetMajorId);

		validateInterviewApplyRuleOrThrow(requester, targetMajor);
		validateReapplyAllowedOrThrow(requesterId, targetMajorId);

		MemberAcademic requesterAcademic = memberInfoReader.getAcademicOrNull(requesterId);
		MemberAcademic targetMajorAcademic = memberInfoReader.getAcademicOrNull(targetMajorId);

		validateMajorSnapshotRequiredOrThrow(targetMajor, targetMajorAcademic);

		InterviewForm saved = interviewFormRepository.save(InterviewForm.create(requesterId, targetMajorId, request));

		InterviewStudentSnapshot studentSnapshot = InterviewStudentSnapshot.create(saved, requester, requesterAcademic);
		InterviewMajorSnapshot majorSnapshot = InterviewMajorSnapshot.create(saved, targetMajor, targetMajorAcademic);

		saved.attachStudentSnapshot(studentSnapshot);
		saved.attachMajorSnapshot(majorSnapshot);

		// InterviewForm만 save해도 cascade로 snapshot 저장된다. (현재 매핑 기준)
		interviewFormRepository.save(saved);

        eventPublisher.publishEvent(new NotificationEvent(
                targetMajorId,          // 받는 사람 : 멘토
                requesterId,            // 보낸 사람 : 학생(신청자)
                "INTERVIEW_REQUEST",    // 타입
                "새로운 인터뷰 요청이 도착했습니다.", // 알림 내용
                "/major-profile?tab=interviews" // 클릭 시 이동할 URL
        ));

		return InterviewFormResponse.appliedDetail(majorSnapshot, saved);
	}

	@Transactional
	public void updateInterviewFormStatus(Long memberId, Long interviewId, InterviewStatusUpdateRequest request) {
		memberInfoReader.validateMajorRoleOrThrow(memberId);

		InterviewForm form = interviewFormRepository.findById(interviewId)
			.orElseThrow(() -> new BusinessException(ErrorCode.INTERVIEW_404));

		if (!Objects.equals(form.getMajorMemberId(), memberId)) {
			throw new BusinessException(ErrorCode.AUTH_403);
		}

		InterviewFormStatus target = request.status();
		String message = request.majorMessage();

		if (target == InterviewFormStatus.ACCEPTED) {
			if (isBlank(message)) {
				throw new BusinessException(ErrorCode.INTERVIEW_400_MESSAGE_REQUIRED);
			}
			form.accept(message);

            eventPublisher.publishEvent(new NotificationEvent(
                    form.getStudentMemberId(), // 받는 사람: 학생
                    memberId,                  // 보낸 사람: 멘토
                    "INTERVIEW_ACCEPTED",      // 타입
                    "인터뷰 요청이 수락되었습니다!", // 알림 내용
                    "/mypage?tab=applied" // 클릭 시 이동할 URL
            ));
			return;
		}

		if (target == InterviewFormStatus.REJECTED) {
			if (isBlank(message)) {
				throw new BusinessException(ErrorCode.INTERVIEW_400_MESSAGE_REQUIRED);
			}
			form.reject(message);

            eventPublisher.publishEvent(new NotificationEvent(
                    form.getStudentMemberId(), // 받는 사람: 학생
                    memberId,                  // 보낸 사람: 멘토
                    "INTERVIEW_REJECTED",      // 타입
                    "인터뷰 요청이 거절되었습니다.", // 알림 내용
                    "/mypage?tab=applied" // 클릭 시 이동할 URL
            ));
			return;
		}

		if (target == InterviewFormStatus.COMPLETED) {
			form.complete();

            eventPublisher.publishEvent(new NotificationEvent(
                    form.getStudentMemberId(), // 받는 사람: 학생
                    memberId,                  // 보낸 사람: 멘토
                    "INTERVIEW_COMPLETED",     // 타입
                    "인터뷰가 완료되었습니다. 리뷰를 작성해주세요!", // 알림 내용
                    "/mypage?tab=completed" // 클릭 시 이동할 URL
            ));
			return;
		}

		// PENDING 등으로의 변경은 허용하지 않음
		throw new BusinessException(ErrorCode.INTERVIEW_400_INVALID_STATE);
	}

	private Pageable applySort(Pageable pageable, PageSort sort) {
		Sort s = switch (sort) {
			case CREATED_AT_ASC -> Sort.by(Sort.Direction.ASC, "createdAt");
			case CREATED_AT_DESC -> Sort.by(Sort.Direction.DESC, "createdAt");
		};
		return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), s);
	}

	private Page<InterviewFormResponse> getApplied(Long studentId, InterviewFormStatus status, Pageable pageable) {
		Page<InterviewForm> page = (status == null)
			? interviewFormRepository.findByStudentMemberId(studentId, pageable)
			: interviewFormRepository.findByStudentMemberIdAndStatus(studentId, status, pageable);

		List<InterviewForm> forms = page.getContent();
		if (forms.isEmpty())
			return Page.empty(pageable);

		List<Long> interviewIds = extractInterviewIds(forms);

		Map<Long, InterviewMajorSnapshot> majorSnapshotMap = indexByInterviewId(
			interviewMajorSnapshotRepository.findByInterviewIdIn(interviewIds),
			InterviewMajorSnapshot::getInterviewId
		);

		List<InterviewFormResponse> content = forms.stream()
			.map(form -> InterviewFormResponse.appliedSummary(
				getOrSnapshotMissing(majorSnapshotMap, form.getInterviewId()),
				form
			))
			.toList();

		return new PageImpl<>(content, pageable, page.getTotalElements());
	}

	private Page<InterviewFormResponse> getCompletedWithoutReview(Long studentId, Pageable pageable) {
		Page<InterviewForm> page = interviewFormRepository.findCompletedWithoutReview(
			studentId, InterviewFormStatus.COMPLETED, pageable
		);

		if (page.isEmpty())
			return Page.empty(pageable);

		List<InterviewForm> forms = page.getContent();
		List<Long> interviewIds = extractInterviewIds(forms);

		Map<Long, InterviewMajorSnapshot> majorSnapshotMap = interviewMajorSnapshotRepository.findByInterviewIdIn(
				interviewIds)
			.stream()
			.collect(Collectors.toMap(
				InterviewMajorSnapshot::getInterviewId,
				Function.identity(),
				(a, b) -> a
			));

		List<InterviewFormResponse> content = forms.stream()
			.map(form -> InterviewFormResponse.appliedSummary(
				getOrSnapshotMissing(majorSnapshotMap, form.getInterviewId()),
				form
			))
			.toList();

		return new PageImpl<>(content, pageable, page.getTotalElements());
	}

	private Page<InterviewFormResponse> getReceived(Long majorId, InterviewFormStatus status, Pageable pageable) {
		Page<InterviewForm> page = (status == null)
			? interviewFormRepository.findByMajorMemberId(majorId, pageable)
			: interviewFormRepository.findByMajorMemberIdAndStatus(majorId, status, pageable);

		List<InterviewForm> forms = page.getContent();
		if (forms.isEmpty())
			return Page.empty(pageable);

		List<Long> interviewIds = extractInterviewIds(forms);

		Map<Long, InterviewStudentSnapshot> studentSnapshotMap = indexByInterviewId(
			interviewStudentSnapshotRepository.findByInterviewIdIn(interviewIds),
			InterviewStudentSnapshot::getInterviewId
		);

		List<InterviewFormResponse> content = forms.stream()
			.map(form -> InterviewFormResponse.receivedSummary(
				getOrSnapshotMissing(studentSnapshotMap, form.getInterviewId()),
				form
			))
			.toList();

		return new PageImpl<>(content, pageable, page.getTotalElements());
	}

	private void validateCreateRequestOrThrow(Long requesterId, Long targetMajorId) {
		if (requesterId == null || targetMajorId == null) {
			throw new BusinessException(ErrorCode.COMMON_400);
		}
		if (Objects.equals(requesterId, targetMajorId)) {
			throw new BusinessException(ErrorCode.INTERVIEW_400_SELF_REQUEST_NOT_ALLOWED);
		}
	}

	private void validateInterviewApplyRuleOrThrow(MemberProfile requester, MemberProfile targetMajor) {
		if (targetMajor.getRole() != MemberRole.MAJOR) {
			throw new BusinessException(ErrorCode.INTERVIEW_400_TARGET_NOT_MAJOR);
		}
		if (requester.getRole() == MemberRole.ADMIN) {
			throw new BusinessException(ErrorCode.AUTH_403);
		}
	}

	private void validateReapplyAllowedOrThrow(Long requesterId, Long targetMajorId) {
		boolean hasActiveRequest = interviewFormRepository.existsByStudentMemberIdAndMajorMemberIdAndStatusIn(
			requesterId,
			targetMajorId,
			List.of(InterviewFormStatus.PENDING, InterviewFormStatus.ACCEPTED)
		);

		if (hasActiveRequest) {
			throw new BusinessException(ErrorCode.INTERVIEW_409_ALREADY_EXISTS);
		}
	}

	private void validateMajorSnapshotRequiredOrThrow(MemberProfile major, MemberAcademic academic) {
		if (major.getStatus() == null) {
			throw new BusinessException(ErrorCode.MAJOR_400_STATUS_REQUIRED);
		}
		if (academic == null || isBlank(academic.getUniversity()) || isBlank(academic.getMajor())) {
			throw new BusinessException(ErrorCode.MAJOR_400_ACADEMIC_REQUIRED);
		}
	}

	private List<Long> extractInterviewIds(List<InterviewForm> forms) {
		return forms.stream()
			.map(InterviewForm::getInterviewId)
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

	private <S> S getOrSnapshotMissing(Map<Long, S> map, Long key) {
		S value = map.get(key);
		if (value == null) {
			throw new BusinessException(ErrorCode.INTERVIEW_500_SNAPSHOT_MISSING);
		}
		return value;
	}

	private boolean isBlank(String value) {
		return value == null || value.isBlank();
	}
}
