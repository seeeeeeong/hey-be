package hey.io.heybackend.common.resolver;



import hey.io.heybackend.common.config.jwt.JwtTokenProvider;
import hey.io.heybackend.domain.system.dto.TokenDTO;
import io.jsonwebtoken.Claims;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class AuthUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(JwtTokenProvider.class) &&
                parameter.hasParameterAnnotation(AuthUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Claims claims = (Claims) authentication.getPrincipal();
        Long memberId = Long.parseLong((String) claims.get("memberId"));

        return TokenDTO.builder()
                .memberId(memberId)
                .build();
    }
}
