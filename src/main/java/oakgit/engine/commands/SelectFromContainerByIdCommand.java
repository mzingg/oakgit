package oakgit.engine.commands;

import lombok.*;
import oakgit.engine.Command;
import oakgit.engine.CommandResult;
import oakgit.engine.model.ContainerEntry;

import java.util.Collections;
import java.util.List;

/**
 * This class offers a {@link Command} to select an entry by id
 */
@RequiredArgsConstructor
@Getter
@ToString
public class SelectFromContainerByIdCommand implements Command {

    @NonNull
	private final String containerName;

    @NonNull
	private final String id;

	@Setter
    private List<String> resultFieldList = Collections.emptyList();

    public <T extends ContainerEntry<T>> CommandResult buildResult(@NonNull T foundEntry) {
		return new SingleEntryResult<T>(containerName, foundEntry);
	}

}
