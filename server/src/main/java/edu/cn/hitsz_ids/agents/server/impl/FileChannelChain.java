package edu.cn.hitsz_ids.agents.server.impl;

import edu.cn.hitsz_ids.agents.grpc.OpenOption;
import edu.cn.hitsz_ids.agents.server.core.bridge.chain.Chain;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

class FileChannelChain implements Chain {
    FileChannel channel;

    protected File open(String path, OpenOption... options) throws IOException {
        var file = new File(path);
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
        var byteBuffer = ByteBuffer.allocate(length);
        var len = channel.read(byteBuffer);
        var real = byteBuffer.array();
        System.arraycopy(real, 0, bytes, off, len);
        return len;
    }

    protected void close() throws IOException {
        if (channel != null) {
            channel.close();
        }
    }

    protected long position(long index) throws IOException {
        if (index >= channel.size()) {
            index = channel.size();
        }
        channel.position(index);
        return index;
    }
}
