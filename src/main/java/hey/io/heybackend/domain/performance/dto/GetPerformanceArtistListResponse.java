package hey.io.heybackend.domain.performance.dto;

import com.querydsl.core.annotations.QueryProjection;
import hey.io.heybackend.domain.artist.entity.Artist;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetPerformanceArtistListResponse {

    private Long artistId;
    private String name;

    @QueryProjection
    public GetPerformanceArtistListResponse(Long artistId, String name) {
        this.artistId = artistId;
        this.name = name;
    }

}
