package kr.java.aibe4_project2_team2_be.majormate.domain.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CheckProviderResponse {
    private String provider;
}
