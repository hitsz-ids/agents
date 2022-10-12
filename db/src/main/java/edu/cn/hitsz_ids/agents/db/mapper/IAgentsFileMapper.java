/*
 *
 */

package edu.cn.hitsz_ids.agents.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.cn.hitsz_ids.agents.db.pojo.returns.SearchPathReturns;
import org.apache.ibatis.annotations.Param;

interface IAgentsFileMapper extends BaseMapper<AgentsFileEntity> {
    String queryBridge(@Param("identity")String identity);
    SearchPathReturns queryPathInfo(@Param("identity")String identity);
}