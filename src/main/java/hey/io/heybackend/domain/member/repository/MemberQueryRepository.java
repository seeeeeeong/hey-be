package hey.io.heybackend.domain.member.repository;


import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.member.enums.Provider;

import java.util.List;
import java.util.Optional;

public interface MemberQueryRepository {

    /**
     * <p>refreshToken으로 Member 조회</p>
     *
     * @param refreshToken
     * @return Optional<Member>
     */
    Optional<Member> findByRefreshToken(String refreshToken);

    /**
     * <p>플랫폼 정보로 Member 조회</p>
     *
     * @param provider
     * @param providerUid
     * @return Optional<Member>
     */
    Optional<Member> findByProviderUidAndProvider(Provider provider, String providerUid);

    /**
     * <p>사용자 권한 정보</p>
     *
     * @param memberId 사용자 ID
     * @return 사용자 권한 목록
     */
    List<String> selectUserAuthList(Long memberId);

    Optional<Member> selectMemberByProviderUid(String providerUid);

}
