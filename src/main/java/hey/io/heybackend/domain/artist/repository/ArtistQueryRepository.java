package hey.io.heybackend.domain.artist.repository;

import hey.io.heybackend.domain.artist.entity.Artist;

import java.util.Optional;

public interface ArtistQueryRepository {


    /**
     * <p>아트스트 상세 정보</p>
     *
     * @return 아티스트 상세 정보
     */
    Optional<Artist> getArtistDetail(Long artistId);

}
