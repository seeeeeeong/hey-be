package hey.io.heybackend.domain.mypage.service;

import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.exception.notfound.EntityNotFoundException;
import hey.io.heybackend.domain.member.dto.AuthenticatedMember;
import hey.io.heybackend.domain.member.dto.MemberInterestRequest;
import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.member.entity.MemberInterest;
import hey.io.heybackend.domain.member.enums.InterestCategory;
import hey.io.heybackend.domain.member.repository.MemberInterestRepository;
import hey.io.heybackend.domain.member.repository.MemberRepository;
import hey.io.heybackend.domain.mypage.dto.MyPageDto.MemberDetailResponse;
import hey.io.heybackend.domain.mypage.dto.MyPageDto.MemberDetailResponse.MemberInterestDto;
import hey.io.heybackend.domain.mypage.dto.MyPageDto.ModifyMemberRequest;
import hey.io.heybackend.domain.performance.enums.PerformanceGenre;
import hey.io.heybackend.domain.performance.enums.PerformanceType;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final MemberRepository memberRepository;
    private final MemberInterestRepository memberInterestRepository;


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
    public Long modifyMember(AuthenticatedMember authenticatedMember, ModifyMemberRequest modifyMemberRequest) {

        // 1. 회원 조회
        Member member = memberRepository.findById(authenticatedMember.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        // 2. 닉네임 수정
        member.updateNickname(modifyMemberRequest.getNickname());

        // 3. 관심 정보 삭제
        memberInterestRepository.deleteByMember(member);

        // 4. 관심 정보 등록
        insertMemberInterests(member, modifyMemberRequest);

        return member.getMemberId();
    }

    // 관심 정보 등록
    private void insertMemberInterests(Member member, ModifyMemberRequest modifyMemberRequest) {

        // 관심 유형 저장
        if (modifyMemberRequest.getType() != null) {
            modifyMemberRequest.getType().forEach(type -> {
                MemberInterest memberInterest = MemberInterest.builder()
                    .member(member)
                    .interestCategory(InterestCategory.TYPE)
                    .interestCode(type.getCode())
                    .build();
                memberInterestRepository.save(memberInterest);
            });
        }

        // 관심 장르 저장
        if (modifyMemberRequest.getGenre() != null) {
            modifyMemberRequest.getGenre().forEach(genre -> {
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
