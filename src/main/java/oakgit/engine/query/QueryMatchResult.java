package oakgit.engine.query;

import lombok.Data;
import oakgit.engine.Command;

import java.util.Map;
import java.util.function.Function;

@Data
public class QueryMatchResult {
    public Function<Map<Integer, Object>, Command> commandSupplier;
    private boolean interested;
    private String originQuery;
}
