package edu.cn.hitsz_ids.agents.utils;

import java.util.Objects;

public enum BridgeType implements IBridgeType {
    DISK("DISK", "磁盘"),;
    private final String name;
    private final String msg;

    BridgeType(String name, String msg) {
        this.name = name;
        this.msg = msg;
    }

    public String getName() {
        return name;
    }

    public String getMsg() {
        return msg;
    }
}
