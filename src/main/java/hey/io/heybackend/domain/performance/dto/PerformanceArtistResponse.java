package hey.io.heybackend.domain.performance.dto;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PerformanceArtistResponse {

    @Schema(description = "아티스트 ID", example = "1")
    private Long artistId;

    @Schema(description = "아티스트 명", example = "김문경")
    private String name;

    @QueryProjection
    public PerformanceArtistResponse(Long artistId, String name) {
        this.artistId = artistId;
        this.name = name;
    }

}
