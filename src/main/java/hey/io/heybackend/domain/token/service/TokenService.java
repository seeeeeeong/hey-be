package hey.io.heybackend.domain.token.service;

import hey.io.heybackend.common.config.jwt.JwtTokenProvider;
import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.token.dto.TokenDto;
import hey.io.heybackend.domain.token.entity.Token;
import hey.io.heybackend.domain.auth.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * <p>토큰 재발급</p>
     *
     * @return TokenDTO
     */
    @Transactional
    public TokenDto reIssueToken(Member member) {
        TokenDto tokenDto = jwtTokenProvider.createToken(member);
        tokenRepository.deleteByMemberId(member.getMemberId());

        Token token = Token.builder()
                .memberId(member.getMemberId())
                .refreshToken(tokenDto.getRefreshToken())
                .userId(null)
                .build();

        tokenRepository.saveAndFlush(token);
        return tokenDto;
    }

    @Transactional
    public void insertToken(Member member, String refreshToken) {
        tokenRepository.deleteByMemberId(member.getMemberId());
        Token token = Token.builder()
                .memberId(member.getMemberId())
                .refreshToken(refreshToken)
                .userId(null)
                .build();
        tokenRepository.save(token);
    }
}
