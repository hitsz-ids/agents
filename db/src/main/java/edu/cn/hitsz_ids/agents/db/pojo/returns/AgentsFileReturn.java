package edu.cn.hitsz_ids.agents.db.pojo.returns;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
public class AgentsFileReturn {
    private String identity;
    private String name;
    private Long size;
    private Date createdTime;
    private Date lastModified;
    private String bridge;
    private String isDirectory;
}
