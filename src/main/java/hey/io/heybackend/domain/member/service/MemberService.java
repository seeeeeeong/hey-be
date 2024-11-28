package hey.io.heybackend.domain.member.service;

import hey.io.heybackend.common.util.NicknameUtil;
import hey.io.heybackend.domain.auth.service.AuthService;
import hey.io.heybackend.domain.member.dto.*;
import hey.io.heybackend.domain.member.dto.MemberInfoResDto.MemberInterestDto;
import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.member.entity.MemberInterest;
import hey.io.heybackend.domain.member.entity.SocialAccount;
import hey.io.heybackend.domain.member.enums.InterestCategory;
import hey.io.heybackend.domain.member.enums.Provider;
import hey.io.heybackend.domain.performance.enums.PerformanceGenre;
import hey.io.heybackend.domain.performance.enums.PerformanceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberCommandService memberCommandService;
    private final MemberQueryService memberQueryService;
    private final AuthService authService;

    private final NicknameUtil nicknameUtil;


    /**
     * <p>약관 동의</p>
     *
     * @param memberDto 인증된 사용자 정보
     * @param request
     * @return 회원 ID
     */
    @Transactional
    public Long modifyMemberTerms(MemberDto memberDto, MemberTermsReqDto request) {

        Member member = memberQueryService.getByMemberId(memberDto.getMemberId());
        memberCommandService.updateOptionalTermsAgreed(member, request.getBasicTermsAgreed());
        return member.getMemberId();
    }

    /**
     * <p>관심 정보</p>
     *
     * @param memberDto 인증된 사용자 정보
     * @param request
     * @return 회원 ID
     */
    @Transactional
    public Long createMemberInterest(MemberDto memberDto, MemberInterestReqDto request) {

        Member member = memberQueryService.getByMemberId(memberDto.getMemberId());

        List<MemberInterest> memberInterests = memberQueryService.getByMember(member);
        memberCommandService.deleteMemberInterests(memberInterests);

        List<MemberInterest> newMemberInterests = new ArrayList<>();

        if (request.getType() != null) {
            for (PerformanceType type : request.getType()) {
                MemberInterest memberInterest = MemberInterest.of(member, InterestCategory.TYPE.getCode(), type.getCode());
                newMemberInterests.add(memberInterest);
            }
        }

        if (request.getGenre() != null) {
            for (PerformanceGenre genre : request.getGenre()) {
                MemberInterest memberInterest = MemberInterest.of(member, InterestCategory.GENRE.getCode(), genre.getCode());
                newMemberInterests.add(memberInterest);
            }
        }
        memberCommandService.createMemberInterest(newMemberInterests);
        return member.getMemberId();

    }

    /**
     * <p>회원 정보 조회</p>
     *
     * @param memberDto 인증된 사용자 정보
     * @return 회원 정보
     */
    public MemberInfoResDto getMemberInfo(MemberDto memberDto) {

        Member member = memberQueryService.getByMemberId(memberDto.getMemberId());

        List<MemberInterest> memberInterests = memberQueryService.getByMember(member);

        List<PerformanceType> types = memberInterests.stream()
                .filter(interest -> interest.getInterestCategory().equals(InterestCategory.TYPE.getCode()))
                .map(interest -> PerformanceType.valueOf(interest.getInterestCode()))
                .collect(Collectors.toList());

        List<PerformanceGenre> genres = memberInterests.stream()
                .filter(interest -> interest.getInterestCategory().equals(InterestCategory.GENRE.getCode()))
                .map(interest -> PerformanceGenre.valueOf(interest.getInterestCode()))
                .collect(Collectors.toList());

        MemberInterestDto interests = MemberInterestDto.builder()
                .type(types)
                .genre(genres)
                .build();

        MemberInfoResDto memberInfoResDto = MemberInfoResDto.builder()
                .memberId(member.getMemberId())
                .nickname(member.getNickname())
                .accessedAt(member.getAccessedAt())
                .build();

        memberInfoResDto.setInterests(interests);

        return memberInfoResDto;
    }

    /**
     * <p>닉네임 중복 확인</p>
     *
     * @param nickname
     * @return 닉네임 중복 여부
     */
    public Boolean existsNickname(String nickname) {
        return memberQueryService.existsByNickname(nickname);
    }


    /**
     * <p>회원 정보 수정</p>
     *
     * @param memberDto 인증된 사용자 정보
     * @param request
     * @return 회원 ID
     */
    @Transactional
    public Long modifyMember(MemberDto memberDto, ModifyMemberReqDto request) {

        Member member = memberQueryService.getByMemberId(memberDto.getMemberId());

        member.updateNickname(request.getNickname());

        List<MemberInterest> memberInterests = memberQueryService.getByMember(member);
        memberCommandService.deleteMemberInterests(memberInterests);

        List<MemberInterest> newMemberInterests = new ArrayList<>();

        if (request.getType() != null) {
            for (PerformanceType type : request.getType()) {
                MemberInterest memberInterest = MemberInterest.of(member, InterestCategory.TYPE.getCode(), type.getCode());
                newMemberInterests.add(memberInterest);
            }
        }

        if (request.getGenre() != null) {
            for (PerformanceGenre genre : request.getGenre()) {
                MemberInterest memberInterest = MemberInterest.of(member, InterestCategory.GENRE.getCode(), genre.getCode());
                newMemberInterests.add(memberInterest);
            }
        }

        memberCommandService.createMemberInterest(newMemberInterests);
        return member.getMemberId();

    }

    /**
     * <p>회원 생성/업데이트</p>
     *
     * @param email
     * @param name
     * @param provider
     * @param providerUid
     * @return Member
     */
    @Transactional
    public Member insertOrUpdateMember(String email, String name, Provider provider, String providerUid) {
        Member member = memberQueryService.getByProviderUid(provider, providerUid);

        String nickname;
        do {
            nickname = nicknameUtil.generateNickname();
        } while (memberQueryService.existsByNickname(nickname));

        if (member == null) {
            Member newMember = memberCommandService.createMember(email, name, nickname);
            memberCommandService.createMemberPush(newMember);
            authService.insertUserAuth(newMember);
            return newMember;
        }
        memberCommandService.updateMember(member, email, name);
        return member;
    }

    /**
     * <p>소셜 정보 생성/업데이트</p>
     *
     * @param member
     * @param provider
     * @param providerUid
     */
    @Transactional
    public void insertOrUpdateSocialAccount(Member member, Provider provider, String providerUid) {
        SocialAccount socialAccount = memberQueryService.getByMemberAndProvider(member, provider);
        if (socialAccount == null) {
            memberCommandService.createSocialAccount(member, provider, providerUid);
        }
        memberCommandService.updateSocialAccount(socialAccount, providerUid);
    }

}
