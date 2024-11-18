package hey.io.heybackend.domain.performance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import hey.io.heybackend.domain.artist.enums.ArtistType;
import hey.io.heybackend.domain.file.dto.FileDto;
import hey.io.heybackend.domain.performance.enums.PerformanceGenre;
import hey.io.heybackend.domain.performance.enums.PerformanceStatus;
import hey.io.heybackend.domain.performance.enums.PerformanceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class PerformanceDetailResDto {

    @Schema(description = "공연 ID", example = "87")
    private Long performanceId;

    @Schema(description = "공연 유형", example = "국내 콘서트")
    private PerformanceType performanceType;

    @Schema(description = "공연 장르", example = "힙합/R&B")
    private List<String> genres;

    @Schema(description = "공연 상태", example = "공연 예정")
    private PerformanceStatus performanceStatus;

    @Schema(description = "공연명", example = "현대카드 Curated 95, 한로로 X 윤마치 X QWER")
    private String performanceName;

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

    @Schema(description = "팔로우 여부", example = "false")
    private Boolean isFollowed;

    @Schema(description = "장소명", example = "현대카드 언더스테이지")
    private String placeName;

    @Schema(description = "주소", example = "서울특별시 용산구 이태원로 246 (한남동)")
    private String address;

    @Schema(description = "위도", example = "37.5365851")
    private double latitude;

    @Schema(description = "경도", example = "127.0006662")
    private double longitude;

    @Schema(description = "파일 목록", nullable = true)
    private List<FileDto> files;

    @Schema(description = "가격 목록")
    private List<PerformanceDetailResDto.PerformancePriceDto> prices;

    @Schema(description = "예매 목록")
    private List<PerformanceDetailResDto.PerformanceTicketingDto> ticketings;

    @Schema(description = "아티스트 목록")
    private List<PerformanceDetailResDto.PerformanceArtistDto> artists;

    public void setGenres(List<PerformanceDetailResDto.PerformanceGenreDto> genres) {
        this.genres = convertGenres(genres);
    }

    private static List<String> convertGenres(List<PerformanceDetailResDto.PerformanceGenreDto> genres) {
        return genres.stream()
                .map(performanceGenre -> {
                    PerformanceGenre genre = performanceGenre.performanceGenre;
                    if (genre == PerformanceGenre.HIPHOP || genre == PerformanceGenre.RNB) {
                        // HIPHOP과 RNB 장르를 힙합/R&B로 통합하여 표현
                        return "힙합/R&B";
                    } else if (genre == PerformanceGenre.INDIE || genre == PerformanceGenre.ROCK) {
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
    public static class PerformanceGenreDto {
        private PerformanceGenre performanceGenre;

    }

    @Getter
    @AllArgsConstructor
    public static class PerformancePriceDto {

        @Schema(description = "공연 가격 ID", example = "112")
        private Long priceId;

        @Schema(description = "가격 설명", example = "전석 스탠딩")
        private String priceInfo;

        @Schema(description = "금액", example = "88000")
        private Integer priceAmount;

    }

    @Getter
    @AllArgsConstructor
    public static class PerformanceTicketingDto {

        @Schema(description = "티케팅 ID", example = "116")
        private Long ticketingId;

        @Schema(description = "예매처", example = "MELON")
        private String ticketingBooth;

        @Schema(description = "티켓 프리미엄", example = "아티스트 선예매")
        private String ticketingPremium;

        @Schema(description = "티켓 오픈 시간", example = "2024.10.01 00:00")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm")
        private LocalDateTime openDatetime;

        @Schema(description = "예매 URL", example = "https://ticket.melon.com/performance/index.htm?prodId=210451")
        private String ticketingUrl;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PerformanceArtistDto {

        @Schema(description = "아티스트 ID", example = "8")
        private Long artistId;

        @Schema(description = "아티스트명", example = "윤지영")
        private String name;

        @Schema(description = "아티스트 영문명", example = "Yoon Jiyoung")
        private String engName;

        @Schema(description = "아티스트 유형", example = "솔로")
        private ArtistType artistType;

        @Schema(description = "파일 목록", nullable = true)
        private List<FileDto> files;

    }
}
