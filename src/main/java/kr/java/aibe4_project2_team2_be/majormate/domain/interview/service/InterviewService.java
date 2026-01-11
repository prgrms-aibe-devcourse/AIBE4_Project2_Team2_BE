package kr.java.aibe4_project2_team2_be.majormate.domain.interview.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

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
import kr.java.aibe4_project2_team2_be.majormate.domain.member.service.MemberInfoReader;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.InterviewFormStatus;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.BusinessException;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InterviewService {

	private final InterviewFormRepository interviewFormRepository;
	private final InterviewStudentSnapshotRepository interviewStudentSnapshotRepository;
	private final InterviewMajorSnapshotRepository interviewMajorSnapshotRepository;

	private final MemberInfoReader memberInfoReader;

	public List<AppliedInterviewFormResponse> getAppliedInterviewForms(Long studentId) {
		List<InterviewForm> forms = interviewFormRepository.findByStudentMemberIdOrderByCreatedAtDesc(studentId);

		if (forms.isEmpty()) {
			return List.of();
		}

		List<Long> interviewIds = extractInterviewIds(forms);

		Map<Long, InterviewMajorSnapshot> majorSnapshotMap = indexByInterviewId(
			interviewMajorSnapshotRepository.findByInterviewIdIn(interviewIds),
			InterviewMajorSnapshot::getInterviewId
		);

		return forms.stream()
			.map(form -> AppliedInterviewFormResponse.from(
				getOrInternalError(majorSnapshotMap, form.getInterviewId()), form
			)).toList();
	}

	public List<ReceivedInterviewFormResponse> getReceivedInterviewForms(Long majorId) {
		memberInfoReader.validateMajorRoleOrThrow(majorId);

		List<InterviewForm> forms = interviewFormRepository.findByMajorMemberIdOrderByCreatedAtAsc(majorId);

		if (forms.isEmpty()) {
			return List.of();
		}

		List<Long> interviewIds = extractInterviewIds(forms);

		Map<Long, InterviewStudentSnapshot> studentSnapshotMap = indexByInterviewId(
			interviewStudentSnapshotRepository.findByInterviewIdIn(interviewIds),
			InterviewStudentSnapshot::getInterviewId
		);

		return forms.stream()
			.map(form -> ReceivedInterviewFormResponse.from(
				getOrInternalError(studentSnapshotMap, form.getInterviewId()),
				form
			)).toList();
	}

	@Transactional
	public AppliedInterviewFormResponse createInterviewForm(
		Long studentId, Long majorId, InterviewFormCreateRequest request
	) {
		// validateCreateFormRequestOrThrow(studentId, majorId);
		//
		// memberInfoReader.validateMajorRoleOrThrow(majorId);
		// validateReapplyAllowedOrThrow(studentId, majorId);
		//
		// MemberProfile student = memberInfoReader.getProfileWithAcademicOrThrow(studentId);
		// MemberProfile major = memberInfoReader.getProfileWithAcademicOrThrow(majorId);
		//
		// MemberAcademic studentAcademic = memberInfoReader.createAcademic(studentId);
		// MemberAcademic majorAcademic = memberInfoReader.createAcademic(majorId);
		//
		// InterviewForm saved = interviewFormRepository.save(InterviewForm.create(studentId, majorId, request));
		//
		// interviewStudentSnapshotRepository.save(
		// 	InterviewStudentSnapshot.create(saved, student, studentAcademic)
		// );
		//
		// InterviewMajorSnapshot majorSnapshot = interviewMajorSnapshotRepository.save(
		// 	InterviewMajorSnapshot.create(saved, major, majorAcademic)
		// );
		//
		// return AppliedInterviewFormResponse.from(majorSnapshot, saved);

		return null;
	}

	private void validateCreateFormRequestOrThrow(Long studentId, Long majorId) {
		if (studentId == null || majorId == null) {
			throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
		}
		if (Objects.equals(studentId, majorId)) {
			throw new BusinessException(ErrorCode.INTERVIEW_SELF_REQUEST_NOT_ALLOWED);
		}
	}

	private void validateReapplyAllowedOrThrow(Long studentId, Long majorId) {
		boolean hasActiveRequest = interviewFormRepository.existsByStudentMemberIdAndMajorMemberIdAndStatusIn(
			studentId,
			majorId,
			List.of(InterviewFormStatus.PENDING, InterviewFormStatus.ACCEPTED)
		);

		if (hasActiveRequest) {
			throw new BusinessException(ErrorCode.APPLICATION_ALREADY_EXISTS);
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

	private <S> S getOrInternalError(Map<Long, S> map, Long key) {
		S value = map.get(key);
		if (value == null) {
			throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
		return value;
	}
}
