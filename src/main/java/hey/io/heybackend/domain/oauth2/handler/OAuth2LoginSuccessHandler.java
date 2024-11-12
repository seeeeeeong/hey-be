package hey.io.heybackend.domain.oauth2.handler;

import hey.io.heybackend.common.config.jwt.JwtTokenProvider;
import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.exception.notfound.EntityNotFoundException;
import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.member.entity.MemberPush;
import hey.io.heybackend.domain.member.entity.SocialAccount;
import hey.io.heybackend.domain.member.enums.MemberStatus;
import hey.io.heybackend.domain.member.enums.Provider;
import hey.io.heybackend.domain.member.enums.PushType;
import hey.io.heybackend.domain.member.repository.MemberPushRepository;
import hey.io.heybackend.domain.member.repository.MemberRepository;
import hey.io.heybackend.domain.member.repository.SocialAccountRepository;
import hey.io.heybackend.domain.member.service.MemberService;
import hey.io.heybackend.domain.oauth2.service.PrincipalDetails;
import hey.io.heybackend.domain.system.dto.TokenDTO;
import hey.io.heybackend.domain.system.entity.Auth;
import hey.io.heybackend.domain.system.entity.Token;
import hey.io.heybackend.domain.system.entity.UserAuth;
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
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final MemberRepository memberRepository;
    private final UserAuthRepository userAuthRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final AuthRepository authRepository;
    private final TokenRepository tokenRepository;
    private final MemberPushRepository memberPushRepository;

    private final JwtTokenProvider jwtTokenProvider;

    private final MemberService memberService;


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

        memberService.saveOrUpdateSocialAccount(member, provider, providerUid);

        // 3. Token 발급
        TokenDTO tokenDTO = jwtTokenProvider.createToken(member);

        // 4. accessToken, refreshToken 응답
        response.addHeader(jwtTokenProvider.getAccessHeader(), "Bearer " + tokenDTO.getAccessToken());
        response.addHeader(jwtTokenProvider.getRefreshHeader(), "Bearer " + tokenDTO.getRefreshToken());

        // TODO -> 첫 로그인 구분, 응답
        if (!member.getOptionalTermsAgreed()) {
            response.sendRedirect("/member/terms");
        } else {
            response.sendRedirect("/main");
        }

        jwtTokenProvider.sendAccessAndRefreshToken(response, tokenDTO.getAccessToken(), tokenDTO.getRefreshToken());

        // 4. RefreshToken 저장
        insertToken(member, tokenDTO.getRefreshToken());
    }


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
