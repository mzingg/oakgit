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
public class OakGitDriverConfiguration {

    private static final String URL_PREFIX = "jdbc:oakgit://";

    public static final OakGitDriverConfiguration INVALID_CONFIGURATION = new OakGitDriverConfiguration("invalid url", Version.valueOf("0.0.0"), "oakgit");

    public static OakGitDriverConfiguration fromUrl(String url, Version version, String artifactId) {
        if (StringUtils.startsWith(url, URL_PREFIX)) {
            return new OakGitDriverConfiguration(url, version, artifactId);
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