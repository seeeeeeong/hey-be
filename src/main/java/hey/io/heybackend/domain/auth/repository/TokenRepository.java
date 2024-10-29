package hey.io.heybackend.domain.auth.repository;

import hey.io.heybackend.domain.auth.entity.Token;
import hey.io.heybackend.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByMember(Member member);

}
