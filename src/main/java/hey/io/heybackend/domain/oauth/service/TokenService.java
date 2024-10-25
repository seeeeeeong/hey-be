package hey.io.heybackend.domain.oauth.service;

import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.oauth.entity.Token;
import hey.io.heybackend.domain.oauth.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TokenService {


    private final TokenRepository tokenRepository;

    public Optional<Token> findTokenByMember(Member member) {
        return tokenRepository.findByMember(member);
    }


}
