package edu.cn.hitsz_ids.agents.server.core.utils;

import edu.cn.hitsz_ids.agents.grpc.OpenOption;

import java.util.List;

public class OpenOptionUtils {
    public static OpenOption[] toArray(List<OpenOption> list) {
        if (list == null) {
            return new OpenOption[0];
        }
        OpenOption[] options = new OpenOption[list.size()];
        for (int i = 0; i < options.length; i++) {
            options[i] = list.get(i);
        }
        return options;
    }
}
