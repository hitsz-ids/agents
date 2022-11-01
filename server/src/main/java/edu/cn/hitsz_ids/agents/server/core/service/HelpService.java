package edu.cn.hitsz_ids.agents.server.core.service;

import edu.cn.hitsz_ids.agents.db.mapper.AgentsFileHandler;
import edu.cn.hitsz_ids.agents.db.pojo.returns.AgentsFileReturn;
import edu.cn.hitsz_ids.agents.db.pojo.returns.SearchInfoReturns;
import edu.cn.hitsz_ids.agents.grpc.*;
import edu.cn.hitsz_ids.agents.server.core.bridge.bridge.Bridge;
import edu.cn.hitsz_ids.agents.server.core.utils.PathUtils;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class HelpService extends HelpServiceGrpc.HelpServiceImplBase {
    private String getRelativePath(String identity, String name, String directory) {
        String relativePath;
        String realName = identity + PathUtils.extension(name);
        if (StringUtils.isEmpty(directory)) {
            relativePath = realName;
        } else {
            relativePath = Path.of(directory + File.separator + realName).toString();
        }
        return relativePath;
    }

    @Override
    public void listFiles(ListRequest request, StreamObserver<ListResponse> responseObserver) {
        String directory = request.getDirectory();
        try (AgentsFileHandler handler = new AgentsFileHandler(false)) {
            String bridge = request.getBridgeType();
            if (StringUtils.isEmpty(bridge)) {
                bridge = null;
            }
            List<String> children = handler.selectDir(directory, bridge);
            List<AgentsFileReturn> list = handler.selectFilesByDirectory(directory, bridge);
            ListResponse.Builder response = ListResponse.newBuilder();
            for (AgentsFileReturn agentsFileReturn : list) {
                response.addFiles(AgentsFile.newBuilder()
                        .setName(agentsFileReturn.getName())
                        .setDirectory(directory)
                        .setBridge(agentsFileReturn.getBridge())
                        .setUri(agentsFileReturn.getBridge() + "://" + agentsFileReturn.getIdentity())
                        .setPath(getRelativePath(agentsFileReturn.getIdentity(), agentsFileReturn.getName(), directory))
                        .build());
            }
            if (!CollectionUtils.isEmpty(children)) {
                for (String child : children) {
                    response.addFiles(AgentsFile.newBuilder()
                            .setDirectory(child)
                            .setIsDirectory(true)
                            .build());
                }
            }
            responseObserver.onNext(response.build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void delete(DeleteRequest request, StreamObserver<DeleteResponse> responseObserver) {
        Bridge<?> bridge = TemporaryVariable.getInstance().getBridge();
        String identity = TemporaryVariable.getInstance().getIdentity();

        try (AgentsFileHandler handler = new AgentsFileHandler(false)) {
            SearchInfoReturns returns = handler.searchInfoByIdentity(identity);
            boolean success = bridge.delete(identity, returns.getName(), returns.getDirectory());
            if (success) {
                handler.deleteByIdentity(identity);
                handler.commit();
            }
            responseObserver.onNext(DeleteResponse.newBuilder()
                    .setSuccess(success)
                    .build());
            responseObserver.onCompleted();
        } catch (IOException e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }
}
