package hey.io.heybackend.common.resolver;

import hey.io.heybackend.domain.member.dto.AuthenticatedMember;
import java.util.Set;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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
        return parameter.getParameterType().equals(AuthenticatedMember.class) &&
                parameter.hasParameterAnnotation(AuthUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        AuthenticatedMember authenticatedMember = (AuthenticatedMember) principal;

        Long memberId = authenticatedMember.getMemberId();
        Set<GrantedAuthority> authorities = (Set<GrantedAuthority>) authenticatedMember.getAuthorities();

        return AuthenticatedMember.builder()
                .memberId(memberId)
                .authorities(authorities)
                .build();
    }
}
