package edu.cn.hitsz_ids.agents.client.io;

import edu.cn.hitsz_ids.agents.client.obsever.observer.Writer;
import edu.cn.hitsz_ids.agents.core.bridge.IBridgeType;
import edu.cn.hitsz_ids.agents.core.bridge.IOType;
import edu.cn.hitsz_ids.agents.grpc.AgentsFile;
import edu.cn.hitsz_ids.agents.grpc.AgentsMetadata;
import edu.cn.hitsz_ids.agents.grpc.OpenOption;
import edu.cn.hitsz_ids.agents.grpc.StreamGrpc;
import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;

import java.io.IOException;
import java.util.UUID;

public class OutputStream extends java.io.OutputStream {
    private final ManagedChannel channel;
    private final StreamGrpc.StreamStub stub;
    private final Writer writer;
    private boolean closed;
    private final Object closeLock = new Object();
    private AgentsFile file;

    private OutputStream(IBridgeType IBridgeType, String identity) throws IOException {
        channel = Connector.channel();
        var header = new Metadata();
        header.put(AgentsMetadata.TYPE, IOType.CREATE);
        header.put(AgentsMetadata.BRIDGE, IBridgeType.getName());
        header.put(AgentsMetadata.IDENTITY, identity);
        stub = StreamGrpc.newStub(channel).withInterceptors(MetadataUtils.newAttachHeadersInterceptor(header));
        writer = new Writer();
    }

    OutputStream(String uri, OpenOption option) throws IOException {
        channel = Connector.channel();
        var header = new Metadata();
        header.put(AgentsMetadata.TYPE, IOType.SEARCH);
        header.put(AgentsMetadata.URI, uri);
        stub = StreamGrpc.newStub(channel).withInterceptors(MetadataUtils.newAttachHeadersInterceptor(header));
        writer = new Writer();
        var sender = stub.output(writer);
        writer.setSender(sender);
        open(option);
    }

    private void open(OpenOption option) throws IOException {
        var open = writer.open(option);
        file = open.getFile();
    }

    OutputStream(IBridgeType IBridgeType,
                 String name,
                 String directory,
                 String identity,
                 OpenOption option) throws IOException {
        this(IBridgeType, identity);
        var sender = stub.output(writer);
        writer.setSender(sender);
        writer.create(name, directory, identity);
        open(option);
    }


    OutputStream(IBridgeType IBridgeType,
                 String name,
                 String directory,
                 OpenOption option) throws IOException {
        this(IBridgeType, name, directory, UUID.randomUUID().toString(), option);
    }

    OutputStream(IBridgeType IBridgeType,
                 String name,
                 OpenOption option) throws IOException {
        this(IBridgeType, name, "", UUID.randomUUID().toString(), option);
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

    public void position(long index) throws IOException {
        writer.position(index);
    }

    public String getUri() {
        return file.getUri();
    }

    public long getSize() {
        return file.getSize();
    }

}
