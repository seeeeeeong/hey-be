package hey.io.heybackend.domain.main.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import hey.io.heybackend.domain.file.dto.FileDto;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class HomeResDto {

    private List<TopRatedPerformanceDto> topRatedPerformances;
    private List<TopRatedArtistDto> topRatedArtists;
    private List<NewPerformanceDto> newPerformances;

    @Builder
    public HomeResDto(List<TopRatedPerformanceDto> topRatedPerformances,
                      List<TopRatedArtistDto> topRatedArtists,
                      List<NewPerformanceDto> newPerformances) {
        this.topRatedPerformances = topRatedPerformances;
        this.topRatedArtists = topRatedArtists;
        this.newPerformances = newPerformances;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class TopRatedPerformanceDto {
        private Long performanceId;
        private String performanceName;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
        private LocalDate startDate;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
        private LocalDate endDate;
        private List<FileDto> files;
        private int rank;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class TopRatedArtistDto {
        private Long artistId;
        private String artistName;
        private List<FileDto> files;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class NewPerformanceDto {
        private Long performanceId;
        private String performanceName;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
        private LocalDate startDate;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
        private LocalDate endDate;
        private List<FileDto> files;
    }
}
