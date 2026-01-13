package kr.java.aibe4_project2_team2_be.majormate.domain.interview.service;

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

import kr.java.aibe4_project2_team2_be.majormate.domain.interview.dto.request.InterviewFormCreateRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.dto.response.AppliedInterviewFormResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.dto.response.ReceivedInterviewFormResponse;
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
import kr.java.aibe4_project2_team2_be.majormate.global.exception.BusinessExceptionNew;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCodeNew;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InterviewService {

	private final InterviewFormRepository interviewFormRepository;
	private final InterviewStudentSnapshotRepository interviewStudentSnapshotRepository;
	private final InterviewMajorSnapshotRepository interviewMajorSnapshotRepository;

	private final MemberInfoReader memberInfoReader;

	public Page<AppliedInterviewFormResponse> getAppliedInterviewForms(Long requesterId, Pageable pageable) {
		Page<InterviewForm> page = interviewFormRepository.findByStudentMemberId(requesterId, pageable);

		List<InterviewForm> forms = page.getContent();
		if (forms.isEmpty()) {
			return Page.empty(pageable);
		}

		List<Long> interviewIds = extractInterviewIds(forms);

		Map<Long, InterviewMajorSnapshot> majorSnapshotMap = indexByInterviewId(
			interviewMajorSnapshotRepository.findByInterviewIdIn(interviewIds),
			InterviewMajorSnapshot::getInterviewId
		);

		List<AppliedInterviewFormResponse> content = forms.stream()
			.map(form -> AppliedInterviewFormResponse.fromSummary(
				getOrSnapshotMissing(majorSnapshotMap, form.getInterviewId()),
				form
			))
			.toList();

		return new PageImpl<>(content, pageable, page.getTotalElements());
	}

	public Page<AppliedInterviewFormResponse> getCompletedInterviewsWithoutReview(Long studentId,
		Pageable pageable) {
		Page<InterviewForm> page = interviewFormRepository.findCompletedWithoutReview(
			studentId, InterviewFormStatus.COMPLETED, pageable
		);

		if (page.isEmpty()) {
			return Page.empty(pageable);
		}

		List<InterviewForm> forms = page.getContent();
		List<Long> interviewIds = forms.stream()
			.map(InterviewForm::getInterviewId)
			.distinct()
			.toList();

		Map<Long, InterviewMajorSnapshot> majorSnapshotMap = interviewMajorSnapshotRepository.findByInterviewIdIn(
				interviewIds)
			.stream()
			.collect(Collectors.toMap(
				InterviewMajorSnapshot::getInterviewId,
				Function.identity(),
				(a, b) -> a
			));

		List<AppliedInterviewFormResponse> content = forms.stream()
			.map(form -> AppliedInterviewFormResponse.fromSummary(
				getOrSnapshotMissing(majorSnapshotMap, form.getInterviewId()),
				form
			))
			.toList();

		return new PageImpl<>(content, pageable, page.getTotalElements());
	}

	public Page<ReceivedInterviewFormResponse> getReceivedInterviewForms(Long majorId, Pageable pageable) {
		memberInfoReader.validateMajorRoleOrThrow(majorId);

		Page<InterviewForm> page = interviewFormRepository.findByMajorMemberId(majorId, pageable);

		List<InterviewForm> forms = page.getContent();
		if (forms.isEmpty()) {
			return Page.empty(pageable);
		}

		List<Long> interviewIds = extractInterviewIds(forms);

		Map<Long, InterviewStudentSnapshot> studentSnapshotMap = indexByInterviewId(
			interviewStudentSnapshotRepository.findByInterviewIdIn(interviewIds),
			InterviewStudentSnapshot::getInterviewId
		);

		List<ReceivedInterviewFormResponse> content = forms.stream()
			.map(form -> ReceivedInterviewFormResponse.fromSummary(
				getOrSnapshotMissing(studentSnapshotMap, form.getInterviewId()),
				form
			))
			.toList();

		return new PageImpl<>(content, pageable, page.getTotalElements());
	}

	public AppliedInterviewFormResponse getAppliedInterviewFormDetail(Long requesterId, Long interviewId) {
		InterviewForm form = interviewFormRepository.findById(interviewId)
			.orElseThrow(() -> new BusinessExceptionNew(ErrorCodeNew.INTERVIEW_404));

		if (!Objects.equals(form.getStudentMemberId(), requesterId)) {
			throw new BusinessExceptionNew(ErrorCodeNew.AUTH_403);
		}

		InterviewMajorSnapshot snapshot = interviewMajorSnapshotRepository.findById(interviewId)
			.orElseThrow(() -> new BusinessExceptionNew(ErrorCodeNew.INTERVIEW_500_SNAPSHOT_MISSING));

		return AppliedInterviewFormResponse.fromDetail(snapshot, form);
	}

	public ReceivedInterviewFormResponse getReceivedInterviewFormDetail(Long majorId, Long interviewId) {
		memberInfoReader.validateMajorRoleOrThrow(majorId);

		InterviewForm form = interviewFormRepository.findById(interviewId)
			.orElseThrow(() -> new BusinessExceptionNew(ErrorCodeNew.INTERVIEW_404));

		if (!Objects.equals(form.getMajorMemberId(), majorId)) {
			throw new BusinessExceptionNew(ErrorCodeNew.AUTH_403);
		}

		InterviewStudentSnapshot snapshot = interviewStudentSnapshotRepository.findById(interviewId)
			.orElseThrow(() -> new BusinessExceptionNew(ErrorCodeNew.INTERVIEW_500_SNAPSHOT_MISSING));

		return ReceivedInterviewFormResponse.fromDetail(snapshot, form);
	}

	@Transactional
	public AppliedInterviewFormResponse createInterviewForm(Long requesterId, Long targetMajorId,
		InterviewFormCreateRequest request) {

		validateCreateRequestOrThrow(requesterId, targetMajorId);

		MemberProfile requester = memberInfoReader.getProfileOrThrow(requesterId);
		MemberProfile targetMajor = memberInfoReader.getProfileOrThrow(targetMajorId);

		validateInterviewApplyRuleOrThrow(requester, targetMajor);

		validateReapplyAllowedOrThrow(requesterId, targetMajorId);

		MemberAcademic requesterAcademic = memberInfoReader.getAcademicOrNull(requesterId);
		MemberAcademic targetMajorAcademic = memberInfoReader.getAcademicOrNull(targetMajorId);

		validateMajorSnapshotRequiredOrThrow(targetMajor, targetMajorAcademic);

		InterviewForm saved = interviewFormRepository.save(InterviewForm.create(requesterId, targetMajorId, request));

		InterviewStudentSnapshot studentSnapshot = interviewStudentSnapshotRepository.save(
			InterviewStudentSnapshot.create(saved, requester, requesterAcademic)
		);

		InterviewMajorSnapshot majorSnapshot = interviewMajorSnapshotRepository.save(
			InterviewMajorSnapshot.create(saved, targetMajor, targetMajorAcademic)
		);

		return AppliedInterviewFormResponse.fromDetail(majorSnapshot, saved);
	}

	@Transactional
	public void accept(Long majorId, Long interviewId, String message) {
		memberInfoReader.validateMajorRoleOrThrow(majorId);

		InterviewForm form = interviewFormRepository.findById(interviewId)
			.orElseThrow(() -> new BusinessExceptionNew(ErrorCodeNew.INTERVIEW_404));

		if (!Objects.equals(form.getMajorMemberId(), majorId)) {
			throw new BusinessExceptionNew(ErrorCodeNew.AUTH_403);
		}
		if (form.getStatus() != InterviewFormStatus.PENDING) {
			throw new BusinessExceptionNew(ErrorCodeNew.INTERVIEW_400_INVALID_STATE);
		}

		form.accept(message);
	}

	@Transactional
	public void reject(Long majorId, Long interviewId, String message) {
		memberInfoReader.validateMajorRoleOrThrow(majorId);

		InterviewForm form = interviewFormRepository.findById(interviewId)
			.orElseThrow(() -> new BusinessExceptionNew(ErrorCodeNew.INTERVIEW_404));

		if (!Objects.equals(form.getMajorMemberId(), majorId)) {
			throw new BusinessExceptionNew(ErrorCodeNew.AUTH_403);
		}
		if (form.getStatus() != InterviewFormStatus.PENDING) {
			throw new BusinessExceptionNew(ErrorCodeNew.INTERVIEW_400_INVALID_STATE);
		}

		form.reject(message);
	}

	@Transactional
	public void complete(Long majorId, Long interviewId) {
		memberInfoReader.validateMajorRoleOrThrow(majorId);

		InterviewForm form = interviewFormRepository.findById(interviewId)
			.orElseThrow(() -> new BusinessExceptionNew(ErrorCodeNew.INTERVIEW_404));

		if (!Objects.equals(form.getMajorMemberId(), majorId)) {
			throw new BusinessExceptionNew(ErrorCodeNew.AUTH_403);
		}
		if (form.getStatus() != InterviewFormStatus.ACCEPTED) {
			throw new BusinessExceptionNew(ErrorCodeNew.INTERVIEW_400_INVALID_STATE);
		}

		form.complete();
	}

	private void validateCreateRequestOrThrow(Long requesterId, Long targetMajorId) {
		if (requesterId == null || targetMajorId == null) {
			throw new BusinessExceptionNew(ErrorCodeNew.COMMON_400);
		}
		if (Objects.equals(requesterId, targetMajorId)) {
			throw new BusinessExceptionNew(ErrorCodeNew.INTERVIEW_400_SELF_REQUEST_NOT_ALLOWED);
		}
	}

	private void validateInterviewApplyRuleOrThrow(MemberProfile requester, MemberProfile targetMajor) {
		if (targetMajor.getRole() != MemberRole.MAJOR) {
			throw new BusinessExceptionNew(ErrorCodeNew.INTERVIEW_400_TARGET_NOT_MAJOR);
		}

		if (requester.getRole() == MemberRole.ADMIN) {
			throw new BusinessExceptionNew(ErrorCodeNew.AUTH_403);
		}
	}

	private void validateReapplyAllowedOrThrow(Long requesterId, Long targetMajorId) {
		boolean hasActiveRequest = interviewFormRepository.existsByStudentMemberIdAndMajorMemberIdAndStatusIn(
			requesterId,
			targetMajorId,
			List.of(InterviewFormStatus.PENDING, InterviewFormStatus.ACCEPTED)
		);

		if (hasActiveRequest) {
			throw new BusinessExceptionNew(ErrorCodeNew.INTERVIEW_409_ALREADY_EXISTS);
		}
	}

	private void validateMajorSnapshotRequiredOrThrow(MemberProfile major, MemberAcademic academic) {
		if (major.getStatus() == null) {
			throw new BusinessExceptionNew(ErrorCodeNew.MAJOR_400_STATUS_REQUIRED);
		}
		if (academic == null || isBlank(academic.getUniversity()) || isBlank(academic.getMajor())) {
			throw new BusinessExceptionNew(ErrorCodeNew.MAJOR_400_ACADEMIC_REQUIRED);
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
			throw new BusinessExceptionNew(ErrorCodeNew.INTERVIEW_500_SNAPSHOT_MISSING);
		}
		return value;
	}

	private boolean isBlank(String value) {
		return value == null || value.isBlank();
	}
}
