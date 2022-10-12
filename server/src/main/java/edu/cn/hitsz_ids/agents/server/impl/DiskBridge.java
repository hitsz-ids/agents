package edu.cn.hitsz_ids.agents.server.impl;

import edu.cn.hitsz_ids.agents.grpc.OpenOption;
import edu.cn.hitsz_ids.agents.server.core.bridge.bridge.Bridge;
import edu.cn.hitsz_ids.agents.server.core.file.AgentsFile;
import edu.cn.hitsz_ids.agents.server.core.utils.PathUtils;
import edu.cn.hitsz_ids.agents.utils.IBridgeType;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
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

    @Override
    public AgentsFile open(String name, String directory, OpenOption... options) throws IOException {
        File file = chain.open(directory + name, options);
        return AgentsFile.builder()
                .name(file.getName())
                .length(0L)
                .path(file.getPath())
                // .directory()
                .bridge(getName())
                .size(0L)
                .createdTime(new Date())
                .lastModified(new Date())
                .build();
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
    public AgentsFile create(String identity,
                       String name,
                       String directory) throws IOException {
        if (StringUtils.isBlank(directory)) {
            directory = File.separator;
        } else {
            if (!directory.startsWith(File.separator)) {
                directory = File.separator + directory;
            }
            if (!directory.endsWith(File.separator)) {
                directory = directory + File.separator;
            }
        }
        Path dirPath = Paths.get(BASE_DIR + directory);
        String extension = PathUtils.extension(name);
        Path path = Paths.get(dirPath.toFile().getCanonicalPath() + identity + extension);
        if (!Objects.equals(path.toFile().getCanonicalPath(), path.toFile().getAbsolutePath())) {
            // 判断传入的文件路径和真实的文件路径是否一样
            throw new IOException("文件路径错误请检查");
        }
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
        return AgentsFile.builder()
                .name(name)
                .length(0L)
                .path(path.toString())
                .directory(directory)
                .bridge(getName())
                .size(0L)
                .createdTime(new Date())
                .lastModified(new Date())
                .build();
    }

    @Override
    public void close() throws IOException {
        chain.close();
    }

    @Override
    public boolean delete(AgentsFile file) throws IOException {
        Path path = Paths.get(file.getPath());
        if(Files.exists(path)) {
            Files.delete(path);
            return true;
        }
        return false;
    }
}
