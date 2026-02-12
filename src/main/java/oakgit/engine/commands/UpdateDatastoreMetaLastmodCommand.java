package oakgit.engine.commands;

import lombok.Getter;
import lombok.NonNull;
import oakgit.engine.model.DatastoreMetaEntry;

@Getter
public class UpdateDatastoreMetaLastmodCommand
    extends AbstractContainerCommand<DatastoreMetaEntry> {

  @NonNull private final String id;

  private final long lastmod;

  private final Long lastmodThreshold;

  public UpdateDatastoreMetaLastmodCommand(
      @NonNull String containerName, @NonNull String id, long lastmod, Long lastmodThreshold) {
    super(containerName);
    this.id = id;
    this.lastmod = lastmod;
    this.lastmodThreshold = lastmodThreshold;
  }
}
