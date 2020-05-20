package com.diconium.oakgit.engine.commands;

import com.diconium.oakgit.engine.Command;
import com.diconium.oakgit.engine.CommandResult;
import com.diconium.oakgit.engine.model.ContainerEntry;
import com.diconium.oakgit.engine.model.UpdateSet;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@ToString
public class UpdatDataInContainerCommand implements Command {

    @NonNull
    private String containerName = "";

    @NonNull
    private String id = "";

    private long modCount = 0L;

    @NonNull
    private UpdateSet data = new UpdateSet();

    private List<String> setExpressions = Collections.emptyList();

    public <T extends ContainerEntry<T>> CommandResult buildResult(@NonNull T foundEntry) {
        return new UpdateDataInContainerCommandResult(this, foundEntry);
    }
}
