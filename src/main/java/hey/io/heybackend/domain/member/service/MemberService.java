package hey.io.heybackend.domain.member.service;

import hey.io.heybackend.common.exception.BusinessException;
import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.exception.notfound.EntityNotFoundException;
import hey.io.heybackend.common.util.NicknameUtil;
import hey.io.heybackend.domain.auth.dto.AuthenticatedMember;
import hey.io.heybackend.domain.member.dto.*;
import hey.io.heybackend.domain.member.dto.MemberDto.MemberDetailResponse;
import hey.io.heybackend.domain.member.dto.MemberDto.MemberInterestRequest;
import hey.io.heybackend.domain.member.dto.MemberDto.MemberTermsRequest;
import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.member.entity.MemberInterest;
import hey.io.heybackend.domain.member.entity.MemberPush;
import hey.io.heybackend.domain.member.enums.InterestCategory;
import hey.io.heybackend.domain.member.enums.PushType;
import hey.io.heybackend.domain.member.repository.MemberInterestRepository;
import hey.io.heybackend.domain.member.repository.MemberPushRepository;
import hey.io.heybackend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final NicknameUtil nicknameUtil;

    private final MemberRepository memberRepository;
    private final MemberPushRepository memberPushRepository;
    private final MemberInterestRepository memberInterestRepository;

    /**
     * <p>약관 동의 수정</p>
     *
     * @param authenticatedMember 인증 회원 정보
     * @param memberTermsRequest 약관 동의 정보
     * @return 회원 ID
     */
    @Transactional
    public Long modifyMemberTerms(AuthenticatedMember authenticatedMember, MemberTermsRequest memberTermsRequest) {
        Member member = memberRepository.findById(authenticatedMember.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        member.updateOptionalTermsAgreed(memberTermsRequest.getBasicTermsAgreed());
        member.updateMemberStatus();
        return member.getMemberId();
    }

    /**
     * <p>관심 정보 등록</p>
     *
     * @param authenticatedMember 인증 회원 정보
     * @param memberInterestRequest 관심 정보
     * @return 회원 ID
     */
    @Transactional
    public Long createMemberInterest(AuthenticatedMember authenticatedMember, MemberInterestRequest memberInterestRequest) {

        Member member = memberRepository.findById(authenticatedMember.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        memberInterestRepository.deleteByMember(member);

        List<MemberInterest> newMemberInterests = memberInterestRequest.toMemberInterests(member);
        memberInterestRepository.saveAll(newMemberInterests);
        return member.getMemberId();

    }

    /**
     * <p>회원 상세</p>
     *
     * @param authenticatedMember 인증 회원 정보
     * @return 회원 상세 정보
     */
    public MemberDetailResponse getMemberInfo(AuthenticatedMember authenticatedMember) {

        MemberDetailResponse memberDetail = memberRepository.selectMemberDetail(authenticatedMember.getMemberId());

        if (memberDetail == null) {
            throw new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND);
        }

        List<String> typeList = memberRepository.selectMemberInterestList(InterestCategory.TYPE, authenticatedMember.getMemberId());
        List<String> genreList = memberRepository.selectMemberInterestList(InterestCategory.GENRE, authenticatedMember.getMemberId());

        MemberDetailResponse.MemberInterestDto interests = MemberDetailResponse.MemberInterestDto.of(typeList, genreList);

        return MemberDetailResponse.of(memberDetail, interests);
    }

    /**
     * <p>닉네임 중복 확인</p>
     *
     * @param nickname
     * @return 닉네임 중복 여부
     */
    public Boolean existsNickname(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    /**
     * <p>회원 정보 수정</p>
     *
     * @param authenticatedMember 인증 회원 정보
     * @param modifyMemberRequest 회원 정보
     * @return 회원 ID
     */
    @Transactional
    public Long modifyMember(AuthenticatedMember authenticatedMember, MemberDto.ModifyMemberRequest modifyMemberRequest) {

        Member member = memberRepository.findById(authenticatedMember.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        if (existsNickname(modifyMemberRequest.getNickname())) {
            throw new BusinessException(ErrorCode.DUPLICATED_NICKNAME);
        }
        member.updateNickname(modifyMemberRequest.getNickname());

        memberInterestRepository.deleteByMember(member);
        List<MemberInterest> newMemberInterests = modifyMemberRequest.toMemberInterests(member);
        memberInterestRepository.saveAll(newMemberInterests);

        return member.getMemberId();
    }


    public Optional<Member> getMemberByProviderUid(String providerUid) {
        return memberRepository.selectMemberByProviderUid(providerUid);
    }

    @Transactional
    public Member insertMember(String email, String name) {
        Member newMember = Member.of(email, name, getNickname());
        return memberRepository.save(newMember);
    }

    private String getNickname() {
        String nickname;
        do {
            nickname = nicknameUtil.generateNickname();
        } while (existsNickname(nickname));
        return nickname;
    }

    @Transactional
    public void insertMemberPush(Member member) {
        MemberPush memberPush = MemberPush.builder()
                .member(member)
                .pushType(PushType.PERFORMANCE)
                .pushEnabled(true)
                .build();
        memberPushRepository.save(memberPush);
    }

    public Member getByRefreshToken(String refreshToken) {
        return memberRepository.findByRefreshToken(refreshToken).orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
    }
}
