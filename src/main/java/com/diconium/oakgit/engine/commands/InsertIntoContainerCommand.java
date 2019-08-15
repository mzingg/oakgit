package com.diconium.oakgit.engine.commands;

import com.diconium.oakgit.engine.Command;
import com.diconium.oakgit.engine.model.ContainerEntry;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * 
 * This class {@link InsertIntoContainerCommand} offers to insert data in a container.
 *
 */
@Getter
@Setter
@ToString
public class InsertIntoContainerCommand<T extends ContainerEntry> implements Command {

    @NonNull
    private String containerName = StringUtils.EMPTY;

    @NonNull
    private T data;

    public InsertIntoContainerCommand(@NonNull Class<T> dataClass) {
        try {
            this.data = dataClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
           throw new IllegalArgumentException(e);
        }
    }

}
