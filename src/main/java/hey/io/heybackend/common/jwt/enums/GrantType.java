package hey.io.heybackend.common.jwt.enums;

import hey.io.heybackend.common.mapper.EnumMapperType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GrantType implements EnumMapperType {

    BEARER("Bearer");

    private final String description; // Enum 설명

    @Override
    public String getCode() {
        return null;
    }
}
