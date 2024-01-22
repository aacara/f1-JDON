package kernel.jdon.global.exception;

import kernel.jdon.error.ErrorCode;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class UnAuthorizedException extends AuthenticationException {
	private final transient ErrorCode errorCode;

	public UnAuthorizedException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}