package hey.io.heybackend.domain.artist.dto;

import hey.io.heybackend.domain.artist.enums.ArtistStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ArtistListResponse {

    private String name; // 아티스트명
    private ArtistStatus artistStatus; // 아티스트 상태

}
