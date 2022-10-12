/*
 *
 */

package edu.cn.hitsz_ids.agents.db.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class DbConfig extends Config {
    private String driver;
    private String database;
    private String u;
    private String p;
}