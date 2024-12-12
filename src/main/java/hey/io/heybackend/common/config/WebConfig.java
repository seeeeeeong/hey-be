package hey.io.heybackend.common.config;

import hey.io.heybackend.common.resolver.AuthUserArgumentResolver;
import hey.io.heybackend.common.resolver.GuestOrAuthUserArgumentResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthUserArgumentResolver authUserArgumentResolver;
    private final GuestOrAuthUserArgumentResolver guestOrAuthUserArgumentResolver;


    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authUserArgumentResolver);
        resolvers.add(guestOrAuthUserArgumentResolver);
    }
}
