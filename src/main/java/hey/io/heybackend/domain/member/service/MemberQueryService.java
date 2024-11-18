package hey.io.heybackend.domain.member.service;

import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.exception.notfound.EntityNotFoundException;
import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.member.entity.MemberInterest;
import hey.io.heybackend.domain.member.entity.SocialAccount;
import hey.io.heybackend.domain.member.enums.Provider;
import hey.io.heybackend.domain.member.repository.MemberInterestRepository;
import hey.io.heybackend.domain.member.repository.MemberRepository;
import hey.io.heybackend.domain.member.repository.SocialAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryService {

    private final MemberRepository memberRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final MemberInterestRepository memberInterestRepository;

    public Member getMemberByMemberId(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
    }


    public Member getMemberByRefreshToken(String refreshToken) {
        return memberRepository.findMemberByRefreshToken(refreshToken).orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
    }

    public Optional<Member> getMemberByEmailAndProvider(String email, Provider provider) {
        return memberRepository.findAllByEmail(email).stream()
                .filter(existingMember -> existingMember.getSocialAccounts().stream()
                        .anyMatch(socialAccount -> socialAccount.getProvider() == provider))
                .findFirst();
    }

    public boolean existsMemberByNickname(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    public List<MemberInterest> getMemberInterestsByMember(Member member) {
        return memberInterestRepository.findByMember(member);
    }

    public SocialAccount getSocialAccountByMemberAndProvider(Member member, Provider provider) {
        return socialAccountRepository.findByMemberAndProvider(member, provider)
                .orElse(null);
    }

}
