package hey.io.heybackend.domain.artist.dto;

import hey.io.heybackend.domain.artist.entity.Artist;
import hey.io.heybackend.domain.artist.enums.ArtistGenre;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class GetArtistDetailResponse {

    private Long artistId;
    private String name;
    private List<ArtistGenre> genres;

    public static GetArtistDetailResponse from(Artist artist) {

        List<ArtistGenre> genres = findGenres(artist);

        return GetArtistDetailResponse.builder()
                .artistId(artist.getArtistId())
                .name(artist.getName())
                .genres(genres)
                .build();
    }

    private static List<ArtistGenre> findGenres(Artist artist) {
        return artist.getGenres().stream()
                .map(genre -> genre.getArtistGenre())
                .collect(Collectors.toList());
    }
}
