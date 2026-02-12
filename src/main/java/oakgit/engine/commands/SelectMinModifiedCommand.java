package oakgit.engine.commands;

import lombok.Getter;
import lombok.NonNull;
import oakgit.engine.Command;

@Getter
public class SelectMinModifiedCommand implements Command {

  @NonNull private final String tableName;

  public SelectMinModifiedCommand(@NonNull String tableName) {
    this.tableName = tableName;
  }
}
