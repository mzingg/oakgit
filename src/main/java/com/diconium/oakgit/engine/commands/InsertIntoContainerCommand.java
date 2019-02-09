package com.diconium.oakgit.engine.commands;

import com.diconium.oakgit.engine.Command;
import com.diconium.oakgit.model.ContainerEntry;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 
 * This class {@link InsertIntoContainerCommand} offers to insert data in a container.
 *
 */
@Getter
@RequiredArgsConstructor
public class InsertIntoContainerCommand<T extends ContainerEntry> implements Command {

    @NonNull
    private final String containerName;

    @NonNull
    private final T data;


    @Override
    public String toString() {
        return "InsertIntoContainerCommand{" +
                "containerName='" + containerName + '\'' +
                ", data=" + data +
                '}';
    }
}
