package oakgit.engine.commands;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import oakgit.engine.Command;

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
