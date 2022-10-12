package edu.cn.hitsz_ids.agents.db.mapper;

import edu.cn.hitsz_ids.agents.db.helper.DbHandler;
import edu.cn.hitsz_ids.agents.db.pojo.params.CreateParams;
import edu.cn.hitsz_ids.agents.db.pojo.returns.SearchPathReturns;

public class AgentsFileHandler extends DbHandler<IAgentsFileMapper> {
    public AgentsFileHandler(boolean commit) {
        super(commit, IAgentsFileMapper.class);
    }

    public AgentsFileHandler() {
        super(IAgentsFileMapper.class);
    }

    public String search(String identity) {
        return mapper.queryBridge(identity);
    }

    public SearchPathReturns searchPath(String identity) {
        return  mapper.queryPathInfo(identity);
    }

    public void create(CreateParams params) {
        mapper.insert(AgentsFileEntity.builder()
                .bridge(params.getBridge())
                .directory(params.getDirectory())
                .identity(params.getIdentity())
                .size(params.getSize())
                .name(params.getName())
                .build());
    }
}
