package hey.io.heybackend.common.config.filter;

import hey.io.heybackend.common.config.jwt.JwtTokenProvider;
import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.member.repository.MemberRepository;
import hey.io.heybackend.domain.system.dto.TokenDTO;
import hey.io.heybackend.domain.system.entity.Token;
import hey.io.heybackend.domain.system.repository.TokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;


    /**
     * <p>Jwt 인증 필터</p>
     *
     * Request Header - AccessToken만 요청
     * AccessToken 만료 시, Request Header - RefreshToken(AccessToken과 함께 요청)
     *
     * 1. RefreshToken이 없고, AccessToken이 유효한 경우 -> 인증 성공 처리, RefreshToken 재발급 X
     * 2. RefeshToken이 업고, AccessToken이 없거나 유효하지 않은 경우 -> 인증 실패 처리, 403 Error
     * 3. RefreshToken이 있는 경우 -> DB의 RefreshToken과 일치하면 AccessToken, RefreshToken 재발급
     *
     *
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 사용자 요청 헤더에서 RefreshToken 추출
        String refreshToken = jwtTokenProvider.extractRefreshToken(request)
                .filter(jwtTokenProvider::validateToken)
                .orElse(null);

        // 2.1 RefreshToken이 요청 헤더에 존재
        // 사용자의 AccessToken이 만료되어서, RefreshToken까지 보낸 경우
        if (refreshToken != null) {
            // RefreshToken 검증 후, 토큰 재발급
            checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
            return;
        }

        // 2.2 RefreshToken이 요청 헤더에 존재하지 않음
        // AccessToken이 없거나 유효하지 않다면, 인증 객체가 담기지 않은 상태로 다음 필터로 넘어가기 때문에 403 Error 발생
        // AccessToken이 유효하다면, 인증 객체가 담긴 상태로 다음 필터로 넘어가기 때문에 인증 성공
        if (refreshToken == null) {
            checkAccessTokenAndAuthentication(request, response, filterChain);
        }
    }


    /**
     * <p>RefreshToken으로 유저 정보 조회 및 토큰 재발급</p>
     *
     * 헤더의 RefreshToken으로 DB에서 유저를 조회, 유저가 존재한다면
     * Token 생성 및 DB의 RefreshToken 업데이트
     * jwtTokenprovider.sendAccessAndRefreshToken()로 응답 해더에 전송
     *
     */
    public void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
        memberRepository.findByRefreshToken(refreshToken)
                .ifPresent(member -> {
                    TokenDTO tokenDTO = reIssueToken(member);
                    jwtTokenProvider.sendAccessAndRefreshToken(response, tokenDTO.getAccessToken(), tokenDTO.getRefreshToken());
                });

    }

    /**
     * <p>Token 재발급 및 DB의 RefreshToken 업데이트</p>
     * jwtTokenProvider.createToken()으로 토큰 재발급
     * DB에 재발급한 RefreshToken 업데이트 후 Flush
     *
     */
    private TokenDTO reIssueToken(Member member) {
        TokenDTO tokenDTO = jwtTokenProvider.createToken(member);

        tokenRepository.deleteByMemberId(member.getMemberId());

        Token token = Token.builder()
                        .memberId(member.getMemberId())
                        .refreshToken(tokenDTO.getRefreshToken())
                        .userId(null)
                        .build();

        tokenRepository.saveAndFlush(token);
        return tokenDTO;

    }

    /**
     * <p>AccessToken 체크 및 인증 처리</p>
     * request에서 extractAccessToken()으로 AccessToken 추출 및 검증
     * AccessToken이 유효할 경우, saveAuthenticaation()으로 인증 처리
     * 인증 허가 처리된 객체를 SecurityContextHolder에 담은 후, 다음 인증 필터로 진행
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
     * <p>인증 허가</p>
     *
     * SecurityContextHolder.getContext()로 SecurityContext를 꺼낸 후,
     * setAuthentication()을 이용하여 Authentication 객체에 대한 인증 허가 처리
     *
     */
    public void saveAuthentication(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
