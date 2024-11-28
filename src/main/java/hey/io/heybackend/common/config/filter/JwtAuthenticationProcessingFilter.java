package hey.io.heybackend.common.config.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import hey.io.heybackend.common.exception.unauthorized.UnAuthorizedException;
import hey.io.heybackend.common.jwt.JwtTokenProvider;
import hey.io.heybackend.common.jwt.dto.TokenDto;
import hey.io.heybackend.common.jwt.service.TokenService;
import hey.io.heybackend.common.response.ApiResponse;
import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.member.service.MemberQueryService;
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
    private final MemberQueryService memberQueryService;


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String refreshToken = jwtTokenProvider.extractRefreshToken(request)
                    .filter(jwtTokenProvider::validateToken)
                    .orElse(null);

            if (refreshToken != null) {
                checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
                return;
            }

            if (refreshToken == null) {
                checkAccessTokenAndAuthentication(request, response, filterChain);
            }

        } catch (UnAuthorizedException e) {
            handleUnauthorizedException(response, e);
        }
    }

    private void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
        Member member = memberQueryService.getByRefreshToken(refreshToken);
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
