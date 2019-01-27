package com.diconium.oakgit.commons;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SelectItem;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QueryParserResult {

    public enum ResultType {
        INSERT,
        CREATE,
        SELECT,
        ERROR,
        UNKNOWN;
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
    	if(type == null) {
    		throw new IllegalArgumentException();
    	}
        this.type = type;
    }

    public String getId() {
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
            id = getInsertTuples().getOrDefault(COLUMN_NAME_ID, StringUtils.EMPTY);
        }
        return id;
    }

    public Map<String, String> getInsertTuples() {
        Map<String, String> result = new LinkedHashMap<>();

        for (int i = 0; i < insertColumns.size() && i < insertExpressions.size(); i++) {
            String columnName = insertColumns.get(i).getColumnName();
            String columnValue = StringUtils.EMPTY;
            if (insertExpressions.get(i) instanceof StringValue) {
                StringValue value = (StringValue) insertExpressions.get(i);
                columnValue = value.getValue();
            } else if (insertExpressions.get(i) instanceof JdbcParameter) {
                JdbcParameter value = (JdbcParameter) insertExpressions.get(i);
                columnValue = "?#" + value.getIndex();
            }

            if (StringUtils.isNotBlank(columnName) && StringUtils.isNotBlank(columnValue)) {
                result.put(columnName, columnValue);
            }
        }

        return result;
    }
}
