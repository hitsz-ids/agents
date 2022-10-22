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
@TableName("agents_file")
class AgentsFileEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("identity")
    private String identity;
    @TableField("name")
    private String name;
    @TableField("size")
    private Long size;
    @TableField("status")
    private int status;
    @TableField("directory")
    private String directory;
    @TableField("created_time")
    private Date createdTime;
    @TableField("last_modified")
    private Date lastModified;
    @TableField("bridge")
    private String bridge;
}