package edu.cn.hitsz_ids.agents.server.core.utils;

import org.apache.commons.lang3.StringUtils;

public class PathUtils {
    public static final String BASE_DIR = "/opt/data";
    public static String extension(String name) {
        if (StringUtils.isBlank(name)) {
            return "";
        }
        int index = name.lastIndexOf(".");
        if (index == -1) {
            return "";
        } else {
            return name.substring(index);
        }
    }
}
