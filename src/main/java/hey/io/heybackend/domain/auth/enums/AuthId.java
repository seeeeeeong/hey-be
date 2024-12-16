package hey.io.heybackend.domain.auth.enums;

import hey.io.heybackend.common.mapper.EnumMapperType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthId implements EnumMapperType {

    SUPER_ADMIN("슈퍼 관리자 권한"),
    PERFORMANCE_MANAGER("공연 담당자 권한"),
    ARTIST_MANAGER("아티스트 담당자 권한"),
    PUSH_MANAGER("PUSH 알림 담당자 권한"),
    MEMBER_SNS("SNS 로그인 권한"),
    IS_AUTHENTICATED_FULLY("인증 완료"),
    ANONYMOUS("익명 권한")
    ;

    private final String description; // Enum 설명

    @Override
    public String getCode() {
        return name();

    }

}
