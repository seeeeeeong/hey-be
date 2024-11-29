package hey.io.heybackend.domain.oauth.service;

import hey.io.heybackend.common.jwt.dto.TokenDto;
import hey.io.heybackend.common.jwt.service.TokenService;
import hey.io.heybackend.domain.auth.entity.Auth;
import hey.io.heybackend.domain.auth.enums.AuthType;
import hey.io.heybackend.domain.auth.service.AuthService;
import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.member.entity.SocialAccount;
import hey.io.heybackend.domain.member.service.MemberService;
import hey.io.heybackend.domain.member.service.SocialAccountService;
import hey.io.heybackend.domain.oauth.dto.SocialUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuthLoginService {

    private final SocialAccountService socialAccountService;
    private final MemberService memberService;
    private final TokenService tokenService;
    private final AuthService authService;

    /**
     * <p>소셜 로그인</p>
     *
     * @param userInfo 소셜 회원 정보
     * @return 토큰 정보
     */
    public TokenDto processLogin(SocialUserInfo userInfo) {
        Member member = insertOrUpdateMember(userInfo);
        insertOrUpdateSocialAccount(member, userInfo);
        return insertToken(member);
    }

    /**
     * <p>유저 저장/업데이트</p>
     *
     * @param userInfo 소셜 회원 정보
     * @return 회원 정보
     */
    private Member insertOrUpdateMember(SocialUserInfo userInfo) {
        // 유저 조회
        Optional<Member> existingMember = memberService.getMemberByProviderUid(userInfo.getProviderUid());

        // 유저 업데이트
        if (existingMember.isPresent()) {
            Member member = existingMember.get();
            member.updateMember(userInfo.getEmail(), userInfo.getName());
            return member;
        } else {
            // 유저 등록
            Member newMember = memberService.insertMember(userInfo.getEmail(), userInfo.getName());

            // 유저 권한 설정
            insertUserAuth(newMember);

            // 푸시 알림 설정
            insertMemberPush(newMember);

            return newMember;
        }
    }

    /**
     * <p>유저 권한 설정</p>
     *
     * @param member
     */
    private void insertUserAuth(Member member) {
        // 권한 리스트 조회
        List<Auth> auths = authService.getAuthList(List.of(AuthType.MEMBER_SNS.getCode(), AuthType.IS_AUTHENTICATED_FULLY.getCode()));

        // 유저 권한 설정
        authService.insertUserAuth(String.valueOf(member.getMemberId()), auths);
    }

    /**
     * <p>푸시 알림 설정</p>
     *
     * @param member
     */
    private void insertMemberPush(Member member) {
        // 1. 회원에 대한 푸시 알림 설정을 추가
        memberService.insertMemberPush(member);
    }

    /**
     * <p>소셜 계정 저장/업데이트</p>
     *
     * @param member     회원 정보
     * @param userInfo   소셜 회원 정보
     * @return 소설 계정 정보
     */
    private SocialAccount insertOrUpdateSocialAccount(Member member, SocialUserInfo userInfo) {
        // 소설 계정 조회
        Optional<SocialAccount> existingSocialAccount = socialAccountService.getSocialAccount(userInfo.getProviderUid());

        // 소설 계정 업데이트
        if (existingSocialAccount.isPresent()) {
            SocialAccount socialAccount = existingSocialAccount.get();
            socialAccount.updateSocialAccount(userInfo.getProvider(), userInfo.getProviderUid());  // 소셜 계정 정보 업데이트
            return socialAccount;
        } else {
            // 소설 계정 등록
            SocialAccount newSocialAccount = SocialAccount.of(
                    member,
                    userInfo.getProvider(),
                    userInfo.getProviderUid()
            );
            return socialAccountService.insertSocialAccount(newSocialAccount);
        }
    }

    /**
     * <p>토큰 저장</p>
     *
     * @param member 회원 정보
     * @return 토큰 정보
     */
    private TokenDto insertToken(Member member) {
        return tokenService.insertToken(member);
    }
}
