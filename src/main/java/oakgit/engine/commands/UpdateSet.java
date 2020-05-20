package oakgit.engine.commands;

import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import oakgit.engine.model.DocumentEntry;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

@ToString
public class UpdateSet {

    @Setter
    private List<String> setExpressions = Collections.emptyList();

    private final Map<String, Object> updatedValues = new HashMap<>();

    public UpdateSet withValue(@NonNull String name, Object value) {
        updatedValues.put(name.trim().toLowerCase(), value);
        return this;
    }

    public UpdateSet update(DocumentEntry entityToUpdate) {
        whenHasValue("MODIFIED", Long.class, entityToUpdate, entityToUpdate::setModified);
        whenHasValue("HASBINARY", Integer.class, entityToUpdate, entityToUpdate::setHasBinary);
        whenHasValue("DELETEDONCE", Integer.class, entityToUpdate, entityToUpdate::setDeletedOnce);
        whenHasValue("MODCOUNT", Long.class, entityToUpdate, entityToUpdate::setModCount);
        whenHasValue("CMODCOUNT", Long.class, entityToUpdate, entityToUpdate::setCModCount);
        whenHasValue("DSIZE", Long.class, entityToUpdate, entityToUpdate::setDSize);
        whenHasValue("VERSION", Integer.class, entityToUpdate, entityToUpdate::setVersion);
        whenHasValue("SDTYPE", Integer.class, entityToUpdate, entityToUpdate::setSdType);
        whenHasValue("SDMAXREVTIME", Long.class, entityToUpdate, entityToUpdate::setSdMaxRevTime);
        whenHasValue("DATA", byte[].class, entityToUpdate, entityToUpdate::setData);
        whenHasValue("BDATA", byte[].class, entityToUpdate, entityToUpdate::setBdata);
        return this;
    }

    @SuppressWarnings("unchecked")
    private <T> UpdateSet whenHasValue(@NonNull String name, Class<T> targetType, DocumentEntry entityToUpdate, Consumer<T> setter) {
        String key = name.trim().toLowerCase();
        if (updatedValues.containsKey(key)) {
            Object value = updatedValues.get(key);
            if (value instanceof Function) {
                value = ((Function<DocumentEntry, ?>) value).apply(entityToUpdate);
            }

            if (value == null || targetType.isAssignableFrom(value.getClass())) {
                setter.accept((T) value);
            }
        }
        return this;
    }
}
