package oakgit.engine.commands;

import oakgit.engine.CommandResult;
import oakgit.engine.model.ContainerEntry;

import java.util.List;

public interface MultipleEntitiesCommandResultProvider {

    String getContainerName();

    <T extends ContainerEntry<T>> CommandResult buildResult(List<T> foundEntries);

}
