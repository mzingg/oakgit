package com.diconium.oakgit.engine.commands;

import com.diconium.oakgit.engine.model.ContainerEntry;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * This class {@link InsertIntoContainerCommand} offers to insert data in a container.
 */
@Getter
@Setter
@ToString(callSuper = true)
public class InsertIntoContainerCommand<T extends ContainerEntry<T>> extends AbstractCommand<InsertIntoContainerCommand<T>> {

    @NonNull
    private String containerName = StringUtils.EMPTY;

    @NonNull
    private T data;

    public InsertIntoContainerCommand(@NonNull Class<T> dataClass) {
        try {
            this.data = dataClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
