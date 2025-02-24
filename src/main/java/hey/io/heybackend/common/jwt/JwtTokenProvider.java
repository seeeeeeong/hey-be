package hey.io.heybackend.common.jwt;

import hey.io.heybackend.common.exception.BusinessException;
import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.exception.unauthorized.UnAuthorizedException;
import hey.io.heybackend.domain.member.dto.AuthenticatedMember;
import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.user.dto.TokenDto;
import hey.io.heybackend.domain.user.service.RedisService;
import hey.io.heybackend.domain.user.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Slf4j
@Getter
@Component
@RequiredArgsConstructor
@PropertySource("classpath:application-oauth.yml")
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access.expiration}")
    private long accessTokenTime;

    @Value("${jwt.refresh.expiration}")
    private long refreshTokenTime;

    @Value("${jwt.access.header}")
    private String accessHeader;

    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private final UserService userService;
    private final RedisService redisService;

    // 토큰에서 인증 정보 추출
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaim(token);

        // 사용자 정보 조회
        String memberId = claims.getSubject();
        AuthenticatedMember authenticatedMember = userService.loadUserByUsername(memberId);

        // 권한 정보 조회 및 변환
        @SuppressWarnings("unchecked")
        List<LinkedHashMap<String, String>> authoritiesMap = (List<LinkedHashMap<String, String>>) claims.get("authorities");
        List<? extends GrantedAuthority> authorities = authoritiesMap.stream()
            .map(authMap -> new SimpleGrantedAuthority(authMap.get("authority")))
            .toList();

        return new UsernamePasswordAuthenticationToken(authenticatedMember, token, authorities);
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            parseClaim(token);
            if (redisService.isBlacklisted(token)) {
                throw new UnAuthorizedException(ErrorCode.EXPIRED_JWT);
            }

            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
            throw new UnAuthorizedException(ErrorCode.MALFORMED_JWT);
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
            throw new UnAuthorizedException(ErrorCode.EXPIRED_JWT);
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
            throw new UnAuthorizedException(ErrorCode.UNSUPPORTED_JWT);
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
            throw new UnAuthorizedException(ErrorCode.ILLEGAL_JWT);
        }
    }

    // 인증 정보로 토큰 생성
    public TokenDto createToken(Member member) {
        Date now = new Date();

        // Access Token 생성
        String accessToken = Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .setSubject(String.valueOf(member.getMemberId()))
            .addClaims(getClaims(member))
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime() + accessTokenTime))
            .signWith(getSigningKey(), SignatureAlgorithm.HS512)
            .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime() + refreshTokenTime))
            .signWith(getSigningKey(), SignatureAlgorithm.HS512)
            .compact();

        return TokenDto.builder()
                .memberId(member.getMemberId())
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(formatExpirationTime(getExpirationTime(accessToken)))
                .memberStatus(member.getMemberStatus())
                .build();
    }

    // 인증 정보로 claims 생성
    private Claims getClaims(Member member) {
        List<SimpleGrantedAuthority> authorities = userService.getAuthorities(member);

        Claims claims = Jwts.claims();
        claims.put("userInfo", AuthenticatedMember.of(member, authorities));
        claims.put("authorities", authorities);
        return claims;
    }

    // 토큰 만료 시간 반환
    public Date getExpirationTime(String token) {
        Claims claims = parseClaim(token);
        return claims.getExpiration();
    }

    // 만료 시간 포맷팅
    private String formatExpirationTime(Date expirationTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        return sdf.format(expirationTime);
    }

    // Claims 파싱
    private Claims parseClaim(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    // accessToken 추출
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(accessHeader))
                .filter(refreshToken -> refreshToken.startsWith("Bearer "))
                .map(refreshToken -> refreshToken.replace("Bearer ", ""));
    }

    // refreshToken 추출
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(refreshHeader))
                .filter(refreshToken -> refreshToken.startsWith("Bearer "))
                .map(refreshToken -> refreshToken.replace("Bearer ", ""));
    }

    // accessToken, refreshToken 전송
    public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) {
        response.setStatus(HttpServletResponse.SC_OK);

        setAccessTokenHeader(response, accessToken);
        setRefreshTokenHeader(response, refreshToken);
    }

    // accessToken 헤더 설정
    public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
        response.setHeader(accessHeader, accessToken);
    }

    // refreshToken 헤더 설정
    public void setRefreshTokenHeader(HttpServletResponse response, String refreshToken) {
        response.setHeader(refreshHeader, refreshToken);
    }

    // 서명 키 반환
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }
}
