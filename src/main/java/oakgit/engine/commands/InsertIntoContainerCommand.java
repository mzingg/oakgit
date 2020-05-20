package oakgit.engine.commands;

import oakgit.engine.Command;
import oakgit.engine.model.ContainerEntry;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

/**
 * This class {@link InsertIntoContainerCommand} offers to insert data in a container.
 */
@Getter
@Setter
@ToString
public class InsertIntoContainerCommand<T extends ContainerEntry<T>> implements Command {

    @NonNull
    private String containerName = "";

    @NonNull
    private T data;

    public InsertIntoContainerCommand(@NonNull Class<T> dataClass) {
        this.data = ContainerEntry.emptyOf(dataClass);
    }

}
