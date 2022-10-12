package edu.cn.hitsz_ids.agents.client.obsever.response;

import edu.cn.hitsz_ids.agents.grpc.Request;
import edu.cn.hitsz_ids.agents.grpc.Response;

public class WriteCase extends DataCase<Request.Write, Response.Write> {
    public WriteCase(Request.Write params) {
        super(params);
    }

    @Override
    public void assemble(Request.Builder builder, Request.Write params) {
        builder.setWrite(params);
    }
}
