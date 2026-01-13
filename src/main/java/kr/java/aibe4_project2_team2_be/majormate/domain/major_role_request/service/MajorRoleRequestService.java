package kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.dto.request.RoleRequestCreateRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.dto.response.RoleRequestDetailResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.dto.response.RoleRequestResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.entity.MajorRoleRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.repository.MajorRoleRequestRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberAcademic;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberProfile;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.repository.MemberProfileRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.service.MemberInfoReader;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberStatus;
import kr.java.aibe4_project2_team2_be.majormate.global.common.service.S3FileService;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.BusinessExceptionNew;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCodeNew;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MajorRoleRequestService {

	private final MajorRoleRequestRepository majorRoleRequestRepository;
	private final S3FileService s3Service;
	private final MemberInfoReader memberInfoReader;

	// 1. 등록
	@Transactional
	public Long createRequest(Long memberId, RoleRequestCreateRequest requestDto, MultipartFile documentFile) {
		MemberProfile memberProfile = memberInfoReader.getProfileWithAcademicOrThrow(memberId);

		validateMajorRequestEligibilityOrThrow(memberProfile);

		MemberAcademic academic = memberProfile.getAcademic();
		if (academic == null) {
			memberInfoReader.createAcademicIfAbsent(memberProfile);
			academic = memberProfile.getAcademic();
		}

		validateMajorRequestAcademicOrThrow(academic);

		String documentUrl = s3Service.upload(documentFile);

		MajorRoleRequest request = MajorRoleRequest.createRequest(
			memberProfile,
			memberProfile.getAcademic().getUniversity(),
			memberProfile.getAcademic().getMajor(),
			requestDto.getContent(),
			documentUrl
		);
		return majorRoleRequestRepository.save(request).getRequestId();
	}

	private void validateMajorRequestEligibilityOrThrow(MemberProfile profile) {
		MemberStatus status = profile.getStatus();
		if (status == null) {
			throw new BusinessExceptionNew(ErrorCodeNew.MAJOR_REQUEST_400_STATUS_REQUIRED);
		}
		if (status != MemberStatus.ENROLLED && status != MemberStatus.GRADUATED) {
			throw new BusinessExceptionNew(ErrorCodeNew.MAJOR_REQUEST_400_INVALID_STATUS);
		}
	}

	private void validateMajorRequestAcademicOrThrow(MemberAcademic academic) {
		if (academic == null) {
			throw new BusinessExceptionNew(ErrorCodeNew.MAJOR_REQUEST_400_ACADEMIC_REQUIRED);
		}
		if (isBlank(academic.getUniversity()) || isBlank(academic.getMajor())) {
			throw new BusinessExceptionNew(ErrorCodeNew.MAJOR_REQUEST_400_ACADEMIC_REQUIRED);
		}
	}

	private boolean isBlank(String value) {
		return value == null || value.isBlank();
	}

	// 2. 재제출
	@Transactional
	public void resubmitRequest(Long requestId, Long memberId, String newContent, MultipartFile newFile) {
		MajorRoleRequest request = majorRoleRequestRepository.findById(requestId)
			.orElseThrow(() -> new EntityNotFoundException("신청한 내용을 찾을 수 없습니다"));

		if (!request.getMemberProfile().getMemberId().equals(memberId)) {
			throw new BusinessExceptionNew(ErrorCodeNew.MAJOR_ROLE_REQUEST_403_NOT_OWNER);
		}

		s3Service.delete(request.getDocumentUrl());
		String newUrl = s3Service.upload(newFile);

		request.resubmit(newContent, newUrl);
	}

	// 내 요청 목록 조회
	public List<RoleRequestResponse> getMyRequests(Long memberId) {
		return majorRoleRequestRepository.findAllByMemberProfile_MemberIdOrderByCreatedAtDesc(memberId).stream()
			.map(RoleRequestResponse::from)
			.collect(Collectors.toList());
	}

	// 상세 조회
	public RoleRequestDetailResponse MyGetRequestDetail(Long requestId, Long memberId) {
		MajorRoleRequest request = majorRoleRequestRepository.findById(requestId)
			.orElseThrow(() -> new EntityNotFoundException("신청한 내용을 찾을 수 없습니다"));

		// 본인 확인 (관리자 권한 체크 로직 추가 필요)
		if (!request.getMemberProfile().getMemberId().equals(memberId)) {
			// TODO: 관리자인 경우 통과시키는 로직 추가
			throw new BusinessExceptionNew(ErrorCodeNew.COMMON_403);
		}
		return RoleRequestDetailResponse.from(request);
	}
}
