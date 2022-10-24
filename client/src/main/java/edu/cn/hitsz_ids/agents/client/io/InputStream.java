package edu.cn.hitsz_ids.agents.client.io;

import edu.cn.hitsz_ids.agents.client.obsever.observer.Reader;
import edu.cn.hitsz_ids.agents.core.bridge.IOType;
import edu.cn.hitsz_ids.agents.grpc.OpenOption;
import edu.cn.hitsz_ids.agents.core.BridgeType;
import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import io.grpc.stub.StreamObserver;
import edu.cn.hitsz_ids.agents.grpc.AgentsMetadata;
import edu.cn.hitsz_ids.agents.grpc.Request;
import edu.cn.hitsz_ids.agents.grpc.StreamGrpc;

import java.io.IOException;

public class InputStream extends java.io.InputStream {
    private final ManagedChannel channel;
    private final Reader reader;
    private boolean closed;
    private final Object closeLock = new Object();

    public InputStream(String path) throws IOException {
        channel = Connector.channel();
        var header = new Metadata();
        header.put(AgentsMetadata.TYPE, IOType.SEARCH);
        header.put(AgentsMetadata.URI, path);
        var stub = StreamGrpc.newStub(channel)
                .withInterceptors(MetadataUtils.newAttachHeadersInterceptor(header));
        reader = new Reader();
        var sender = stub.input(reader);
        reader.setSender(sender);
        reader.open(OpenOption.OP_READ);
    }

    @Override
    public int read() throws IOException {
        if (closed) {
            throw new IOException("文件流已经关闭");
        }
        return reader.read(new byte[1], 0, 1);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (closed) {
            throw new IOException("文件流已经关闭");
        }
        return reader.read(b, off, len);
    }

    @Override
    public void close() throws IOException {
        synchronized (closeLock) {
            if (closed) {
                return;
            }
            reader.close();
            channel.shutdown();
            closed = true;
        }
    }
}
