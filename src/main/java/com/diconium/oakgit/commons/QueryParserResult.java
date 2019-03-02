package com.diconium.oakgit.commons;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SelectItem;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class QueryParserResult {

    public enum ResultType {
        INSERT,
        CREATE,
        SELECT,
        UPDATE,
        ERROR,
        UNKNOWN
    }

    public static final QueryParserResult ERROR_RESULT = new QueryParserResult(ResultType.ERROR);
    public static final String COLUMN_NAME_ID = "ID";
    public static final StringValue NULL_WHERE_EXPRESSION = new StringValue(StringUtils.EMPTY);

    @Getter
    private ResultType type;

    @Getter
    @Setter
    private String tableName = StringUtils.EMPTY;

    @NonNull
    @Setter
    private List<Expression> insertExpressions = new ArrayList<>();

    @NonNull
    @Setter
    private List<Column> insertColumns = new ArrayList<>();

    @NonNull
    @Setter
    private List<SelectItem> selectItems = new ArrayList<>();

    @NonNull
    @Setter
    private Expression whereExpression = NULL_WHERE_EXPRESSION;

    public QueryParserResult(ResultType type) {
        if (type == null) {
            throw new IllegalArgumentException();
        }
        this.type = type;
    }

    public String getId(Map<Integer, Object> placeholderData) {
        String id = StringUtils.EMPTY;
        if (getType() == ResultType.SELECT) {
            if (whereExpression instanceof EqualsTo) {
                Expression left = ((EqualsTo) whereExpression).getLeftExpression();
                Expression right = ((EqualsTo) whereExpression).getRightExpression();
                String sRight = StringUtils.EMPTY;
                if (right instanceof StringValue) {
                    sRight = ((StringValue) right).getValue();
                } else if (right instanceof LongValue) {
                    sRight = ((LongValue) right).getStringValue();
                }
                if (left instanceof Column && ((Column) left).getColumnName().equals(COLUMN_NAME_ID) && StringUtils.isNotEmpty(sRight)) {
                    id = sRight;
                }
            }
        } else if (getType() == ResultType.INSERT) {
            id = (String) getInsertData(placeholderData).getOrDefault(COLUMN_NAME_ID, StringUtils.EMPTY);
        }
        return id;
    }

    public Tuple2<String, String> getSelectIdRange(Map<Integer, Object> placeholderData) {
        if (getType() == ResultType.SELECT) {
            if (whereExpression instanceof AndExpression) {
                AndExpression andExpression = (AndExpression)whereExpression;
                if (andExpression.getLeftExpression() instanceof GreaterThan && andExpression.getRightExpression() instanceof MinorThan) {
                    GreaterThan leftExpression = (GreaterThan) andExpression.getLeftExpression();
                    MinorThan rightExpression = (MinorThan) andExpression.getRightExpression();

                    String leftColumnName = ((Column) leftExpression.getLeftExpression()).getColumnName();
                    Object leftColumnValue = StringUtils.EMPTY;
                    if (leftExpression.getRightExpression() instanceof StringValue) {
                        StringValue value = (StringValue) leftExpression.getRightExpression();
                        leftColumnValue = value.getValue();
                    } else if (leftExpression.getRightExpression() instanceof JdbcParameter) {
                        JdbcParameter value = (JdbcParameter) leftExpression.getRightExpression();
                        leftColumnValue = placeholderData.getOrDefault(value.getIndex(), "?#" + value.getIndex());
                    }

                    String rightColumnName = ((Column) rightExpression.getLeftExpression()).getColumnName();
                    Object rightColumnValue = StringUtils.EMPTY;
                    if (rightExpression.getRightExpression() instanceof StringValue) {
                        StringValue value = (StringValue) rightExpression.getRightExpression();
                        rightColumnValue = value.getValue();
                    } else if (rightExpression.getRightExpression() instanceof JdbcParameter) {
                        JdbcParameter value = (JdbcParameter) rightExpression.getRightExpression();
                        rightColumnValue = placeholderData.getOrDefault(value.getIndex(), "?#" + value.getIndex());
                    }

                    if (leftColumnName.equals(COLUMN_NAME_ID) && rightColumnName.equals(COLUMN_NAME_ID)) {
                        return Tuple.of(leftColumnValue.toString(), rightColumnValue.toString());
                    }

                }
            }
        }

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
