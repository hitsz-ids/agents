package edu.cn.hitsz_ids.agents.db.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import edu.cn.hitsz_ids.agents.db.helper.DbHandler;
import edu.cn.hitsz_ids.agents.db.pojo.params.CreateParams;
import edu.cn.hitsz_ids.agents.db.pojo.returns.AgentsFileReturn;
import edu.cn.hitsz_ids.agents.db.pojo.returns.SearchInfoReturns;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AgentsFileHandler extends DbHandler<IAgentsFileMapper> {
    public AgentsFileHandler(boolean commit) {
        super(commit, IAgentsFileMapper.class);
    }

    public AgentsFileHandler() {
        super(IAgentsFileMapper.class);
    }

    public List<String> selectDir(String directory, String bridgeType) {
        return mapper.queryDir(directory, bridgeType);
    }

    public List<AgentsFileReturn> selectFilesByDirectory(String directory, String bridge) {
        QueryWrapper<AgentsFileEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper = queryWrapper.eq(AgentsFileTable.COLUMN_DIRECTORY
                , directory);
        if(!StringUtils.isEmpty(bridge)) {
            queryWrapper = queryWrapper.eq(AgentsFileTable.COLUMN_BRIDGE, bridge);
        }
        var list = mapper.selectList(queryWrapper);
        List<AgentsFileReturn> returns = new ArrayList<>();
        for (var agentsFileEntity : list) {
            returns.add(AgentsFileReturn.builder()
                    .name(agentsFileEntity.getName())
                    .size(agentsFileEntity.getSize())
                    .bridge(agentsFileEntity.getBridge())
                    .createdTime(agentsFileEntity.getCreatedTime())
                    .lastModified(agentsFileEntity.getLastModified())
                    .identity(agentsFileEntity.getIdentity())
                    .build());
        }
        return returns;
    }

    public SearchInfoReturns searchInfoByIdentity(String identity) {
        return mapper.queryPathInfo(identity);
    }

    public void deleteByIdentity(String identity) {
        mapper.delete(new QueryWrapper<AgentsFileEntity>()
                .eq(AgentsFileEntity.Table.COLUMN_IDENTITY, identity));
    }

    public void create(CreateParams params) {
        mapper.insert(AgentsFileEntity.builder()
                .bridge(params.getBridge())
                .directory(params.getDirectory())
                .identity(params.getIdentity())
                .size(params.getSize())
                .path(params.getPath())
                .name(params.getName())
                .createdTime(new Date())
                .lastModified(new Date())
                .build());
    }

    public void updateSizeById(Long id, long size) {
        mapper.updateSizeById(id, size);
    }
}
