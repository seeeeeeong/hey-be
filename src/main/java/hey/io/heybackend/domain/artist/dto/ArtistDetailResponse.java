package hey.io.heybackend.domain.artist.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import hey.io.heybackend.domain.artist.entity.Artist;
import hey.io.heybackend.domain.artist.entity.ArtistGenres;
import hey.io.heybackend.domain.artist.enums.ArtistGenre;
import hey.io.heybackend.domain.artist.enums.ArtistType;
import hey.io.heybackend.domain.file.dto.FileDTO;
import hey.io.heybackend.domain.performance.dto.PerformanceListResponse;
import hey.io.heybackend.domain.performance.entity.Performance;
import hey.io.heybackend.domain.performance.entity.PerformanceGenres;
import hey.io.heybackend.domain.performance.enums.PerformanceGenre;
import hey.io.heybackend.domain.performance.enums.TicketStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@Schema(description = "아티스트 상세")
public class ArtistDetailResponse {

    @Schema(description = "아티스트 ID", example = "1")
    private Long artistId;

    @Schema(description = "아티스트명", example = "윤지영")
    private String name;

    @Schema(description = "아티스트 영문명", example = "Yoon Jiyoung")
    private String engName;

    @Schema(description = "아티스트 유형", example = "솔로",
            allowableValues = {"그룹", "솔로"})
    private ArtistType artistType;

    @Schema(description = "외부 URL", example = "http://example.com/artist/yoonjiyoung")
    private String artistUrl;

    @Schema(description = "인기도", example = "85")
    private Integer popularity;

    @Schema(
            description = "아티스트 장르", example = "[\"발라드\", \"힙합\"]",
            allowableValues = {"발라드", "힙합", "R&B", "EDM", "인디", "락", "재즈", "아이돌", "기타"})
    private List<String> genres;

    @Schema(description = "파일 정보",
            example = "[{\"fileId\": 1, \"fileCategory\": \"THUMBNAIL\", \"fileName\": \"example.png\", \"fileUrl\": \"http://example.com/image.png\", \"width\": 640, \"height\": 640}]")
    private List<FileDTO> files;

    @Schema(description = "아티스트 공연 목록")
    private List<PerformanceListResponse> performances;

    public static ArtistDetailResponse of(Artist artist, List<FileDTO> fileList, List<PerformanceListResponse> performanceListResponse) {
        return ArtistDetailResponse.builder()
                .artistId(artist.getArtistId())
                .name(artist.getName())
                .engName(artist.getEngName())
                .artistType(artist.getArtistType())
                .artistUrl(artist.getArtistUrl())
                .popularity(artist.getPopularity())
                .genres(convertGenres(artist.getGenres()))
                .files(fileList)
                .performances(performanceListResponse)
                .build();
    }

    private static List<String> convertGenres(List<ArtistGenres> genreList) {
        return genreList.stream()
                .map(artistGenre -> {
                    ArtistGenre genre = artistGenre.getArtistGenre();
                    if (genre == ArtistGenre.HIPHOP || genre == ArtistGenre.RNB) {
                        return "힙합/R&B";
                    } else if (genre == ArtistGenre.INDIE || genre == ArtistGenre.ROCK) {
                        return "인디/락";
                    }
                    return genre.getDescription();
                })
                .distinct()
                .collect(Collectors.toList());
    }

}
