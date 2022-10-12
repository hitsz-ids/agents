package edu.cn.hitsz_ids.agents.server.core.service;

import com.google.protobuf.ByteString;
import edu.cn.hitsz_ids.agents.db.mapper.AgentsFileHandler;
import edu.cn.hitsz_ids.agents.server.core.file.AgentsFile;
import edu.cn.hitsz_ids.agents.server.core.utils.OpenOptionUtils;
import io.grpc.stub.StreamObserver;
import edu.cn.hitsz_ids.agents.grpc.Request;
import edu.cn.hitsz_ids.agents.grpc.Response;
import edu.cn.hitsz_ids.agents.server.core.bridge.bridge.Bridge;

import java.io.IOException;
import java.util.UUID;

class OutputStream extends Stream {
    public OutputStream(StreamObserver<Response> pusher, Bridge<?> bridge) {
        super(pusher, bridge);
    }

    @Override
    protected Response.Builder request(Request request) throws Exception {
        Response.Builder response = null;
        switch (request.getDataCase()) {
            case CREATE:
                response = create(request.getCreate());
                break;
            case WRITE:
                response = write(request.getWrite());
                break;
        }
        return response;
    }

    protected Response.Builder create(Request.Create create) throws IOException {
        AgentsFile file = null;
        try (AgentsFileHandler handler = new AgentsFileHandler(false)) {
            file = bridge.create(create.getIdentity(),
                    create.getName(),
                    create.getDirectory());
            handler.commit();
        }catch (Exception e) {
            if (file != null) {
                bridge.delete(file);
            }
        }
        return Response.newBuilder().setCreate(Response.Create.newBuilder());
    }

    @Override
    protected void destroy(boolean error) {

    }

    protected Response.Builder write(Request.Write write) throws IOException {
        ByteString byteString = write.getBytes();
        int len = bridge.write(byteString.toByteArray());
        return Response.newBuilder().setWrite(Response.Write.newBuilder()
                .setLength(len)
                .build());
    }
}
