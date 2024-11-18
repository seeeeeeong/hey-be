package hey.io.heybackend.domain.follow.repository;

import hey.io.heybackend.domain.follow.entity.Follow;
import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.follow.enums.FollowType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long>, FollowQueryRepository {
}
