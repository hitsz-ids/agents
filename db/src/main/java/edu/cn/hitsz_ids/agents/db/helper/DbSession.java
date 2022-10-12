package edu.cn.hitsz_ids.agents.db.helper;

import org.apache.ibatis.session.SqlSession;

class DbSession {
    private final SqlSession session;
    public DbSession(SqlSession session) {
        this.session = session;
    }

    public <T> T getMapper(Class<T> clazz) {
        return session.getMapper(clazz);
    }

    public void close() {
        session.close();
    }

    public void commit() {
        session.commit(true);
    }

    public void rollback() {
        session.rollback(true);
    }
}
