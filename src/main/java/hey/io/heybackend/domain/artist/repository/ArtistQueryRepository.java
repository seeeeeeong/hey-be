package hey.io.heybackend.domain.artist.repository;

import hey.io.heybackend.domain.artist.entity.Artist;

import java.util.Optional;

public interface ArtistQueryRepository {

    Optional<Artist> getArtistDetail(Long artistId);

}
