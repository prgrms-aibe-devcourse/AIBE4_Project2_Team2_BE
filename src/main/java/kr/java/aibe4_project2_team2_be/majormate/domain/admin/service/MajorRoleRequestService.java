package kr.java.aibe4_project2_team2_be.majormate.domain.admin.service;

import jakarta.persistence.EntityNotFoundException;
import kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.request.RoleRequestCreateRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.response.RoleRequestDetailResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.response.RoleRequestResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.admin.entity.MajorRoleRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.admin.repository.MajorRoleRequestRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.Member;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberAcademic;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.repository.MemberAcademicRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.repository.MemberRepository;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.ApplicationStatus;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberRole;
import kr.java.aibe4_project2_team2_be.majormate.global.common.service.S3FileService;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.custom.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MajorRoleRequestService {

	private final MajorRoleRequestRepository majorRoleRequestRepository;
	private final MemberRepository memberRepository;
	private final S3FileService s3Service;
	private final MemberAcademicRepository memberAcademicRepository;

	// 1. 등록
	@Transactional
	public Long createRequest(Long memberId, RoleRequestCreateRequest requestDto, MultipartFile documentFile) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다"));

		MemberAcademic academic = memberAcademicRepository.findByMember(member)
			.orElseThrow(() -> new EntityNotFoundException("학적 정보를 찾을 수 없습니다. 먼저 학적 정보를 등록해주세요."));

		String documentUrl = s3Service.upload(documentFile);

		MajorRoleRequest request = MajorRoleRequest.createRequest(
			member,
			academic.getUniversity(),
			academic.getMajor(),
			requestDto.getContent(),
			documentUrl
		);
		return majorRoleRequestRepository.save(request).getRequestId();
	}

	// 2. 재제출
	@Transactional
	public void resubmitRequest(Long requestId, Long memberId, String newContent, MultipartFile newFile) {
		MajorRoleRequest request = majorRoleRequestRepository.findById(requestId)
			.orElseThrow(() -> new EntityNotFoundException("신청한 내용을 찾을 수 없습니다"));

		if (!request.getMember().getMemberId().equals(memberId)) {
			throw new ForbiddenException();
		}

		s3Service.delete(request.getDocumentUrl());
		String newUrl = s3Service.upload(newFile);

		request.resubmit(newContent, newUrl);
	}

	// 내 요청 목록 조회
	public List<RoleRequestResponse> getMyRequests(Long memberId) {
		return majorRoleRequestRepository.findAllByMember_MemberIdOrderByCreatedAtDesc(memberId).stream()
			.map(RoleRequestResponse::from)
			.collect(Collectors.toList());
	}

	// 상세 조회
	public RoleRequestDetailResponse MyGetRequestDetail(Long requestId, Long memberId) {
		MajorRoleRequest request = majorRoleRequestRepository.findById(requestId)
			.orElseThrow(() -> new EntityNotFoundException("신청한 내용을 찾을 수 없습니다"));

		// 본인 확인 (관리자 권한 체크 로직 추가 필요)
		if (!request.getMember().getMemberId().equals(memberId)) {
			// TODO: 관리자인 경우 통과시키는 로직 추가
			throw new ForbiddenException();
		}
		return RoleRequestDetailResponse.from(request);
	}


	// ==========================================
	//  ⬇️ [추가됨] 관리자 기능 구현 (누락된 부분)
	// ==========================================

	// 3. 관리자 - 요청 목록 조회 (대기중 & 재제출 상태만)
	public List<MajorRoleRequest> getPendingRequests() {
		return majorRoleRequestRepository.findByApplicationStatusInOrderByCreatedAtDesc(
			List.of(ApplicationStatus.PENDING, ApplicationStatus.RESUBMITTED)
		);
	}

	// 4. 관리자 - 요청 상세 조회
	public MajorRoleRequest getRequestDetail(Long requestId) {
		return majorRoleRequestRepository.findById(requestId)
			.orElseThrow(() -> new EntityNotFoundException("요청 정보를 찾을 수 없습니다."));
	}

	// 5. 관리자 - 승인
	@Transactional
	public void acceptRequest(Long requestId, Long adminId) {
		MajorRoleRequest request = getRequestDetail(requestId);
		Member admin = memberRepository.findById(adminId)
			.orElseThrow(() -> new EntityNotFoundException("관리자 정보를 찾을 수 없습니다."));

		// 요청 상태 변경 (APPROVED)
		request.accept(admin);

		// 학생의 권한을 전공자(MAJOR)로 변경
		request.getMember().updateRole(MemberRole.MAJOR);
	}

	// 6. 관리자 - 반려
	@Transactional
	public void rejectRequest(Long requestId, Long adminId, String reason) {
		MajorRoleRequest request = getRequestDetail(requestId);
		Member admin = memberRepository.findById(adminId)
			.orElseThrow(() -> new EntityNotFoundException("관리자 정보를 찾을 수 없습니다."));

		// 요청 상태 변경 (REJECTED) 및 사유 저장
		request.reject(admin, reason);
	}
}