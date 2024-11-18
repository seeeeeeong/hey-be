package hey.io.heybackend.common.config;

import hey.io.heybackend.common.config.filter.JwtAuthenticationProcessingFilter;
import hey.io.heybackend.common.config.jwt.JwtAccessDeniedHandler;
import hey.io.heybackend.common.config.jwt.JwtAuthenticationEntryPoint;
import hey.io.heybackend.common.config.jwt.JwtTokenProvider;
import hey.io.heybackend.domain.member.service.MemberQueryService;
import hey.io.heybackend.domain.oauth2.handler.OAuth2LoginFailureHandler;
import hey.io.heybackend.domain.oauth2.handler.OAuth2LoginSuccessHandler;
import hey.io.heybackend.domain.oauth2.service.PrincipalOauth2UserService;
import hey.io.heybackend.domain.token.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;

    private final PrincipalOauth2UserService principalOauth2UserService;
    private final MemberQueryService memberQueryService;
    private final TokenService tokenService;

    private final JwtTokenProvider jwtTokenProvider;

    @Value("${spring.profiles.active}")
    private String profiles;

    /**
     * <p>HttpSecurity 설정</p>
     *
     * @param http HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception throws Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 설정
        return setCsrf(http)
                // 세션을 사용하지 않음
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 요청 인증 설정
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/health_check").permitAll() // Swagger 경로 허용
                        .requestMatchers("/access").permitAll() // 토큰 발급 기능 허용
                        .requestMatchers("/performances/**").permitAll() // 공연 조회 기능 허용
                        .requestMatchers("/artists/**").permitAll() // 아티스트 조회 기능 허용
                        .requestMatchers("/main").permitAll() // 메인
                        .requestMatchers("/member/terms").permitAll() // 약간 동의
                        .anyRequest().authenticated() // 그 외 요청은 인증 필요
                )

                // oauth2 Login 설정
                .oauth2Login(oauth2Login -> oauth2Login
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userService(principalOauth2UserService))
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler))

                // 익명 권한 설정
                .anonymous(anonymous -> anonymous
                        .principal("guest")
                        .authorities("ANONYMOUS"))

                // JWT 인증 필터 적용
                .addFilterBefore(new JwtAuthenticationProcessingFilter(memberQueryService, tokenService, jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)

                // 예외 처리 적용
                .exceptionHandling(exceptionHandling -> {
//                    exceptionHandling.authenticationEntryPoint(new JwtAuthenticationEntryPoint());
                    exceptionHandling.accessDeniedHandler(new JwtAccessDeniedHandler());
                })

                .build();
    }

    private HttpSecurity setCsrf(HttpSecurity http) throws Exception {
        if ("local".equals(profiles)) { // 로컬 환경에서 CSRF 기능 제거
            http.csrf(AbstractHttpConfigurer::disable);
        }
        if ("dev".equals(profiles)) { // 개발 환경에서 CSRF 기능 제거
            http.csrf(AbstractHttpConfigurer::disable);
        }
        if ("prod".equals(profiles)) { // 운영 환경에서 SSL 적용 (https + 포트 전환)
            http.requiresChannel(requiresChannel -> requiresChannel.anyRequest().requiresSecure());
        }
        return http;
    }

    /**
     * <p>인증 관리 bean 설정</p>
     *
     * @return AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) {
        return http.getSharedObject(AuthenticationManager.class);
    }

    /**
     * <p>BCryptPasswordEncoder bean 설정</p>
     *
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * <p>RestTemplate bean 설정</p>
     *
     * @return RestTemplate
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}

