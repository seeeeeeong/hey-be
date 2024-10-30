package hey.io.heybackend.domain.performance.repository;

import hey.io.heybackend.domain.artist.entity.Artist;
import hey.io.heybackend.domain.file.entity.File;
import hey.io.heybackend.domain.performance.dto.*;
import hey.io.heybackend.domain.performance.entity.Performance;
import hey.io.heybackend.domain.performance.entity.PerformanceArtist;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface PerformanceQueryRepository {

    Slice<Performance> getPerformanceList(PerformanceFilterRequest request, Pageable pageable);

    Performance getPerformanceDetail(Long performanceId);
}
