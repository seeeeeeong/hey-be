package hey.io.heybackend.domain.oauth.service;

import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.exception.notfound.EntityNotFoundException;
import hey.io.heybackend.common.jwt.dto.TokenDto;
import hey.io.heybackend.common.jwt.service.TokenService;
import hey.io.heybackend.common.util.NicknameUtil;
import hey.io.heybackend.domain.auth.entity.Auth;
import hey.io.heybackend.domain.auth.enums.AuthType;
import hey.io.heybackend.domain.auth.service.AuthService;
import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.member.entity.MemberPush;
import hey.io.heybackend.domain.member.entity.SocialAccount;
import hey.io.heybackend.domain.member.repository.MemberPushRepository;
import hey.io.heybackend.domain.member.repository.MemberRepository;
import hey.io.heybackend.domain.member.repository.SocialAccountRepository;
import hey.io.heybackend.domain.oauth.dto.SocialUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuthLoginService {

    private final SocialAccountRepository socialAccountRepository;
    private final MemberRepository memberRepository;
    private final MemberPushRepository memberPushRepository;

    private final TokenService tokenService;
    private final AuthService authService;

    private final NicknameUtil nicknameUtil;

    /**
     * <p>소셜 로그인</p>
     *
     * @param userInfo 소셜 회원 정보
     * @return 토큰 정보
     */
    public TokenDto processLogin(SocialUserInfo userInfo) {
        // 회원 저장/업데이트
        Member member = insertOrUpdateMember(userInfo);
        // 소셜 계정 저장/업데이트
        insertOrUpdateSocialAccount(member, userInfo);
        // 토큰 저장
        return insertToken(member);
    }

    /**
     * <p>회원 등록/업데이트</p>
     *
     * @param userInfo 소셜 회원 정보
     * @return 회원 정보
     */
    private Member insertOrUpdateMember(SocialUserInfo userInfo) {
        // 회원 정보 조회
        Optional<Member> existingMember = memberRepository.selectMemberByProviderUid(userInfo.getProviderUid());

        if (existingMember.isPresent()) {
            // 회원 업데이트
            Member member = existingMember.get();
            member.updateMember(userInfo.getEmail(), userInfo.getName());
            return member;
        } else {
            // 회원 저장
            Member newMember = insertMember(userInfo.getEmail(), userInfo.getName());
            // 회원 권한 저장
            insertUserAuth(newMember);
            // 푸시 알림 저장
            insertMemberPush(newMember);
            return newMember;
        }
    }

    /**
     * <p>소셜 계정 저장/업데이트</p>
     *
     * @param member 회원 정보
     * @param userInfo 소셜 회원 정보
     * @return 소셜 계정 정보
     */
    private SocialAccount insertOrUpdateSocialAccount(Member member, SocialUserInfo userInfo) {
        // 소설 계정 정보 조회
        Optional<SocialAccount> existingSocialAccount = socialAccountRepository.findByProviderUid(userInfo.getProviderUid());

        if (existingSocialAccount.isPresent()) {
            // 소설 계정 업데이트
            SocialAccount socialAccount = existingSocialAccount.get();
            socialAccount.updateSocialAccount(userInfo.getProvider(), userInfo.getProviderUid());
            return socialAccount;
        } else {
            // 소설 계정 저장
            SocialAccount newSocialAccount = SocialAccount.of(member, userInfo.getProvider(), userInfo.getProviderUid());
            return socialAccountRepository.save(newSocialAccount);
        }
    }

    // 회원 조회
    public Member getMemberByRefreshToken(String refreshToken) {
        return memberRepository.selectMemberByRefreshToken(refreshToken).orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
    }

    // 회원 저장
    private Member insertMember(String email, String name) {
        Member newMember = Member.of(email, name, generateNickname());
        return memberRepository.save(newMember);
    }

    // 회원 권한 저장
    private void insertUserAuth(Member member) {
        List<Auth> auths = authService.getAuthList(List.of(AuthType.MEMBER_SNS.getCode(), AuthType.IS_AUTHENTICATED_FULLY.getCode()));
        authService.insertUserAuth(String.valueOf(member.getMemberId()), auths);
    }

    // 푸시 알림 저장
    private void insertMemberPush(Member member) {
        MemberPush memberPush = MemberPush.of(member);
        memberPushRepository.save(memberPush);
    }

    // 토큰 저장
    private TokenDto insertToken(Member member) {
        return tokenService.insertToken(member);
    }

    // 닉네임 생성
    private String generateNickname() {
        String nickname;
        do {
            nickname = nicknameUtil.generateNickname();
        } while (memberRepository.existsByNickname(nickname));
        return nickname;
    }
}

