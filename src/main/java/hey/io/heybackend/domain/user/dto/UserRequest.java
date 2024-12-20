package hey.io.heybackend.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "사용자 정보")
public class UserRequest {

  @Schema(description = "사용자 email", defaultValue = "admin")
  @NotBlank(message = "이메일을 입력해주세요.")
  private String email;

  @Schema(description = "비밀번호", defaultValue = "1", format = "password")
  @NotBlank(message = "비밀번호를 입력해주세요.")
  private String password;
}
