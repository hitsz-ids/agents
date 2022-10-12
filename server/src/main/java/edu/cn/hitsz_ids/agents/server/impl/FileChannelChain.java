package edu.cn.hitsz_ids.agents.server.impl;

import edu.cn.hitsz_ids.agents.grpc.OpenOption;
import edu.cn.hitsz_ids.agents.server.core.bridge.chain.Chain;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.List;

class FileChannelChain implements Chain {
    FileChannel channel;

    protected File open(String path, OpenOption... options) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            throw new IOException("File Not Found");
        }
        channel = FileChannel.open(file.toPath(), OptionsUtils.getOpenOptions(options));
        return file;
    }

    protected int write(byte[] bytes) throws IOException {
        return channel.write(ByteBuffer.wrap(bytes));
    }

    protected int read(byte[] bytes, int off, int length) throws IOException {
        channel.position(off);
        ByteBuffer byteBuffer = ByteBuffer.allocate(length);
        int len = channel.read(byteBuffer);
        int remind;
        while (len != -1 && len < length) {
            remind = channel.read(byteBuffer, len);
            len += remind;
        }
        byte[] real = byteBuffer.array();
        System.arraycopy(real, 0, bytes, 0, real.length);
        return len;
    }

    protected void close() throws IOException {
        if (channel != null) {
            channel.close();
        }
    }
}
