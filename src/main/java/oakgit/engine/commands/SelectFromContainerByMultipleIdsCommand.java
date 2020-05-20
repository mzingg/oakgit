package oakgit.engine.commands;

import lombok.*;
import oakgit.engine.Command;
import oakgit.engine.CommandResult;
import oakgit.engine.model.ContainerEntry;

import java.util.Collections;
import java.util.List;

/**
 * This class offers a {@link Command} to select an entry by a list of ids
 */
@RequiredArgsConstructor
@Getter
@ToString
public class SelectFromContainerByMultipleIdsCommand implements Command {

    @NonNull
    private final String containerName;

    @NonNull
    private final List<String> ids;

    @NonNull
    @Setter
    private List<String> resultFieldList = Collections.emptyList();

    public <T extends ContainerEntry<T>> CommandResult buildResult(List<T> foundEntries) {
        return new MultipleEntriesResult<T>(containerName, foundEntries);
    }

}
