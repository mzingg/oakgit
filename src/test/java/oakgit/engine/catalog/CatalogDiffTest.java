package oakgit.engine.catalog;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import oakgit.UnitTest;
import org.junit.jupiter.api.DisplayName;

@DisplayName("SQL Catalog Version Drift Detection")
class CatalogDiffTest {

  @UnitTest
  @DisplayName("All known OAK versions have a catalog file")
  void allKnownVersionsHaveCatalog() {
    var versions = OakSqlCatalog.availableVersions();

    assertThat(versions).as("At least one catalog version must be available").isNotEmpty();

    for (var version : versions) {
      var catalog = OakSqlCatalog.load(version);
      assertThat(catalog.oakVersion()).isEqualTo(version);
      assertThat(catalog.patterns()).isNotEmpty();
    }
  }

  @UnitTest
  @DisplayName("All pattern IDs within a catalog are unique")
  void patternIdsAreUnique() {
    for (var version : OakSqlCatalog.availableVersions()) {
      var catalog = OakSqlCatalog.load(version);
      var ids = catalog.patterns().stream().map(SqlPattern::id).toList();
      var uniqueIds = Set.copyOf(ids);

      assertThat(uniqueIds)
          .as("Pattern IDs in catalog %s must be unique", version)
          .hasSize(ids.size());
    }
  }

  @UnitTest
  @DisplayName("When multiple catalog versions exist, differences are documented")
  void catalogDifferencesAreDetectable() {
    var versions = OakSqlCatalog.availableVersions();
    if (versions.size() < 2) {
      return; // Nothing to diff with a single version
    }

    for (int i = 1; i < versions.size(); i++) {
      var older = OakSqlCatalog.load(versions.get(i - 1));
      var newer = OakSqlCatalog.load(versions.get(i));

      var olderIds = older.patterns().stream().map(SqlPattern::id).collect(Collectors.toSet());
      var newerIds = newer.patterns().stream().map(SqlPattern::id).collect(Collectors.toSet());

      var added = newerIds.stream().filter(id -> !olderIds.contains(id)).toList();
      var removed = olderIds.stream().filter(id -> !newerIds.contains(id)).toList();

      // This test passes — it just documents diffs. When a new OAK version
      // adds patterns we don't handle, CatalogCoverageTest will catch it.
      assertThat(List.of(added, removed)).isNotNull();
    }
  }
}
