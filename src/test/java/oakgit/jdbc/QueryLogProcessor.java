package oakgit.jdbc;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import oakgit.SandboxTest;
import oakgit.engine.CommandFactory;
import oakgit.engine.query.QueryMatchResult;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static oakgit.util.Matchers.isPresent;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@Slf4j
public class QueryLogProcessor {

  @SandboxTest
  void validateQueryCoverage() throws IOException {
    Path logDir = Path.of("../run/author/git");
    Path testDir = Path.of("src/test/java/oakgit");

    Map<String, Integer> queryCounterIndex = new TreeMap<>();

    log.info("------------------------------------------------------------");
    log.info("Read and update index");
    log.info("------------------------------------------------------------");
    Path queryIndex = readIndex(logDir, queryCounterIndex);
    readQueryLogAndUpdateIndex(queryIndex, logDir, queryCounterIndex);
    log.info("OK");

    log.info("------------------------------------------------------------");
    log.info("Validating coverage");
    log.info("------------------------------------------------------------");
    CommandFactory factory = new CommandFactory();
    queryCounterIndex.keySet().forEach(query -> {
      log.info(query);
      Optional<QueryMatchResult> queryMatchResult = factory.match(query);
      assertThat(queryMatchResult, isPresent());
      assertTrue(queryIsCoveredByTestIn(query, testDir.resolve("engine/query/analyzer")), "Query is not covered by Analyzer Test");
//      assertTrue(queryIsCoveredByTestIn(query, testDir.resolve("engine/CommandFactoryTest.java")), "Query is not covered by CommandFactory Test");
    });

    log.info("------------------------------------------------------------");
    log.info("Checking tests for unindexed queries");
    log.info("------------------------------------------------------------");
    logObsoleteQueriesIn(testDir, queryCounterIndex);
  }

  private void logObsoleteQueriesIn(Path testDir, Map<String, Integer> queryCounterIndex) {
    try (Stream<Path> paths = Files.walk(testDir)) {
      paths.filter(Files::isRegularFile)
          .filter(path -> path.getFileName().toString().endsWith(".java"))
          .peek(path -> log.info(path.toString()))
          .flatMap(path -> {
            try {
              return Files.lines(path);
            } catch (IOException e) {
              return Stream.of();
            }
          })
          .map(this::isProbablyAQuery)
          .filter(query -> query.isPresent() && !queryCounterIndex.containsKey(query.get()))
          .forEach(query -> log.warn("ATTENTION (not indexed): {}", query.get()));
    } catch (IOException e) {
      fail(e);
    }
  }

  private Optional<String> isProbablyAQuery(String line) {
    Pattern queryPattern = Pattern.compile(".*\"((?:create|select|update|insert).+)\".*", Pattern.CASE_INSENSITIVE);
    Matcher matcher = queryPattern.matcher(line);
    return matcher.matches() && !line.contains("// this is fine") ? Optional.of(matcher.group(1)) : Optional.empty();
  }

  @NonNull
  private Path readIndex(Path logDir, Map<String, Integer> queryIndexCounter) throws IOException {
    Path queryIndex = logDir.resolve("query.index");
    if (queryIndex.toFile().exists()) {
      Files.lines(queryIndex).forEach(line -> {
        String query = StringUtils.substringAfterLast(line, ">>");
        Integer count = Integer.parseInt(StringUtils.substringBefore(line, ">>"));
        queryIndexCounter.put(query, count);
      });
    }
    return queryIndex;
  }

  private void readQueryLogAndUpdateIndex(Path queryIndex, Path logDir, Map<String, Integer> queryIndexCounter) throws IOException {
    Path queryLog = logDir.resolve("query.log");
    if (queryLog.toFile().exists()) {
      String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
      Path queryLogSnapshot = logDir.resolve(String.format("query.%s.log", timeStamp));

      Files.lines(queryLog).forEach(line ->
          {
            String query = StringUtils.substringAfterLast(line, ">>");
            if (!query.isBlank() && !query.contains("PlaceholderData")) {
              int count = 1;
              if (queryIndexCounter.containsKey(query)) {
                count = queryIndexCounter.get(query) + 1;
              }
              queryIndexCounter.put(query, count);
            }
            writeToFile(line, queryLogSnapshot);
          }
      );

      Files.deleteIfExists(queryIndex);
      queryIndexCounter.entrySet().stream()
          .sorted((e1, e2) -> e1.getKey().compareToIgnoreCase(e2.getKey()))
          .forEach(e -> writeToFile(e.getValue() + ">>" + e.getKey(), queryIndex));

      Files.deleteIfExists(queryLog);
    }
  }

  private boolean queryIsCoveredByTestIn(String query, Path testDir) {
    try (Stream<Path> paths = Files.walk(testDir)) {
      return paths.filter(Files::isRegularFile)
          .filter(path -> path.getFileName().toString().endsWith(".java"))
          .flatMap(path -> {
            try {
              return Files.lines(path);
            } catch (IOException e) {
              return Stream.of();
            }
          })
          .anyMatch(line -> line.contains(query));
    } catch (IOException ignored) {
      // falls through to default false return
    }
    return false;
  }

  private void writeToFile(String query, Path path) {
    try {
      Files.writeString(
          path,
          query + "\r\n",
          StandardCharsets.UTF_8,
          StandardOpenOption.CREATE, StandardOpenOption.APPEND
      );
    } catch (IOException ioException) {
      throw new IllegalStateException(ioException);
    }
  }
}
