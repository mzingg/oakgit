package com.diconium.oak.jdbc;

import com.github.zafarkhaja.semver.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class JaggitDriverConfiguration {

    private static final String URL_PREFIX = "jdbc:jaggit://";

    public static final JaggitDriverConfiguration INVALID_CONFIGURATION = new JaggitDriverConfiguration("invalid url", Version.valueOf("0.0.0"), "jaggit");

    public static JaggitDriverConfiguration fromUrl(String url, Version version, String artifactId) {
        if (StringUtils.startsWith(url, URL_PREFIX)) {
            return new JaggitDriverConfiguration(url, version, artifactId);
        }

        return INVALID_CONFIGURATION;
    }

    @NonNull
    private final String url;

    @NonNull
    private final Version version;

    @NonNull
    private final String artifactId;

    public Path getGitDirectory() {
        return Paths.get(StringUtils.substringAfter(url, URL_PREFIX));
    }

    public String getDirectoryName() {
        Path gitDirectory = getGitDirectory();
        return gitDirectory.getName(gitDirectory.getNameCount() - 1).toString();
    }

}
