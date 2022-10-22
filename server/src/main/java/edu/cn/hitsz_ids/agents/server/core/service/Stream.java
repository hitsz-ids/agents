package edu.cn.hitsz_ids.agents.server.core.service;

import edu.cn.hitsz_ids.agents.core.exception.ClientException;
import edu.cn.hitsz_ids.agents.core.exception.ExceptionUtils;
import edu.cn.hitsz_ids.agents.core.exception.ServerException;
import edu.cn.hitsz_ids.agents.db.mapper.AgentsFileHandler;
import edu.cn.hitsz_ids.agents.db.pojo.returns.SearchPathReturns;
import edu.cn.hitsz_ids.agents.grpc.Exception;
import edu.cn.hitsz_ids.agents.grpc.*;
import edu.cn.hitsz_ids.agents.server.core.bridge.bridge.Bridge;
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
    protected final String scheme;
    protected final String identity;
    private boolean closed;
    private boolean opened;
    protected final Object lock = new Object();
    protected Long primaryKey;
    protected long size;

    protected long newPosition;
    protected OpenOption option;

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
                case OPEN -> response = open(request.getOpen());
                case CLOSE -> response = close();
                case POSITION -> response = position(request.getPosition());
                case EXCEPTION -> {
                    printStackTrace(request.getException());
                    return;
                }
                case DATA_NOT_SET -> throw new IOException("接收的数据类型不正确");
                default -> response = request(request);
            }
            if (response != null) {
                response.setId(request.getId());
                push(response.build());
            }
        } catch (java.lang.Exception e) {
            exception(e);
        }
    }

    protected Response.Builder position(Request.Position position) throws IOException {
        synchronized (lock) {
            if (option == OpenOption.OP_APPEND) {
                throw new IOException("OP_APPEND追加模式下，不能移动文件指针的位置");
            }
            isOpened();
            isClosed();
            newPosition = bridge.position(position.getIndex());
            return Response.newBuilder().setPosition(
                    Response.Position.newBuilder().build()
            );
        }
    }

    protected Response.Builder open(Request.Open open) throws IOException {
        synchronized (lock) {
            if (opened) {
                throw new IOException("文件已经打开");
            }
            isClosed();
            try (AgentsFileHandler handler = new AgentsFileHandler()) {
                SearchPathReturns searchPathReturns = handler.searchInfo(identity);
                primaryKey = searchPathReturns.getId();
                if (!Objects.equals(searchPathReturns.getBridge(), scheme)) {
                    throw new ServerException("当前文件的存储位置不匹配");
                }
                option = open.getOption();
                AgentsFile.Builder file = bridge.open(
                        identity,
                        searchPathReturns.getName(),
                        searchPathReturns.getDirectory(),
                        option);
                size = file.getSize();
                opened = true;
                return Response.newBuilder().setOpen(Response.Open.newBuilder()
                        .setFile(file)
                        .build());
            }
        }
    }

    protected Response.Builder close() throws IOException {
        synchronized (lock) {
            if (!opened) {
                throw new IOException("文件未打开");
            }
            if (closed) {
                throw new IOException("文件已关打开");
            }
            bridge.close();
            closed = true;
            return Response.newBuilder().setClose(Response.Close.newBuilder().build());
        }
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
        StackTraceElement[] stackTrace = ExceptionUtils.stackTrace(exception.getStackTrace().toByteArray());
        new ClientException(stackTrace, exception.getMessage()).printStackTrace();
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
                    bridge.completed(error);
                }
                destroy(error);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                completed = true;
            }
        }
    }

    public String getUri() {
        return scheme + "://" + identity;
    }

    public void isOpened() throws IOException {
        if (!opened) {
            throw new IOException("文件未打开");
        }
    }

    public void isClosed() throws IOException {
        if (closed) {
            throw new IOException("文件已关闭");
        }
    }

    protected void updateSize(int len) {
        if (option == OpenOption.OP_APPEND) {
            size += len;
        } else {
            long remainder = size - newPosition;
            long increased = len - remainder;
            if (increased > 0) {
                size += increased;
            }
        }
    }
}
