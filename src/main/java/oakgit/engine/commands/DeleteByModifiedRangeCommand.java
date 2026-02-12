package oakgit.engine.commands;

import lombok.Getter;
import lombok.NonNull;
import oakgit.engine.Command;

@Getter
public class DeleteByModifiedRangeCommand implements Command {

  @NonNull private final String tableName;

  public DeleteByModifiedRangeCommand(@NonNull String tableName) {
    this.tableName = tableName;
  }
}
