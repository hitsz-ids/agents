package edu.cn.hitsz_ids.agents.client.obsever.observer;

import com.google.protobuf.ByteString;
import edu.cn.hitsz_ids.agents.client.obsever.manager.CaseManager;
import edu.cn.hitsz_ids.agents.client.obsever.response.CloseCase;
import edu.cn.hitsz_ids.agents.client.obsever.response.OpenCase;
import edu.cn.hitsz_ids.agents.grpc.*;
import edu.cn.hitsz_ids.agents.grpc.Exception;
import io.grpc.Metadata;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import edu.cn.hitsz_ids.agents.utils.ExceptionUtils;
import edu.cn.hitsz_ids.agents.utils.ServerException;

import java.io.IOException;
import java.lang.reflect.Array;

public abstract class Observer implements StreamObserver<Response> {
    private IOException exception;
    protected StreamObserver<Request> sender;
    protected CaseManager caseManager;
    private final Object errorLock = new Object();

    public final void setSender(StreamObserver<Request> sender) {
        this.sender = sender;
        caseManager = new CaseManager(this);
    }

    protected abstract Request response(Response res) throws IOException;

    public final void open(OpenOption... options) throws IOException {
        isException();
        Request.Open.Builder builder = Request.Open.newBuilder();
        if (options != null && options.length > 0) {
            for (int i = 0; i < options.length; i++) {
                builder.setOptions(i, options[i]);
            }
        }
        caseManager.await(new OpenCase(builder.build()));
        isException();
    }

    public void close() throws IOException {
        isException();
        caseManager.await(new CloseCase(
                Request.Close.newBuilder()
                        .build()));
        isException();
    }

    @Override
    public final void onNext(Response res) {
        Request req;
        try {
            switch (res.getDataCase()) {
                case OPEN:
                    caseManager.single(res.getId(), res.getOpen());
                    return;
                case CLOSE:
                    caseManager.single(res.getId(), res.getClose());
                    return;
                case DATA_NOT_SET:
                    throw new IOException("接收的数据类型不正确");
                default:
                    req = this.response(res);
                    if (req != null) {
                        sender.onNext(req);
                    }
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            error(e);
        }
    }

    public final void complete() {
        caseManager.destroy();
        sender.onCompleted();
    }

    @Override
    public final void onError(Throwable throwable) {
        synchronized (errorLock) {
            try {
                ServerException observer;
                Metadata metadata = ((StatusRuntimeException) throwable).getTrailers();
                if (metadata != null) {
                    byte[] bytes = metadata.get(AgentsMetadata.STACK_TRACE);
                    byte[] msgBytes = metadata.get(AgentsMetadata.MESSAGE);
                    if (bytes != null) {
                        observer = new ServerException(
                                ExceptionUtils.toThrowable(bytes,
                                        msgBytes));
                    } else {
                        observer = new ServerException(throwable);
                    }
                } else {
                    observer = new ServerException(throwable);
                }
                if (exception == null) {
                    exception = observer;
                }
            } finally {
                complete();
            }
        }
    }

    @Override
    public final void onCompleted() {
        //nothing to do
    }

    public final void isException() throws IOException {
        if (exception != null) {
            throw exception;
        }
    }

    public final void error(IOException e) {
        synchronized (errorLock) {
            if (exception != null) {
                return;
            }
            exception = e;
            byte[] bytes = ExceptionUtils.toBytes(e);
            String message = (e.getClass().getName() + ":" + e.getMessage());
            sender.onNext(Request.newBuilder()
                    .setException(
                            Exception.newBuilder()
                                    .setMessage(message)
                                    .setStackTrace(ByteString.copyFrom(bytes))
                                    .build()
                    )
                    .build());
            complete();
        }
    }

    public void send(Request request) {
        sender.onNext(request);
    }
}
