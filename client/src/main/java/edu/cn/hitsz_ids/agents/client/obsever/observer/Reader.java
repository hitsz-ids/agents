package edu.cn.hitsz_ids.agents.client.obsever.observer;

import com.google.protobuf.ByteString;
import edu.cn.hitsz_ids.agents.client.obsever.response.ReadCase;
import edu.cn.hitsz_ids.agents.grpc.Request;
import edu.cn.hitsz_ids.agents.grpc.Response;

import java.io.IOException;

public class Reader extends Observer {
    public int read(byte[] bytes, int off, int length) throws IOException {
        isException();
        ReadCase readCase = new ReadCase(
                Request.Read.newBuilder()
                        .setOff(off)
                        .setLen(length)
                        .build());
        Response.Read read = caseManager.await(readCase);
        isException();
        if (read != null) {
            int len = read.getLen();
            if (len == -1) {
                return -1;
            }
            ByteString bss = read.getBytes();
            bss.copyTo(bytes, Math.min(bytes.length, off));
            return len;
        }
        return -1;
    }

    @Override
    protected Request response(Response response) throws IOException {
        Object data;
        if (response.getDataCase() == Response.DataCase.READ) {
            data = response.getRead();
            caseManager.single(response.getId(), data);
        }
        return null;
    }

}