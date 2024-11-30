package hey.io.heybackend.domain.member.repository;

import hey.io.heybackend.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> , MemberQueryRepository{
    boolean existsByNickname(String nickname);
}
