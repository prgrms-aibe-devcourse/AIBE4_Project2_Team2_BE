package kr.java.aibe4_project2_team2_be.majormate.domain.auth.oauth2;

public interface OAuth2UserInfo {
    String getProviderId();
    String getProvider();
    String getEmail();
    String getName();
}
