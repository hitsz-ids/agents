/*
 *
 */

package edu.cn.hitsz_ids.agents.db.config;

import lombok.Data;


@Data
public abstract class Config {
    private String host;
    private int port;
}