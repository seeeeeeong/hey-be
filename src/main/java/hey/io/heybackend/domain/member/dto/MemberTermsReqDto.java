package hey.io.heybackend.domain.member.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberTermsReqDto {

    @NotNull
    private Boolean basicTermsAgreed;

}
