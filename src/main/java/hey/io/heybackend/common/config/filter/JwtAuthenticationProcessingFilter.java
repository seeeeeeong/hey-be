package hey.io.heybackend.common.config.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import hey.io.heybackend.common.exception.unauthorized.UnAuthorizedException;
import hey.io.heybackend.common.jwt.JwtTokenProvider;
import hey.io.heybackend.common.jwt.dto.TokenDto;
import hey.io.heybackend.common.jwt.service.TokenService;
import hey.io.heybackend.common.response.ApiResponse;
import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.oauth.service.OAuthLoginService;
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

    private final JwtTokenProvider jwtTokenProvider;

    private final TokenService tokenService;
    private final OAuthLoginService oAuthLoginService;


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // refreshToken 토큰 추출, 유효성 검사
            String refreshToken = jwtTokenProvider.extractRefreshToken(request)
                    .filter(jwtTokenProvider::validateToken)
                    .orElse(null);

            // refreshToken이 존재하면, accessToken 토큰 재발급
            if (refreshToken != null) {
                checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
                return;
            }

            // refreshToen이 존재하지 않으면, accessToken 인증
            if (refreshToken == null) {
                checkAccessTokenAndAuthentication(request, response, filterChain);
            }

        } catch (UnAuthorizedException e) {
            handleUnauthorizedException(response, e);
        }
    }

    // accessToken 재발급
    private void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
        Member member = oAuthLoginService.getMemberByRefreshToken(refreshToken);

        TokenDto tokenDto = tokenService.insertToken(member);
        jwtTokenProvider.sendAccessAndRefreshToken(response, tokenDto.getAccessToken(), tokenDto.getRefreshToken());
    }

    // accessToken 인증
    private void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response,
                                                  FilterChain filterChain) throws ServletException, IOException {
        jwtTokenProvider.extractAccessToken(request)
                        .filter(jwtTokenProvider::validateToken)
                        .ifPresent(accessToken -> saveAuthentication(jwtTokenProvider.getAuthentication(accessToken)));

        filterChain.doFilter(request, response);
    }

    // 인증 객체 저장
    private void saveAuthentication(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // 인증 실패 에러
    private void handleUnauthorizedException(HttpServletResponse response, UnAuthorizedException e) throws IOException {
        ApiResponse<?> apiResponse = ApiResponse.failure(e.getErrorCode());

        String jsonErrorResponse = new ObjectMapper().writeValueAsString(apiResponse);

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setCharacterEncoding("utf-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(jsonErrorResponse);
    }

}
