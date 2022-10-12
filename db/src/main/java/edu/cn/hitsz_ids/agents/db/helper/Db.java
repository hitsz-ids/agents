package edu.cn.hitsz_ids.agents.db.helper;

import edu.cn.hitsz_ids.agents.db.config.DbConfig;

import java.io.IOException;

public class Db {
    private volatile static Db instance;
    private DbHelper helper;

    private Db() {
    }

    public static Db getInstance() {
        if (instance == null) {
            synchronized (Db.class) {
                if (instance == null) {
                    instance = new Db();
                }
            }
        }
        return instance;
    }

    public void init(DbConfig config) throws IOException {
        helper = new DbHelper(config);
    }

    DbSession open() {
        return open(true);
    }

    DbSession open(boolean commit) {
        return new DbSession(helper.openSession(commit));
    }
}
