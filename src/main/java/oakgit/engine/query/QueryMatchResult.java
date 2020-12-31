package oakgit.engine.query;

import lombok.Data;
import oakgit.engine.Command;
import oakgit.engine.model.PlaceholderData;

import java.util.function.BiFunction;

@Data
public class QueryMatchResult {
  public BiFunction<PlaceholderData, Integer, Command> commandSupplier;
  private boolean interested;
  private String originQuery;
}
