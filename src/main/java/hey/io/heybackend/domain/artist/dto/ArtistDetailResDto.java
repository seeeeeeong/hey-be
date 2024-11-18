package hey.io.heybackend.domain.artist.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import hey.io.heybackend.domain.artist.enums.ArtistGenre;
import hey.io.heybackend.domain.artist.enums.ArtistType;
import hey.io.heybackend.domain.file.dto.FileDto;
import hey.io.heybackend.domain.performance.enums.TicketStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class ArtistDetailResDto {

    @Schema(description = "아티스트 ID", example = "1")
    private Long artistId;

    @Schema(description = "아티스트 장르", example = "힙합/R&B")
    private List<String> genres;

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

    @Schema(description = "팔로우 여부", example = "true")
    private Boolean isFollowed;

    @Schema(description = "파일 목록", nullable = true)
    private List<FileDto> files;

    @Schema(description = "아티스트 공연 목록")
    private List<ArtistPerformanceDto> performances;

    public void setGenres(List<ArtistDetailResDto.ArtistGenreDto> genres) {
        this.genres = convertGenres(genres);
    }

    private static List<String> convertGenres(List<ArtistDetailResDto.ArtistGenreDto> genres) {
        return genres.stream()
                .map(artistGenre -> {
                    ArtistGenre genre = artistGenre.artistGenre;
                    if (genre == ArtistGenre.HIPHOP || genre == ArtistGenre.RNB) {
                        // HIPHOP과 RNB 장르를 힙합/R&B로 통합하여 표현
                        return "힙합/R&B";
                    } else if (genre == ArtistGenre.INDIE || genre == ArtistGenre.ROCK) {
                        // INDIE와 ROCK 장르를 인디/락으로 통합하여 표현
                        return "인디/락";
                    }
                    return genre.getDescription();
                })
                .distinct()
                .collect(Collectors.toList());
    }

    @Getter
    @AllArgsConstructor
    public static class ArtistGenreDto {
        private ArtistGenre artistGenre;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ArtistPerformanceDto {
        @Schema(description = "공연 ID", example = "1")
        private Long performanceId;

        @Schema(description = "공연명", example = "문학 콘서트 [과천]")
        private String performanceName;

        @Schema(description = "티켓 오픈 시간", example = "2024.10.01 00:00")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm")
        private LocalDateTime openDatetime;

        @Schema(description = "티켓 상태", example = "판매 예정")
        private TicketStatus ticketStatus;

        @Schema(description = "공연 시작 일자", example = "2024.10.05")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
        private LocalDate startDate;

        @Schema(description = "공연 종료 일자", example = "2024.10.05")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
        private LocalDate endDate;

        @Schema(description = "장소명", example = "과천시민회관")
        private String placeName;

        @Schema(description = "팔로우 여부", example = "true")
        private Boolean isFollowed;

        @Schema(description = "파일 목록")
        private List<FileDto> files;
    }

}
