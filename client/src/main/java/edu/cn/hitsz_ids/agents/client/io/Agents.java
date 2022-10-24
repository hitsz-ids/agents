package edu.cn.hitsz_ids.agents.client.io;

import edu.cn.hitsz_ids.agents.core.BridgeType;
import edu.cn.hitsz_ids.agents.grpc.AgentsFile;
import edu.cn.hitsz_ids.agents.grpc.OpenOption;
import edu.cn.hitsz_ids.agents.core.bridge.IBridgeType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Agents {
    public static InputStream open(String uri) throws IOException {
        return new InputStream(uri);
    }

    public static OutputStream open(String uri, OpenOption option) throws IOException {
        return new OutputStream(uri, option);
    }

    public static OutputStream create(
            IBridgeType IBridgeType,
            String name,
            String directory,
            String identity
    ) throws IOException {
        return new OutputStream(IBridgeType,
                name,
                directory,
                identity, OpenOption.OP_WRITE);
    }

    public static OutputStream create(
            IBridgeType IBridgeType,
            String name,
            String directory
    ) throws IOException {
        return new OutputStream(IBridgeType,
                name,
                directory, OpenOption.OP_WRITE);
    }

    public static OutputStream create(
            IBridgeType IBridgeType,
            String name
    ) throws IOException {
        return new OutputStream(IBridgeType,
                name,
                "", OpenOption.OP_WRITE);
    }

    public static List<AgentsFile> listFiles(String directory, BridgeType type) {
        return Help.getInstance().listFiles(directory, type);
    }

    public static List<AgentsFile> listFiles(String directory) {
        return listFiles(directory, null);
    }

    public static boolean delete(String uri) {
        return Help.getInstance().delete(uri);
    }
}
