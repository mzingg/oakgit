package oakgit.engine.store;

import java.util.Base64;

public final class StorageDocumentCodec {

  private StorageDocumentCodec() {}

  public static String toJson(StorageDocument doc) {
    var sb = new StringBuilder("{\n");
    var first = true;

    first = appendString(sb, "id", doc.getId(), first);
    first = appendLong(sb, "modified", doc.getModified(), first);
    first = appendLong(sb, "modCount", doc.getModCount(), first);
    first = appendLong(sb, "cModCount", doc.getCModCount(), first);
    first = appendLong(sb, "dSize", doc.getDSize(), first);
    first = appendLong(sb, "sdMaxRevTime", doc.getSdMaxRevTime(), first);
    first = appendLong(sb, "lastmod", doc.getLastmod(), first);
    first = appendInteger(sb, "hasBinary", doc.getHasBinary(), first);
    first = appendInteger(sb, "deletedOnce", doc.getDeletedOnce(), first);
    first = appendInteger(sb, "version", doc.getVersion(), first);
    first = appendInteger(sb, "sdType", doc.getSdType(), first);
    first = appendInteger(sb, "lvl", doc.getLvl(), first);
    first = appendBytes(sb, "data", doc.getData(), first);
    appendBytes(sb, "bdata", doc.getBdata(), first);

    sb.append("\n}");
    return sb.toString();
  }

  public static StorageDocument fromJson(String json) {
    var doc = new StorageDocument();
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

      applyField(doc, key, valueResult.value());
    }

    if (doc.getId() == null) {
      throw new IllegalArgumentException("Missing required field: id");
    }
    return doc;
  }

  private static void applyField(StorageDocument doc, String key, String value) {
    switch (key) {
      case "id" -> doc.setId(value);
      case "modified" -> doc.setModified(parseLong(value));
      case "modCount" -> doc.setModCount(parseLong(value));
      case "cModCount" -> doc.setCModCount(parseLong(value));
      case "dSize" -> doc.setDSize(parseLong(value));
      case "sdMaxRevTime" -> doc.setSdMaxRevTime(parseLong(value));
      case "lastmod" -> doc.setLastmod(parseLong(value));
      case "hasBinary" -> doc.setHasBinary(parseInt(value));
      case "deletedOnce" -> doc.setDeletedOnce(parseInt(value));
      case "version" -> doc.setVersion(parseInt(value));
      case "sdType" -> doc.setSdType(parseInt(value));
      case "lvl" -> doc.setLvl(parseInt(value));
      case "data" -> doc.setData(decodeBytes(value));
      case "bdata" -> doc.setBdata(decodeBytes(value));
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
