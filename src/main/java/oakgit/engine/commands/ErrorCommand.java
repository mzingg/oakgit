package oakgit.engine.commands;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import oakgit.engine.Command;
import lombok.Getter;
import lombok.ToString;

/**
 * {@link ErrorCommand} saves an errorMessage.
 */
@RequiredArgsConstructor
@Getter
@ToString
public class ErrorCommand implements Command {

    @NonNull
    private final String errorMessage;

}
