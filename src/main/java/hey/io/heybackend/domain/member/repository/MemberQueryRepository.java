package hey.io.heybackend.domain.member.repository;


import hey.io.heybackend.domain.member.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberQueryRepository {

    Optional<Member> findByRefreshToken(String refreshToken);

    /**
     * <p>사용자 권한 정보</p>
     *
     * @param memberId 사용자 ID
     * @return 사용자 권한 목록
     */
    List<String> selectUserAuthList(Long memberId);

}
