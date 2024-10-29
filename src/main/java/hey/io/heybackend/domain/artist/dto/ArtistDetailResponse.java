package hey.io.heybackend.domain.artist.dto;

import hey.io.heybackend.domain.artist.entity.Artist;
import hey.io.heybackend.domain.artist.enums.ArtistType;
import hey.io.heybackend.domain.file.dto.FileDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ArtistDetailResponse {

    @Schema(description = "아티스트 ID", example = "1")
    private Long artistId; // 아티스트의 고유 ID

    @Schema(description = "아티스트 이름", example = "윤지영")
    private String name; // 아티스트의 이름

    @Schema(description = "아티스트 영문 이름", example = "Yoon Jiyoung")
    private String engName; // 아티스트의 영문 이름

    @Schema(description = "아티스트 유형", example = "SOLO",
            allowableValues = {"GROUP", "SOLO"})
    private String artistType; // 아티스트 유형 (그룹 또는 솔로)

    @Schema(description = "아티스트 URL", example = "http://example.com/artist/yoonjiyoung")
    private String artistUrl; // 아티스트의 웹사이트 URL

    @Schema(description = "인기 지수", example = "85")
    private Integer popularity; // 아티스트의 인기 지수

    @Schema(description = "장르 리스트", example = "[\"발라드\", \"힙합\"]")
    private List<String> genres; // 아티스트의 장르

    @Schema(description = "파일 정보 리스트",
            example = "[{\"fileId\": 1, \"fileCategory\": \"THUMBNAIL\", \"fileName\": \"example.png\", \"fileUrl\": \"http://example.com/image.png\", \"width\": 640, \"height\": 640}]")
    private List<FileDTO> files; // 아티스트의 파일 정보

    public static ArtistDetailResponse of(Artist artist, List<String> genreList, List<FileDTO> fileList) {
        return ArtistDetailResponse.builder()
                .artistId(artist.getArtistId())
                .name(artist.getName())
                .engName(artist.getEngName())
                .artistType(artist.getArtistType().getDescription())
                .artistUrl(artist.getArtistUrl())
                .popularity(artist.getPopularity())
                .genres(genreList)
                .files(fileList)
                .build();
    }

}
