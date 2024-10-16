package hey.io.heybackend.domain.performance.repository;

import hey.io.heybackend.domain.performance.entity.PerformanceTicketing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformanceTicketingRepository extends JpaRepository<PerformanceTicketing, Long> {
}
