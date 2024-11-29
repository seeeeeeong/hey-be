package hey.io.heybackend.domain.member.service;

import hey.io.heybackend.common.exception.BusinessException;
import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.exception.notfound.EntityNotFoundException;
import hey.io.heybackend.common.util.NicknameUtil;
import hey.io.heybackend.domain.auth.dto.AuthenticatedMember;
import hey.io.heybackend.domain.member.dto.*;
import hey.io.heybackend.domain.member.dto.MemberDto.MemberDetailResponse;
import hey.io.heybackend.domain.member.dto.MemberDto.MemberDetailResponse.MemberInterestDto;
import hey.io.heybackend.domain.member.dto.MemberDto.MemberInterestRequest;
import hey.io.heybackend.domain.member.dto.MemberDto.MemberTermsRequest;
import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.member.entity.MemberInterest;
import hey.io.heybackend.domain.member.enums.InterestCategory;
import hey.io.heybackend.domain.member.repository.MemberInterestRepository;
import hey.io.heybackend.domain.member.repository.MemberPushRepository;
import hey.io.heybackend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
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
        // 1. 회원 조회
        Member member = memberRepository.findById(authenticatedMember.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        // 2. 약관 동의 여부 수정
        member.updateOptionalTermsAgreed(memberTermsRequest.getBasicTermsAgreed());

        // 3. 회원 상태 수정
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

        // 1. 회원 조회
        Member member = memberRepository.findById(authenticatedMember.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        // 2. 관심 정보 삭제
        memberInterestRepository.deleteByMember(member);

        // 3. 관심 정보 등록
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

        // 1. 회원 상세 정보 조회
        MemberDetailResponse memberDetail = memberRepository.selectMemberDetail(authenticatedMember.getMemberId());
        if (memberDetail == null) {
            throw new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND);
        }

        // 2. 관심 정보 조회
        List<String> typeList = memberRepository.selectMemberInterestList(InterestCategory.TYPE, authenticatedMember.getMemberId());
        List<String> genreList = memberRepository.selectMemberInterestList(InterestCategory.GENRE, authenticatedMember.getMemberId());
        MemberInterestDto interests = MemberInterestDto.of(typeList, genreList);

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

        // 1. 회원 조회
        Member member = memberRepository.findById(authenticatedMember.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        // 2. 닉네임 수정
        member.updateNickname(modifyMemberRequest.getNickname());

        // 3. 관심 정보 삭제
        memberInterestRepository.deleteByMember(member);

        // 4. 관심 정보 등록
        List<MemberInterest> newMemberInterests = modifyMemberRequest.toMemberInterests(member);
        memberInterestRepository.saveAll(newMemberInterests);

        return member.getMemberId();
    }
}
