package hey.io.heybackend.domain.user.dto;

import hey.io.heybackend.domain.member.enums.MemberStatus;
import hey.io.heybackend.domain.user.entity.Token;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "인증 토큰 정보")
public class TokenDto {

    @Schema(description = "사용자 ID", example = "1")
    private Long memberId;

    @Schema(description = "권한 위임 유형", defaultValue = "Bearer")
    private String grantType;

    @Schema(description = "액세스 토큰", example = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9")
    private String accessToken;

    @Schema(description = "리프레시 토큰", example = "n5zd87kVPy0SXxDQpjO7oBby7EYA3lxmZP")
    private String refreshToken;

    @Schema(description = "액세스 토큰 만료 일시", pattern = "yyyy-MM-dd HH:mi:ss", example = "2024-10-28 12:06:55")
    private String expiresIn;

    @Schema(description = "회원 상태", example = "ACTIVE")
    private MemberStatus memberStatus;

    public Token toToken() {
        return Token.builder()
            .memberId(this.memberId)
            .refreshToken(this.refreshToken)
            .build();
    }
}
