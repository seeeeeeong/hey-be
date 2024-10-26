package hey.io.heybackend.domain.oauth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import hey.io.heybackend.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OAuthUserProfile {

    private String oauthId;
    private String email;

}
