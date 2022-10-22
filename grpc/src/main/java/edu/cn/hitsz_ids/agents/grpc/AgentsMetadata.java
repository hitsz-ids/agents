package edu.cn.hitsz_ids.agents.grpc;

import io.grpc.Metadata;

import static io.grpc.Metadata.BINARY_HEADER_SUFFIX;

public class AgentsMetadata {
    public static final Metadata.Key<String> TYPE = Metadata.Key.of("type",
            Metadata.ASCII_STRING_MARSHALLER);
    public static final Metadata.Key<String> URI = Metadata.Key.of("uri",
            Metadata.ASCII_STRING_MARSHALLER);
    public static final Metadata.Key<String> BRIDGE = Metadata.Key.of("bridge",
            Metadata.ASCII_STRING_MARSHALLER);

    public static final Metadata.Key<String> IDENTITY = Metadata.Key.of("identity",
            Metadata.ASCII_STRING_MARSHALLER);
    public static final Metadata.Key<byte[]> STACK_TRACE = Metadata.Key.of("stack-trace" + BINARY_HEADER_SUFFIX,
            Metadata.BINARY_BYTE_MARSHALLER);
    public static final Metadata.Key<byte[]> MESSAGE = Metadata.Key.of("message" + BINARY_HEADER_SUFFIX,
            Metadata.BINARY_BYTE_MARSHALLER);

    public static String getBridge(Metadata metadata) {
        return getString(metadata, BRIDGE);
    }
    public static String getIdentity(Metadata metadata) {
        return getString(metadata, IDENTITY);
    }
    public static String getType(Metadata metadata) {
        return getString(metadata, TYPE);
    }

    public static String getPath(Metadata metadata) {
        return getString(metadata, URI);
    }

    private static String getString(Metadata metadata, Metadata.Key<String> key) {
        return metadata.get(key);
    }

    private static byte[] getBytes(Metadata metadata, Metadata.Key<byte[]> bytes) {
        return metadata.get(bytes);
    }

}
