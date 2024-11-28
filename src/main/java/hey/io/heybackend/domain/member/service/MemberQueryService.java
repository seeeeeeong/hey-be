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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryService {

    private final MemberRepository memberRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final MemberInterestRepository memberInterestRepository;

    public Member getByMemberId(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
    }

    public Member getByProviderUid(Provider provider, String providerUid) {
        return memberRepository.findByProviderUidAndProvider(provider, providerUid).orElse(null);
    }

    public Member getByRefreshToken(String refreshToken) {
        return memberRepository.findByRefreshToken(refreshToken).orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
    }

    public boolean existsByNickname(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    public List<MemberInterest> getByMember(Member member) {
        return memberInterestRepository.findByMember(member);
    }

    public SocialAccount getByMemberAndProvider(Member member, Provider provider) {
        return socialAccountRepository.findByMemberAndProvider(member, provider).orElse(null);
    }

}
