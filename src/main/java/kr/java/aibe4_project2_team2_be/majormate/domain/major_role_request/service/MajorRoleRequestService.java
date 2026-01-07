package kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.entity.MajorRoleRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.repository.MajorRoleRequestRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.repository.MemberRepository;
import kr.java.aibe4_project2_team2_be.majormate.global.common.service.S3FileService;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.custom.ForbiddenException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MajorRoleRequestService {

	private final MajorRoleRequestRepository majorRoleRequestRepository;
	private final MemberRepository memberRepository;

	private final S3FileService s3Service;

	//등록

	@Transactional
	public Long createRequest(Long memberId, String content, MultipartFile documentFile) {

		// 파일 업로드
		String documentUrl = s3Service.upload(documentFile);

		MajorRoleRequest request = MajorRoleRequest.createRequest(member, content, documentUrl);
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

}
