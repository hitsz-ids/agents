package edu.cn.hitsz_ids.agents.core.exception;

import java.io.IOException;

public class ServerException extends IOException {
    public ServerException(Throwable cause) {
        super(cause);
    }

    public ServerException(StackTraceElement[] stackTrace, String message) {
        super(message);
        setStackTrace(stackTrace);
    }

    public ServerException(String message) {
        super(message);
    }
}
