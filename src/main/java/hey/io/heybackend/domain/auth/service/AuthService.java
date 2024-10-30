package hey.io.heybackend.domain.auth.service;

import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.exception.notfound.EntityNotFoundException;
import hey.io.heybackend.common.jwt.dto.JwtTokenDto;
import hey.io.heybackend.common.jwt.service.TokenManager;
import hey.io.heybackend.domain.auth.client.AuthProviderClient;
import hey.io.heybackend.domain.auth.dto.LoginResponse;
import hey.io.heybackend.domain.auth.entity.Token;
import hey.io.heybackend.domain.auth.repository.TokenRepository;
import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.member.enums.Provider;
import hey.io.heybackend.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final AuthProviderClient authProviderClient;
    private final MemberService memberService;
    private final TokenManager tokenManager;
    private final TokenRepository tokenRepository;

    public LoginResponse googleLogin(String code) {
        String googleAccessToken = authProviderClient.getGoogleAccessToken(code);
        Map<String, Object> userInfo = authProviderClient.getUserInfo(googleAccessToken);
        String email = (String) userInfo.get("email");
        String sub = (String) userInfo.get("sub");

        Member member = memberService.validateRegisteredMemberByEmail(email, Provider.GOOGLE);
        Token token = tokenRepository.findByMember(member)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.TOKEN_NOT_FOUND));

        if (member == null) {
            throw new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND, "email: " + email + "sub: " + sub);
        }

        JwtTokenDto jwtTokenDto = tokenManager.createJwtTokenDto(member.getMemberId());
        token.updateRefreshToken(jwtTokenDto);

        return LoginResponse.of(jwtTokenDto);
    }

}
