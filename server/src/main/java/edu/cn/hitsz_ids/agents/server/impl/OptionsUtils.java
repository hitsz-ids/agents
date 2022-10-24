package edu.cn.hitsz_ids.agents.server.impl;

import edu.cn.hitsz_ids.agents.grpc.OpenOption;

import java.nio.file.StandardOpenOption;

public class OptionsUtils {
    public static StandardOpenOption[] getOpenOptions(OpenOption... options) {
        StandardOpenOption[] openOptions;
        if (options != null) {
            openOptions = new StandardOpenOption[options.length];
            for (int i = 0; i < options.length; i++) {
                switch (options[i]) {
                    case OP_READ -> openOptions[i] = StandardOpenOption.READ;
                    case OP_WRITE -> openOptions[i] = StandardOpenOption.WRITE;
                    case OP_APPEND -> openOptions[i] = StandardOpenOption.APPEND;
                }
            }
        } else {
            openOptions = new StandardOpenOption[0];
        }
        return openOptions;
    }
}
