package edu.cn.hitsz_ids.agents.db.pojo.returns;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class SearchInfoReturns {
    private Long id;
    private String name;
    private String directory;
    private String bridge;
    private long size;
    private Date createdTime;
    private Date lastModified;
}
