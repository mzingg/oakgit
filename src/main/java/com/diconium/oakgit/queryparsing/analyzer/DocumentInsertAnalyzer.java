package com.diconium.oakgit.queryparsing.analyzer;

import com.diconium.oakgit.engine.commands.InsertIntoContainerCommand;
import com.diconium.oakgit.engine.model.DocumentEntry;
import com.diconium.oakgit.queryparsing.QueryAnalyzer;
import com.diconium.oakgit.queryparsing.QueryMatchResult;

import java.util.regex.Pattern;

public class DocumentInsertAnalyzer implements QueryAnalyzer {

    private final Pattern INSERT_PATTERN = Pattern.compile("insert into ([\\w_]+)" +
        "\\(ID, MODIFIED, HASBINARY, DELETEDONCE, MODCOUNT, CMODCOUNT, DSIZE, VERSION, SDTYPE, SDMAXREVTIME, DATA, BDATA\\) " +
        "values \\(\\?, \\?, \\?, \\?, \\?, \\?, \\?,\\s+2, \\?, \\?, \\?, \\?\\)"
    );

    @Override
    public QueryMatchResult matchAndCollect(String sqlQuery) {
        return withPatternMatch(sqlQuery, INSERT_PATTERN, (result, matcher) -> {
            String tableName = matcher.group(1);
            result.setCommandSupplier(placeholderData -> {
                DocumentEntry data = new DocumentEntry();
                data.setId(typedNullsafe(placeholderData.get(1), String.class));
                data.setModified(typedNullsafe(placeholderData.get(2), Long.class));
                data.setHasBinary(typedNullsafe(placeholderData.get(3), Integer.class));
                data.setDeletedOnce(typedNullsafe(placeholderData.get(4), Integer.class));
                data.setModCount(typedNullsafe(placeholderData.get(5), Long.class));
                data.setCModCount(typedNullsafe(placeholderData.get(6), Long.class));
                data.setDSize(typedNullsafe(placeholderData.get(7), Long.class));
                data.setVersion(2);
                data.setSdType(typedNullsafe(placeholderData.get(8), Integer.class));
                data.setSdMaxRevTime(typedNullsafe(placeholderData.get(9), Long.class));
                data.setData(typedNullsafe(placeholderData.get(10), byte[].class));
                data.setBdata(typedNullsafe(placeholderData.get(11), byte[].class));

                return new InsertIntoContainerCommand<>(DocumentEntry.class)
                    .setContainerName(tableName)
                    .setData(data);
            });
            return result;
        });
    }

    @SuppressWarnings("unchecked")
    private <T> T typedNullsafe(Object object, Class<T> targetClass) {
        if (object != null) {
            if (targetClass.equals(String.class)) {
                return (T) object.toString();
            } else if (targetClass.equals(byte[].class)) {
                return (T) ((String) object).getBytes();
            } else if (targetClass.isAssignableFrom(object.getClass())) {
                return (T) object;
            } else {
                throw new IllegalArgumentException("Object " + object + " is not of type " + targetClass);
            }
        }
        return null;
    }

}
