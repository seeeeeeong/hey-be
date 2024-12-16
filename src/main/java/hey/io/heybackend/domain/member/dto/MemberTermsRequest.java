package hey.io.heybackend.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@Schema(description = "약관 동의 여부")
public class MemberTermsRequest {

  @NotNull
  @Schema(description = "필수 약관 동의 여부", example = "true")
  private Boolean basicTermsAgreed;

}
