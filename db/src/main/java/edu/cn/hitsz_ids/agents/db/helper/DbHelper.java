package edu.cn.hitsz_ids.agents.db.helper;

import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import edu.cn.hitsz_ids.agents.db.config.DbConfig;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

class DbHelper {
    private static final String TEMPLATE = "jdbc:mysql://%s:%s/%s?zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&rewriteBatchedStatements=true&tinyInt1isBit=false&useSSL=false";
    private SqlSessionFactory sqlSessionFactory;

    public DbHelper(DbConfig config) throws IOException {
        initDbConfig(config);
    }

    public SqlSession openSession(boolean autoCommit) {
        return sqlSessionFactory.openSession(autoCommit);
    }

    public void initDbConfig(DbConfig config) throws IOException {
        Properties properties = new Properties();
        properties.put("jdbc.driver", config.getDriver());
        properties.put("jdbc.url", String.format(TEMPLATE,
                config.getHost(),
                config.getPort(),
                config.getDatabase()));
        properties.put("jdbc.username", config.getU());
        properties.put("jdbc.password", config.getP());
        String resource = "db-config.xml";
        try (InputStream inputStream = Resources.getResourceAsStream(resource)) {
            sqlSessionFactory = new MybatisSqlSessionFactoryBuilder().build(inputStream, properties);
        }
    }
}
