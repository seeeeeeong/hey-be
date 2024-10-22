package hey.io.heybackend.domain.performance.repository;

import hey.io.heybackend.domain.performance.entity.PerformanceArtist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerformanceArtistRepository extends JpaRepository<PerformanceArtist, Long>{

}
