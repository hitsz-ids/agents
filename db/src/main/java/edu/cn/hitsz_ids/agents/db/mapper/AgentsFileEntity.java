/*
 *
 */

package edu.cn.hitsz_ids.agents.db.mapper;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @author jianchuanli
 * @since 2022/5/5 10:21 上午
 */
@Data
@Builder
@TableName(AgentsFileEntity.Table.TABLE_NAME)
class AgentsFileEntity {
    public static class Table {
        public static final String TABLE_NAME = "agents_file";
        public static final String COLUMN_DIRECTORY = "directory";
        public static final String COLUMN_IDENTITY = "identity";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SIZE = "size";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_CREATED_TIME = "created_time";
        public static final String COLUMN_LAST_MODIFIED = "last_modified";
        public static final String COLUMN_BRIDGE = "bridge";
        public static final String COLUMN_PATH = "path";
    }
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField(Table.COLUMN_IDENTITY)
    private String identity;
    @TableField(Table.COLUMN_NAME)
    private String name;
    @TableField(Table.COLUMN_SIZE)
    private Long size;
    @TableField(Table.COLUMN_STATUS)
    private int status;
    @TableField(Table.COLUMN_DIRECTORY)
    private String directory;
    @TableField(Table.COLUMN_CREATED_TIME)
    private Date createdTime;
    @TableField(Table.COLUMN_LAST_MODIFIED)
    private Date lastModified;
    @TableField(Table.COLUMN_BRIDGE)
    private String bridge;
    @TableField(Table.COLUMN_PATH)
    private String path;
}