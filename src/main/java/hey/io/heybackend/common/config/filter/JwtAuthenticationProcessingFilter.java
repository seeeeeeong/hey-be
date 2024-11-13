package hey.io.heybackend.common.config.filter;

import hey.io.heybackend.common.config.jwt.JwtTokenProvider;
import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.exception.notfound.EntityNotFoundException;
import hey.io.heybackend.common.exception.unauthorized.UnAuthorizedException;
import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.member.repository.MemberRepository;
import hey.io.heybackend.domain.system.dto.TokenDTO;
import hey.io.heybackend.domain.system.entity.Token;
import hey.io.heybackend.domain.system.repository.TokenRepository;
import hey.io.heybackend.domain.system.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    private final TokenService tokenService;


    /**
     * <p>Jwt 인증 필터</p>
     *
     * Request Header -> accessToken (accessToken 유효)
     * Request Header -> accessToken, refreshToken (accessToken 만료)
     *
     * 1. refreshToken이 없고, accessToken이 유효한 경우 -> 인증 성공
     * 2. refreshToken이 없고, accessToken이 유효하지 않은 경우 -> 인증 실패
     * 3. refreshToken이 있고, 검증에 성공한 경우 -> accessToken, refreshToken 재발급
     *
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Request Header에서 refreshToken 추출
        String refreshToken = jwtTokenProvider.extractRefreshToken(request)
                .filter(jwtTokenProvider::validateToken)
                .orElse(null);


        // 2.1 refreshToken 존재할 경우
        if (refreshToken != null) {
            // refreshToken 검증, 토큰 재발급
            checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
            return;
        }

        // 2.2 refreshToken 존재하지 않을 경우
        if (refreshToken == null) {
            // accessToken 검증, 인증 성공/실패 처리
            checkAccessTokenAndAuthentication(request, response, filterChain);
        }
    }


    /**
     * <p>refreshToken 검증, 토큰 재발급</p>
     *
     */
    public void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
        Member member = memberRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new UnAuthorizedException(ErrorCode.INVALID_REFRESH_TOKEN));

        TokenDTO tokenDTO = tokenService.reIssueToken(member);
        jwtTokenProvider.sendAccessAndRefreshToken(response, tokenDTO.getAccessToken(), tokenDTO.getRefreshToken());
    }


    /**
     * <p>accessToken 검증, 인증 성공/실패 처리</p>
     *
     */
    public void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response,
                                                  FilterChain filterChain) throws ServletException, IOException {

        jwtTokenProvider.extractAccessToken(request)
                        .filter(jwtTokenProvider::validateToken)
                                .ifPresent(accessToken -> saveAuthentication(jwtTokenProvider.getAuthentication(accessToken)));

        filterChain.doFilter(request, response);
    }

    /**
     * <p>인증 성공</p>
     *
     */
    public void saveAuthentication(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
