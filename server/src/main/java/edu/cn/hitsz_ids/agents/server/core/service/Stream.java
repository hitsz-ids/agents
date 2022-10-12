package edu.cn.hitsz_ids.agents.server.core.service;

import edu.cn.hitsz_ids.agents.db.mapper.AgentsFileHandler;
import edu.cn.hitsz_ids.agents.db.pojo.returns.SearchPathReturns;
import edu.cn.hitsz_ids.agents.grpc.AgentsMetadata;
import edu.cn.hitsz_ids.agents.grpc.Exception;
import edu.cn.hitsz_ids.agents.grpc.Request;
import edu.cn.hitsz_ids.agents.grpc.Response;
import edu.cn.hitsz_ids.agents.server.core.bridge.bridge.Bridge;
import edu.cn.hitsz_ids.agents.server.core.file.AgentsFile;
import edu.cn.hitsz_ids.agents.server.core.utils.OpenOptionUtils;
import edu.cn.hitsz_ids.agents.utils.ClientException;
import edu.cn.hitsz_ids.agents.utils.ExceptionUtils;
import edu.cn.hitsz_ids.agents.utils.ServerException;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

abstract class Stream implements StreamObserver<Request> {
    protected final Bridge<?> bridge;
    private final StreamObserver<Response> pusher;
    private boolean completed;
    private final Object completeLock = new Object();

    private final String scheme;
    private final String identity;

    public Stream(StreamObserver<Response> pusher, Bridge<?> bridge) {
        this.pusher = pusher;
        this.bridge = bridge;
        identity = TemporaryVariable.getInstance().getIdentity();
        scheme = TemporaryVariable.getInstance().getScheme();
    }

    protected abstract Response.Builder request(Request request) throws java.lang.Exception;

    @Override
    public void onNext(Request request) {
        try {
            Response.Builder response;
            switch (request.getDataCase()) {
                case OPEN:
                    response = open(request.getOpen());
                    break;
                case CLOSE:
                    response = close();
                    break;
                case EXCEPTION:
                    printStackTrace(request.getException());
                    return;
                case DATA_NOT_SET:
                    throw new IOException("接收的数据类型不正确");
                default:
                    response = request(request);
                    break;
            }
            if (response != null) {
                response.setId(request.getId());
                push(response.build());
            }
        } catch (java.lang.Exception e) {
            exception(e);
        }
    }

    protected Response.Builder open(Request.Open open) throws IOException {
        try (AgentsFileHandler handler = new AgentsFileHandler()) {
            SearchPathReturns searchPathReturns = handler.searchPath(identity);
            if (Objects.equals(searchPathReturns.getBridge(), scheme)) {
                throw new ServerException("当前文件的存储位置不匹配");
            }
            AgentsFile file = bridge.open(searchPathReturns.getName(),
                    searchPathReturns.getDirectory(),
                    OpenOptionUtils.toArray(open.getOptionsList()));
            return Response.newBuilder().setOpen(Response.Open.newBuilder()
                    .setLength(file.getLength())
                    .setName(file.getName())
                    .build());
        }
    }

    protected Response.Builder close() throws IOException {
        bridge.close();
        return Response.newBuilder().setClose(Response.Close.newBuilder().build());
    }


    private void exception(java.lang.Exception exception) {
        Metadata metadata = new Metadata();
        metadata.put(AgentsMetadata.STACK_TRACE, ExceptionUtils.toBytes(exception));
        metadata.put(AgentsMetadata.MESSAGE,
                (exception.getClass().getName() + ":" + exception.getMessage()).getBytes(StandardCharsets.UTF_8));
        pusher.onError(Status.INTERNAL.asRuntimeException(metadata));
        exception.printStackTrace();
        completed(false, true);
    }

    private void printStackTrace(Exception exception) {
        Throwable throwable = ExceptionUtils.toThrowable(exception.getStackTrace().toByteArray(),
                exception.getMessage().getBytes(StandardCharsets.UTF_8));
        new ClientException(throwable).printStackTrace();
        completed(true, false);
    }

    @Override
    public void onError(Throwable throwable) {
        completed(true, false);
    }

    @Override
    public void onCompleted() {

    }

    protected void destroy(boolean error) {
    }

    protected void push(Response res) {
        pusher.onNext(res);
    }

    public void completed(boolean whether, boolean error) {
        // todo 释放内存
        synchronized (completeLock) {
            if (completed) {
                return;
            }
            try {
                if (whether) {
                    pusher.onCompleted();
                }
                if (bridge != null) {
                    bridge.close();
                }
                destroy(error);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                completed = true;
            }
        }
    }
}
