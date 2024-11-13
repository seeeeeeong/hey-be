package hey.io.heybackend.domain.oauth2.handler;

import hey.io.heybackend.common.config.jwt.JwtTokenProvider;
import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.member.enums.Provider;
import hey.io.heybackend.domain.member.repository.MemberPushRepository;
import hey.io.heybackend.domain.member.repository.MemberRepository;
import hey.io.heybackend.domain.member.repository.SocialAccountRepository;
import hey.io.heybackend.domain.member.service.MemberService;
import hey.io.heybackend.domain.oauth2.service.PrincipalDetails;
import hey.io.heybackend.domain.system.dto.TokenDTO;
import hey.io.heybackend.domain.system.entity.Token;
import hey.io.heybackend.domain.system.repository.AuthRepository;
import hey.io.heybackend.domain.system.repository.TokenRepository;
import hey.io.heybackend.domain.system.repository.UserAuthRepository;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final TokenRepository tokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;


    /**
     * <p>OAuth2 로그인 성공</p>
     *
     * 사용자가 OAuth2 로그인 성공시 호출
     * 회원 정보 저장 및 JWT 토큰 생성
     *
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
        Member member = memberService.saveOrUpdateMember(email, name, provider);

        // 3. SocialAccount saveOrUpdate
        memberService.saveOrUpdateSocialAccount(member, provider, providerUid);

        // 3. JWT 토큰 발급
        TokenDTO tokenDTO = jwtTokenProvider.createToken(member);

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

        // 4. refreshToken 저장
        insertToken(member, tokenDTO.getRefreshToken());

        log.info("accessToken: " + tokenDTO.getAccessToken());
        log.info("refreshToken: " + tokenDTO.getRefreshToken());

    }


    /**
     * <p>refreshToken 저장</p>
     *
     * @param member
     * @param refreshToken
     */
    private void insertToken(Member member, String refreshToken) {
        tokenRepository.deleteByMemberId(member.getMemberId());

        Token token = Token.builder()
                .memberId(member.getMemberId())
                .refreshToken(refreshToken)
                .userId(null)
                .build();

        tokenRepository.save(token);
    }
}
