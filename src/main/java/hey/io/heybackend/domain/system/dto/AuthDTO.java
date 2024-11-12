package hey.io.heybackend.domain.system.dto;

import lombok.Getter;

@Getter
public class AuthDTO {

    private String authId; // 권한 ID
    private String upperAuthId; // 상위 권한 ID
    private String name; // 권한명
}
