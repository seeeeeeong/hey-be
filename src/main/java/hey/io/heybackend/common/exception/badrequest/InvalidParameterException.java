package hey.io.heybackend.common.exception.badrequest;

import hey.io.heyscheduler.common.exception.BusinessException;
import hey.io.heyscheduler.common.exception.ErrorCode;

public class InvalidParameterException extends BusinessException {

    public InvalidParameterException(ErrorCode errorCode) {
        super(errorCode);
    }
}
