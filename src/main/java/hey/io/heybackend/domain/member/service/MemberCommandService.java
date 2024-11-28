package hey.io.heybackend.domain.member.service;

import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.member.entity.MemberInterest;
import hey.io.heybackend.domain.member.entity.MemberPush;
import hey.io.heybackend.domain.member.entity.SocialAccount;
import hey.io.heybackend.domain.member.enums.Provider;
import hey.io.heybackend.domain.member.repository.MemberInterestRepository;
import hey.io.heybackend.domain.member.repository.MemberPushRepository;
import hey.io.heybackend.domain.member.repository.MemberRepository;
import hey.io.heybackend.domain.member.repository.SocialAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberCommandService {

    private final SocialAccountRepository socialAccountRepository;
    private final MemberInterestRepository memberInterestRepository;
    private final MemberPushRepository memberPushRepository;
    private final MemberRepository memberRepository;


    @Transactional
    public Member createMember(String email, String name, String nickname) {
        Member member = Member.create(email, name, nickname);
        return memberRepository.save(member);
    }

    @Transactional
    public void updateMember(Member member, String email, String name) {
        member.updateMember(email, name);
    }

    @Transactional
    public void updateOptionalTermsAgreed(Member member,
                                          Boolean optionalTermsAgreed) {
        member.updateOptionalTermsAgreed(optionalTermsAgreed);
    }

    @Transactional
    public void createMemberPush(Member member) {
        MemberPush memberPush = MemberPush.of(member);
        memberPushRepository.save(memberPush);
    }

    @Transactional
    public void createSocialAccount(Member member, Provider provider, String providerUid) {
        SocialAccount socialAccount = SocialAccount.of(member, provider, providerUid);
        socialAccountRepository.save(socialAccount);
    }

    @Transactional
    public void updateSocialAccount(SocialAccount socialAccount, String providerUid) {
        socialAccount.updateProviderUid(providerUid);
    }

    @Transactional
    public void createMemberInterest(List<MemberInterest> memberInterests) {
        memberInterestRepository.saveAll(memberInterests);
    }

    @Transactional
    public void deleteMemberInterests(List<MemberInterest> memberInterests) {
        memberInterestRepository.deleteAll(memberInterests);
    }

}
