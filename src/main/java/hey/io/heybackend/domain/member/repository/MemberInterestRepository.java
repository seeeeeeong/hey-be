package hey.io.heybackend.domain.member.repository;

import hey.io.heybackend.domain.member.entity.Member;
import hey.io.heybackend.domain.member.entity.MemberInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberInterestRepository extends JpaRepository<MemberInterest, Long> {
    void deleteByMember(Member member);
}
