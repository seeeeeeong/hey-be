package hey.io.heybackend.domain.user.service;

import hey.io.heybackend.common.jwt.JwtTokenProvider;
import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.user.dto.TokenDto;
import hey.io.heybackend.domain.user.entity.Token;
import hey.io.heybackend.domain.user.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    private final TokenRepository tokenRepository;

    /**
     * <p>토큰 저장</p>
     *
     * @param member 회원 정보
     * @return 토큰 정보
     */
    @Transactional
    public TokenDto insertToken(Member member) {
        TokenDto tokenDto = jwtTokenProvider.createToken(member);

        redisService.setRefreshToken(member.getMemberId(), tokenDto.getRefreshToken());
        return tokenDto;
    }
}
