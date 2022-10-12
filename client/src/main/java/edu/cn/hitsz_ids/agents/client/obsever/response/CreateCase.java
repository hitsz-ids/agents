package edu.cn.hitsz_ids.agents.client.obsever.response;

import edu.cn.hitsz_ids.agents.grpc.Request;
import edu.cn.hitsz_ids.agents.grpc.Response;

public class CreateCase extends DataCase<Request.Create, Response.Create>{
    public CreateCase(Request.Create params) {
        super(params);
    }

    @Override
    public void assemble(Request.Builder builder, Request.Create params) {
        builder.setCreate(params);
    }
}
