package hey.io.heybackend.domain.oauth.controller;

import hey.io.heybackend.domain.oauth.dto.OauthLoginResponse;
import hey.io.heybackend.domain.oauth.service.OAuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class OAuthController {

    private final OAuthService OAuthService;

    @PostMapping("/google/login")
    public ResponseEntity<OauthLoginResponse> googleLogin(HttpServletRequest request) {

        OauthLoginResponse oauthLoginResponse = OAuthService.googleLogin(request);
        return ResponseEntity.status(HttpStatus.OK).body(oauthLoginResponse);

    }

}
