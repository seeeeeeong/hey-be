package hey.io.heybackend.domain.member.service;

import hey.io.heybackend.common.jwt.dto.TokenDto;
import hey.io.heybackend.common.jwt.service.TokenService;
import hey.io.heybackend.domain.auth.entity.Auth;
import hey.io.heybackend.domain.auth.enums.AuthType;
import hey.io.heybackend.domain.auth.service.AuthService;
import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.member.entity.SocialAccount;
import hey.io.heybackend.domain.oauth.dto.SocialUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberOAuthService {

    private final SocialAccountService socialAccountService;
    private final MemberService memberService;
    private final TokenService tokenService;
    private final AuthService authService;

    public TokenDto processLogin(SocialUserInfo userInfo) {
        Member member = insertOrUpdateMember(userInfo);
        insertOrUpdateSocialAccount(member, userInfo);
        return insertToken(member);
    }

    private Member insertOrUpdateMember(SocialUserInfo userInfo) {
        Optional<Member> existingMember = memberService.getMemberByProviderUid(userInfo.getProviderUid());

        if (existingMember.isPresent()) {
            Member member = existingMember.get();
            member.updateMember(userInfo.getEmail(), userInfo.getName());
            return member;
        } else {
            Member newMember = memberService.insertMember(userInfo.getEmail(), userInfo.getName());
            insertUserAuth(newMember);
            insertMemberPush(newMember);
            return newMember;
        }
    }

    private void insertUserAuth(Member member) {
        List<Auth> auths = authService.getAuthList(List.of(AuthType.MEMBER_SNS.getCode(), AuthType.IS_AUTHENTICATED_FULLY.getCode()));
        authService.insertUserAuth(String.valueOf(member.getMemberId()), auths);
    }

    private void insertMemberPush(Member member) {
        memberService.insertMemberPush(member);
    }

    private SocialAccount insertOrUpdateSocialAccount(Member member, SocialUserInfo userInfo) {
        Optional<SocialAccount> existingSocialAccount = socialAccountService.getSocialAccount(userInfo.getProviderUid());

        if (existingSocialAccount.isPresent()) {
            SocialAccount socialAccount = existingSocialAccount.get();
            socialAccount.updateSocialAccount(userInfo.getProvider(), userInfo.getProviderUid());
            return socialAccount;
        } else {
            SocialAccount newSocialAccount = SocialAccount.of(
                    member,
                    userInfo.getProvider(),
                    userInfo.getProviderUid()
            );
            return socialAccountService.saveSocialAccount(newSocialAccount);
        }
    }

    private TokenDto insertToken(Member member) {
        return tokenService.insertToken(member);
    }
}
