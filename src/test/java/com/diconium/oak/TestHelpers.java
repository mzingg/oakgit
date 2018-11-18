package com.diconium.oak;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestHelpers {

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
