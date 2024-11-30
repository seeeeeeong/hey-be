package hey.io.heybackend.common.jwt.repository;


import hey.io.heybackend.common.jwt.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    void deleteByMemberId(Long memberId);
}
