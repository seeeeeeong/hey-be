package hey.io.heybackend.domain.member.repository;


import hey.io.heybackend.common.repository.Querydsl5RepositorySupport;
import hey.io.heybackend.domain.member.entity.Member;

import java.util.List;
import java.util.Optional;

import static hey.io.heybackend.domain.member.entity.QMember.member;
import static hey.io.heybackend.domain.system.entity.QToken.token;
import static hey.io.heybackend.domain.system.entity.QUserAuth.userAuth;


public class MemberQueryRepositoryImpl extends Querydsl5RepositorySupport implements MemberQueryRepository {

    public MemberQueryRepositoryImpl() {
        super(Member.class);
    }


    /**
     * <p>refreshToken을 가지는 Member 조회</p>
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

}