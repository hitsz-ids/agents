package edu.cn.hitsz_ids.agents.server.core.service;

import edu.cn.hitsz_ids.agents.core.bridge.IBridgeType;
import edu.cn.hitsz_ids.agents.server.core.bridge.bridge.Bridge;
import edu.cn.hitsz_ids.agents.server.core.bridge.bridge.BridgeFactory;
import edu.cn.hitsz_ids.agents.server.core.utils.Channel;
import io.grpc.Server;
import io.grpc.ServerInterceptor;
import io.grpc.ServerInterceptors;
import io.grpc.ServerServiceDefinition;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.ClientAuth;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContextBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslProvider;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class AgentsService {
    private final int listenPort;
    private final List<ServerServiceDefinition> definitions = new ArrayList<>();
    private Server server;
    private final NettyServerBuilder builder;
    private static final String CERT_CHAIN_FILE_PATH = "server.pem";
    private static final String TRUST_CERT_FILE_PATH = "ca.pem";
    private static final String PRIVATE_KEY_FILE_PATH = "server-pkcs8.key";
    private void initService() {
        var service = new StreamService();
        var helpService = new HelpService();
        definitions.add(ServerInterceptors.intercept(service, new BridgeInterceptor()));
        definitions.add(ServerInterceptors.intercept(helpService, new BridgeInterceptor()));
    }
    public AgentsService(int port) throws SSLException {
        this.listenPort = port;
        initService();
        var sslContextBuilder = getSslContextBuilder();
        builder = NettyServerBuilder
                .forPort(listenPort)
                .permitKeepAliveWithoutCalls(true)
                .permitKeepAliveTime(5, TimeUnit.SECONDS)
                .maxConnectionIdle(10L, TimeUnit.SECONDS)
                .maxInboundMessageSize(Channel.MAX_SEND_BLOCK_SIZE)
                .sslContext(sslContextBuilder.build());
    }

    public <T extends Bridge<?>> void registerBridge(IBridgeType IBridgeType,
                                                     Class<T> clazz) {
        BridgeFactory.getInstance().addBridge(IBridgeType, clazz);
    }

    public void registerInterceptor(ServerInterceptor interceptor) {
        var size = definitions.size();
        ServerServiceDefinition definition;
        for (int i = 0; i < size; i++) {
            definition = definitions.get(i);
            definition = ServerInterceptors.intercept(definition, interceptor);
            definitions.add(i, definition);
        }
    }

    public void start() throws IOException {
        server = builder.addServices(definitions).build();
        server.start();
        log.info("服务已经启动，监听端口为{}", listenPort);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                log.error("shutting down gRPC server since JVM is shutting down");
                AgentsService.this.stop();
                log.error("server shut down");
            }
        });
    }

    private SslContextBuilder getSslContextBuilder() throws SecurityException {
        URL cert = AgentsService.class.getClassLoader().getResource(CERT_CHAIN_FILE_PATH);
        if (cert == null) {
            throw new SecurityException("未找到对应的证书");
        }
        URL key = AgentsService.class.getClassLoader().getResource(PRIVATE_KEY_FILE_PATH);
        if (key == null) {
            throw new SecurityException("未找到对应的私钥");
        }
        URL ca = AgentsService.class.getClassLoader().getResource(TRUST_CERT_FILE_PATH);
        if (ca == null) {
            throw new SecurityException("未找到对应的跟证书");
        }
        SslContextBuilder sslClientContextBuilder = SslContextBuilder.forServer(
                new File(cert.getFile()),
                new File(key.getFile()));
        sslClientContextBuilder.trustManager(new File(ca.getFile()));
        sslClientContextBuilder.clientAuth(ClientAuth.REQUIRE);
        return GrpcSslContexts.configure(sslClientContextBuilder,
                SslProvider.OPENSSL);
    }

    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    /**
     * 等待server运行结束
     */
    public void awaitTermination() throws InterruptedException {
        server.awaitTermination();
    }
}
