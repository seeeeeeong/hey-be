package hey.io.heybackend.domain.file.enums;

import hey.io.heybackend.common.mapper.EnumMapperType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileType implements EnumMapperType {
    IMAGE("이미지"),
    AUDIO("오디오"),
    VIDEO("비디오");

    private final String description; // Enum 설명

    @Override
    public String getCode() {
        return name();
    }
}
