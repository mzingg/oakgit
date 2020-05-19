package com.diconium.oakgit.engine.commands;

import com.diconium.oakgit.engine.Command;
import com.diconium.oakgit.engine.model.ContainerEntry;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * This class {@link InsertIntoContainerCommand} offers to insert data in a container.
 */
@Getter
@Setter
@ToString
public class InsertIntoContainerCommand<T extends ContainerEntry<T>> implements Command {

    @NonNull
    private String containerName = StringUtils.EMPTY;

    @NonNull
    private T data;

    public InsertIntoContainerCommand(@NonNull Class<T> dataClass) {
        this.data = ContainerEntry.emptyOf(dataClass);
    }

}
