package hey.io.heybackend.common.exception.badrequest;


import hey.io.heybackend.common.exception.BusinessException;
import hey.io.heybackend.common.exception.ErrorCode;

public class InvalidParameterException extends BusinessException {

    public InvalidParameterException(ErrorCode errorCode) {
        super(errorCode);
    }
}
