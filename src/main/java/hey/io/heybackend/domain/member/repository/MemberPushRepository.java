package hey.io.heybackend.domain.member.repository;

import hey.io.heybackend.domain.member.entity.MemberPush;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberPushRepository extends JpaRepository<MemberPush, Long> {

}
