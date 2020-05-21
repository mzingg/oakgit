package oakgit.engine.commands;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import oakgit.engine.Command;
import oakgit.engine.CommandResult;
import oakgit.engine.model.ContainerEntry;

import java.util.Collections;

/**
 * This class offers a {@link Command} to update an entry
 */
@RequiredArgsConstructor
@Getter
@ToString
public class UpdatDataInContainerCommand implements Command {

    @NonNull
    private final String containerName;

    @NonNull
    private final String id;

    private final long modCount;

    @NonNull
    private final UpdateSet data;

    public <T extends ContainerEntry<T>> CommandResult buildResult(@NonNull T updatedEntry) {
        return new SingleEntryResult<>(containerName, updatedEntry, Collections.emptyList());
    }
}
