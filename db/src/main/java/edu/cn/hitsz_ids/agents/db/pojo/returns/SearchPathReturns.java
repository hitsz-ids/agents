package edu.cn.hitsz_ids.agents.db.pojo.returns;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchPathReturns {
    private Long id;
    private String name;
    private String directory;
    private String bridge;
    private long size;
}
