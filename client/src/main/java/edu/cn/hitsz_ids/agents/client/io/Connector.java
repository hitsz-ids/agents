package edu.cn.hitsz_ids.agents.client.io;

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NegotiationType;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContextBuilder;

import javax.net.ssl.SSLException;
import java.io.File;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class Connector {
    private static final String AUTHORITY = "agents";
    public static ManagedChannel channel() throws SSLException {
        return NettyChannelBuilder.forAddress(
                        "127.0.0.1", 10000)
                .negotiationType(NegotiationType.TLS)
                .overrideAuthority(AUTHORITY)
                .sslContext(buildSslContext())
                .keepAliveWithoutCalls(true)
                .keepAliveTime(5, TimeUnit.SECONDS)
                .build();
    }
    private static SslContext buildSslContext() throws SSLException {
        SslContextBuilder builder = GrpcSslContexts.forClient();
        URL cert = Connector.class.getClassLoader().getResource("client.pem");
        if (cert == null) {
            return null;
        }
        URL key = Connector.class.getClassLoader().getResource("client-pkcs8.key");
        if (key == null) {
            return null;
        }
        URL ca =Connector.class.getClassLoader().getResource( "ca.pem");
        if (ca == null) {
            return null;
        }
        builder.trustManager(new File(ca.getFile()));
        builder.keyManager(new File(cert.getFile()), new File(key.getFile()));
        return builder.build();
    }
}
