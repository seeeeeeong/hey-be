package hey.io.heybackend.domain.oauth2.handler;

import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        ApiResponse<?> apiResponse = ApiResponse.failure(ErrorCode.INTERNAL_ERROR);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonErrorResponse = objectMapper.writeValueAsString(apiResponse);

        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        response.getWriter().write(jsonErrorResponse);
    }
}
