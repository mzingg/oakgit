package oakgit.engine.model.test;

import lombok.RequiredArgsConstructor;
import oakgit.engine.model.ContainerEntry;
import oakgit.jdbc.OakGitResultSet;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class RedTestEntry implements ContainerEntry<RedTestEntry> {

  private final String id;

  public RedTestEntry() {
    this.id = "";
  }

  @Override
  public RedTestEntry copy() {
    return new RedTestEntry(id);
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
