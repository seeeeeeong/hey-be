package hey.io.heybackend.common.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import org.springframework.http.HttpStatus;

@JsonFormat(
        shape = Shape.OBJECT
)
public enum ErrorCode {
    INVALID_INPUT_VALUE(400, "C001", "잘못된 입력값입니다."),
    ENTITY_NOT_FOUND(400, "C002", "엔티티를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(500, "C003", "서버 오류"),
    PARSING_ERROR(500, "C004", "파싱에 실패했습니다."),

    TOKEN_EXPIRED(401, "J-001", "토큰이 만료되었습니다."),
    NOT_VALID_TOKEN(401, "J-002", "해당 토큰은 유효한 토큰이 아닙니다."),
    NOT_EXISTS_AUTHORIZATION(401, "J-003", "Authorization Header가 빈값입니다."),
    NOT_VALID_BEARER_GRANT_TYPE(401, "J-004", "인증 타입이 Bearer 타입이 아닙니다."),
    REFRESH_TOKEN_NOT_FOUND(401, "J-005", "해당 refresh token은 존재하지 않습니다."),
    REFRESH_TOKEN_EXPIRED(401, "J-006", "해당 refresh token은 만료됐습니다."),
    NOT_ACCESS_TOKEN_TYPE(401, "J-007", "해당 토큰은 ACCESS TOKEN이 아닙니다."),
    FORBIDDEN_ADMIN(403, "J-008", "관리자 Role이 아닙니다."),



    AUTHORITY_NOT_FOUND(404, "S001", "유저 권한이 없습니다."),
    INVALID_TOKEN(400, "S002", "유효하지 않은 토큰입니다."),
    JWT_ACCESS_TOKEN_NOT_FOUND(404, "S003", "jwt access token이 없습니다."),
    JWT_REFRESH_TOKEN_NOT_FOUND(404, "S004", "jwt refresh token이 없습니다."),
    EXPIRED_JWT_ACCESS_TOKEN(400, "S005", "jwt access token이 만료되었습니다."),
    EXPIRED_JWT_REFRESH_TOKEN(400, "S006", "jwt refresh token이 만료되었습니다."),
    AUTH_CODE_NOT_FOUND(404, "S007", "authorization header가 비었습니다."),
    JWT_TOKEN_NOT_FOUND(404, "S008", "jwt token이 없습니다."),


    PERFORMANCE_NOT_FOUND(404, "P001", "공연을 찾을 수 없습니다."),


    ARTIST_NOT_FOUND(404, "A001", "아티스트를 찾을 수 없습니다.");


    private final int status;
    private final String code;
    private final String message;

    private ErrorCode(final int status, final String code, final String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public int getStatus() {
        return this.status;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
