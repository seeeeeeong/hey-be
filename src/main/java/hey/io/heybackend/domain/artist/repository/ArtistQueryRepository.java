package hey.io.heybackend.domain.artist.repository;

import hey.io.heybackend.domain.artist.dto.ArtistPerformanceResponse;
import hey.io.heybackend.domain.artist.entity.Artist;
import hey.io.heybackend.domain.performance.entity.Performance;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

public interface ArtistQueryRepository {

    Slice<Performance> getArtistPerformanceList(Long artistId, String exceptClosed, Pageable pageable);


}
