package oakgit.jdbc;

import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class OakGitDriverConfiguration {

  public static final OakGitDriverConfiguration INVALID_CONFIGURATION =
      new OakGitDriverConfiguration("invalid url", DriverVersion.ZERO, "oakgit");
  private static final String URL_PREFIX = "jdbc:oakgit://";
  @NonNull private final String url;
  @NonNull private final DriverVersion version;
  @NonNull private final String artifactId;

  public static OakGitDriverConfiguration fromUrl(
      String url, DriverVersion version, String artifactId) {
    if (StringUtils.startsWith(url, URL_PREFIX)) {
      return new OakGitDriverConfiguration(url, version, artifactId);
    }

    return INVALID_CONFIGURATION;
  }

  public Path getGitDirectory() {
    return Paths.get(StringUtils.substringAfter(url, URL_PREFIX));
  }

  public String getDirectoryName() {
    Path gitDirectory = getGitDirectory();
    return gitDirectory.getName(gitDirectory.getNameCount() - 1).toString();
  }
}
