package kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.service;

import java.util.List;
import java.util.stream.Collectors;

import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.ApplicationStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.dto.response.RoleRequestDetailResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.Member;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.repository.MemberRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.dto.request.RoleRequestCreateRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.dto.response.RoleRequestResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.entity.MajorRoleRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.repository.MajorRoleRequestRepository;
import kr.java.aibe4_project2_team2_be.majormate.global.common.service.S3FileService;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.custom.ForbiddenException;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MajorRoleRequestService {

	private final MajorRoleRequestRepository majorRoleRequestRepository;
	private final MemberRepository memberRepository;

	private final S3FileService s3Service;

	//등록

	@Transactional
	public Long createRequest(Long memberId, RoleRequestCreateRequest requestDto, MultipartFile documentFile) {
		Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다"));

		// 파일 업로드
		String documentUrl = s3Service.upload(documentFile);

		MajorRoleRequest request = MajorRoleRequest.createRequest(
			member,
			requestDto.getUniversityName(),
			requestDto.getMajorName(),
			requestDto.getContent(),
			documentUrl
		);
		return majorRoleRequestRepository.save(request).getRequestId();
	}


	// TODO 승인, 반려 관리자


	// 반려 후 재제출
	@Transactional
	public void resubmitRequest(Long requestId, Long memberId, String newContent, MultipartFile newFile) {
		MajorRoleRequest request = majorRoleRequestRepository.findById(requestId)
			.orElseThrow(() -> new EntityNotFoundException("신청한 내용을 찾을 수 없습니다"));

		if (!request.getMember().getMemberId().equals(memberId)) {
			throw new ForbiddenException();
		}

		// 기존 파일 삭제
		s3Service.delete(request.getDocumentUrl());

		// 새 파일 업로드
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
	public RoleRequestDetailResponse getRequestDetail(Long requestId, Long memberId) {
		MajorRoleRequest request = majorRoleRequestRepository.findById(requestId)
			.orElseThrow(() -> new EntityNotFoundException("신청한 내용을 찾을 수 없습니다"));
		// 본인 확인 (관리자 권한 체크 로직 추가 필요)
		if (!request.getMember().getMemberId().equals(memberId)) {
			// TODO: 관리자인 경우 통과시키는 로직 추가
			throw new ForbiddenException();
		}
		return RoleRequestDetailResponse.from(request);
	}

    // 1. 관리자용 - 대기 중인 요청 목록 조회
    public List<MajorRoleRequest> getPendingRequests() {
        // PENDING(대기)과 RESUBMITTED(재제출) 상태인 요청을 모두 가져옴
        return majorRoleRequestRepository.findByApplicationStatusInOrderByCreatedAtDesc(
                List.of(ApplicationStatus.PENDING, ApplicationStatus.RESUBMITTED)
        );
    }

    // 2. 관리자용 - 요청 상세 조회
    public MajorRoleRequest getRequestDetail(Long requestId) {
        return majorRoleRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("요청을 찾을 수 없습니다."));
    }

    // 3. 관리자용 - 승인 처리
    @Transactional
    public void acceptRequest(Long requestId, Long adminId) {
        // 관리자 정보 조회
        Member admin = memberRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("관리자 정보를 찾을 수 없습니다."));

        // 요청 정보 조회
        MajorRoleRequest request = majorRoleRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("요청을 찾을 수 없습니다."));

        // A. 요청 상태 변경 (PENDING -> ACCEPTED) 및 이력 저장
        // 엔티티에 만들어두신 accept 메서드 활용!
        request.accept(admin);

        // B. [중요] 실제 회원의 등급(Role) 변경
        Member applicant = request.getMember();
        // Member 엔티티에 updateRole 같은 메서드가 필요합니다. (아래 참고)
        // applicant.upgradeToMajor();
	}

    // 4. 관리자용 - 반려 처리
    @Transactional
    public void rejectRequest(Long requestId, Long adminId, String rejectReason) {
        Member admin = memberRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("관리자 정보를 찾을 수 없습니다."));

        MajorRoleRequest request = majorRoleRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("요청을 찾을 수 없습니다."));

        // 엔티티에 만들어두신 reject 메서드 활용!
        request.reject(admin, rejectReason);
    }
}
