package hey.io.heybackend.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {


    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 비활성화
        http.csrf(AbstractHttpConfigurer::disable);

        // 세션을 사용하지 않음
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/health_check").permitAll() // Swagger 경로 허용
                .requestMatchers("/access").permitAll() // 토큰 발급 기능 허용
                .requestMatchers("/performances/**").permitAll()
                .requestMatchers("/artists/**").permitAll()
                .requestMatchers("/auth/login/**").permitAll()
                .anyRequest().authenticated() // 그 외 요청은 인증 필요
        );


        return http.build();
    }

}

