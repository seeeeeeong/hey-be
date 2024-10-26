package hey.io.heybackend.common.util;

import hey.io.heybackend.common.exception.BusinessException;
import hey.io.heybackend.common.exception.ErrorCode;
import hey.io.heybackend.common.jwt.JwtProperties;
import hey.io.heybackend.common.jwt.constant.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HeaderUtils {


    public static String getJwtToken(HttpServletRequest request, TokenType tokenType) {
        String authorization = request.getHeader(JwtProperties.JWT_TOKEN_HEADER);

        if (Objects.isNull(authorization)) {
            if (tokenType == TokenType.ACCESS) {
                throw new BusinessException(ErrorCode.JWT_TOKEN_NOT_FOUND);
            } else if (tokenType == TokenType.REFRESH) {
                throw new BusinessException(ErrorCode.JWT_REFRESH_TOKEN_NOT_FOUND);
            } else if (tokenType == TokenType.BOTH) {
                throw new BusinessException(ErrorCode.JWT_TOKEN_NOT_FOUND);
            }
        }
        String[] tokens = StringUtils.delimitedListToStringArray(authorization, " ");

        if (tokens.length != 2 || !"Bearer".equals(tokens[0])) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        return tokens[1];
    }

    public static String getAuthCode(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (!StringUtils.hasText(authorization)) {
            throw new BusinessException(ErrorCode.AUTH_CODE_NOT_FOUND);
        }

        String[] tokens = StringUtils.delimitedListToStringArray(authorization, " ");
        if (tokens.length != 2 || !"Bearer".equals(tokens[0])) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
        return tokens[1];
    }
}
