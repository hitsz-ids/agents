package edu.cn.hitsz_ids.agents.core.exception;

import java.io.IOException;

public class ClientException extends IOException {
    public ClientException(Throwable cause) {
        super(cause);
    }

    public ClientException(StackTraceElement[] stackTrace, String message) {
        super(message);
        setStackTrace(stackTrace);
    }
}
