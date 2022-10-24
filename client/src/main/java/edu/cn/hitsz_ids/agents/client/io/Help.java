package edu.cn.hitsz_ids.agents.client.io;

import edu.cn.hitsz_ids.agents.core.bridge.IOType;
import edu.cn.hitsz_ids.agents.grpc.*;
import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLException;
import java.util.List;

@Slf4j
public class Help {
    HelpServiceGrpc.HelpServiceBlockingStub stub;
    private volatile static Help instance;

    public static Help getInstance() {
        if (instance == null) {
            synchronized (Help.class) {
                if (instance == null) {
                    instance = new Help();
                }
            }
        }
        return instance;
    }

    Help() {
        try {
            ManagedChannel channel = Connector.channel();
            stub = HelpServiceGrpc.newBlockingStub(channel);
        } catch (SSLException e) {
            log.error(e.getMessage(), e);
        }
    }

    List<AgentsFile> listFiles(String directory) {
        Metadata header = new Metadata();
        header.put(AgentsMetadata.TYPE, IOType.SKIP);
        return stub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(header))
                .listFiles(ListRequest.newBuilder()
                        .setDirectory(directory)
                        .buildPartial()).getFilesList();
    }

    boolean delete(String uri) {
        Metadata header = new Metadata();
        header.put(AgentsMetadata.TYPE, IOType.SEARCH);
        header.put(AgentsMetadata.URI, uri);
        return stub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(header))
                .delete(DeleteRequest.newBuilder()
                        .buildPartial()).getSuccess();

    }
}
