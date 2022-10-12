package edu.cn.hitsz_ids.agents.server;

import edu.cn.hitsz_ids.agents.db.config.DbConfig;
import edu.cn.hitsz_ids.agents.db.helper.Db;
import edu.cn.hitsz_ids.agents.server.impl.DiskBridge;
import edu.cn.hitsz_ids.agents.server.core.service.AgentsService;
import edu.cn.hitsz_ids.agents.utils.BridgeType;

import java.io.IOException;

public class Application {
    public static void main(String[] args) throws IOException, InterruptedException {
        AgentsService service = new AgentsService(10000);
        DbConfig dbConfig = new DbConfig();
        dbConfig.setDatabase("agents-db");
        dbConfig.setU("root");
        dbConfig.setP("root@123");
        dbConfig.setPort(3306);
        dbConfig.setHost("127.0.0.1");
        dbConfig.setDriver("com.mysql.cj.jdbc.Driver");
        Db.getInstance().init(dbConfig);
        service.registerBridge(BridgeType.DISK, DiskBridge.class);
        service.start();
        service.awaitTermination();
    }
}
