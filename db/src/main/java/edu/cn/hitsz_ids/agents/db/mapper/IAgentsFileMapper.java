/*
 *
 */

package edu.cn.hitsz_ids.agents.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.cn.hitsz_ids.agents.db.pojo.returns.SearchPathReturns;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
interface IAgentsFileMapper extends BaseMapper<AgentsFileEntity> {
    String queryBridge(@Param("identity")String identity);
    SearchPathReturns queryPathInfo(@Param("identity")String identity);

    SearchPathReturns queryPathInfoByPath(@Param("path")String path);

    void updateSizeById(@Param("id") Long id, @Param("size") long size);
}