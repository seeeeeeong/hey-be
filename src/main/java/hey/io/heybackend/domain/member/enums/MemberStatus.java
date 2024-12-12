package hey.io.heybackend.domain.member.enums;

import hey.io.heybackend.common.mapper.EnumMapperType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberStatus implements EnumMapperType {
    INIT("등록"),

    ACTIVE("활성화"),
    LOCKED("잠금"),
    QUIT("탈퇴"),
    DELETED("삭제");

    private final String description; // Enum 설명

    @Override
    public String getCode() {
        return name();
    }

}
