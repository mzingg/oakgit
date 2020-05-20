package oakgit.engine.commands;

import lombok.*;
import oakgit.engine.Command;
import oakgit.engine.model.ContainerEntry;

/**
 * This class offers a {@link Command} to insert data in a container.
 */
@RequiredArgsConstructor
@Getter
@ToString
public class InsertIntoContainerCommand<T extends ContainerEntry<T>> implements Command {

    @NonNull
    private final String containerName;

    @NonNull
    private final T data;

}
