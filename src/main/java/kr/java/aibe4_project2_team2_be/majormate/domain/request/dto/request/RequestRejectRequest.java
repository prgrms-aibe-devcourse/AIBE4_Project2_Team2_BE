package kr.java.aibe4_project2_team2_be.majormate.domain.request.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RequestRejectRequest {

    @NotBlank(message = "반려 사유는 필수입니다.")
    private String reason;
}