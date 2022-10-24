package edu.cn.hitsz_ids.agents.server.core.service;

import edu.cn.hitsz_ids.agents.db.mapper.AgentsFileHandler;
import edu.cn.hitsz_ids.agents.db.pojo.params.CreateParams;
import edu.cn.hitsz_ids.agents.grpc.Request;
import edu.cn.hitsz_ids.agents.grpc.Response;
import edu.cn.hitsz_ids.agents.server.core.bridge.bridge.Bridge;
import edu.cn.hitsz_ids.agents.server.core.utils.PathUtils;
import io.grpc.stub.StreamObserver;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

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
        var handler = new AgentsFileHandler(false);
        try (handler) {
            bridge.create(create.getIdentity(),
                    create.getName(),
                    create.getDirectory());
            var name = identity + PathUtils.extension(create.getName());
            var relativePath = name;
            var directory = create.getDirectory();
            if (!StringUtils.isEmpty(directory)) {
                relativePath = Path.of(directory + File.separator + name).toString();
            }
            handler.create(CreateParams.builder()
                    .size(0L)
                    .bridge(scheme)
                    .path(relativePath)
                    .directory(create.getDirectory())
                    .identity(create.getIdentity())
                    .name(create.getName())
                    .build());
            handler.commit();
        } catch (Exception e) {
            handler.rollback();
            bridge.delete(identity, create.getName(), create.getDirectory());
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
            var byteString = write.getBytes();
            var len = bridge.write(byteString.toByteArray());
            updateSize(len);
            var fileHandler = new AgentsFileHandler(false);
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
