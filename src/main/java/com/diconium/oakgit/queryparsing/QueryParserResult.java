package com.diconium.oakgit.queryparsing;

import com.diconium.oakgit.engine.Command;
import com.diconium.oakgit.queryparsing.analyzer.EmptyAnalyzer;
import lombok.*;
import net.sf.jsqlparser.statement.Statement;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class QueryParserResult {

    private static final Statement NO_STATEMENT_SET = statementVisitor -> {
    };

    @Getter
    @NonNull
    private final QueryAnalyzer analyzer;

    @NonNull
    private final Statement statement;

    @Getter
    @Setter(AccessLevel.PRIVATE)
    private boolean errorState;

    @Getter
    @Setter(AccessLevel.PRIVATE)
    private boolean unknownState;

    @Getter
    @Setter(AccessLevel.PRIVATE)
    private String message;

    public QueryParserResult(QueryAnalyzer analyzer) {
        this(analyzer, NO_STATEMENT_SET);
    }

    @SuppressWarnings("unused")
    public static <T extends Statement> QueryParserResult Error(QueryAnalyzer queryAnalyzer, Class<T> statementClass, String message, Object... messageParameters) {
        return new QueryParserResult(queryAnalyzer, null)
                .setErrorState(true)
                .setMessage(String.format(message, messageParameters));
    }

    public static QueryParserResult Unknown(QueryAnalyzer queryAnalyzer, Statement statement, String message, Object... messageParameters) {
        return new QueryParserResult(queryAnalyzer, statement)
                .setUnknownState(true)
                .setMessage(String.format(message, messageParameters));
    }

    public static QueryParserResult Error(String message, Object... messageParameters) {
        return new QueryParserResult(new EmptyAnalyzer())
                .setErrorState(true)
                .setMessage(String.format(message, messageParameters));
    }

    public static QueryParserResult Unknown(String message, Object... messageParameters) {
        return new QueryParserResult(new EmptyAnalyzer())
                .setUnknownState(true)
                .setMessage(String.format(message, messageParameters));
    }

    public boolean isValid() {
        return !errorState && !unknownState;
    }

    public String getTableName() {
        return analyzer.getTableName(statement);
    }

    public Optional<QueryId> getId(Map<Integer, Object> placeholderData) {
        return analyzer.getId(statement, placeholderData);
    }

    public <T> Optional<T> getDataField(String fieldName, Class<T> targetType, Map<Integer, Object> placeholderData) {
        return analyzer.getDataField(statement, fieldName, targetType, placeholderData);
    }

    public Command asCommand(Map<Integer, Object> placeholderData) {
        return analyzer.createCommand(statement, placeholderData);
    }

}
