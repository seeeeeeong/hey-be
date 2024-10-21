package hey.io.heybackend.domain.artist.enums;

import hey.io.heybackend.common.mapper.EnumMapperType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ArtistGenre implements EnumMapperType {

    BALLAD("발라드"),
    HIPHOP("힙합"),
    RNB("R&B"),
    EDM("EDM"),
    INDIE("인디"),
    ROCK("락"),
    JAZZ("재즈"),
    IDOL("아이돌"),
    ETC("기타");

    private final String description; // Enum 설명

    @Override
    public String getCode() {
        return name();
    }

}
