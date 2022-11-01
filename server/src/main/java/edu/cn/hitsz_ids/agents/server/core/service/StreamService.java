package edu.cn.hitsz_ids.agents.server.core.service;

import io.grpc.stub.StreamObserver;
import edu.cn.hitsz_ids.agents.grpc.Request;
import edu.cn.hitsz_ids.agents.grpc.Response;
import edu.cn.hitsz_ids.agents.grpc.StreamGrpc;

class StreamService extends StreamGrpc.StreamImplBase {
    @Override
    public StreamObserver<Request> input(StreamObserver<Response> responseObserver) {
        return new InputStream(responseObserver, TemporaryVariable.getInstance().getBridge());
    }

    @Override
    public StreamObserver<Request> output(StreamObserver<Response> responseObserver) {
        return new OutputStream(responseObserver, TemporaryVariable.getInstance().getBridge());
    }
}
