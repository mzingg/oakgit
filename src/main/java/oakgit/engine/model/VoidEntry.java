package oakgit.engine.model;

import oakgit.jdbc.OakGitResultSet;

import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.UUID;

public class VoidEntry implements ContainerEntry<VoidEntry> {

  @Override
  public String getId() {
    return UUID.randomUUID().toString();
  }

  @Override
  public LinkedHashMap<String, OakGitResultSet.Column> getAvailableColumnsByName() {
    return new LinkedHashMap<>();
  }

  @Override
  public Optional<ColumnGetterResult> entryGetter(String fieldName) {
    return Optional.empty();
  }

  @Override
  public VoidEntry copy() {
    return new VoidEntry();
  }

}
