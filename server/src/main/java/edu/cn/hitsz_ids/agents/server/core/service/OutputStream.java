package edu.cn.hitsz_ids.agents.server.core.service;

import com.google.protobuf.ByteString;
import edu.cn.hitsz_ids.agents.db.mapper.AgentsFileHandler;
import edu.cn.hitsz_ids.agents.db.pojo.params.CreateParams;
import edu.cn.hitsz_ids.agents.grpc.AgentsFile;
import edu.cn.hitsz_ids.agents.grpc.Request;
import edu.cn.hitsz_ids.agents.grpc.Response;
import edu.cn.hitsz_ids.agents.server.core.bridge.bridge.Bridge;
import io.grpc.stub.StreamObserver;

import java.io.IOException;

class OutputStream extends Stream {
    public OutputStream(StreamObserver<Response> pusher, Bridge<?> bridge) {
        super(pusher, bridge);
    }

    @Override
    protected Response.Builder request(Request request) throws Exception {
        return switch (request.getDataCase()) {
            case CREATE -> create(request.getCreate());
            case WRITE -> write(request.getWrite());
            default -> null;
        };
    }

    protected Response.Builder create(Request.Create create) throws IOException {
        AgentsFile.Builder file = null;
        AgentsFileHandler handler = new AgentsFileHandler(false);
        try (handler) {
            file = bridge.create(create.getIdentity(),
                    create.getName(),
                    create.getDirectory());
            handler.create(CreateParams.builder()
                    .size(0L)
                    .bridge(scheme)
                    .directory(create.getDirectory())
                    .identity(create.getIdentity())
                    .name(create.getName())
                    .build());
            handler.commit();
        } catch (Exception e) {
            handler.rollback();
            if (file != null) {
                bridge.delete(file.build());
            }
            throw e;
        }
        return Response.newBuilder().setCreate(
                Response.Create.newBuilder()
                        .setUri(getUri())
                        .build());
    }

    protected Response.Builder write(Request.Write write) throws IOException {
        synchronized (lock) {
            isOpened();
            isClosed();
            ByteString byteString = write.getBytes();
            int len = bridge.write(byteString.toByteArray());
            updateSize(len);
            AgentsFileHandler fileHandler = new AgentsFileHandler(false);
            try (fileHandler) {
                fileHandler.updateSizeById(primaryKey, size);
                fileHandler.commit();
            } catch (Exception e) {
                fileHandler.rollback();
            }
            return Response.newBuilder().setWrite(Response.Write.newBuilder()
                    .setLength(len)
                    .build());
        }
    }
}
