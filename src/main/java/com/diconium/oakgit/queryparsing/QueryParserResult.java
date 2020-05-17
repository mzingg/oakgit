package com.diconium.oakgit.queryparsing;

import com.diconium.oakgit.engine.Command;
import com.diconium.oakgit.queryparsing.analyzer.EmptyAnalyzer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.sf.jsqlparser.statement.Statement;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class QueryParserResult {

    private static final Statement NO_STATEMENT_SET = statementVisitor -> {};

    public enum ResultType {
        INSERT,
        CREATE,
        SELECT,
        UPDATE,
        DELETE,
        ERROR,
        UNKNOWN
    }

    @SuppressWarnings("unused")
    public static <T extends Statement> QueryParserResult Error(QueryAnalyzer queryAnalyzer, Class<T> statementClass, String message, Object ... messageParameters) {
        return new QueryParserResult(queryAnalyzer, (T)null, ResultType.ERROR)
                .setErrorState(true)
                .setMessage(String.format(message, messageParameters));
    }

    public static QueryParserResult Unknown(QueryAnalyzer queryAnalyzer, Statement statement, String message, Object ... messageParameters) {
        return new QueryParserResult(queryAnalyzer, statement, ResultType.UNKNOWN)
                .setUnknownState(true)
                .setMessage(String.format(message, messageParameters));
    }

    public static QueryParserResult Error(String message, Object ... messageParameters) {
        return new QueryParserResult(new EmptyAnalyzer(ResultType.ERROR))
                .setErrorState(true)
                .setMessage(String.format(message, messageParameters));
    }

    public static QueryParserResult Unknown(String message, Object ... messageParameters) {
        return new QueryParserResult(new EmptyAnalyzer(ResultType.UNKNOWN))
                .setUnknownState(true)
                .setMessage(String.format(message, messageParameters));
    }

    public QueryParserResult(QueryAnalyzer analyzer) {
        this(analyzer, NO_STATEMENT_SET, ResultType.UNKNOWN);
    }

    @NonNull
    private final QueryAnalyzer analyzer;

    @NonNull
    private final Statement statement;

    @NonNull
    @Getter
    private final ResultType resultType;

    @Getter
    @Setter(AccessLevel.PRIVATE)
    private boolean errorState;

    @Getter
    @Setter(AccessLevel.PRIVATE)
    private boolean unknownState;

    @Getter
    @Setter(AccessLevel.PRIVATE)
    private String message;

    public boolean isValid() {
        return !errorState && !unknownState;
    }

    public String getTableName() {
        return analyzer.getTableName(statement);
    }

    public String getId(Map<Integer, Object> placeholderData) {
        return analyzer.getId(statement, placeholderData).orElseThrow(IllegalStateException::new).value();
    }

    public <T> Optional<T> getDataField(String fieldName, Class<T> targetType, Map<Integer, Object> placeholderData) {
        return analyzer.getDataField(statement, fieldName, targetType, placeholderData);
    }

    public Command createCommand(Map<Integer, Object> placeholderData) {
        return analyzer.createCommand(statement, placeholderData);
    }

}
