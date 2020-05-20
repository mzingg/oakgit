package oakgit.engine.query.analyzer;

import oakgit.engine.commands.InsertIntoContainerCommand;
import oakgit.engine.model.DocumentEntry;
import oakgit.engine.query.QueryAnalyzer;
import oakgit.engine.query.QueryMatchResult;

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

                return new InsertIntoContainerCommand<>(tableName, data);
            });
            return result;
        });
    }

    @SuppressWarnings("unchecked")
    private <T> T typedNullsafe(Object object, Class<T> targetClass) {
        if (object != null) {
            if (!(object instanceof String) && targetClass.equals(String.class)) {
                return (T) object.toString();
            } else if (object instanceof String && targetClass.equals(byte[].class)) {
                return (T) ((String) object).getBytes();
            } else if (targetClass.isAssignableFrom(object.getClass())) {
                return (T) object;
            } else if (targetClass.equals(Long.class) && object instanceof Integer) {
                return (T) Long.valueOf(((Integer)object).longValue());
            } else {
                throw new IllegalArgumentException("Object " + object + " is not of type " + targetClass);
            }
        }
        return null;
    }

}
