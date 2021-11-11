package oakgit.engine;

import lombok.Getter;
import lombok.Setter;
import oakgit.engine.commands.ErrorCommand;
import oakgit.engine.model.PlaceholderData;
import oakgit.engine.query.QueryAnalyzer;
import oakgit.engine.query.QueryMatchResult;
import oakgit.engine.query.analyzer.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Getter
@Setter
public class CommandFactory {

  private static final List<QueryAnalyzer> DEFAULT_ANALYZERS = new ArrayList<>();

  static {
    DEFAULT_ANALYZERS.add(new CreateAnalyzer());
    DEFAULT_ANALYZERS.add(new DocumentInsertAnalyzer());
    DEFAULT_ANALYZERS.add(new SelectByModifiedAnalyzer());
    DEFAULT_ANALYZERS.add(new SelectInAnalyzer());
    DEFAULT_ANALYZERS.add(new SelectByRangeAnalyzer());
    DEFAULT_ANALYZERS.add(new SelectByIdAnalyzer());
    DEFAULT_ANALYZERS.add(new UpdateAnalyzer());
  }

  private List<QueryAnalyzer> analyzers;

  public CommandFactory() {
    this.analyzers = DEFAULT_ANALYZERS;
  }

  /**
   * Returns a {@link Command} for a given SQL string.
   *
   * @param sqlCommand {@link String}
   * @return {@link Command}, {@link ErrorCommand} in case the SQL was not recognized as a command.
   */
  public Command getCommandForSql(String sqlCommand) {
    return getCommandForSql(sqlCommand, new PlaceholderData(), Integer.MAX_VALUE);
  }

  /**
   * Returns a {@link Command} for a given SQL using placeholder data from prepared statement.
   *
   * @param sqlCommand      {@link String}
   * @param placeholderData {@link Map}
   * @param selectionLimit  int
   * @return {@link Command}, {@link ErrorCommand} in case the SQL was not recognized as a command.
   */
  public Command getCommandForSql(String sqlCommand, PlaceholderData placeholderData, int selectionLimit) {
    return match(sqlCommand)
        .map(matchResult -> matchResult.getCommandSupplier().apply(placeholderData, selectionLimit))
        .orElse(new ErrorCommand("Error while parsing the query " + sqlCommand));
  }

  public Optional<QueryMatchResult> match(String sqlQuery) {
    for (QueryAnalyzer analyzer : analyzers) {
      QueryMatchResult matchResult = analyzer.matchAndCollect(sqlQuery);
      if (matchResult != null && matchResult.isInterested()) {
        return Optional.of(matchResult);
      }
    }

    return Optional.empty();
  }

}
