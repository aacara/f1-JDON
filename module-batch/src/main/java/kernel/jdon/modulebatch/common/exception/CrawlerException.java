package kernel.jdon.modulebatch.common.exception;

import kernel.jdon.modulecommon.error.ErrorCode;
import lombok.Getter;

@Getter
public class CrawlerException extends RuntimeException {
    private final transient ErrorCode errorCode;

    public CrawlerException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
