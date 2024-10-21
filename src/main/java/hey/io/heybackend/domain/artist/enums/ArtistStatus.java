package hey.io.heybackend.domain.artist.enums;

import hey.io.heybackend.common.mapper.EnumMapperType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ArtistStatus implements EnumMapperType {

    INIT("아티스트 등록"),
    PATCH("정보 연동 완료"),
    ENABLE("활성화"),
    DISABLE("비활성화");

    private final String description; // Enum 설명

    @Override
    public String getCode() {
        return name();
    }

}
