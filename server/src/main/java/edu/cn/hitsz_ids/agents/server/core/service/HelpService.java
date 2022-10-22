package edu.cn.hitsz_ids.agents.server.core.service;

import edu.cn.hitsz_ids.agents.db.mapper.AgentsFileHandler;
import edu.cn.hitsz_ids.agents.db.pojo.returns.SearchPathReturns;
import edu.cn.hitsz_ids.agents.grpc.*;
import edu.cn.hitsz_ids.agents.server.core.bridge.bridge.Bridge;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.io.IOException;

public class HelpService extends HelpServiceGrpc.HelpServiceImplBase {
    @Override
    public void listFiles(ListRequest request, StreamObserver<ListResponse> responseObserver) {
        String directory = request.getDirectory();

        try {
            Bridge<?> bridge = TemporaryVariable.getInstance().getBridge();
            bridge.listFiles(directory);
            responseObserver.onCompleted();
        } catch (IOException e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void delete(DeleteRequest request, StreamObserver<DeleteResponse> responseObserver) {
        Bridge<?> bridge = TemporaryVariable.getInstance().getBridge();
        String identity = TemporaryVariable.getInstance().getIdentity();
        try (AgentsFileHandler handler = new AgentsFileHandler(false);) {
            SearchPathReturns returns = handler.searchInfoByIdentity(identity);
            responseObserver.onNext(DeleteResponse.newBuilder()
                    .setSuccess(bridge.delete(identity, returns.getName(), returns.getDirectory()))
                    .build());
            responseObserver.onCompleted();
        } catch (IOException e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }
}
