package edu.cn.hitsz_ids.agents.db.helper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public class DbHandler<T extends BaseMapper<?>> implements AutoCloseable {
    protected final T mapper;
    private final DbSession dbSession;

    public DbHandler(Class<T> clazz) {
        this(true, clazz);
    }
    public DbHandler(boolean commit, Class<T> clazz) {
        dbSession = Db.getInstance().open(commit);
        mapper = dbSession.getMapper(clazz);
    }

    @Override
    public void close() {
        dbSession.close();
    }

    public void commit() {
        dbSession.commit();
    }
}
