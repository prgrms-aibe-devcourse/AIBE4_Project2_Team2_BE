package kr.java.aibe4_project2_team2_be.majormate.domain.interview.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.java.aibe4_project2_team2_be.majormate.domain.interview.dto.InterviewCreateRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.Interview;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.InterviewMajorSnapshot;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.InterviewStudentSnapshot;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.repository.InterviewMajorSnapshotRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.repository.InterviewRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.repository.InterviewStudentSnapshotRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.Member;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberAcademic;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.repository.MemberAcademicRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.repository.MemberRepository;
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

	private final MemberRepository memberRepository;
	private final MemberAcademicRepository memberAcademicRepository;

	@Transactional
	public void createInterview(Long studentId, Long majorId, InterviewCreateRequest request) {
		validateCreate(studentId, majorId);

		Member student = getMemberOrThrow(studentId);
		Member major = getMemberOrThrow(majorId);
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

	private Member getMemberOrThrow(Long memberId) {
		return memberRepository.findByMemberId(memberId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
	}

	private void validateMajorRoleOrThrow(Member major) {
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

	private MemberAcademic getAcademicOrThrow(Member member) {
		return memberAcademicRepository.findByMember(member)
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
		Member student,
		MemberAcademic studentAcademic,
		Member major,
		MemberAcademic majorAcademic
	) {
		InterviewStudentSnapshot studentSnapshot = buildStudentSnapshot(interview, student, studentAcademic);
		InterviewMajorSnapshot majorSnapshot = buildMajorSnapshot(interview, major, majorAcademic);

		interviewStudentSnapshotRepository.save(studentSnapshot);
		interviewMajorSnapshotRepository.save(majorSnapshot);
	}

	private InterviewStudentSnapshot buildStudentSnapshot(
		Interview interview,
		Member student,
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
		Member major,
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
