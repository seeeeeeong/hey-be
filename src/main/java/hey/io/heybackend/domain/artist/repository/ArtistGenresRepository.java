package hey.io.heybackend.domain.artist.repository;

import hey.io.heybackend.domain.artist.dto.ArtistDetailResDto.ArtistGenreDto;
import hey.io.heybackend.domain.artist.entity.Artist;
import hey.io.heybackend.domain.artist.entity.ArtistGenres;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtistGenresRepository extends JpaRepository<ArtistGenres, Long> {

    List<ArtistGenreDto> findArtistGenresByArtist(Artist artist);

}
