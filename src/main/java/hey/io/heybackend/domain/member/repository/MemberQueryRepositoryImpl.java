package hey.io.heybackend.domain.member.repository;


import hey.io.heybackend.common.repository.Querydsl5RepositorySupport;
import hey.io.heybackend.domain.member.dto.MemberDto;
import hey.io.heybackend.domain.member.dto.QMemberDto_MemberDetailResponse;
import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.member.enums.InterestCategory;

import java.util.List;
import java.util.Optional;

import static hey.io.heybackend.common.jwt.entity.QToken.token;
import static hey.io.heybackend.domain.auth.entity.QUserAuth.userAuth;
import static hey.io.heybackend.domain.member.entity.QMember.member;
import static hey.io.heybackend.domain.member.entity.QMemberInterest.memberInterest;
import static hey.io.heybackend.domain.member.entity.QSocialAccount.socialAccount;


public class MemberQueryRepositoryImpl extends Querydsl5RepositorySupport implements MemberQueryRepository {

    public MemberQueryRepositoryImpl() {
        super(Member.class);
    }

    /**
     * <p>회원 정보</p>
     *
     * @param providerUid
     * @return 회원 정보
     */
    @Override
    public Optional<Member> selectMemberByProviderUid(String providerUid) {
        return Optional.ofNullable(
                selectFrom(member)
                        .join(socialAccount).on(member.memberId.eq(socialAccount.member.memberId))
                        .where(socialAccount.providerUid.eq(providerUid))
                        .fetchOne()
        );
    }

    /**
     * <p>회원 정보</p>
     *
     * @param refreshToken refresh 토큰
     * @return 회원 정보
     */
    @Override
    public Optional<Member> selectMemberByRefreshToken(String refreshToken) {
        Member optionalMember = select(member)
                .from(member)
                .join(token).on(member.memberId.eq(token.memberId)).fetchJoin()
                .where(token.refreshToken.eq(refreshToken))
                .fetchFirst();

        return Optional.ofNullable(optionalMember);
    }

    /**
     * <p>회원 상세</p>
     *
     * @param memberId 회원 ID
     * @return 회원 상세 정보
     */
    @Override
    public MemberDto.MemberDetailResponse selectMemberDetail(Long memberId) {
        return select(new QMemberDto_MemberDetailResponse(
                member.memberId,
                member.nickname,
                member.accessedAt
        )).from(member)
                .where(member.memberId.eq(memberId))
                .fetchOne();
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
     * <p>회원 관심 목록</p>
     *
     * @param category 카테고리
     * @param memberId 회원 ID
     * @return 회원 관심 목록 정보
     */
    @Override
    public List<String> selectMemberInterestList(InterestCategory category, Long memberId) {
        return select(memberInterest.interestCode)
                .from(memberInterest)
                .where(memberInterest.interestCategory.eq(category), memberInterest.member.memberId.eq(memberId))
                .fetch();
    }

}