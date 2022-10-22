package edu.cn.hitsz_ids.agents.server.core.bridge.bridge;

import edu.cn.hitsz_ids.agents.core.bridge.IBridgeType;
import edu.cn.hitsz_ids.agents.grpc.AgentsFile;
import edu.cn.hitsz_ids.agents.grpc.OpenOption;
import edu.cn.hitsz_ids.agents.server.core.bridge.chain.Chain;

import java.io.IOException;
import java.util.List;

public abstract class Bridge<C extends Chain> {
    protected final C chain;
    private final IBridgeType type;
    public Bridge(IBridgeType type) {
        this.type = type;
        chain = createChain();
    }

    protected abstract C createChain();

    public abstract AgentsFile.Builder open(String identity,
                                    String name,
                                    String directory,
                                    OpenOption... options) throws IOException;

    public abstract long position(long index) throws IOException;

    public abstract int write(byte[] bytes) throws IOException;

    public abstract AgentsFile.Builder create(String identity,
                                      String name,
                                      String directory) throws IOException;

    public abstract int read(byte[] bytes, int off, int length) throws IOException;

    public abstract void close() throws IOException;

    public abstract boolean delete(AgentsFile file) throws IOException;

    public abstract boolean delete(String identity,
                          String name,
                          String directory) throws IOException;

    public final String getName() {
        return type.getName();
    }

    public final String getMsg() {
        return type.getMsg();
    }

    public final IBridgeType getType() {
        return type;
    }

    public abstract List<AgentsFile> listFiles(String directory) throws IOException;

    public final void completed(boolean error) throws IOException {}
}
