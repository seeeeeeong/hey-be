package hey.io.heybackend.domain.oauth2.handler;

import hey.io.heybackend.common.config.jwt.JwtTokenProvider;
import hey.io.heybackend.domain.auth.service.AuthService;
import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.member.entity.SocialAccount;
import hey.io.heybackend.domain.member.enums.NicknameType;
import hey.io.heybackend.domain.member.enums.Provider;
import hey.io.heybackend.domain.member.service.MemberCommandService;
import hey.io.heybackend.domain.member.service.MemberQueryService;
import hey.io.heybackend.domain.oauth2.service.PrincipalDetails;
import hey.io.heybackend.domain.token.dto.TokenDto;
import hey.io.heybackend.domain.token.service.TokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    private final MemberCommandService memberCommandService;
    private final MemberQueryService memberQueryService;
    private final AuthService authService;
    private final TokenService tokenService;

    /**
     * <p>OAuth2 로그인 성공</p>
     */
    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login Success");

        // 1. PrincipalDetails 객체 조회
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        String email = principalDetails.getEmail();
        String name = principalDetails.getName();
        Provider provider = principalDetails.getProvider();
        String providerUid = principalDetails.getProviderUid();

        // 2. 조회한 email, provider 정보로 Member saveOrUpdate
        Member member = saveOrUpdateMember(email, name, provider);

        // 3. SocialAccount saveOrUpdate
        saveOrUpdateSocialAccount(member, provider, providerUid);

        // 3. JWT 토큰 발급
        TokenDto tokenDTO = tokenService.reIssueToken(member);

        // 4. accessToken, refreshToken 헤더에 담아 응답
        response.addHeader(jwtTokenProvider.getAccessHeader(), "Bearer " + tokenDTO.getAccessToken());
        response.addHeader(jwtTokenProvider.getRefreshHeader(), "Bearer " + tokenDTO.getRefreshToken());

        // 첫 로그인일 경우, 약관 동의 페이지 리다이렉트
        if (!member.getOptionalTermsAgreed()) {
            response.sendRedirect("/member/terms");
        // 메인 페이지 리다이렉트
        } else {
            response.sendRedirect("/main");
        }

        jwtTokenProvider.sendAccessAndRefreshToken(response, tokenDTO.getAccessToken(), tokenDTO.getRefreshToken());

        log.info("accessToken: " + tokenDTO.getAccessToken());
        log.info("refreshToken: " + tokenDTO.getRefreshToken());

    }

    private Member saveOrUpdateMember(String email, String name, Provider provider) {
        Optional<Member> optionalMember = memberQueryService.getMemberByEmailAndProvider(email, provider);

        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            member.updateMember(email, name != null ? name : member.getName());
            return member;
        } else {
            Member newMember = memberCommandService.insertMember(email, name, getNickname());
            memberCommandService.insertMemberPush(newMember);
            authService.insertUserAuth(newMember);
            return newMember;
        }
    }

    private String getNickname() {
        String nickname;
        do {
            String nicknameBase = NicknameType.getRandomNickname();
            int randomNumber = new Random().nextInt(100000);
            nickname = String.format("%s_%05d", nicknameBase, randomNumber);
        } while (memberQueryService.existsMemberByNickname(nickname));
        return nickname;
    }

    private void saveOrUpdateSocialAccount(Member member, Provider provider, String providerUid) {
        SocialAccount optionalSocialAccount = memberQueryService.getSocialAccountByMemberAndProvider(member, provider);
        if (optionalSocialAccount == null) {
            memberCommandService.insertSocialAccount(member, provider, providerUid);
        }
        optionalSocialAccount.updateProviderUid(providerUid);
    }

}
