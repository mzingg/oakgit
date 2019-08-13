package com.diconium.oakgit.queryparsing;

import com.diconium.oakgit.queryparsing.analyzer.EmptyAnalyzer;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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

    public static QueryParserResult Error(QueryAnalyzer queryAnalyzer, Statement statement, String message, Object ... messageParameters) {
        return new QueryParserResult(queryAnalyzer, statement)
                .setErrorState(true)
                .setMessage(String.format(message, messageParameters));
    }

    @SuppressWarnings("unused")
    public static <T extends Statement> QueryParserResult Error(QueryAnalyzer queryAnalyzer, Class<T> statementClass, String message, Object ... messageParameters) {
        return new QueryParserResult(queryAnalyzer, (T)null)
                .setErrorState(true)
                .setMessage(String.format(message, messageParameters));
    }

    public static QueryParserResult Unknown(QueryAnalyzer queryAnalyzer, Statement statement, String message, Object ... messageParameters) {
        return new QueryParserResult(queryAnalyzer, statement)
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
        this(analyzer, NO_STATEMENT_SET);
    }

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

    @Getter
    @Setter
    private String tableName = StringUtils.EMPTY;

    @NonNull
    @Setter
    private List<Expression> insertExpressions = new ArrayList<>();

    @NonNull
    @Setter
    private List<Column> insertColumns = new ArrayList<>();

    public boolean isValid() {
        return !errorState && !unknownState;
    }

    public ResultType getType() {
        return analyzer.getResultType();
    }

    public String getId(Map<Integer, Object> placeholderData) {
        return analyzer.getId(statement, placeholderData);
    }

    public Tuple2<String, String> getSelectIdRange(Map<Integer, Object> placeholderData) {
//        if (getType() == ResultType.SELECT) {
//            if (whereExpression instanceof AndExpression) {
//                AndExpression andExpression = (AndExpression)whereExpression;
//                if (andExpression.getLeftExpression() instanceof GreaterThan && andExpression.getRightExpression() instanceof MinorThan) {
//                    GreaterThan leftExpression = (GreaterThan) andExpression.getLeftExpression();
//                    MinorThan rightExpression = (MinorThan) andExpression.getRightExpression();
//
//                    String leftColumnName = ((Column) leftExpression.getLeftExpression()).getColumnName();
//                    Object leftColumnValue = StringUtils.EMPTY;
//                    if (leftExpression.getRightExpression() instanceof StringValue) {
//                        StringValue value = (StringValue) leftExpression.getRightExpression();
//                        leftColumnValue = value.getValue();
//                    } else if (leftExpression.getRightExpression() instanceof JdbcParameter) {
//                        JdbcParameter value = (JdbcParameter) leftExpression.getRightExpression();
//                        leftColumnValue = placeholderData.getOrDefault(value.getIndex(), "?#" + value.getIndex());
//                    }
//
//                    String rightColumnName = ((Column) rightExpression.getLeftExpression()).getColumnName();
//                    Object rightColumnValue = StringUtils.EMPTY;
//                    if (rightExpression.getRightExpression() instanceof StringValue) {
//                        StringValue value = (StringValue) rightExpression.getRightExpression();
//                        rightColumnValue = value.getValue();
//                    } else if (rightExpression.getRightExpression() instanceof JdbcParameter) {
//                        JdbcParameter value = (JdbcParameter) rightExpression.getRightExpression();
//                        rightColumnValue = placeholderData.getOrDefault(value.getIndex(), "?#" + value.getIndex());
//                    }
//
//                    if (leftColumnName.equals(COLUMN_NAME_ID) && rightColumnName.equals(COLUMN_NAME_ID)) {
//                        return Tuple.of(leftColumnValue.toString(), rightColumnValue.toString());
//                    }
//
//                }
//            }
//        }

        return Tuple.of("0", "0");
    }

    public Map<String, Object> getInsertData(Map<Integer, Object> placeholderData) {
        Map<String, Object> result = new LinkedHashMap<>();

        for (int i = 0; i < insertColumns.size() && i < insertExpressions.size(); i++) {
            String columnName = insertColumns.get(i).getColumnName();
            Object columnValue = StringUtils.EMPTY;
            if (insertExpressions.get(i) instanceof StringValue) {
                StringValue value = (StringValue) insertExpressions.get(i);
                columnValue = value.getValue();
            } else if (insertExpressions.get(i) instanceof JdbcParameter) {
                JdbcParameter value = (JdbcParameter) insertExpressions.get(i);
                columnValue = placeholderData.getOrDefault(value.getIndex(), "?#" + value.getIndex());
            }

            result.put(columnName, columnValue);
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> getInsertDataField(String fieldName, Class<T> targetType, Map<Integer, Object> placeholderData) {
        Object fieldValue = getInsertData(placeholderData).getOrDefault(fieldName, null);
        if (fieldValue != null && targetType.isAssignableFrom(fieldValue.getClass())) {
            return Optional.of((T) fieldValue);
        }

        return Optional.empty();
    }

}
