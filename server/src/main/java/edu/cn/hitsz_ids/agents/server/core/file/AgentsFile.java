package edu.cn.hitsz_ids.agents.server.core.file;

import lombok.Builder;
import lombok.Data;

import java.io.File;
import java.util.Date;

@Data
@Builder
public class AgentsFile {
    private long length;
    private String name;
    private String identity;
    private Long size;
    private String directory;
    private Date createdTime;
    private Date lastModified;
    private String bridge;
    private String path;
}
