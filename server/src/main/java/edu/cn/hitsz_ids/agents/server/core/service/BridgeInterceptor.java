package edu.cn.hitsz_ids.agents.server.core.service;

import edu.cn.hitsz_ids.agents.grpc.AgentsMetadata;
import edu.cn.hitsz_ids.agents.server.core.bridge.bridge.Bridge;
import edu.cn.hitsz_ids.agents.server.core.bridge.bridge.BridgeFactory;
import edu.cn.hitsz_ids.agents.core.exception.ServerException;
import io.grpc.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import static edu.cn.hitsz_ids.agents.core.bridge.IOType.CREATE;
import static edu.cn.hitsz_ids.agents.core.bridge.IOType.SEARCH;

class BridgeInterceptor implements ServerInterceptor {
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall,
                                                                 Metadata metadata,
                                                                 ServerCallHandler<ReqT, RespT> serverCallHandler) {
        String type = AgentsMetadata.getType(metadata);
        boolean goon = false;
        String name = null;
        String identity = null;
        if (Objects.equals(type, SEARCH)) {
            // 通过路径查询文件的实际存储位置来获取bridge的类型
            String path = AgentsMetadata.getPath(metadata);
            try {
                URI uri = new URI(path);
                name = uri.getScheme();
                identity = uri.getHost();
                goon = true;
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        } else if (Objects.equals(type, CREATE)) {
            // 通过传递参数确认bridge的类型
            name = AgentsMetadata.getBridge(metadata);
            identity = AgentsMetadata.getIdentity(metadata);
        }
        Bridge<?> bridge = null;
        try {
            bridge = BridgeFactory.getInstance().create(name);
            if (!Objects.isNull(bridge)) {
                goon = true;
            }
        } catch (ServerException e) {
            e.printStackTrace();
        }
        if (!goon) {
            serverCall.close(Status.NOT_FOUND.withDescription("未找到对应的执行类型"), metadata);
            return new ServerCall.Listener<ReqT>() {
            };
        }
        TemporaryVariable.getInstance().addIdentity(identity);
        TemporaryVariable.getInstance().addScheme(name);
        TemporaryVariable.getInstance().addBridge(bridge);
        return serverCallHandler.startCall(
                new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(serverCall) {
                }, metadata);
    }
}
