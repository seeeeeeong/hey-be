package hey.io.heybackend.domain.performance.dto;

import com.querydsl.core.annotations.QueryProjection;
import hey.io.heybackend.domain.artist.enums.ArtistType;
import hey.io.heybackend.domain.file.dto.FileDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PerformanceArtistResponse {

    private Long artistId;
    private String name;
    private String engName;
    private ArtistType artistType;
    private List<FileDTO> files;

    @QueryProjection
    public PerformanceArtistResponse(Long artistId, String name, String engName, ArtistType artistType) {
        this.artistId = artistId;
        this.name = name;
        this.engName = engName;
        this.artistType = artistType;
    }

    public void setFiles(List<FileDTO> files) {
        this.files = files;
    }


}
