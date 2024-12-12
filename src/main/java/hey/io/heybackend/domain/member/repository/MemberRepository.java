package hey.io.heybackend.domain.member.repository;

import hey.io.heybackend.domain.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> , MemberQueryRepository{
    Optional<Member> findByEmail(String email);
    boolean existsByNickname(String nickname);
}
