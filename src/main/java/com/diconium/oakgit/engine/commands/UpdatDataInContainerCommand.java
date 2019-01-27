package com.diconium.oakgit.engine.commands;

import com.diconium.oakgit.engine.CommandResult;
import com.diconium.oakgit.engine.model.ContainerEntry;
import com.diconium.oakgit.engine.model.UpdateSet;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
@ToString(callSuper = true)
public class UpdatDataInContainerCommand extends AbstractCommand<UpdatDataInContainerCommand> {

    @NonNull
    private String containerName = StringUtils.EMPTY;

    @NonNull
    private String id = StringUtils.EMPTY;

    private long modCount = 0L;

    @NonNull
    private UpdateSet data = new UpdateSet();

    public <T extends ContainerEntry<T>> CommandResult buildResult(@NonNull ContainerEntry<T> foundEntry) {
        return new UpdateDataInContainerCommandResult<>(this, foundEntry);
    }
}
