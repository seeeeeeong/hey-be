package hey.io.heybackend.domain.performance.dto;

import hey.io.heybackend.domain.performance.entity.PerformanceGenres;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PerformanceGenreElement {

    private String performanceGenre;

    public static PerformanceGenreElement from(PerformanceGenres genre) {
        return PerformanceGenreElement.builder()
                .performanceGenre(genre.getPerformanceGenre().getDescription())
                .build();
    }
}
