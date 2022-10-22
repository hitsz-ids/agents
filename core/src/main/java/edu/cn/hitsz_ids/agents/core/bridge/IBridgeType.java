package edu.cn.hitsz_ids.agents.core.bridge;

public interface IBridgeType {
    String getName();

    String getMsg();

    default String getScheme() {
        return getName() + "://";
    }
}
