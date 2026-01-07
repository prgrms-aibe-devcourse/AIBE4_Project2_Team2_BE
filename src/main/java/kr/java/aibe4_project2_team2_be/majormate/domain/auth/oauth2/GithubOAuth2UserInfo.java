package kr.java.aibe4_project2_team2_be.majormate.domain.auth.oauth2;

import java.util.Map;

public class GithubOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;
    private String email; // Cache for fetched email

    public GithubOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.email = (String) attributes.get("email");
    }

    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getProvider() {
        return "github";
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getName() {
        String name = (String) attributes.get("name");
        return name != null ? name : (String) attributes.get("login");
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
