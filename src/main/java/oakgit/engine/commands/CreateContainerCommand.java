package oakgit.engine.commands;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import oakgit.engine.Command;

/**
 * This class offers a {@link Command} to create a new container
 */
@RequiredArgsConstructor
@Getter
@ToString
public class CreateContainerCommand implements Command {

    @NonNull
    private final String containerName;

}
