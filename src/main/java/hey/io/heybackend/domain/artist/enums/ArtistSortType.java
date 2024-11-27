package hey.io.heybackend.domain.artist.enums;

import hey.io.heybackend.common.mapper.EnumMapperType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ArtistSortType implements EnumMapperType {
    NAME_ASC("아티스트명 가나다순");
//    OPEN_CLOSER("티켓 오픈 임박순");

    private final String description; // Enum 설명

    @Override
    public String getCode() {
        return name();
    }
}
