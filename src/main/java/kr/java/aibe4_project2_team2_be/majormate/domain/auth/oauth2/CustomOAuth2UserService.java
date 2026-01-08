package kr.java.aibe4_project2_team2_be.majormate.domain.auth.oauth2;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import kr.java.aibe4_project2_team2_be.majormate.domain.auth.entity.SocialAccount;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.repository.SocialAccountRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberProfile;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.repository.MemberProfileRepository;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.AuthProvider;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberRole;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberStatus;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCode;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.custom.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final MemberProfileRepository memberProfileRepository;
	private final SocialAccountRepository socialAccountRepository;
	private final RestTemplate restTemplate = new RestTemplate();

	@Override
	@Transactional
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oauth2User = super.loadUser(userRequest);

		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		Map<String, Object> attributes = oauth2User.getAttributes();

		// Get OAuth2 user info
		OAuth2UserInfo userInfo = getOAuth2UserInfo(registrationId, attributes);

		// For GitHub, fetch email from API if not provided
		if ("github".equalsIgnoreCase(registrationId) &&
			(userInfo.getEmail() == null || userInfo.getEmail().isEmpty())) {
			String email = fetchGithubEmail(userRequest);
			if (email != null && userInfo instanceof GithubOAuth2UserInfo) {
				((GithubOAuth2UserInfo)userInfo).setEmail(email);
			}
		}

		if (userInfo.getEmail() == null || userInfo.getEmail().isEmpty()) {
			throw new BadRequestException(ErrorCode.OAUTH2_EMAIL_NOT_FOUND);
		}

		// Find or create member
		MemberProfile memberProfile = findOrCreateMember(userInfo, registrationId);

		// Return custom OAuth2User with member ID
		return new CustomOAuth2User(memberProfile, attributes);
	}

    private OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId.toLowerCase()) {
            case "google" -> new GoogleOAuth2UserInfo(attributes);
            case "github" -> new GithubOAuth2UserInfo(attributes);
            case "kakao" -> new KakaoOAuth2UserInfo(attributes);
            case "naver" -> new NaverOAuth2UserInfo(attributes);
            default -> throw new BadRequestException(ErrorCode.OAUTH2_PROVIDER_NOT_SUPPORTED);
        };
    }

	private MemberProfile findOrCreateMember(OAuth2UserInfo userInfo, String registrationId) {
		AuthProvider provider = AuthProvider.valueOf(registrationId.toUpperCase());
		String email = userInfo.getEmail();
		String providerId = userInfo.getProviderId();

		// 1. Check if OAuth2 account already exists (by provider + providerId)
		SocialAccount socialAccount = socialAccountRepository.findByAuthProviderAndProviderId(provider, providerId)
			.orElse(null);

		if (socialAccount != null) {
			MemberProfile memberProfile = socialAccount.getMemberProfile();
			log.info("Existing OAuth2 user logged in - ID: {}, Provider: {}", memberProfile.getMemberId(), provider);
			return memberProfile;
		}

		// 2. Check if email already exists (account linking)
		MemberProfile memberProfile = memberProfileRepository.findByEmail(email).orElse(null);

		if (memberProfile != null) {
			// Link OAuth2 account to existing account
			log.info("Linking OAuth2 account to existing account - Email: {}, Provider: {}", email, provider);
			SocialAccount newSocialAccount = SocialAccount.builder()
				.memberProfile(memberProfile)
				.authProvider(provider)
				.providerId(providerId)
				.build();
			socialAccountRepository.save(newSocialAccount);
			return memberProfile;
		}

		// 3. Create new OAuth2 user
		return createOAuth2Member(userInfo, provider, providerId);
	}

	private MemberProfile createOAuth2Member(OAuth2UserInfo userInfo, AuthProvider provider, String providerId) {
		String email = userInfo.getEmail();
		String name = userInfo.getName();

		// Generate unique username (provider + random UUID)
		String username = generateUniqueUsername(provider.name().toLowerCase());

		// Use email prefix if name is not provided (max 20 characters)
		String emailPrefix = email.split("@")[0];
		String displayName = (name != null && !name.isEmpty()) ? name : emailPrefix;
		displayName = displayName.substring(0, Math.min(displayName.length(), 20));

		// Generate unique nickname from name or email
		String nickname = generateUniqueNickname(name != null ? name : emailPrefix);

		// Create new member without password
		MemberProfile newMemberProfile = MemberProfile.builder()
			.username(username)
			.email(email)
			.password(null)  // OAuth2 users don't have passwords
			.name(displayName) // Use displayName from HEAD
			.nickname(nickname)
			.status(MemberStatus.ENROLLED)
			.role(MemberRole.STUDENT)
			.build();

		MemberProfile savedMemberProfile = memberProfileRepository.save(newMemberProfile);

		// Create social account
		SocialAccount socialAccount = SocialAccount.builder()
			.memberProfile(savedMemberProfile)
			.authProvider(provider)
			.providerId(providerId)
			.build();
		socialAccountRepository.save(socialAccount);

		log.info("New OAuth2 user created - ID: {}, Email: {}, Provider: {}",
			savedMemberProfile.getMemberId(), email, provider);

		return savedMemberProfile;
	}

	private String generateUniqueUsername(String prefix) {
		String username;
		int attempts = 0;
		do {
			username = prefix + "_" + UUID.randomUUID().toString().substring(0, 8);
			attempts++;
		} while (memberProfileRepository.existsByUsername(username) && attempts < 10);

		if (attempts >= 10) {
			throw new BadRequestException(ErrorCode.DUPLICATE_USERNAME);
		}

		return username;
	}

    private String generateUniqueNickname(String baseName) {
        // Remove special characters and limit length
        String sanitized = baseName.replaceAll("[^가-힣a-zA-Z0-9]", "");
        sanitized = sanitized.substring(0, Math.min(sanitized.length(), 15)); // Reduced to 15 to allow for suffix

		if (sanitized.isEmpty()) {
			sanitized = "user";
		}

        // First try without suffix
        if (!memberProfileRepository.existsByNickname(sanitized)) { // Fixed to memberProfileRepository
            return sanitized;
        }

        // If duplicate, add random 4-character suffix
        int attempts = 0;
        String nickname;
        do {
            String randomSuffix = generateRandomString(4);
            nickname = sanitized + "_" + randomSuffix;
            attempts++;
        } while (memberProfileRepository.existsByNickname(nickname) && attempts < 100); // Fixed to memberProfileRepository

        if (attempts >= 100) {
            throw new BadRequestException(ErrorCode.DUPLICATE_NICKNAME);
        }

		return nickname;
	}

    private String generateRandomString(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

	private String fetchGithubEmail(OAuth2UserRequest userRequest) {
		try {
			String accessToken = userRequest.getAccessToken().getTokenValue();

			HttpHeaders headers = new HttpHeaders();
			headers.setBearerAuth(accessToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);

			ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
				"https://api.github.com/user/emails",
				HttpMethod.GET,
				entity,
				new ParameterizedTypeReference<List<Map<String, Object>>>() {
				}
			);

			if (response.getBody() != null) {
				// Find the primary verified email
				for (Map<String, Object> emailData : response.getBody()) {
					Boolean primary = (Boolean)emailData.get("primary");
					Boolean verified = (Boolean)emailData.get("verified");
					String email = (String)emailData.get("email");

					if (Boolean.TRUE.equals(primary) && Boolean.TRUE.equals(verified) && email != null) {
						log.info("Fetched GitHub primary email successfully");
						return email;
					}
				}

				// If no primary email, return the first verified email
				for (Map<String, Object> emailData : response.getBody()) {
					Boolean verified = (Boolean)emailData.get("verified");
					String email = (String)emailData.get("email");

					if (Boolean.TRUE.equals(verified) && email != null) {
						log.info("Fetched GitHub verified email successfully");
						return email;
					}
				}
			}

			log.warn("No verified email found in GitHub user emails");
			return null;
		} catch (Exception e) {
			log.error("Failed to fetch GitHub user emails", e);
			return null;
		}
	}
}
