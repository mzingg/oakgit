package oakgit.engine.catalog;

import java.util.List;

public record SqlPattern(
    String id,
    String operation,
    String description,
    String sqlTemplate,
    String exampleSql,
    List<String> tables,
    String expectedAnalyzer,
    boolean implemented,
    String priority) {}
