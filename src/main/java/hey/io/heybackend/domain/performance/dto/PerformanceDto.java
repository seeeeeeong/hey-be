package hey.io.heybackend.domain.performance.dto;

import com.querydsl.core.annotations.QueryProjection;
import hey.io.heybackend.common.response.SliceResponse;
import hey.io.heybackend.domain.artist.dto.ArtistDto.ArtistListResponse;
import hey.io.heybackend.domain.file.dto.FileDto;
import hey.io.heybackend.domain.performance.enums.PerformanceGenre;
import hey.io.heybackend.domain.performance.enums.PerformanceSortType;
import hey.io.heybackend.domain.performance.enums.PerformanceStatus;
import hey.io.heybackend.domain.performance.enums.PerformanceType;
import hey.io.heybackend.domain.performance.enums.TicketStatus;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class PerformanceDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @Schema(description = "공연 조회 조건")
    public static class PerformanceSearchCondition {

        @Parameter(description = "공연 유형", array = @ArraySchema(schema = @Schema(implementation = PerformanceType.class)))
        private List<PerformanceType> performanceType;

        @Parameter(description = "공연 장르", array = @ArraySchema(schema = @Schema(implementation = PerformanceGenre.class)))
        private List<PerformanceGenre> performanceGenre;

        @Parameter(description = "티켓 상태", array = @ArraySchema(schema = @Schema(implementation = TicketStatus.class)))
        private List<TicketStatus> ticketStatus;

        @Parameter(description = "공연 상태", array = @ArraySchema(schema = @Schema(implementation = PerformanceStatus.class)))
        private List<PerformanceStatus> performanceStatus;

        @Schema(description = "공연명", nullable = true)
        private String performanceName;

        @Schema(description = "아티스트 ID", hidden = true)
        private Long artistId;

        @NotNull
        @Schema(description = "정렬 기준(등록일 기준 최신순, 공연 시작일 기준 최신순)", implementation = PerformanceSortType.class)
        private PerformanceSortType sortType;

        public static PerformanceSearchCondition of(Long artistId) {
            return PerformanceSearchCondition.builder()
                .artistId(artistId)
                .sortType(PerformanceSortType.LATEST_START)
                .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @Schema(description = "공연 목록")
    public static class PerformanceListResponse {

        @Schema(description = "공연 ID", example = "1")
        private Long performanceId;

        @Schema(description = "공연명", example = "문학 콘서트 [과천]")
        private String performanceName;

        @Setter
        @Schema(description = "티켓 오픈 시간", nullable = true, pattern = "yyyy.MM.dd HH:mm", example = "2024.10.28 12:00")
        private String openDatetime;

        @Schema(description = "티켓 상태", example = "판매 예정")
        private String ticketStatus;

        @Schema(description = "공연 시작 일자", pattern = "yyyy.MM.dd", example = "2024.10.28")
        private String startDate;

        @Schema(description = "공연 종료 일자", pattern = "yyyy.MM.dd", example = "2024.10.28")
        private String endDate;

        @Schema(description = "장소명", nullable = true, example = "과천시민회관")
        private String placeName;

        @Setter
        @Schema(description = "팔로우 여부", example = "true")
        private Boolean isFollow;

        @Setter
        @Schema(description = "썸네일 파일명", nullable = true, example = "PF_PF239871_240424_132953.jpg")
        private String fileName;

        @Setter
        @Schema(description = "썸네일 URL", nullable = true, example = "https://hey-bucket.s3.amazonaws.com/app/performance/337/22b803b7-18c4-477e-8703-334183d54e65.jpg")
        private String fileUrl;

        @QueryProjection
        public PerformanceListResponse(Long performanceId, String performanceName, TicketStatus ticketStatus,
            LocalDate startDate, LocalDate endDate, String placeName, Boolean isFollow) {
            this.performanceId = performanceId;
            this.performanceName = performanceName;
            this.ticketStatus = ticketStatus.getDescription();
            this.startDate = startDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
            this.endDate = endDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
            this.placeName = placeName;
            this.isFollow = isFollow;
        }

        @QueryProjection
        public PerformanceListResponse(Long performanceId, LocalDateTime openDatetime) {
            this.performanceId = performanceId;
            this.openDatetime = openDatetime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
        }

        // slice response 데이터 생성
        public static SliceResponse<PerformanceListResponse> sliceOf(
            SliceResponse<PerformanceListResponse> performanceList, List<PerformanceListResponse> ticketingList,
            List<FileDto> fileList) {
            for (PerformanceListResponse performanceDto : performanceList.getContent()) {
                setTicketingResponse(ticketingList, performanceDto);
                setFileResponse(fileList, performanceDto);
            }
            return performanceList;
        }

        // response 데이터 생성
        public static List<PerformanceListResponse> of(
            List<PerformanceListResponse> performanceList, List<PerformanceListResponse> ticketingList,
            List<FileDto> fileList) {
            for (PerformanceListResponse performanceDto : performanceList) {
                setTicketingResponse(ticketingList, performanceDto);
                setFileResponse(fileList, performanceDto);
            }
            return performanceList;
        }

        // PerformanceListResponse ← ticketing
        private static void setTicketingResponse(List<PerformanceListResponse> ticketingList,
            PerformanceListResponse performanceDto) {
            ticketingList.stream()
                .filter(ticketing -> ticketing.getPerformanceId().equals(performanceDto.getPerformanceId()))
                .findFirst()
                .ifPresent(ticketing -> performanceDto.setOpenDatetime(ticketing.getOpenDatetime()));
        }

        // PerformanceListResponse ← file
        private static void setFileResponse(List<FileDto> fileList,
            PerformanceListResponse performanceDto) {
            fileList.stream()
                .filter(file -> file.getEntityId().equals(performanceDto.getPerformanceId()))
                .findFirst()
                .ifPresent(file -> {
                    performanceDto.setFileName(file.getFileName());
                    performanceDto.setFileUrl(file.getFileUrl());
                });
        }
    }

    @Getter
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "공연 상세")
    public static class PerformanceDetailResponse {

        @Schema(description = "공연 ID", example = "87")
        private Long performanceId;

        @Schema(description = "공연 유형", example = "국내 콘서트")
        private String performanceType;

        @Schema(description = "공연명", example = "현대카드 Curated 95, 한로로 X 윤마치 X QWER")
        private String name;

        @Schema(description = "공연 영문명", nullable = true, example = "Hyundai Card Curated 95, Hanrolo X Yoonmachi X QWER")
        private String engName;

        @Schema(description = "티켓 오픈 시간", nullable = true, pattern = "yyyy.MM.dd HH:mm", example = "2024.10.28 12:00")
        private String openDatetime;

        @Schema(description = "티켓 상태", example = "판매 예정")
        private String ticketStatus;

        @Schema(description = "공연 시작 일자", pattern = "yyyy.MM.dd", example = "2024.10.09")
        private String startDate;

        @Schema(description = "공연 종료 일자", pattern = "yyyy.MM.dd", example = "2024.10.09")
        private String endDate;

        @Schema(description = "공연 시간", nullable = true, example = "1시간 30분")
        private String runningTime;

        @Schema(description = "관람 연령", nullable = true, example = "만 7세 이상")
        private String viewingAge;

        @Schema(description = "공연 상태", example = "ONGOING")
        private String performanceStatus;

        @Schema(description = "팔로우 여부", example = "true")
        private Boolean isFollow;

        @Schema(description = "공연 장르", nullable = true, example = "힙합/R&B")
        private List<String> genres;

        @Schema(description = "장소 정보", nullable = true)
        private PlaceDto place;

        @Schema(description = "공연 가격 정보", nullable = true)
        private List<PriceDto> prices;

        @Schema(description = "공연 예매 정보", nullable = true)
        private List<TicketingDto> ticketings;

        @Schema(description = "공연 파일 정보", nullable = true)
        private List<FileDto> files;

        @Schema(description = "아티스트 정보", nullable = true)
        private List<ArtistListResponse> artists;

        @Getter
        @Builder
        private static class PlaceDto {

            @Schema(description = "장소 ID", example = "65")
            private Long placeId;

            @Schema(description = "장소명", example = "현대카드 언더스테이지")
            private String name;

            @Schema(description = "주소", nullable = true, example = "서울특별시 용산구 이태원로 246 (한남동)")
            private String address;

            @Schema(description = "위도", nullable = true, example = "37.5365851")
            private Double latitude;

            @Schema(description = "경도", nullable = true, example = "127.0006662")
            private Double longitude;

            private static PlaceDto of(Long placeId, String name, String address,
                Double latitude, Double longitude) {
                return PlaceDto.builder()
                    .placeId(placeId)
                    .name(name)
                    .address(address)
                    .latitude(latitude)
                    .longitude(longitude)
                    .build();
            }
        }

        @Getter
        @NoArgsConstructor
        public static class PriceDto {

            @Schema(description = "공연 가격 ID", example = "112")
            private Long priceId;

            @Schema(description = "가격 설명", example = "전석 스탠딩")
            private String priceInfo;

            @Schema(description = "금액", example = "88000")
            private Integer priceAmount;
        }

        @Getter
        @NoArgsConstructor
        public static class TicketingDto {

            @Schema(description = "예매 ID", example = "116")
            private Long ticketingId;

            @Schema(description = "예매처", example = "MELON")
            private String ticketingBooth;

            @Schema(description = "티켓 프리미엄", nullable = true, example = "아티스트 선예매")
            private String ticketingPremium;

            @Schema(description = "티켓 오픈 시간", nullable = true, example = "2024.10.01 00:00")
            private String openDatetime;

            @Schema(description = "예매 URL", nullable = true, example = "https://ticket.melon.com/performance/index.htm?prodId=210451")
            private String ticketingUrl;

            @QueryProjection
            public TicketingDto(Long ticketingId, String ticketingBooth, String ticketingPremium,
                LocalDateTime openDatetime, String ticketingUrl) {
                this.ticketingId = ticketingId;
                this.ticketingBooth = ticketingBooth;
                this.ticketingPremium = ticketingPremium;
                this.openDatetime = openDatetime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
                this.ticketingUrl = ticketingUrl;
            }
        }

        @QueryProjection
        public PerformanceDetailResponse(Long performanceId, PerformanceType performanceType, String name,
            String engName, TicketStatus ticketStatus, LocalDate startDate, LocalDate endDate, String runningTime,
            String viewingAge, PerformanceStatus performanceStatus, Boolean isFollow, Long placeId, String placeName,
            String address, Double latitude, Double longitude) {
            this.performanceId = performanceId;
            this.performanceType = performanceType.getDescription();
            this.name = name;
            this.engName = engName;
            this.ticketStatus = ticketStatus.getDescription();
            this.startDate = startDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
            this.endDate = endDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
            this.runningTime = runningTime;
            this.viewingAge = viewingAge;
            this.performanceStatus = performanceStatus.getCode();
            this.isFollow = isFollow;
            this.place = PlaceDto.of(placeId, placeName, address, latitude, longitude);
        }

        // response 데이터 생성
        public static PerformanceDetailResponse of(PerformanceDetailResponse performanceDetail,
            List<PerformanceGenre> genreList, List<PriceDto> priceList, List<TicketingDto> ticketingList,
            List<FileDto> fileList, List<ArtistListResponse> artistList) {
            return performanceDetail.toBuilder()
                .genres(getGenreDescription(genreList))
                .prices(priceList)
                .openDatetime(getOpenDatetime(ticketingList))
                .ticketings(ticketingList)
                .files(fileList)
                .artists(artistList)
                .build();
        }

        // 장르 추출
        private static List<String> getGenreDescription(List<PerformanceGenre> genreList) {
            return genreList.stream().map(genre ->
                    switch (genre) {
                        case HIPHOP, RNB -> "힙합/R&B";
                        case INDIE, ROCK -> "인디/락";
                        default -> genre.getDescription();
                    })
                .distinct()
                .toList();
        }

        // 가장 빠른 예매 시간 추출
        private static String getOpenDatetime(List<TicketingDto> ticketingList) {
            return ticketingList.stream()
                .findFirst()
                .map(TicketingDto::getOpenDatetime)
                .orElse(null);
        }
    }
}
