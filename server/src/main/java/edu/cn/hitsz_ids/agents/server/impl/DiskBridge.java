package edu.cn.hitsz_ids.agents.server.impl;

import edu.cn.hitsz_ids.agents.core.bridge.IBridgeType;
import edu.cn.hitsz_ids.agents.db.mapper.AgentsFileHandler;
import edu.cn.hitsz_ids.agents.grpc.AgentsFile;
import edu.cn.hitsz_ids.agents.grpc.OpenOption;
import edu.cn.hitsz_ids.agents.server.core.bridge.bridge.Bridge;
import edu.cn.hitsz_ids.agents.server.core.utils.PathUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static edu.cn.hitsz_ids.agents.server.core.utils.PathUtils.BASE_DIR;

public class DiskBridge extends Bridge<FileChannelChain> {
    public DiskBridge(IBridgeType type) {
        super(type);
    }

    @Override
    public FileChannelChain createChain() {
        return new FileChannelChain();
    }

    private Path getRealPath(String identity, String name, String directory) throws IOException {
        Path dirPath = Paths.get(BASE_DIR + directory);
        String extension = PathUtils.extension(name);
        Path path = Paths.get(dirPath.toFile().getCanonicalPath() + File.separator + identity + extension);
        if (!Objects.equals(path.toFile().getCanonicalPath(), path.toFile().getAbsolutePath())) {
            // 判断传入的文件路径和真实的文件路径是否一样
            throw new IOException("文件路径错误请检查");
        }
        return path;
    }

    @Override
    public AgentsFile.Builder open(String identity, String name, String directory, OpenOption... options) throws IOException {
        Path path = getRealPath(identity, name, directory);
        File file = chain.open(path.toString(), options);
        return AgentsFile.newBuilder()
                .setName(file.getName())
                .setSize(file.length())
                .setPath(file.getPath())
                .setDirectory(directory)
                .setBridge(getName());
    }

    @Override
    public int write(byte[] bytes) throws IOException {
        return chain.write(bytes);
    }

    @Override
    public int read(byte[] bytes, int off, int len) throws IOException {
        return chain.read(bytes, off, len);
    }

    @Override
    public AgentsFile.Builder create(String identity,
                                     String name,
                                     String directory) throws IOException {
        Path path = getRealPath(identity, name, directory);
        Path dirPath = Paths.get(BASE_DIR + directory);
        boolean exists = Files.exists(dirPath);
        if (!exists) {
            Files.createDirectories(dirPath);
        } else {
            boolean isDirectory = Files.isDirectory(dirPath);
            if (!isDirectory) {
                Files.delete(dirPath);
                Files.createDirectories(dirPath);
            }
        }
        Files.deleteIfExists(path);
        Files.createFile(path);
        return AgentsFile.newBuilder()
                .setName(name)
                .setSize(0L)
                .setPath(path.toString())
                .setDirectory(directory)
                .setBridge(getName())
                .setCreatedTime(DateFormatUtils.format(new Date(),
                        DateFormatUtils.ISO_8601_EXTENDED_TIME_TIME_ZONE_FORMAT.getPattern()))
                .setLastModified(DateFormatUtils.format(new Date(),
                        DateFormatUtils.ISO_8601_EXTENDED_TIME_TIME_ZONE_FORMAT.getPattern()));
    }

    @Override
    public void close() throws IOException {
        chain.close();
    }

    @Override
    public boolean delete(AgentsFile file) throws IOException {
        Path path = Paths.get(file.getPath());
        if (Files.exists(path)) {
            Files.delete(path);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(String identity,
                          String name,
                          String directory) throws IOException {
        Path path = getRealPath(identity, name, directory);
        if (Files.exists(path)) {
            Files.delete(path);
            return true;
        }
        return false;
    }

    @Override
    public long position(long index) throws IOException {
        return chain.position(index);
    }

    @Override
    public List<AgentsFile> listFiles(String directory) throws IOException {
        try (AgentsFileHandler handler = new AgentsFileHandler(false)){
            dir(BASE_DIR + directory, handler);
        }
        return null;
    }

    private void dir(String directory, AgentsFileHandler handler) {
        File file = new File(directory);
        if (!file.isDirectory()) {
            return;
        }
        File[] files = file.listFiles();
        if (Objects.isNull(files)) {
            return;
        }
        File item;
        for (File value : files) {
            item = value;
            if (item.isDirectory()) {
                dir(item.getPath(), handler);
            } else {
                handler.searchInfoByPath(file.getPath());
            }
        }
    }
}
