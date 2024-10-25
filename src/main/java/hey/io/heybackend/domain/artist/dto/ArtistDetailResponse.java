package hey.io.heybackend.domain.artist.dto;

import hey.io.heybackend.domain.artist.entity.Artist;
import hey.io.heybackend.domain.artist.enums.ArtistGenre;
import hey.io.heybackend.domain.artist.enums.ArtistType;
import hey.io.heybackend.domain.file.dto.FileDTO;
import hey.io.heybackend.domain.performance.dto.PerformanceDetailResponse;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ArtistDetailResponse {

    private Long artistId;
    private String name;
    private String engName;
    private ArtistType artistType;
    private String artistUrl;
    private Integer popularity;
    private List<String> genres;
    private Boolean isFollow;
    private List<FileDTO> files;


    public static ArtistDetailResponse from(Artist artist) {

        return ArtistDetailResponse.builder()
                .artistId(artist.getArtistId())
                .name(artist.getName())
                .engName(artist.getEngName())
                .artistType(artist.getArtistType())
                .artistUrl(artist.getArtistUrl())
                .popularity(artist.getPopularity())

                .genres(artist.getGenres().stream()
                        .map(genre -> genre.getArtistGenre().name())
                        .collect(Collectors.toList()))

                .isFollow(false)

                .files(artist.getFiles().stream()
                        .map(file -> FileDTO.from(file))
                        .collect(Collectors.toList()))

                .build();
    }

    public void setIsFollow(Boolean isFollow) {
        this.isFollow = isFollow;
    }

}
