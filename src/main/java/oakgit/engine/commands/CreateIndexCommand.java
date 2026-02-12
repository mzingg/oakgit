package oakgit.engine.commands;

import lombok.Getter;
import lombok.NonNull;
import oakgit.engine.Command;

@Getter
public class CreateIndexCommand implements Command {

  @NonNull private final String indexName;

  @NonNull private final String tableName;

  public CreateIndexCommand(@NonNull String indexName, @NonNull String tableName) {
    this.indexName = indexName;
    this.tableName = tableName;
  }
}
