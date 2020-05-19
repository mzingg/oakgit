package com.diconium.oakgit.engine;

import com.diconium.oakgit.engine.commands.ErrorCommand;
import com.diconium.oakgit.engine.commands.NoOperationCommand;
import com.diconium.oakgit.engine.query.QueryAnalyzer;
import com.diconium.oakgit.engine.query.QueryMatchResult;
import com.diconium.oakgit.engine.query.analyzer.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class CommandFactory {

    private static final List<QueryAnalyzer> DEFAULT_ANALYZERS = new ArrayList<>();

    static {
        DEFAULT_ANALYZERS.add(new CreateAnalyzer());
        DEFAULT_ANALYZERS.add(new DeleteAnalyzer());
        DEFAULT_ANALYZERS.add(new DatastoreMetaInsertAnalyzer());
        DEFAULT_ANALYZERS.add(new DocumentInsertAnalyzer());
        DEFAULT_ANALYZERS.add(new SelectByIdAnalyzer());
        DEFAULT_ANALYZERS.add(new SelectByRangeAnalyzer());
        DEFAULT_ANALYZERS.add(new SelectInAnalyzer());
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
     * @return {@link Command}, {@link NoOperationCommand} in case the SQL was not recognized as a command.
     */
    public Command getCommandForSql(String sqlCommand) {
        return getCommandForSql(sqlCommand, Collections.emptyMap());
    }

    /**
     * Returns a {@link Command} for a given SQL using placeholder data from prepared statement.
     *
     * @param sqlCommand      {@link String}
     * @param placeholderData {@link Map}
     * @return {@link Command}, {@link NoOperationCommand} in case the SQL was not recognized as a command.
     */
    public Command getCommandForSql(String sqlCommand, Map<Integer, Object> placeholderData) {
        System.out.println("sqlCommand = " + sqlCommand);
        System.out.println("placeholderData = " + placeholderData);

        return match(sqlCommand)
            .map(matchResult -> matchResult.getCommandSupplier().apply(placeholderData))
            .orElse(new ErrorCommand("Error while parsing the query " + sqlCommand));
    }

    public Optional<QueryMatchResult> match(String sqlQuery) {
        for (QueryAnalyzer analyzer : analyzers) {
            var matchResult = analyzer.matchAndCollect(sqlQuery);
            if (matchResult != null && matchResult.isInterested()) {
                return Optional.of(matchResult);
            }
        }

        return Optional.empty();
    }

}
