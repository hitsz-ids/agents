package edu.cn.hitsz_ids.agents.db.mapper;

import edu.cn.hitsz_ids.agents.db.helper.DbHandler;
import edu.cn.hitsz_ids.agents.db.pojo.params.CreateParams;
import edu.cn.hitsz_ids.agents.db.pojo.returns.AgentsFileReturns;
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

    public SearchPathReturns searchInfoByIdentity(String identity) {
        return mapper.queryPathInfo(identity);
    }

    public SearchPathReturns searchInfoByPath(String path) {
        return mapper.queryPathInfoByPath(path);
    }
    public AgentsFileReturns searchFile(String identity) {
        return AgentsFileReturns.builder().build();
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

    public void updateSizeById(Long id, long size) {
        mapper.updateSizeById(id, size);
    }
}
