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
     * <p>RefreshToken으로 유저 정보 조회 및 토큰 재발급</p>
     *
     * 헤더의 RefreshToken으로 DB에서 유저를 조회, 유저가 존재한다면
     * Token 생성 및 DB의 RefreshToken 업데이트
     * jwtTokenprovider.sendAccessAndRefreshToken()로 응답 해더에 전송
     */
    @Transactional
    public TokenDTO reIssueToken(Member member) {
        TokenDTO tokenDTO = jwtTokenProvider.createToken(member);
        tokenRepository.deleteByMemberId(member.getMemberId()); // 트랜잭션 내에서 delete 호출

        Token token = Token.builder()
                .memberId(member.getMemberId())
                .refreshToken(tokenDTO.getRefreshToken())
                .userId(null)
                .build();

        tokenRepository.saveAndFlush(token); // 트랜잭션 내에서 save 호출
        return tokenDTO;
    }

}
