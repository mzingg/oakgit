package oakgit.engine.model.test;

import lombok.RequiredArgsConstructor;
import oakgit.engine.model.ContainerEntry;
import oakgit.jdbc.OakGitResultSet;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class BlueTestEntry implements ContainerEntry<BlueTestEntry> {

  private final String id;

  public BlueTestEntry() {
    this.id = "";
  }

  @Override
  public BlueTestEntry copy() {
    return new BlueTestEntry(id);
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public Map<String, OakGitResultSet.Column> getAvailableColumnsByName() {
    return null;
  }

  @Override
  public Optional<ColumnGetterResult> entryGetter(String fieldName) {
    return Optional.empty();
  }
}
