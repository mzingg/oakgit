package oakgit.jdbc;

import oakgit.SandboxTest;
import oakgit.engine.CommandFactory;
import oakgit.engine.query.QueryMatchResult;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class QueryLogProcessor {

  @SandboxTest
  void runAnalyzerCheck() throws IOException {

    CommandFactory factory = new CommandFactory();
    Path queryLog = Path.of("../run/author/git/query.log");
    Path queryLogTemp = Path.of("../run/author/git/query.log.tmp");
    Path queryIndex = Path.of("../run/author/git/query.index");

    Map<String, Integer> queryIndexCounter = new HashMap<>();

    Files.deleteIfExists(queryLogTemp);

    if (queryIndex.toFile().exists()) {
      Files.lines(queryIndex).forEach(line -> {
        String query = StringUtils.substringAfterLast(line, ">>");
        Integer count = Integer.parseInt(StringUtils.substringBefore(line, ">>"));
        queryIndexCounter.put(query, count);
      });
    }

    if (queryLog.toFile().exists()) {
      Files.lines(queryLog).forEach(line ->
          {
            String query = StringUtils.substringAfterLast(line, ">>");

            int count = 1;
            if (queryIndexCounter.containsKey(query)) {
              count = queryIndexCounter.get(query) + 1;
            }
            queryIndexCounter.put(query, count);

            Optional<QueryMatchResult> queryMatchResult = factory.match(query);
            if (queryMatchResult.isEmpty()) {
              writeToFile(queryLogTemp, line);
            } else {
              System.out.println("OK: " + line);
            }
          }
      );
    }

    Files.deleteIfExists(queryIndex);
    queryIndexCounter.entrySet().stream()
        .sorted((e1, e2) -> e1.getKey().compareToIgnoreCase(e2.getKey()))
        .forEach(e -> writeToFile(queryIndex, e.getValue() + ">>" + e.getKey()));

    if (queryLogTemp.toFile().exists()) {
      Files.move(queryLogTemp, queryLog, StandardCopyOption.REPLACE_EXISTING);
    } else {
      Files.deleteIfExists(queryLog);
    }
  }

  private void writeToFile(Path queryLogTemp, String query) {
    try {
      Files.writeString(
          queryLogTemp,
          query + "\r\n",
          StandardCharsets.UTF_8,
          StandardOpenOption.CREATE, StandardOpenOption.APPEND
      );
    } catch (IOException ioException) {
      throw new IllegalStateException(ioException);
    }
  }
}
