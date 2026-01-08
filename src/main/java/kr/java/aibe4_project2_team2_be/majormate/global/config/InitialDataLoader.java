package kr.java.aibe4_project2_team2_be.majormate.global.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberProfile;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.repository.MemberProfileRepository;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberRole;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 개발 환경에서 초기 데이터를 자동으로 로드하는 컴포넌트
 * Member 도메인 구현 후 활성화
 */
@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class InitialDataLoader implements CommandLineRunner {

	// TODO: Member 도메인 구현 후 주석 해제
	private final MemberProfileRepository memberProfileRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) {
		log.info("🚀 초기 데이터 로더 시작...");

		// TODO: Member 도메인 구현 후 아래 메서드 주석 해제
		createAdminIfNotExists();
		createTestUsersIfNotExists();

		log.info("✅ 초기 데이터 로더 준비 완료 (Member 도메인 구현 후 활성화 필요)");
	}

	// TODO: Member 도메인 구현 후 주석 해제

	private void createAdminIfNotExists() {
		String adminEmail = "admin@majormate.com";

		if (!memberProfileRepository.existsByEmail(adminEmail)) {
			MemberProfile admin = MemberProfile.builder()
				.email(adminEmail)
				.password(passwordEncoder.encode("admin1234!"))
				.username("admin")
				.name("관리자")
				.nickname("Admin")
				.status(MemberStatus.GRADUATED)
				.role(MemberRole.ADMIN)
				.build();

			memberProfileRepository.save(admin);
			log.info("✅ 관리자 계정 생성: {}", adminEmail);
		}
	}

	private void createTestUsersIfNotExists() {
		// 테스트 학생 계정
		if (!memberProfileRepository.existsByEmail("student@test.com")) {
			MemberProfile student = MemberProfile.builder()
				.email("student@test.com")
				.password(passwordEncoder.encode("test1234!"))
				.username("student")
				.name("테스트학생")
				.nickname("학생1")
				.status(MemberStatus.ENROLLED)
				.role(MemberRole.STUDENT)
				.build();

			memberProfileRepository.save(student);
			log.info("✅ 테스트 학생 계정 생성: student@test.com");
		}

		// 테스트 전공자 계정
		if (!memberProfileRepository.existsByEmail("major@test.com")) {
			MemberProfile major = MemberProfile.builder()
				.email("major@test.com")
				.password(passwordEncoder.encode("test1234!"))
				.username("major")
				.name("테스트전공자")
				.nickname("전공자1")
				.status(MemberStatus.ENROLLED)
				.role(MemberRole.MAJOR)
				.build();

			memberProfileRepository.save(major);
			log.info("✅ 테스트 전공자 계정 생성: major@test.com");
		}
	}
}
