package hey.io.heybackend.common.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ApiResponse<?> apiResponse = ApiResponse.failure(ErrorCode.UNAUTHORIZED);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonErrorResponse = objectMapper.writeValueAsString(apiResponse);

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setCharacterEncoding("utf-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(jsonErrorResponse);
    }

}
