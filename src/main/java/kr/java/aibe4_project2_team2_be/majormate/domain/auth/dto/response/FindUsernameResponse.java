package kr.java.aibe4_project2_team2_be.majormate.domain.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(Include.NON_NULL)
public class FindUsernameResponse {
    private String username;
    private String provider;
}
