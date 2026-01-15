package kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.dto.request.InterviewFormCreateRequest;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.InterviewFormStatus;
import kr.java.aibe4_project2_team2_be.majormate.global.common.entity.BaseEntity;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.BusinessException;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	name = "interview_form",
	indexes = {
		@Index(name = "idx_if_student_created_at", columnList = "student_member_id, created_at"),
		@Index(name = "idx_if_student_status_created_at", columnList = "student_member_id, status, created_at"),
		@Index(name = "idx_if_major_created_at", columnList = "major_member_id, created_at"),
		@Index(name = "idx_if_major_status_created_at", columnList = "major_member_id, status, created_at"),
		@Index(name = "idx_if_student_major_status", columnList = "student_member_id, major_member_id, status")
	}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InterviewForm extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long interviewId;

	@Column(nullable = false)
	private Long studentMemberId;

	@Column(nullable = false)
	private Long majorMemberId;

	@Column(nullable = false, length = 255)
	private String title;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	@Column(nullable = false, length = 255)
	private String interviewMethod;

	@Column(nullable = false)
	private LocalDateTime preferredDatetime;

	@Column(columnDefinition = "TEXT")
	private String extraDescription;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private InterviewFormStatus status = InterviewFormStatus.PENDING;

	@Column(columnDefinition = "TEXT")
	private String majorMessage;

	@OneToOne(mappedBy = "interviewForm", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private InterviewStudentSnapshot studentSnapshot;

	@OneToOne(mappedBy = "interviewForm", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private InterviewMajorSnapshot majorSnapshot;

	private InterviewForm(
		Long studentMemberId, Long majorMemberId,
		String title, String content, String interviewMethod, LocalDateTime preferredDatetime, String extraDescription,
		InterviewFormStatus status, String majorMessage
	) {
		this.studentMemberId = studentMemberId;
		this.majorMemberId = majorMemberId;
		this.title = title;
		this.content = content;
		this.interviewMethod = interviewMethod;
		this.preferredDatetime = preferredDatetime;
		this.extraDescription = extraDescription;
		this.status = status;
		this.majorMessage = majorMessage;
	}

	public static InterviewForm create(Long studentId, Long majorId, InterviewFormCreateRequest request) {
		return new InterviewForm(
			studentId,
			majorId,
			request.title(),
			request.content(),
			request.interviewMethod(),
			request.preferredDatetime(),
			request.extraDescription(),
			InterviewFormStatus.PENDING,
			null
		);
	}

	public void attachMajorSnapshot(InterviewMajorSnapshot majorSnapshot) {
		this.majorSnapshot = majorSnapshot;
		if (majorSnapshot != null && majorSnapshot.getInterviewForm() != this) {
			majorSnapshot.attachInterviewForm(this);
		}
	}

	public void attachStudentSnapshot(InterviewStudentSnapshot studentSnapshot) {
		this.studentSnapshot = studentSnapshot;
		if (studentSnapshot != null && studentSnapshot.getInterviewForm() != this) {
			studentSnapshot.attachInterviewForm(this);
		}
	}

	public void accept(String majorMessage) {
		if (this.status != InterviewFormStatus.PENDING) {
			throw new BusinessException(ErrorCode.INTERVIEW_400_INVALID_STATE);
		}
		this.status = InterviewFormStatus.ACCEPTED;
		this.majorMessage = majorMessage;
	}

	public void reject(String majorMessage) {
		if (this.status != InterviewFormStatus.PENDING) {
			throw new BusinessException(ErrorCode.INTERVIEW_400_INVALID_STATE);
		}
		this.status = InterviewFormStatus.REJECTED;
		this.majorMessage = majorMessage;
	}

	public void complete() {
		if (this.status != InterviewFormStatus.ACCEPTED) {
			throw new BusinessException(ErrorCode.INTERVIEW_400_INVALID_STATE);
		}
		this.status = InterviewFormStatus.COMPLETED;
	}
}
