package hey.io.heybackend.domain.artist.dto;

import com.querydsl.core.annotations.QueryProjection;
import hey.io.heybackend.common.response.SliceResponse;
import hey.io.heybackend.domain.artist.enums.ArtistGenre;
import hey.io.heybackend.domain.artist.enums.ArtistSortType;
import hey.io.heybackend.domain.artist.enums.ArtistType;
import hey.io.heybackend.domain.file.dto.FileDto;
import hey.io.heybackend.domain.performance.dto.PerformanceDto.PerformanceListResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ArtistDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @Schema(description = "아티스트 조회 조건")
    public static class ArtistSearchCondition {

        @Parameter(description = "아티스트 유형", array = @ArraySchema(schema = @Schema(implementation = ArtistType.class)))
        private List<ArtistType> artistType;

        @Parameter(description = "아티스트 장르", array = @ArraySchema(schema = @Schema(implementation = ArtistGenre.class)))
        private List<ArtistGenre> artistGenre;

        @Schema(description = "아티스트명", nullable = true)
        private String artistName;

        @Schema(description = "공연 ID", hidden = true)
        private Long performanceId;

        @NotNull
        @Schema(description = "정렬 기준(아티스트명 가나다순, 티켓 오픈 임박순)", implementation = ArtistSortType.class)
        private ArtistSortType sortType;

        public static ArtistSearchCondition of(Long performanceId) {
            return ArtistSearchCondition.builder()
                .performanceId(performanceId)
                .sortType(ArtistSortType.NAME_ASC)
                .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @Schema(description = "아티스트 목록")
    public static class ArtistListResponse {

        @Schema(description = "아티스트 ID", example = "8")
        private Long artistId;

        @Schema(description = "아티스트명", example = "윤지영")
        private String name;

        @Schema(description = "아티스트 영문명", nullable = true, example = "Yoon Jiyoung")
        private String engName;

        @Schema(description = "아티스트 유형", example = "SOLO")
        private String artistType;

        @Schema(description = "팔로우 여부", example = "true")
        private Boolean isFollow;

        @Setter
        @Schema(description = "썸네일 파일명", nullable = true, example = "PF_PF239871_240424_132953.jpg")
        private String fileName;

        @Setter
        @Schema(description = "썸네일 URL", nullable = true, example = "https://hey-bucket.s3.amazonaws.com/app/performance/337/22b803b7-18c4-477e-8703-334183d54e65.jpg")
        private String fileUrl;

        @QueryProjection
        public ArtistListResponse(Long artistId, String name, String engName, ArtistType artistType, Boolean isFollow) {
            this.artistId = artistId;
            this.name = name;
            this.engName = engName;
            this.artistType = artistType.getCode();
            this.isFollow = isFollow;
        }

        // slice response 데이터 생성
        public static SliceResponse<ArtistListResponse> sliceOf(SliceResponse<ArtistListResponse> artistList,
            List<FileDto> fileList) {
            for (ArtistListResponse artistDto : artistList.getContent()) {
                setFileResponse(fileList, artistDto);
            }
            return artistList;
        }

        // response 데이터 생성
        public static List<ArtistListResponse> of(List<ArtistListResponse> artistList, List<FileDto> fileList) {
            for (ArtistListResponse artistDto : artistList) {
                setFileResponse(fileList, artistDto);
            }
            return artistList;
        }

        // ArtistListResponse ← file
        private static void setFileResponse(List<FileDto> fileList, ArtistListResponse artistDto) {
            fileList.stream()
                .filter(file -> file.getEntityId().equals(artistDto.getArtistId()))
                .findFirst()
                .ifPresent(file -> {
                    artistDto.setFileName(file.getFileName());
                    artistDto.setFileUrl(file.getFileUrl());
                });
        }
    }

    @Getter
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "아티스트 상세")
    public static class ArtistDetailResponse {

        @Schema(description = "아티스트 ID", example = "8")
        private Long artistId;

        @Schema(description = "아티스트명", example = "윤지영")
        private String name;

        @Schema(description = "아티스트 영문명", nullable = true, example = "Yoon Jiyoung")
        private String engName;

        @Schema(description = "아티스트 유형", example = "SOLO")
        private String artistType;

        @Schema(description = "외부 URL", nullable = true, example = "https://open.spotify.com/artist/5SkCZXyRQxw5ZLWAH5r4UJ")
        private String artistUrl;

        @Schema(description = "인기도", nullable = true, example = "85")
        private Integer popularity;

        @Schema(description = "팔로우 여부", example = "true")
        private Boolean isFollow;

        @Schema(description = "아티스트 장르", nullable = true, example = "힙합/R&B")
        private List<String> genres;

        @Schema(description = "아티스트 파일 정보", nullable = true)
        private List<FileDto> files;

        @Schema(description = "공연 정보", nullable = true)
        private List<PerformanceListResponse> performances;

        @QueryProjection
        public ArtistDetailResponse(Long artistId, String name, String engName, ArtistType artistType, String artistUrl,
            Integer popularity, Boolean isFollow) {
            this.artistId = artistId;
            this.name = name;
            this.engName = engName;
            this.artistType = artistType.getCode();
            this.artistUrl = artistUrl;
            this.popularity = popularity;
            this.isFollow = isFollow;
        }

        // response 데이터 생성
        public static ArtistDetailResponse of(ArtistDetailResponse artistDetail, List<ArtistGenre> genreList,
            List<FileDto> fileList, List<PerformanceListResponse> performanceList) {
            return artistDetail.toBuilder()
                .genres(getGenreDescription(genreList))
                .files(fileList)
                .performances(performanceList)
                .build();
        }

        // 장르 추출
        private static List<String> getGenreDescription(List<ArtistGenre> genreList) {
            return genreList.stream().map(genre ->
                    switch (genre) {
                        case HIPHOP, RNB -> "힙합/R&B";
                        case INDIE, ROCK -> "인디/락";
                        default -> genre.getDescription();
                    })
                .distinct()
                .toList();
        }
    }
}
