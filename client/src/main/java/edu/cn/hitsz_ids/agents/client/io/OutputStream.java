package edu.cn.hitsz_ids.agents.client.io;

import edu.cn.hitsz_ids.agents.client.obsever.observer.Writer;
import edu.cn.hitsz_ids.agents.grpc.AgentsMetadata;
import edu.cn.hitsz_ids.agents.grpc.OpenOption;
import edu.cn.hitsz_ids.agents.grpc.Request;
import edu.cn.hitsz_ids.agents.grpc.StreamGrpc;
import edu.cn.hitsz_ids.agents.utils.IBridgeType;
import edu.cn.hitsz_ids.agents.utils.IOType;
import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.UUID;

public class OutputStream extends java.io.OutputStream {
    private final ManagedChannel channel;
    private final StreamGrpc.StreamStub stub;
    private final Writer writer;
    private boolean closed;
    private final Object closeLock = new Object();

    private OutputStream(IBridgeType IBridgeType) throws IOException {
        channel = Connector.channel();
        Metadata header = new Metadata();
        header.put(AgentsMetadata.TYPE, IOType.CREATE);
        header.put(AgentsMetadata.BRIDGE, IBridgeType.getName());
        stub = StreamGrpc.newStub(channel).withInterceptors(MetadataUtils.newAttachHeadersInterceptor(header));
        writer = new Writer();
    }

    public OutputStream(String uri, OpenOption... options) throws IOException {
        channel = Connector.channel();
        Metadata header = new Metadata();
        header.put(AgentsMetadata.TYPE, IOType.SEARCH);
        header.put(AgentsMetadata.URI, uri);
        stub = StreamGrpc.newStub(channel).withInterceptors(MetadataUtils.newAttachHeadersInterceptor(header));
        writer = new Writer();
        writer.open(options);
    }

    public OutputStream(IBridgeType IBridgeType,
                        String name,
                        String directory,
                        String identity,
                        OpenOption... options) throws IOException {
        this(IBridgeType);
        StreamObserver<Request> sender = stub.output(writer);
        writer.setSender(sender);
        writer.create(name, directory, identity, options);
    }

    public OutputStream(IBridgeType IBridgeType,
                        String name,
                        String directory,
                        String identity) throws IOException {
        this(IBridgeType,
                name,
                directory,
                identity,
                OpenOption.OP_READ,
                OpenOption.OP_WRITE
        );
    }

    public OutputStream(IBridgeType IBridgeType,
                        String name,
                        String directory) throws IOException {
        this(IBridgeType,
                name,
                directory,
                UUID.randomUUID().toString(),
                OpenOption.OP_READ,
                OpenOption.OP_WRITE
        );
    }

    public OutputStream(IBridgeType IBridgeType,
                        String name) throws IOException {
        this(IBridgeType, name, "", UUID.randomUUID().toString());
    }

    @Override
    public void write(int b) throws IOException {

    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        writer.write(b, off, len);
    }

    @Override
    public void close() throws IOException {
        synchronized (closeLock) {
            if (closed) {
                return;
            }
            writer.close();
            channel.shutdown();
            closed = true;
        }
    }
}
