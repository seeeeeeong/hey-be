package hey.io.heybackend.domain.performance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import hey.io.heybackend.domain.artist.entity.Artist;
import hey.io.heybackend.domain.file.dto.FileDTO;
import hey.io.heybackend.domain.performance.entity.*;
import hey.io.heybackend.domain.performance.enums.PerformanceGenre;
import hey.io.heybackend.domain.performance.enums.PerformanceStatus;
import hey.io.heybackend.domain.performance.enums.PerformanceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class PerformanceDetailResponse {

    @Schema(description = "공연 ID", example = "87")
    private Long performanceId;

    @Schema(description = "공연 유형", example = "국내 콘서트")
    private String performanceType;

    @Schema(description = "장르 리스트", example = "[\"발라드\", \"힙합\"]")
    private List<String> genres;

    @Schema(description = "공연 상태", example = "공연 예정")
    private String performanceStatus;

    @Schema(description = "공연명", example = "현대카드 Curated 95, 한로로 X 윤마치 X QWER")
    private String name;

    @Schema(description = "공연 영문명", example = "Hyundai Card Curated 95, Hanrolo X Yoonmachi X QWER")
    private String engName;

    @Schema(description = "공연 시작 일자", example = "2024.10.09")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
    private LocalDate startDate;

    @Schema(description = "공연 종료 일자", example = "2024.10.09")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
    private LocalDate endDate;

    @Schema(description = "공연 시간", example = "1시간 30분")
    private String runningTime;

    @Schema(description = "관람 연령", example = "만 7세 이상")
    private String viewingAge;

    @Schema(description = "팔로우", example = "false")
    private Boolean isFollow;

    @Schema(description = "파일 정보")
    private List<FileDTO> files;

    @Schema(description = "장소 정보")
    private PlaceDTO place;

    @Schema(description = "공연 가격 정보")
    private List<PerformancePriceDTO> prices;

    @Schema(description = "예매 정보")
    private List<PerformanceTicketingDTO> ticketings;

    @Schema(description = "아티스트 정보")
    private List<ArtistDTO> artists;

    public static PerformanceDetailResponse of(Performance performance, Boolean isFollow,
                                               List<FileDTO> fileList, List<PerformancePriceDTO> priceList,
                                               List<PerformanceTicketingDTO> ticketingList, List<ArtistDTO> artistList) {

        return PerformanceDetailResponse.builder()
                .performanceId(performance.getPerformanceId())
                .performanceType(performance.getPerformanceType().getDescription())
                .genres(convertGenres(performance.getGenres()))
                .performanceStatus(performance.getPerformanceStatus().getDescription())
                .name(performance.getName())
                .engName(performance.getEngName())
                .startDate(performance.getStartDate())
                .endDate(performance.getEndDate())
                .runningTime(performance.getRunningTime())
                .viewingAge(performance.getViewingAge())
                .isFollow(isFollow)
                .files(fileList)
                .place(PlaceDTO.of(performance.getPlace()))
                .prices(priceList)
                .ticketings(ticketingList)
                .artists(artistList)
                .build();
    }

    private static List<String> convertGenres(List<PerformanceGenres> genreList) {
        return genreList.stream()
                .map(performanceGenre -> {
                    PerformanceGenre genre = performanceGenre.getPerformanceGenre();
                    if (genre == PerformanceGenre.HIPHOP || genre == PerformanceGenre.RNB) {
                        return "힙합/R&B";
                    } else if (genre == PerformanceGenre.INDIE || genre == PerformanceGenre.ROCK) {
                        return "인디/락";
                    }
                    return genre.getDescription();
                })
                .distinct()
                .collect(Collectors.toList());
    }


    @Getter
    @Builder
    public static class PlaceDTO {
        @Schema(description = "장소 ID", example = "65")
        private Long placeId;

        @Schema(description = "장소 이름", example = "현대카드 언더스테이지")
        private String name;

        @Schema(description = "장소 주소", example = "서울특별시 용산구 이태원로 246 (한남동)")
        private String address;

        @Schema(description = "위도", example = "37.5365851")
        private double latitude;

        @Schema(description = "경도", example = "127.0006662")
        private double longitude;

        public static PlaceDTO of(Place place) {
            return PlaceDTO.builder()
                    .placeId(place.getPlaceId())
                    .name(place.getName())
                    .address(place.getAddress())
                    .latitude(place.getLatitude())
                    .longitude(place.getLongitude())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class PerformancePriceDTO {
        @Schema(description = "가격 ID", example = "112")
        private Long priceId;

        @Schema(description = "가격 정보", example = "전석 스탠딩")
        private String priceInfo;

        @Schema(description = "가격 금액", example = "88000")
        private Integer priceAmount;

        public static PerformancePriceDTO of(PerformancePrice price) {
            return PerformancePriceDTO.builder()
                    .priceId(price.getPriceId())
                    .priceInfo(price.getPriceInfo())
                    .priceAmount(price.getPriceAmount())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class PerformanceTicketingDTO {
        @Schema(description = "예매 ID", example = "116")
        private Long ticketingId;

        @Schema(description = "예매 부스", example = "멜론티켓")
        private String ticketingBooth;

        @Schema(description = "프리미엄 예매 정보",
                example = "아티스트 선예매, 라이브네이션 선예매 등 (값이 비어있으면 일반)")
        private String ticketingPremium;

        @Schema(description = "티켓 오픈 시간",
                example = "2024.10.01 00:00:00")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm:ss")
        private LocalDateTime openDatetime;

        @Schema(description = "예매 URL", example = "https://ticket.melon.com/performance/index.htm?prodId=210451")
        private String ticketingUrl;

        public static PerformanceTicketingDTO of(PerformanceTicketing ticketing) {
            return PerformanceTicketingDTO.builder()
                    .ticketingId(ticketing.getTicketingId())
                    .ticketingBooth(ticketing.getTicketingBooth())
                    .ticketingPremium(ticketing.getTicketingPremium())
                    .openDatetime(ticketing.getOpenDatetime())
                    .ticketingUrl(ticketing.getTicketingUrl())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class ArtistDTO {
        @Schema(description = "아티스트 ID", example = "8")
        private Long artistId;

        @Schema(description = "아티스트 이름", example = "윤지영")
        private String name;

        @Schema(description = "아티스트 영문 이름", example = "Yoon Jiyoung")
        private String engName;

        @Schema(description = "아티스트 유형", example = "SOLO",
                allowableValues = {"GROUP", "SOLO"})
        private String artistType;

        @Schema(description = "아티스트 파일 정보")
        private List<FileDTO> files;

        public static ArtistDTO of(Artist artist) {
            return ArtistDTO.builder()
                    .artistId(artist.getArtistId())
                    .name(artist.getName())
                    .engName(artist.getEngName())
                    .artistType(artist.getArtistType().getDescription())
                    .files(artist.getFiles().stream()
                            .map(FileDTO::of)
                            .collect(Collectors.toList()))
                    .build();
        }
    }
}
