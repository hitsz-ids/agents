package edu.cn.hitsz_ids.agents.server.core.service;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import edu.cn.hitsz_ids.agents.grpc.Request;
import edu.cn.hitsz_ids.agents.grpc.Response;
import edu.cn.hitsz_ids.agents.server.core.bridge.bridge.Bridge;

import java.io.IOException;

class InputStream extends Stream {
    InputStream(StreamObserver<Response> pusher, Bridge<?> bridge) {
        super(pusher, bridge);
    }

    @Override
    protected Response.Builder request(Request request) throws Exception {
        Response.Builder response = null;
        if (request.getDataCase() == Request.DataCase.READ) {
            response = read(request.getRead());
        }
        return response;
    }

    protected Response.Builder read(Request.Read read) throws IOException {
        var len = read.getLen();
        var off = read.getOff();
        var bytes = new byte[len];
        var readLength = bridge.read(bytes, off, len);
        return Response.newBuilder().setRead(Response.Read.newBuilder()
                .setBytes(readLength != -1 ?
                        ByteString.copyFrom(bytes, 0, len)
                        : ByteString.EMPTY)
                .setLen(readLength)
                .build());
    }
}
