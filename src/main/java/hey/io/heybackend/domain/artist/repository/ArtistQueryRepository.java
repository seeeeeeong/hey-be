package hey.io.heybackend.domain.artist.repository;

import hey.io.heybackend.domain.artist.dto.GetArtistPerformanceListResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

public interface ArtistQueryRepository {

    Slice<GetArtistPerformanceListResponse> getArtistPerformanceList(Long artistId, String exceptClosed ,Pageable pageable, Sort.Direction direction);

}
