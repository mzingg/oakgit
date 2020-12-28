package oakgit.engine.query;

import lombok.Data;
import oakgit.engine.Command;
import oakgit.engine.model.PlaceholderData;

import java.util.function.Function;

@Data
public class QueryMatchResult {
  public Function<PlaceholderData, Command> commandSupplier;
  private boolean interested;
  private String originQuery;
}
