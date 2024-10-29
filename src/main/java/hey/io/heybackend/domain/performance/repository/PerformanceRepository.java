package hey.io.heybackend.domain.performance.repository;

import hey.io.heybackend.domain.performance.entity.Performance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long>, PerformanceQueryRepository {

    @Query("SELECT p FROM Performance p WHERE p.performanceId = :performanceId AND p.performanceStatus <> 'INIT'")
    Optional<Performance> findByIdAndNotInit(Long performanceId);

}


