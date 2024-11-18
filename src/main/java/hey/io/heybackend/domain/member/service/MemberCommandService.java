package hey.io.heybackend.domain.member.service;

import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.member.entity.MemberInterest;
import hey.io.heybackend.domain.member.entity.MemberPush;
import hey.io.heybackend.domain.member.entity.SocialAccount;
import hey.io.heybackend.domain.member.enums.MemberStatus;
import hey.io.heybackend.domain.member.enums.Provider;
import hey.io.heybackend.domain.member.enums.PushType;
import hey.io.heybackend.domain.member.repository.MemberInterestRepository;
import hey.io.heybackend.domain.member.repository.MemberPushRepository;
import hey.io.heybackend.domain.member.repository.MemberRepository;
import hey.io.heybackend.domain.member.repository.SocialAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberCommandService {

    private final SocialAccountRepository socialAccountRepository;
    private final MemberInterestRepository memberInterestRepository;
    private final MemberPushRepository memberPushRepository;
    private final MemberRepository memberRepository;

    private final MemberQueryService memberQueryService;

    public Member insertMember(String email, String name, String nickname) {
        Member member = Member.builder()
                .email(email)
                .name(name != null ? name : nickname) // 플랫폼 제공 name이 null인 경우, 닉네임 사용
                .nickname(nickname)
                .memberStatus(MemberStatus.ACTIVE)
                .optionalTermsAgreed(false) // 약관 동의 여부 false
                .accessedAt(LocalDateTime.now())
                .build();
        return memberRepository.save(member);
    }

    public void insertMemberInterests(List<MemberInterest> memberInterests) {
        memberInterestRepository.saveAll(memberInterests);
    }

    public void deleteMemberInterests(Member member) {
        List<MemberInterest> memberInterests = memberQueryService.getMemberInterestsByMember(member);
        memberInterestRepository.deleteAll(memberInterests);
    }

    public void insertMemberPush(Member member) {
        MemberPush memberPush = MemberPush.builder()
                .member(member)
                .pushType(PushType.PERFORMANCE)
                .pushEnabled(true)
                .build();
        memberPushRepository.save(memberPush);
    }

    public SocialAccount insertSocialAccount(Member member, Provider provider, String providerUid) {
        SocialAccount socialAccount = SocialAccount.builder()
                .member(member)
                .provider(provider)
                .providerUid(providerUid)
                .build();
        return socialAccountRepository.save(socialAccount);
    }

}
