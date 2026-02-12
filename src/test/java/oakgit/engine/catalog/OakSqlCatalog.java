package oakgit.engine.catalog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public record OakSqlCatalog(String oakVersion, String dialect, List<SqlPattern> patterns) {

  private static final Pattern STRING_VALUE = Pattern.compile("\"([^\"]*?)\"\\s*[:,\\]]");
  private static final Pattern BOOL_VALUE = Pattern.compile(":\\s*(true|false)");

  public static OakSqlCatalog load(String oakVersion) {
    var resourcePath = "oak-sql-catalog/oak-" + oakVersion + ".json";
    try (var stream = OakSqlCatalog.class.getClassLoader().getResourceAsStream(resourcePath)) {
      if (stream == null) {
        throw new IllegalArgumentException(
            "No catalog found for OAK version "
                + oakVersion
                + " (expected resource: "
                + resourcePath
                + ")");
      }
      return parse(stream);
    } catch (IOException e) {
      throw new RuntimeException("Failed to load catalog for OAK " + oakVersion, e);
    }
  }

  public static List<String> availableVersions() {
    // Discovery of available catalogs — for now returns known versions
    // This can be extended to scan the classpath resource directory
    var versions = new ArrayList<String>();
    for (var candidate : List.of("1.90.0")) {
      var resource =
          OakSqlCatalog.class
              .getClassLoader()
              .getResource("oak-sql-catalog/oak-" + candidate + ".json");
      if (resource != null) {
        versions.add(candidate);
      }
    }
    return versions;
  }

  public List<SqlPattern> implementedPatterns() {
    return patterns.stream().filter(SqlPattern::implemented).toList();
  }

  public List<SqlPattern> unimplementedPatterns() {
    return patterns.stream().filter(p -> !p.implemented()).toList();
  }

  static OakSqlCatalog parse(InputStream input) throws IOException {
    var content = readFully(input);
    var oakVersion = extractTopLevelString(content, "oakVersion");
    var dialect = extractTopLevelString(content, "dialect");
    var patterns = parsePatterns(content);
    return new OakSqlCatalog(oakVersion, dialect, patterns);
  }

  private static String readFully(InputStream input) throws IOException {
    try (var reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
      var sb = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        sb.append(line).append('\n');
      }
      return sb.toString();
    }
  }

  private static String extractTopLevelString(String json, String key) {
    var pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]*?)\"");
    var matcher = pattern.matcher(json);
    if (matcher.find()) {
      return matcher.group(1);
    }
    throw new IllegalArgumentException("Missing required field: " + key);
  }

  private static List<SqlPattern> parsePatterns(String json) {
    var patterns = new ArrayList<SqlPattern>();

    // Find the patterns array and split into individual objects
    int patternsStart = json.indexOf("\"patterns\"");
    if (patternsStart < 0) {
      throw new IllegalArgumentException("Missing 'patterns' array");
    }

    int arrayStart = json.indexOf('[', patternsStart);
    // Find matching objects by tracking brace depth
    int depth = 0;
    int objStart = -1;
    for (int i = arrayStart; i < json.length(); i++) {
      char c = json.charAt(i);
      if (c == '{') {
        if (depth == 0) {
          objStart = i;
        }
        depth++;
      } else if (c == '}') {
        depth--;
        if (depth == 0 && objStart >= 0) {
          patterns.add(parsePattern(json.substring(objStart, i + 1)));
          objStart = -1;
        }
      } else if (c == ']' && depth == 0) {
        break;
      }
    }
    return patterns;
  }

  private static SqlPattern parsePattern(String obj) {
    return new SqlPattern(
        extractString(obj, "id"),
        extractString(obj, "operation"),
        extractString(obj, "description"),
        extractString(obj, "sqlTemplate"),
        extractString(obj, "exampleSql"),
        extractStringArray(obj, "tables"),
        extractNullableString(obj, "expectedAnalyzer"),
        extractBoolean(obj, "implemented"),
        extractString(obj, "priority"));
  }

  private static String extractString(String json, String key) {
    var value = extractNullableString(json, key);
    if (value == null) {
      throw new IllegalArgumentException("Missing required field: " + key);
    }
    return value;
  }

  private static String extractNullableString(String json, String key) {
    // Check for null value first
    var nullPattern = Pattern.compile("\"" + key + "\"\\s*:\\s*null");
    if (nullPattern.matcher(json).find()) {
      return null;
    }

    var pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"");
    var matcher = pattern.matcher(json);
    if (matcher.find()) {
      return unescapeJson(matcher.group(1));
    }
    return null;
  }

  private static boolean extractBoolean(String json, String key) {
    var pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*(true|false)");
    var matcher = pattern.matcher(json);
    if (matcher.find()) {
      return Boolean.parseBoolean(matcher.group(1));
    }
    throw new IllegalArgumentException("Missing required boolean field: " + key);
  }

  private static List<String> extractStringArray(String json, String key) {
    var pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*\\[([^\\]]*)\\]");
    var matcher = pattern.matcher(json);
    if (matcher.find()) {
      var arrayContent = matcher.group(1);
      var items = new ArrayList<String>();
      var itemPattern = Pattern.compile("\"([^\"]*?)\"");
      var itemMatcher = itemPattern.matcher(arrayContent);
      while (itemMatcher.find()) {
        items.add(itemMatcher.group(1));
      }
      return items;
    }
    return List.of();
  }

  private static String unescapeJson(String value) {
    return value
        .replace("\\\"", "\"")
        .replace("\\\\", "\\")
        .replace("\\n", "\n")
        .replace("\\t", "\t");
  }
}
