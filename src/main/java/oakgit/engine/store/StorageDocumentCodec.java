package oakgit.engine.store;

import java.util.Base64;

public final class StorageDocumentCodec {

  private StorageDocumentCodec() {}

  public static String toJson(StorageDocument doc) {
    var sb = new StringBuilder("{\n");
    var first = true;

    first = appendString(sb, "id", doc.id(), first);
    first = appendLong(sb, "modified", doc.modified(), first);
    first = appendLong(sb, "modCount", doc.modCount(), first);
    first = appendLong(sb, "cModCount", doc.cModCount(), first);
    first = appendLong(sb, "dSize", doc.dSize(), first);
    first = appendLong(sb, "sdMaxRevTime", doc.sdMaxRevTime(), first);
    first = appendLong(sb, "lastmod", doc.lastmod(), first);
    first = appendInteger(sb, "hasBinary", doc.hasBinary(), first);
    first = appendInteger(sb, "deletedOnce", doc.deletedOnce(), first);
    first = appendInteger(sb, "version", doc.version(), first);
    first = appendInteger(sb, "sdType", doc.sdType(), first);
    first = appendInteger(sb, "lvl", doc.lvl(), first);
    first = appendBytes(sb, "data", doc.data(), first);
    appendBytes(sb, "bdata", doc.bdata(), first);

    sb.append("\n}");
    return sb.toString();
  }

  public static StorageDocument fromJson(String json) {
    var builder = StorageDocument.builder();
    var content = json.strip();
    if (content.length() < 2
        || content.charAt(0) != '{'
        || content.charAt(content.length() - 1) != '}') {
      throw new IllegalArgumentException("Invalid JSON: " + json);
    }
    content = content.substring(1, content.length() - 1).strip();
    if (content.isEmpty()) {
      throw new IllegalArgumentException("Missing required field: id");
    }

    var hasId = false;
    var pos = 0;
    while (pos < content.length()) {
      pos = skipWhitespace(content, pos);
      if (pos >= content.length()) {
        break;
      }
      if (content.charAt(pos) == ',') {
        pos++;
        continue;
      }

      var keyResult = readString(content, pos);
      var key = keyResult.value();
      pos = skipWhitespace(content, keyResult.end());
      if (pos >= content.length() || content.charAt(pos) != ':') {
        throw new IllegalArgumentException("Expected ':' after key: " + key);
      }
      pos = skipWhitespace(content, pos + 1);

      var valueResult = readValue(content, pos);
      pos = valueResult.end();

      if ("id".equals(key)) {
        hasId = true;
      }
      applyField(builder, key, valueResult.value());
    }

    if (!hasId) {
      throw new IllegalArgumentException("Missing required field: id");
    }
    return builder.build();
  }

  private static void applyField(
      StorageDocument.StorageDocumentBuilder builder, String key, String value) {
    switch (key) {
      case "id" -> builder.id(value);
      case "modified" -> builder.modified(parseLong(value));
      case "modCount" -> builder.modCount(parseLong(value));
      case "cModCount" -> builder.cModCount(parseLong(value));
      case "dSize" -> builder.dSize(parseLong(value));
      case "sdMaxRevTime" -> builder.sdMaxRevTime(parseLong(value));
      case "lastmod" -> builder.lastmod(parseLong(value));
      case "hasBinary" -> builder.hasBinary(parseInt(value));
      case "deletedOnce" -> builder.deletedOnce(parseInt(value));
      case "version" -> builder.version(parseInt(value));
      case "sdType" -> builder.sdType(parseInt(value));
      case "lvl" -> builder.lvl(parseInt(value));
      case "data" -> builder.data(decodeBytes(value));
      case "bdata" -> builder.bdata(decodeBytes(value));
      default -> {} // ignore unknown fields
    }
  }

  private static Long parseLong(String value) {
    return "null".equals(value) ? null : Long.valueOf(value);
  }

  private static Integer parseInt(String value) {
    return "null".equals(value) ? null : Integer.valueOf(value);
  }

  private static byte[] decodeBytes(String value) {
    if ("null".equals(value)) {
      return null;
    }
    return Base64.getDecoder().decode(value);
  }

  private static boolean appendString(StringBuilder sb, String key, String value, boolean first) {
    if (value == null) {
      return first;
    }
    if (!first) {
      sb.append(",\n");
    }
    sb.append("  \"").append(key).append("\": \"").append(escapeJson(value)).append("\"");
    return false;
  }

  private static boolean appendLong(StringBuilder sb, String key, Long value, boolean first) {
    if (value == null) {
      return first;
    }
    if (!first) {
      sb.append(",\n");
    }
    sb.append("  \"").append(key).append("\": ").append(value);
    return false;
  }

  private static boolean appendInteger(StringBuilder sb, String key, Integer value, boolean first) {
    if (value == null) {
      return first;
    }
    if (!first) {
      sb.append(",\n");
    }
    sb.append("  \"").append(key).append("\": ").append(value);
    return false;
  }

  private static boolean appendBytes(StringBuilder sb, String key, byte[] value, boolean first) {
    if (value == null) {
      return first;
    }
    if (!first) {
      sb.append(",\n");
    }
    sb.append("  \"")
        .append(key)
        .append("\": \"")
        .append(Base64.getEncoder().encodeToString(value))
        .append("\"");
    return false;
  }

  private static String escapeJson(String value) {
    var sb = new StringBuilder(value.length());
    for (var i = 0; i < value.length(); i++) {
      var c = value.charAt(i);
      switch (c) {
        case '"' -> sb.append("\\\"");
        case '\\' -> sb.append("\\\\");
        case '\n' -> sb.append("\\n");
        case '\r' -> sb.append("\\r");
        case '\t' -> sb.append("\\t");
        default -> sb.append(c);
      }
    }
    return sb.toString();
  }

  private static int skipWhitespace(String s, int pos) {
    while (pos < s.length() && Character.isWhitespace(s.charAt(pos))) {
      pos++;
    }
    return pos;
  }

  private static ParseResult readString(String s, int pos) {
    if (s.charAt(pos) != '"') {
      throw new IllegalArgumentException("Expected '\"' at position " + pos);
    }
    var sb = new StringBuilder();
    var i = pos + 1;
    while (i < s.length()) {
      var c = s.charAt(i);
      if (c == '\\' && i + 1 < s.length()) {
        var next = s.charAt(i + 1);
        switch (next) {
          case '"' -> sb.append('"');
          case '\\' -> sb.append('\\');
          case 'n' -> sb.append('\n');
          case 'r' -> sb.append('\r');
          case 't' -> sb.append('\t');
          default -> {
            sb.append('\\');
            sb.append(next);
          }
        }
        i += 2;
      } else if (c == '"') {
        return new ParseResult(sb.toString(), i + 1);
      } else {
        sb.append(c);
        i++;
      }
    }
    throw new IllegalArgumentException("Unterminated string starting at position " + pos);
  }

  private static ParseResult readValue(String s, int pos) {
    if (s.charAt(pos) == '"') {
      return readString(s, pos);
    }
    var end = pos;
    while (end < s.length()
        && s.charAt(end) != ','
        && s.charAt(end) != '}'
        && !Character.isWhitespace(s.charAt(end))) {
      end++;
    }
    return new ParseResult(s.substring(pos, end), end);
  }

  private record ParseResult(String value, int end) {}
}
