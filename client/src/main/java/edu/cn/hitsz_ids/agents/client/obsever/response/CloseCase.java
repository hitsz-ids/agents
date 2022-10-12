package edu.cn.hitsz_ids.agents.client.obsever.response;

import edu.cn.hitsz_ids.agents.grpc.Request;
import edu.cn.hitsz_ids.agents.grpc.Response;

public class CloseCase extends DataCase<Request.Close, Response.Close> {

    public CloseCase(Request.Close params) {
        super(params);
    }

    @Override
    public void assemble(Request.Builder builder, Request.Close params) {
        builder.setClose(params);
    }
}
