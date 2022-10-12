package edu.cn.hitsz_ids.agents.client.obsever.response;

import edu.cn.hitsz_ids.agents.grpc.Request;
import edu.cn.hitsz_ids.agents.grpc.Response;

public class ReadCase extends DataCase<Request.Read, Response.Read> {
    public ReadCase(Request.Read read) {
        super(read);
    }

    @Override
    public void assemble(Request.Builder builder, Request.Read params) {
        builder.setRead(params);
    }
}
