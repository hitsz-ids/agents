package edu.cn.hitsz_ids.agents.utils;

public interface IBridgeType {
    public String getName();

    public String getMsg();

    default String getScheme() {
        return getName() + "://";
    }
}
