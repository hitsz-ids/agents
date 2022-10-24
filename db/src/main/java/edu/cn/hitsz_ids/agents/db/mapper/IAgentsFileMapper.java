/*
 *
 */

package edu.cn.hitsz_ids.agents.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.cn.hitsz_ids.agents.db.pojo.returns.SearchInfoReturns;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
interface IAgentsFileMapper extends BaseMapper<AgentsFileEntity> {
    String queryBridge(@Param("identity") String identity);

    SearchInfoReturns queryPathInfo(@Param("identity") String identity);

    void updateSizeById(@Param("id") Long id, @Param("size") long size);

    List<String> queryDir(@Param("directory") String directory);
}