package hey.io.heybackend.domain.artist.dto;

import hey.io.heybackend.domain.artist.enums.ArtistType;
import hey.io.heybackend.domain.file.dto.FileDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ArtistListResDto {

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
