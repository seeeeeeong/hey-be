package hey.io.heybackend.domain.performance.dto;

import hey.io.heybackend.domain.artist.entity.Artist;
import hey.io.heybackend.domain.artist.enums.ArtistType;
import hey.io.heybackend.domain.file.dto.FileDTO;
import hey.io.heybackend.domain.performance.entity.Performance;
import hey.io.heybackend.domain.performance.entity.PerformancePrice;
import hey.io.heybackend.domain.performance.entity.PerformanceTicketing;
import hey.io.heybackend.domain.performance.entity.Place;
import hey.io.heybackend.domain.performance.enums.PerformanceType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class PerformanceDetailResponse {

    private PerformanceDTO performance;
    private Boolean isFollow;
    private PlaceDTO place;
    private List<PerformancePriceDTO> prices;
    private List<PerformanceTicketingDTO> ticketings;
    private List<ArtistDTO> artists;



    public static PerformanceDetailResponse from(Performance performance, List<Artist> PerformanceArtist) {
        return PerformanceDetailResponse.builder()
                .performance(PerformanceDTO.from(performance))
                .isFollow(false)
                .place(PlaceDTO.from(performance.getPlace()))
                .prices(performance.getPrices().stream()
                        .map(price -> PerformancePriceDTO.from(price))
                        .collect(Collectors.toList()))

                .ticketings(performance.getTicketings().stream()
                        .map(ticketing -> PerformanceTicketingDTO.from(ticketing))
                        .collect(Collectors.toList()))

                .artists(PerformanceArtist.stream()
                        .map(artist -> ArtistDTO.from(artist))
                        .collect(Collectors.toList()))

                .build();
    }

    public void setIsFollow(Boolean isFollow) {
        this.isFollow = isFollow;
    }

    @Getter
    @Builder
    public static class PerformanceDTO {
        private Long performanceId;
        private PerformanceType performanceType;
        private List<String> genres;
        private String name;
        private String engName;
        private LocalDate startDate;
        private LocalDate endDate;
        private List<FileDTO> files;

        public static PerformanceDTO from(Performance performance) {
            return PerformanceDTO.builder()
                    .performanceId(performance.getPerformanceId())
                    .performanceType(performance.getPerformanceType())

                    .genres(performance.getGenres().stream()
                            .map(genre -> genre.getPerformanceGenre().name())
                            .collect(Collectors.toList()))

                    .name(performance.getName())
                    .engName(performance.getEngName())
                    .startDate(performance.getStartDate())
                    .endDate(performance.getEndDate())

                    .files(performance.getFiles().stream()
                            .map(file -> FileDTO.from(file))
                            .collect(Collectors.toList()))

                    .build();
        }
    }

    @Getter
    @Builder
    public static class PlaceDTO {
        private Long placeId;
        private String name;
        private String address;
        private double latitude;
        private double longitude;


        public static PlaceDTO from(Place place) {
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
        private Long priceId;
        private String priceInfo;
        private Integer priceAmount;

        public static PerformancePriceDTO from(PerformancePrice price) {
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
        private Long ticketingId;
        private String ticketingBooth;
        private String ticketingPremium;
        private LocalDateTime openDatetime;
        private String ticketingUrl;

        public static PerformanceTicketingDTO from(PerformanceTicketing ticketing) {
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
        private Long artistId;
        private String name;
        private String engName;
        private ArtistType artistType;
        private List<FileDTO> files;


        public static ArtistDTO from(Artist artist) {
            return ArtistDTO.builder()
                    .artistId(artist.getArtistId())
                    .name(artist.getName())
                    .engName(artist.getEngName())
                    .artistType(artist.getArtistType())

                    .files(artist.getFiles().stream()
                            .map(file -> FileDTO.from(file))
                            .collect(Collectors.toList()))

                    .build();
        }
    }


}
