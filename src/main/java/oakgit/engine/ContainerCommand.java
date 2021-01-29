package oakgit.engine;

import oakgit.engine.model.ContainerEntry;

public interface ContainerCommand<T extends ContainerEntry<T>> extends Command {

  String getContainerName();

  Class<T> getEntryType();

}
