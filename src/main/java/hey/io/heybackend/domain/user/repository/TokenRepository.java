package hey.io.heybackend.domain.user.repository;

import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.user.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    void deleteByMemberId(Long memberId);

}
