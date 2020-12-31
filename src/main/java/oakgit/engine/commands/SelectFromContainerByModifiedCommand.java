package oakgit.engine.commands;

import lombok.*;
import oakgit.engine.Command;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Getter
@ToString
public class SelectFromContainerByModifiedCommand implements Command {

  @NonNull
  private final String containerName;

  @NonNull
  private final Long modified;

  @NonNull
  private final int limit;

  @Setter
  private List<String> resultFieldList = Collections.emptyList();

}
