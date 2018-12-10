package com.diconium.oak.jdbc;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestHelpers {

    public static final OutputStream DERBY_DEV_NULL = new OutputStream() {
        public void write(int b) {}
    };

    public static Path getTestDirectory(String directoryName) throws IOException {
        Path target = getTestDirectory().resolve(directoryName);
        FileUtils.deleteQuietly(target.toFile());
        return Files.createDirectories(target);
    }

    public static Path getTestDirectory() throws IOException {
        Path target = Paths.get("target", "test-resources");
        return Files.createDirectories(target);
    }
}
