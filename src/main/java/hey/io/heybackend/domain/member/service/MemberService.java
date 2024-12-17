package hey.io.heybackend.domain.member.service;

import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.exception.notfound.EntityNotFoundException;
import hey.io.heybackend.domain.member.dto.AuthenticatedMember;
import hey.io.heybackend.domain.member.dto.MemberInterestRequest;
import hey.io.heybackend.domain.member.dto.MemberTermsRequest;
import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.member.entity.MemberInterest;
import hey.io.heybackend.domain.member.enums.InterestCategory;
import hey.io.heybackend.domain.member.enums.MemberStatus;
import hey.io.heybackend.domain.member.repository.MemberInterestRepository;
import hey.io.heybackend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberInterestRepository memberInterestRepository;

    /**
     * <p>약관 동의</p>
     *
     * @param authenticatedMember 인증 회원 정보
     * @param memberTermsRequest  약관 동의 여부
     * @return 회원 ID
     */
    @Transactional
    public Long modifyMemberTerms(AuthenticatedMember authenticatedMember,
        MemberTermsRequest memberTermsRequest) {

        Member member = memberRepository.findById(authenticatedMember.getMemberId())
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        member.setBasicTermsAgreed(memberTermsRequest.getBasicTermsAgreed());

        if (memberTermsRequest.getBasicTermsAgreed()) {
            member.setMemberStatus(MemberStatus.ACTIVE);
        }

        return member.getMemberId();

    }

    /**
     * <p>관심 정보 등록</p>
     *
     * @param authenticatedMember   인증 회원 정보
     * @param memberInterestRequest 관심 정보 목록
     * @return 회원 ID
     */
    @Transactional
    public Long createMemberInterest(AuthenticatedMember authenticatedMember,
        MemberInterestRequest memberInterestRequest) {

        // 1. 회원 조회
        Member member = memberRepository.findById(authenticatedMember.getMemberId())
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        // 2. 관심 정보 삭제
        memberInterestRepository.deleteByMember(member);

        // 3. 관심 정보 등록
        insertMemberInterests(member, memberInterestRequest);

        return member.getMemberId();
    }

    // 관심 정보 등록
    private void insertMemberInterests(Member member, MemberInterestRequest memberInterestRequest) {

        // 관심 유형 저장
        if (memberInterestRequest.getType() != null) {
            memberInterestRequest.getType().forEach(type -> {
                MemberInterest memberInterest = MemberInterest.builder()
                    .member(member)
                    .interestCategory(InterestCategory.TYPE)
                    .interestCode(type.getCode())
                    .build();
                memberInterestRepository.save(memberInterest);
            });
        }

        // 관심 장르 저장
        if (memberInterestRequest.getGenre() != null) {
            memberInterestRequest.getGenre().forEach(genre -> {
                MemberInterest memberInterest = MemberInterest.builder()
                    .member(member)
                    .interestCategory(InterestCategory.GENRE)
                    .interestCode(genre.getCode())
                    .build();
                memberInterestRepository.save(memberInterest);
            });
        }
    }
}