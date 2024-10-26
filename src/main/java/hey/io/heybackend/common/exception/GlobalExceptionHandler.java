package hey.io.heybackend.common.exception;

import hey.io.heybackend.common.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 비즈니스 예외 처리
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<Object> handleBusinessException(BusinessException e) {
        log.error(e.toString(), e);
        return handleExceptionInternal(e.getErrorCode());
    }

    // 지원하지 않는 HTTP method를 호출할 경우
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupportedException(
        HttpRequestMethodNotSupportedException e) {
        log.error("HttpRequestMethodNotSupportedException : {}", e.getMessage());
        return handleExceptionInternal(ErrorCode.METHOD_NOT_ALLOWED);
    }

    // 존재하지 않는 URI에 접근할 경우
    @ExceptionHandler(NoResourceFoundException.class)
    protected ResponseEntity<Object> handleNoResourceFoundException() {
        return handleExceptionInternal(ErrorCode.API_NOT_FOUND);
    }

    // 쿼리 파라미터 없이 요청할 경우
    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<Object> handleMissingServletRequestParameterException() {
        return handleExceptionInternal(ErrorCode.QUERY_PARAMETER_REQUIRED);
    }

    // 매개변수 유효성 검증에 실패할 경우
    @ExceptionHandler(HandlerMethodValidationException.class)
    protected ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException e) {
        log.error(e.toString(), e);
        return handleExceptionInternal(e);
    }

    // @Valid 유효성 검증에 실패할 경우
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.toString(), e);
        return handleExceptionInternal(e);
    }

    // 그 밖에 발생하는 모든 예외 처리
    @ExceptionHandler(value = {Exception.class, RuntimeException.class, SQLException.class,
        DataIntegrityViolationException.class})
    protected ResponseEntity<Object> handleException(Exception e) {
        log.error(e.toString(), e);
        return handleExceptionInternal(e);
    }

    private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getHttpStatus()).body(ErrorResponse.of(errorCode));
    }

    private ResponseEntity<Object> handleExceptionInternal(HandlerMethodValidationException e) {
        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        return ResponseEntity.status(errorCode.getHttpStatus()).body(validErrorResponse(errorCode, e));
    }

    private ResponseEntity<Object> handleExceptionInternal(BindException e) {
        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        return ResponseEntity.status(errorCode.getHttpStatus()).body(validErrorResponse(errorCode, e));
    }

    private ResponseEntity<Object> handleExceptionInternal(Exception e) {
        ErrorCode errorCode = ErrorCode.INTERNAL_ERROR;
        return ResponseEntity.status(errorCode.getHttpStatus()).body(ErrorResponse.of(errorCode, e));
    }

    private ErrorResponse validErrorResponse(ErrorCode errorCode, HandlerMethodValidationException e) {
        String message = e.getAllValidationResults().stream()
            .map(ParameterValidationResult::getResolvableErrors)
            .flatMap(List::stream)
            .map(MessageSourceResolvable::getDefaultMessage)
            .collect(Collectors.joining(", "));
        return ErrorResponse.of(errorCode, message);
    }

    private ErrorResponse validErrorResponse(ErrorCode errorCode, BindException e) {
        List<ErrorResponse.ValidationError> validationErrorList = e.getBindingResult().getFieldErrors().stream()
            .map(ErrorResponse.ValidationError::of).collect(Collectors.toList());
        return ErrorResponse.of(errorCode, validationErrorList);
    }
}
