package kr.java.aibe4_project2_team2_be.majormate.domain.request.service;

import java.nio.file.AccessDeniedException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.Member;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.repository.MemberRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.request.entity.MajorRoleRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.request.repository.MajorRoleRequestRepository;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.custom.ForbiddenException;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.custom.UnauthorizedException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MajorRoleRequestService {

	private final MajorRoleRequestRepository majorRoleRequestRepository;
	private final MemberRepository memberRepository;

	// TODO 파일 입출력 관련 클래스 구현
	// private final S3Service s3Service;


	//등록

	@Transactional
	public Long createRequest(Long memberId, String content, MultipartFile documentFile) {
		Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다"));

		// 파일 업로드
		// String documentUrl = s3Service.upload(documentFile);
		String documentUrl = "";

		MajorRoleRequest request = MajorRoleRequest.createRequest(member, content, documentUrl);
		return majorRoleRequestRepository.save(request).getId();
	}


	// TODO 승인, 반려 관리자


	// 반려 후 재제출
	@Transactional
	public void resubmitRequest(Long requestId, Long memberId, String newContent, MultipartFile newFile) {
		MajorRoleRequest request = majorRoleRequestRepository.findById(requestId)
			.orElseThrow(() -> new EntityNotFoundException("신청한 내용을 찾을 수 없습니다"));

		if (!request.getMember().getId().equals(memberId)) {
			throw new ForbiddenException();
		}

		// 새 파일 업로드
		// String newUrl = s3Service.upload(newFile);
		String newUrl = "";

		request.resubmit(newContent, newUrl);
	}

}
