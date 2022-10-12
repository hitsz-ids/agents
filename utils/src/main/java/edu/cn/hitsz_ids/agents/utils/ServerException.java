package edu.cn.hitsz_ids.agents.utils;

import java.io.IOException;

public class ServerException extends IOException {
    public ServerException(Throwable cause) {
        super(cause);
    }

    public ServerException(String message) {
        super(message);
    }
}
