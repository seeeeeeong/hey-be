package hey.io.heybackend.common.config.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import hey.io.heybackend.common.config.jwt.JwtTokenProvider;
import hey.io.heybackend.common.exception.unauthorized.UnAuthorizedException;
import hey.io.heybackend.common.response.ApiResponse;
import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.member.service.MemberQueryService;
import hey.io.heybackend.domain.token.dto.TokenDto;
import hey.io.heybackend.domain.token.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    private final MemberQueryService memberQueryService;
    private final TokenService tokenService;

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        try {
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
        } catch (UnAuthorizedException e) {
            handleUnauthorizedException(response, e);
        }
    }

    private void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
        Member member = memberQueryService.getMemberByRefreshToken(refreshToken);

        TokenDto tokenDto = tokenService.reIssueToken(member);
        jwtTokenProvider.sendAccessAndRefreshToken(response, tokenDto.getAccessToken(), tokenDto.getRefreshToken());
    }

    private void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response,
                                                  FilterChain filterChain) throws ServletException, IOException {
        jwtTokenProvider.extractAccessToken(request)
                        .filter(jwtTokenProvider::validateToken)
                                .ifPresent(accessToken -> saveAuthentication(jwtTokenProvider.getAuthentication(accessToken)));

        filterChain.doFilter(request, response);
    }

    private void saveAuthentication(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void handleUnauthorizedException(HttpServletResponse response, UnAuthorizedException e) throws IOException {
        ApiResponse<?> apiResponse = ApiResponse.failure(e.getErrorCode());

        String jsonErrorResponse = new ObjectMapper().writeValueAsString(apiResponse);

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setCharacterEncoding("utf-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(jsonErrorResponse);
    }

}
