package oakgit.engine.commands;

import oakgit.engine.Command;
import oakgit.engine.CommandResult;
import oakgit.engine.model.ContainerEntry;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
public class SelectFromContainerByMultipleIdsCommand implements Command, MultipleEntitiesCommandResultProvider {

    @NonNull
    private String containerName = "";

    @NonNull
    private List<String> ids = Collections.emptyList();
    private List<String> resultFieldList = Collections.emptyList();

    public <T extends ContainerEntry<T>> CommandResult buildResult(List<T> foundEntries) {
        return new MultipleEntitiesCommandResult<T>(this, foundEntries);
    }

}
