package hey.io.heybackend.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import hey.io.heybackend.common.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.FieldError;

import java.util.List;

@Getter
@Builder
public class ErrorResponse {

    private String code;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ValidationError> errors;

    @Getter
    @Builder
    public static class ValidationError {

        private final String field;
        private final String message;

        public static ValidationError of(final FieldError fieldError) {
            return ValidationError.builder()
                    .field(fieldError.getField())
                    .message(fieldError.getDefaultMessage())
                    .build();
        }
    }

    private static ErrorResponse get(String code, String message, List<ValidationError> errors) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .errors(errors)
                .build();
    }

    public static ErrorResponse of(ErrorCode errorCode) {
        return get(errorCode.toString(), errorCode.getMessage(), null);
    }

    public static ErrorResponse of(ErrorCode errorCode, Exception e) {
        return get(errorCode.toString(), errorCode.getMessage(e), null);
    }

    public static ErrorResponse of(ErrorCode errorCode, String message) {
        return get(errorCode.toString(), errorCode.getMessage() + " - " + message, null);
    }

    public static ErrorResponse of(ErrorCode errorCode, List<ValidationError> errors) {
        return get(errorCode.toString(), errorCode.getMessage(), errors);
    }

}