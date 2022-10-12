package edu.cn.hitsz_ids.agents.client.obsever.observer;

import com.google.protobuf.ByteString;
import edu.cn.hitsz_ids.agents.client.obsever.response.CreateCase;
import edu.cn.hitsz_ids.agents.client.obsever.response.WriteCase;
import edu.cn.hitsz_ids.agents.grpc.OpenOption;
import edu.cn.hitsz_ids.agents.grpc.Request;
import edu.cn.hitsz_ids.agents.grpc.Response;

import java.io.IOException;

public class Writer extends Observer {
    @Override
    protected Request response(Response res) throws IOException {
        Object data;
        switch (res.getDataCase()) {
            case WRITE:
                data = res.getWrite();
                break;
            case CREATE:
                data = res.getCreate();
                break;
            default:
                return null;
        }
        caseManager.single(res.getId(), data);
        return null;
    }

    public void write(byte[] bytes, int off, int length) throws IOException {
        caseManager.await(new WriteCase(
                Request.Write.newBuilder()
                        .setBytes(ByteString.copyFrom(bytes, off, length))
                        .setLen(length)
                        .build()));
    }

    public void create(String name,
                       String directory,
                       String identity,
                       OpenOption... options) throws IOException {
        caseManager.await(new CreateCase(Request.Create.newBuilder()
                .setName(name)
                .setIdentity(identity)
                .setDirectory(directory)
                .build()));
    }
}
