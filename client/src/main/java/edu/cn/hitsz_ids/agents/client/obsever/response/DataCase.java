package edu.cn.hitsz_ids.agents.client.obsever.response;

import edu.cn.hitsz_ids.agents.client.obsever.observer.Observer;
import edu.cn.hitsz_ids.agents.grpc.Request;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public abstract class DataCase<P, R> {
    protected final Request.Builder builder = Request.newBuilder();
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    R data;
    private boolean doing = false;
    public DataCase(P params) {
        assemble(builder, params);
    }

    public abstract void assemble(Request.Builder builder, P params);
    public R await(Observer observer, String id) throws IOException {
        lock.lock();
        try {
            observer.send(builder.setId(id).build());
            doing = true;
            var pass = false;
            while (doing) {
                pass = condition.await(60, TimeUnit.SECONDS);
                if (!pass) {
                    throw new IOException("请求超时");
                }
            }
            observer.isException();
            return data;
        } catch (InterruptedException e) {
            throw new IOException("线程异常中断");
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }finally {
            lock.unlock();
        }
    }

    public void single(R data) {
        lock.lock();
        try {
            doing = false;
            this.data = data;
            condition.signal();
        } finally {
            lock.unlock();
        }
    }
}
