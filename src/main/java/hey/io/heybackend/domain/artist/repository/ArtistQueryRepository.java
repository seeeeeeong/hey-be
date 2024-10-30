package hey.io.heybackend.domain.artist.repository;

import hey.io.heybackend.domain.artist.entity.Artist;
import hey.io.heybackend.domain.performance.enums.PerformanceStatus;

import java.util.List;
import java.util.Optional;

public interface ArtistQueryRepository {

    Optional<Artist> getArtistDetail(Long artistId, List<PerformanceStatus> statuses);

}
