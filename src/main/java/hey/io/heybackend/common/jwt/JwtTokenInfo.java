package hey.io.heybackend.common.jwt;

import hey.io.heybackend.domain.member.enums.MemberStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
public class JwtTokenInfo {

    private final Long memberId;
//    private final MemberStatus memberStatus;

    @Builder
    public JwtTokenInfo(Long memberId, MemberStatus memberStatus) {
        this.memberId = memberId;
//        this.memberStatus = memberStatus;
    }

}
