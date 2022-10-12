
package edu.cn.hitsz_ids.agents.db.helper;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.datasource.DataSourceFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;
@Slf4j
public class DruidHelper implements DataSourceFactory {

    private Properties properties;

    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public DataSource getDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(properties.getProperty("url"));
        dataSource.setUsername(properties.getProperty("u"));
        dataSource.setPassword(properties.getProperty("p"));
        dataSource.setQueryTimeout(30);
        try {
            dataSource.init();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return dataSource;
    }

}
