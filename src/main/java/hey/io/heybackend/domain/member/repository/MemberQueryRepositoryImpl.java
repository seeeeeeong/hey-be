package hey.io.heybackend.domain.member.repository;


import hey.io.heybackend.common.repository.Querydsl5RepositorySupport;
import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.member.enums.Provider;

import java.util.List;
import java.util.Optional;

import static hey.io.heybackend.common.jwt.entity.QToken.token;
import static hey.io.heybackend.domain.auth.entity.QUserAuth.userAuth;
import static hey.io.heybackend.domain.member.entity.QMember.member;
import static hey.io.heybackend.domain.member.entity.QSocialAccount.socialAccount;


public class MemberQueryRepositoryImpl extends Querydsl5RepositorySupport implements MemberQueryRepository {

    public MemberQueryRepositoryImpl() {
        super(Member.class);
    }

    /**
     * <p>사용자 권한 정보</p>
     *
     * @param memberId 사용자 ID
     * @return 사용자 권한 목록
     */
    @Override
    public List<String> selectUserAuthList(Long memberId) {
        return select(userAuth.auth.authId)
            .from(userAuth)
            .where(userAuth.userId.eq(String.valueOf(memberId)))
            .fetch();
    }

    /**
     * <p>refreshToken으로 Member 조회</p>
     *
     * @param refreshToken
     * @return Optional<Member>
     */
    @Override
    public Optional<Member> findByRefreshToken(String refreshToken) {
        Member optionalMember = select(member)
                .from(member)
                .join(token).on(member.memberId.eq(token.memberId)).fetchJoin()
                .where(token.refreshToken.eq(refreshToken))
                .fetchFirst();

        return Optional.ofNullable(optionalMember);
    }


    /**
     * <p>플랫폼 정보로 Member 조회</p>
     *
     * @param provider
     * @param providerUid
     * @return Optional<Member>
     */
    @Override
    public Optional<Member> findByProviderUidAndProvider(Provider provider, String providerUid) {
        return Optional.ofNullable(
                selectFrom(member)
                        .join(socialAccount).on(member.memberId.eq(socialAccount.member.memberId))
                        .where(socialAccount.provider.eq(provider), socialAccount.providerUid.eq(providerUid))
                        .fetchOne()
        );
    }

    @Override
    public Optional<Member> selectMemberByProviderUid(String providerUid) {
        return Optional.ofNullable(
                selectFrom(member)
                        .join(socialAccount).on(member.memberId.eq(socialAccount.member.memberId))
                        .where(socialAccount.providerUid.eq(providerUid))
                        .fetchOne()
        );
    }
}