package hey.io.heybackend.domain.member.service;

import hey.io.heybackend.domain.member.dto.*;
import hey.io.heybackend.domain.member.dto.MemberInfoResDto.MemberInterestDto;
import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.member.entity.MemberInterest;
import hey.io.heybackend.domain.member.enums.InterestCategory;
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

    /**
     * <p>약관 동의</p>
     *
     * @param memberDto 인증된 사용자 정보
     * @param request
     * @return 회원 ID
     */
    @Transactional
    public Long modifyMemberTerms(MemberDto memberDto, MemberTermsReqDto request) {

        Member member = memberQueryService.getMemberByMemberId(memberDto.getMemberId());
        member.updateOptionalTermsAgreed(request.getBasicTermsAgreed());
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

        Member member = memberQueryService.getMemberByMemberId(memberDto.getMemberId());

        memberCommandService.deleteMemberInterests(member);

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
        memberCommandService.insertMemberInterests(newMemberInterests);
        return member.getMemberId();

    }

    /**
     * <p>회원 정보 조회</p>
     *
     * @param memberDto 인증된 사용자 정보
     * @return 회원 정보
     */
    public MemberInfoResDto getMemberInfo(MemberDto memberDto) {

        Member member = memberQueryService.getMemberByMemberId(memberDto.getMemberId());

        List<MemberInterest> memberInterests = memberQueryService.getMemberInterestsByMember(member);

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
        return memberQueryService.existsMemberByNickname(nickname);
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

        Member member = memberQueryService.getMemberByMemberId(memberDto.getMemberId());

        member.updateNickname(request.getNickname());

        memberCommandService.deleteMemberInterests(member);

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

        memberCommandService.insertMemberInterests(newMemberInterests);

        return member.getMemberId();

    }



}
