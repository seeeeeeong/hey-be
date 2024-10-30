package hey.io.heybackend.domain.auth.controller;


import hey.io.heybackend.domain.auth.service.AuthService;
import hey.io.heybackend.domain.auth.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login/oauth2/callback/google")
    public ResponseEntity<LoginResponse> googleLogin(@RequestParam(name = "code") String code) {
        LoginResponse loginResponse = authService.googleLogin(code);
        return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
    }

}
