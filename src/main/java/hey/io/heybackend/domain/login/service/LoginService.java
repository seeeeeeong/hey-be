package hey.io.heybackend.domain.login.service;

import com.nimbusds.jose.JOSEException;
import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.exception.notfound.EntityNotFoundException;
import hey.io.heybackend.domain.auth.enums.AuthId;
import hey.io.heybackend.domain.login.client.OAuthClient;
import hey.io.heybackend.domain.login.dto.SocialUserInfo;
import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.member.entity.MemberPush;
import hey.io.heybackend.domain.member.entity.SocialAccount;
import hey.io.heybackend.domain.member.enums.MemberStatus;
import hey.io.heybackend.domain.member.enums.NicknameType;
import hey.io.heybackend.domain.member.enums.Provider;
import hey.io.heybackend.domain.member.enums.PushType;
import hey.io.heybackend.domain.member.repository.MemberPushRepository;
import hey.io.heybackend.domain.member.repository.MemberRepository;
import hey.io.heybackend.domain.member.repository.SocialAccountRepository;
import hey.io.heybackend.domain.user.dto.TokenDto;
import hey.io.heybackend.domain.user.entity.UserAuth;
import hey.io.heybackend.domain.user.repository.UserAuthRepository;
import hey.io.heybackend.domain.user.service.TokenService;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

    private final OAuthClient oAuthClient;

    private final MemberRepository memberRepository;
    private final MemberPushRepository memberPushRepository;
    private final UserAuthRepository userAuthRepository;
    private final SocialAccountRepository socialAccountRepository;

    private final TokenService tokenService;

    /**
     * <p>로그인</p>
     *
     * @param provider kakao, google, apple
     * @param code Authorization Code
     * @return 발급 받은 토큰 정보
     */
    @Transactional
    public TokenDto login(Provider provider, String code) throws ParseException, IOException, JOSEException {
        String token = getAccessTokenOrIdToken(provider, code);
        SocialUserInfo socialUserInfo = getSocialUserInfo(provider, token);

        Optional<Member> optionalMember = memberRepository.selectMemberByProviderUid(socialUserInfo.providerUid());

        Member member;

        if (optionalMember.isPresent()) {
            member = updateMember(optionalMember.get(), socialUserInfo);
        } else {
            member = createMember(socialUserInfo);
        }

        return tokenService.insertToken(member);
    }

    private Member createMember(SocialUserInfo socialUserInfo) {

        Member newMember = Member.builder()
            .email(socialUserInfo.email())
            .name(socialUserInfo.name())
            .nickname(NicknameType.getRandomNickname())
            .memberStatus(MemberStatus.INIT)
            .basicTermsAgreed(false)
            .accessedAt(LocalDateTime.now())
            .build();

        Member member = memberRepository.saveAndFlush(newMember);

        MemberPush memberPush = MemberPush.builder()
            .member(newMember)
            .pushType(PushType.PERFORMANCE)
            .pushEnabled(true)
            .build();

        SocialAccount socialAccount = SocialAccount.builder()
            .member(newMember)
            .provider(socialUserInfo.provider())
            .providerUid(socialUserInfo.providerUid())
            .build();

        UserAuth userAuthSns = UserAuth.builder()
            .userId(newMember.getMemberId().toString())
            .authId(AuthId.MEMBER_SNS)
            .build();

        UserAuth userAuthFully = UserAuth.builder()
            .userId(newMember.getMemberId().toString())
            .authId(AuthId.IS_AUTHENTICATED_FULLY)
            .build();

        memberPushRepository.saveAndFlush(memberPush);
        socialAccountRepository.saveAndFlush(socialAccount);
        userAuthRepository.saveAllAndFlush(List.of(userAuthSns, userAuthFully));

        return member;
    }

    private Member updateMember(Member member, SocialUserInfo socialUserInfo) {

        if (member.getMemberStatus() == MemberStatus.INIT) {
            member.updateMember(socialUserInfo.email(), socialUserInfo.name(), MemberStatus.INIT, false, LocalDateTime.now());
        } else {
            member.updateMember(socialUserInfo.email(), socialUserInfo.name(), MemberStatus.ACTIVE, true, LocalDateTime.now());
        }

        memberRepository.saveAndFlush(member);

        SocialAccount socialAccount = socialAccountRepository.findByProviderUid(socialUserInfo.providerUid())
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SOCIAL_ACCOUNT_NOT_FOUND));
        socialAccount.updateSocialAccount(socialUserInfo.provider(), socialUserInfo.providerUid());
        socialAccountRepository.saveAndFlush(socialAccount);

        return member;

    }

    private String getAccessTokenOrIdToken(Provider provider, String code) throws IOException, ParseException, JOSEException {
        return switch (provider) {
            case KAKAO -> oAuthClient.getKakaoAccessToken(code);
            case GOOGLE -> oAuthClient.getGoogleAccessToken(code);
            case APPLE -> oAuthClient.getAppleIdToken(code);
        };
    }

    private SocialUserInfo getSocialUserInfo(Provider provider, String token) throws IOException, ParseException {
        return switch (provider) {
            case KAKAO -> oAuthClient.getKakaoUserInfo(token);
            case GOOGLE -> oAuthClient.getGoogleUserInfo(token);
            case APPLE -> oAuthClient.getAppleUserInfo(token);
        };
    }
}
