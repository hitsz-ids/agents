package edu.cn.hitsz_ids.agents.db.pojo.params;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class CreateParams {
    private String identity;
    private String name;
    private Long size;
    private int status;
    private String directory;
    private Date lastModified;
    private String bridge;
}
