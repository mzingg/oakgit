package com.diconium.oakgit.engine.commands;

import com.diconium.oakgit.engine.Command;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Map;

@Getter
@ToString
public abstract class AbstractCommand<T extends AbstractCommand<T>> implements Command {

    @NonNull
    private String originSql = StringUtils.EMPTY;

    @NonNull
    private Map<Integer, Object> placeholderData = Collections.emptyMap();

    @SuppressWarnings("unchecked")
    public T setOriginSql(@NonNull String originSql) {
        this.originSql = originSql;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setPlaceholderData(@NonNull Map<Integer, Object> placeholderData) {
        this.placeholderData = placeholderData;
        return (T) this;
    }

}
