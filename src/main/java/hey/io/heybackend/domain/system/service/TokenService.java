package hey.io.heybackend.domain.system.service;

import hey.io.heybackend.common.config.jwt.JwtTokenProvider;
import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.system.dto.TokenDTO;
import hey.io.heybackend.domain.system.entity.Token;
import hey.io.heybackend.domain.system.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRepository tokenRepository;

    /**
     * <p>토큰 재발급</p>
     *
     * @return TokenDTO
     */
    @Transactional
    public TokenDTO reIssueToken(Member member) {
        TokenDTO tokenDTO = jwtTokenProvider.createToken(member);
        tokenRepository.deleteByMemberId(member.getMemberId());

        Token token = Token.builder()
                .memberId(member.getMemberId())
                .refreshToken(tokenDTO.getRefreshToken())
                .userId(null)
                .build();

        tokenRepository.saveAndFlush(token);
        return tokenDTO;
    }

}
