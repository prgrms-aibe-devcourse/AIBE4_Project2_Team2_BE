package kr.java.aibe4_project2_team2_be.majormate.domain.interview.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.java.aibe4_project2_team2_be.majormate.domain.interview.dto.InterviewCreateRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.dto.InterviewResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.Interview;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.InterviewMajorSnapshot;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.InterviewStudentSnapshot;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.repository.InterviewMajorSnapshotRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.repository.InterviewRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.repository.InterviewStudentSnapshotRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberAcademic;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberProfile;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.repository.MemberAcademicRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.repository.MemberProfileRepository;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.InterviewStatus;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberRole;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.BusinessException;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCode;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.custom.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InterviewService {

	private final InterviewRepository interviewRepository;
	private final InterviewStudentSnapshotRepository interviewStudentSnapshotRepository;
	private final InterviewMajorSnapshotRepository interviewMajorSnapshotRepository;

	private final MemberProfileRepository memberProfileRepository;
	private final MemberAcademicRepository memberAcademicRepository;

	public List<InterviewResponse> getInterviewRequests(Long studentId) {
		validateStudentId(studentId);

		List<Interview> interviews = findAppliedInterviewsOrThrow(studentId);

		Map<Long, InterviewMajorSnapshot> majorSnapshotMap = loadMajorSnapshotMap(interviews);

		return interviews.stream()
			.map(interview -> toResponse(interview,
					getMajorSnapshotOrThrow(majorSnapshotMap, interview.getInterviewId())
				)
			).toList();
	}

	private void validateStudentId(Long studentId) {
		if (studentId == null) {
			throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
		}
	}

	private List<Interview> findAppliedInterviewsOrThrow(Long studentId) {
		List<Interview> interviews = interviewRepository.findByStudentMemberIdOrderByCreatedAtDesc(studentId);
		if (interviews.isEmpty()) {
			throw new NotFoundException(ErrorCode.INTERVIEW_REQUEST_EMPTY);
		}
		return interviews;
	}

	private Map<Long, InterviewMajorSnapshot> loadMajorSnapshotMap(List<Interview> interviews) {
		List<Long> interviewIds = interviews.stream()
			.map(Interview::getInterviewId)
			.toList();

		return interviewMajorSnapshotRepository.findByInterviewIdIn(interviewIds)
			.stream()
			.collect(Collectors.toMap(
				InterviewMajorSnapshot::getInterviewId,
				s -> s,
				(a, b) -> a
			));
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

	private InterviewResponse toResponse(Interview interview, InterviewMajorSnapshot majorSnapshot) {
		if (majorSnapshot == null) {
			throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
		}

		return new InterviewResponse(
			new InterviewResponse.MajorSnapshot(
				majorSnapshot.getMajorProfileImageUrl(),
				majorSnapshot.getMajorNickname(),
				majorSnapshot.getMajorUniversity(),
				majorSnapshot.getMajorMajor()
			),
			new InterviewResponse.InterviewContent(
				interview.getTitle(),
				interview.getContent(),
				interview.getInterviewMethod(),
				interview.getPreferredDatetime(),
				interview.getExtraDescription()
			),
			interview.getStatus(),
			interview.getMajorMessage()
		);
	}

	@Transactional
	public void createInterview(Long studentId, Long majorId, InterviewCreateRequest request) {
		validateCreate(studentId, majorId);

		MemberProfile student = getMemberOrThrow(studentId);
		MemberProfile major = getMemberOrThrow(majorId);
		validateMajorRoleOrThrow(major);

		validateReapplyAllowedOrThrow(studentId, majorId);

		MemberAcademic studentAcademic = getAcademicOrThrow(student);
		MemberAcademic majorAcademic = getAcademicOrThrow(major);

		Interview saved = saveInterview(studentId, majorId, request);

		saveSnapshots(saved, student, studentAcademic, major, majorAcademic);
	}

	private void validateCreate(Long studentId, Long majorId) {
		if (studentId == null || majorId == null) {
			throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
		}
		if (Objects.equals(studentId, majorId)) {
			throw new BusinessException(ErrorCode.INTERVIEW_SELF_REQUEST_NOT_ALLOWED);
		}
	}

	private MemberProfile getMemberOrThrow(Long memberId) {
		return memberProfileRepository.findByMemberId(memberId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
	}

	private void validateMajorRoleOrThrow(MemberProfile major) {
		if (major.getRole() != MemberRole.MAJOR) {
			throw new BusinessException(ErrorCode.INTERVIEW_TARGET_NOT_MAJOR);
		}
	}

	private void validateReapplyAllowedOrThrow(Long studentId, Long majorId) {
		boolean existsNotCompleted = interviewRepository.existsByStudentMemberIdAndMajorMemberIdAndStatusNot(
			studentId,
			majorId,
			InterviewStatus.COMPLETED
		);

		if (existsNotCompleted) {
			throw new BusinessException(ErrorCode.APPLICATION_ALREADY_EXISTS);
		}
	}

	private MemberAcademic getAcademicOrThrow(MemberProfile memberProfile) {
		return memberAcademicRepository.findByMemberProfile(memberProfile)
			.orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_ACADEMIC_NOT_FOUND));
	}

	private Interview saveInterview(Long studentId, Long majorId, InterviewCreateRequest request) {
		Interview interview = Interview.builder()
			.studentMemberId(studentId)
			.majorMemberId(majorId)
			.title(request.title())
			.content(request.content())
			.interviewMethod(request.interviewMethod())
			.preferredDatetime(request.preferredDatetime())
			.extraDescription(request.extraDescription())
			.build();

		return interviewRepository.save(interview);
	}

	private void saveSnapshots(
		Interview interview,
		MemberProfile student,
		MemberAcademic studentAcademic,
		MemberProfile major,
		MemberAcademic majorAcademic
	) {
		InterviewStudentSnapshot studentSnapshot = buildStudentSnapshot(interview, student, studentAcademic);
		InterviewMajorSnapshot majorSnapshot = buildMajorSnapshot(interview, major, majorAcademic);

		interviewStudentSnapshotRepository.save(studentSnapshot);
		interviewMajorSnapshotRepository.save(majorSnapshot);
	}

	private InterviewStudentSnapshot buildStudentSnapshot(
		Interview interview,
		MemberProfile student,
		MemberAcademic academic
	) {
		return InterviewStudentSnapshot.builder()
			.interview(interview)
			.studentProfileImageUrl(student.getProfileImageUrl())
			.studentNickname(student.getNickname())
			.studentStatus(student.getStatus())
			.studentUniversity(academic.getUniversity())
			.studentMajor(academic.getMajor())
			.build();
	}

	private InterviewMajorSnapshot buildMajorSnapshot(
		Interview interview,
		MemberProfile major,
		MemberAcademic academic
	) {
		return InterviewMajorSnapshot.builder()
			.interview(interview)
			.majorProfileImageUrl(major.getProfileImageUrl())
			.majorNickname(major.getNickname())
			.majorStatus(major.getStatus())
			.majorUniversity(academic.getUniversity())
			.majorMajor(academic.getMajor())
			.build();
	}
}
