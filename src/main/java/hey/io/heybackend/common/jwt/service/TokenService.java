package hey.io.heybackend.common.jwt.service;

import hey.io.heybackend.common.jwt.JwtTokenProvider;
import hey.io.heybackend.common.jwt.dto.TokenDto;
import hey.io.heybackend.common.jwt.entity.Token;
import hey.io.heybackend.common.jwt.repository.TokenRepository;
import hey.io.heybackend.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;

    private final TokenRepository tokenRepository;

    @Transactional
    public void saveRefreshToken(Long memberId, String refreshToken) {
        tokenRepository.save(Token.of(memberId, refreshToken));
    }

    @Transactional
    public TokenDto reIssueToken(Member member) {
        TokenDto tokenDto = jwtTokenProvider.createToken(member);
        tokenRepository.deleteByMemberId(member.getMemberId());

        Token token = Token.of(member.getMemberId(), tokenDto.getRefreshToken());
        tokenRepository.saveAndFlush(token);
        return tokenDto;
    }
}
