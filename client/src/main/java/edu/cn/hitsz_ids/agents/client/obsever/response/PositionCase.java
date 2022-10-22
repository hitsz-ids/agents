package edu.cn.hitsz_ids.agents.client.obsever.response;

import edu.cn.hitsz_ids.agents.grpc.Request;
import edu.cn.hitsz_ids.agents.grpc.Response;

public class PositionCase extends DataCase<Request.Position, Response.Position>{
    public PositionCase(Request.Position params) {
        super(params);
    }

    @Override
    public void assemble(Request.Builder builder, Request.Position params) {
        builder.setPosition(params);
    }
}
