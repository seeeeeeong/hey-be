package hey.io.heybackend.domain.oauth.repository;

import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.oauth.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

}
