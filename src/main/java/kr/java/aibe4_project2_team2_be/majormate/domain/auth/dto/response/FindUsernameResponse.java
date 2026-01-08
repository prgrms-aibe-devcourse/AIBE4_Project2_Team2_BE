package kr.java.aibe4_project2_team2_be.majormate.domain.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindUsernameResponse {

    private String username;
    private String maskedEmail;

    public static FindUsernameResponse of(String username, String email) {
        return FindUsernameResponse.builder()
                .username(username)
                .maskedEmail(maskEmail(email))
                .build();
    }

    private static String maskEmail(String email) {
        String[] parts = email.split("@");
        if (parts.length != 2) {
            return email;
        }

        String localPart = parts[0];
        String domain = parts[1];

        if (localPart.length() <= 2) {
            return localPart.charAt(0) + "***@" + domain;
        }

        String masked = localPart.substring(0, 2) + "***";
        return masked + "@" + domain;
    }
}
