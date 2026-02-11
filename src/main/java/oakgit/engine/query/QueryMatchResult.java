package oakgit.engine.query;

import java.util.function.BiFunction;
import lombok.Data;
import oakgit.engine.Command;
import oakgit.engine.model.PlaceholderData;

@Data
public class QueryMatchResult {
  public BiFunction<PlaceholderData, Integer, Command> commandSupplier;
  private boolean interested;
  private String originQuery;
}
