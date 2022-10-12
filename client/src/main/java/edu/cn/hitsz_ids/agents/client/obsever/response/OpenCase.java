package edu.cn.hitsz_ids.agents.client.obsever.response;

import edu.cn.hitsz_ids.agents.grpc.Request;
import edu.cn.hitsz_ids.agents.grpc.Response;

public class OpenCase extends DataCase<Request.Open, Response.Open> {
    public OpenCase(Request.Open open) {
        super(open);
    }

    @Override
    public void assemble(Request.Builder builder, Request.Open params) {
        builder.setOpen(params);
    }
}
