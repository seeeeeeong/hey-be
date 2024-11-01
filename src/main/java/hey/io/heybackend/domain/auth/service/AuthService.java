package hey.io.heybackend.domain.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;

import hey.io.heybackend.common.jwt.JwtTokenDto;
import hey.io.heybackend.common.jwt.JwtTokenProvider;
import hey.io.heybackend.domain.auth.client.AuthProviderClient;
import hey.io.heybackend.domain.auth.dto.LoginResponse;
import hey.io.heybackend.domain.auth.entity.Token;
import hey.io.heybackend.domain.auth.repository.TokenRepository;
import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.member.enums.Provider;
import hey.io.heybackend.domain.member.service.MemberService;
import hey.io.heybackend.domain.member.service.ValidateMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final AuthProviderClient authProviderClient;
    private final ValidateMemberService validateMemberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRepository tokenRepository;
    private final MemberService memberService;

    @Transactional
    public LoginResponse googleLogin(String code) throws JsonProcessingException {
        String googleAccessToken = authProviderClient.getGoogleAccessToken(code);
        Map<String, Object> userInfo = authProviderClient.getUserInfo(googleAccessToken);

        String email = (String) userInfo.get("email");
        String providerUid = (String) userInfo.get("id");
        Provider provider = Provider.GOOGLE;

        Member member = validateMemberService.validateRegisteredMemberByEmail(email, provider);
        if (member == null) {
            member = memberService.registerMember(email, provider, providerUid);
        }

        JwtTokenDto jwtTokenDto = createJwtTokenDto(member);

        return LoginResponse.of(jwtTokenDto.getAccessToken(), jwtTokenDto.getRefreshToken());
    }

    @Transactional
    public LoginResponse kakaoLogin(String code) throws JsonProcessingException {
        String kakaoAccessToken = authProviderClient.getKakaoAccesssToken(code);
        Map<String, Object> userInfo = authProviderClient.getKakaoUserInfo(kakaoAccessToken);

        String email = (String) userInfo.get("email");
        String providerUid = (String) userInfo.get("id");
        Provider provider = Provider.KAKAO;

        Member member = validateMemberService.validateRegisteredMemberByEmail(email, provider);
        if (member == null) {
            member = memberService.registerMember(email, provider, providerUid);
        }

        JwtTokenDto jwtTokenDto = createJwtTokenDto(member);

        return LoginResponse.of(jwtTokenDto.getAccessToken(), jwtTokenDto.getRefreshToken());
    }



    private JwtTokenDto createJwtTokenDto(Member member) {
        JwtTokenDto jwtTokenDto = jwtTokenProvider.createJwtTokenDto(member.getMemberId());

        Token token = tokenRepository.findByMember(member)
                .orElseGet(() -> tokenRepository.save(Token.create(member, jwtTokenDto.getAccessToken(), jwtTokenDto.getRefreshToken())));

        token.updateRefreshToken(jwtTokenDto);

        return jwtTokenDto;
    }
}
