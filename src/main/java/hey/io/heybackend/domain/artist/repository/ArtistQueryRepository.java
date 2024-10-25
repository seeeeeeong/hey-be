package hey.io.heybackend.domain.artist.repository;

import hey.io.heybackend.domain.artist.dto.ArtistPerformanceResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

public interface ArtistQueryRepository {

    Slice<ArtistPerformanceResponse> getArtistPerformanceList(Long artistId, String exceptClosed , Pageable pageable, Sort.Direction direction);

}
