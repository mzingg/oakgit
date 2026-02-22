package oakgit.engine.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import oakgit.UnitTest;
import oakgit.engine.model.test.RedTestEntry;

class ContainerEntryTest {

  @UnitTest
  void emptyOfWithDocumentEntryCreatesInstance() {
    DocumentEntry actual = ContainerEntry.emptyOf(DocumentEntry.class);

    assertThat(actual).isNotNull();
  }

  @UnitTest
  void emptyOfWithDatastoreDataEntryCreatesInstance() {
    DatastoreDataEntry actual = ContainerEntry.emptyOf(DatastoreDataEntry.class);

    assertThat(actual).isNotNull();
    assertThat(actual.getId()).isEmpty();
  }

  @UnitTest
  void emptyOfWithDatastoreMetaEntryCreatesInstance() {
    DatastoreMetaEntry actual = ContainerEntry.emptyOf(DatastoreMetaEntry.class);

    assertThat(actual).isNotNull();
    assertThat(actual.getId()).isEmpty();
  }

  @UnitTest
  void emptyOfWithUnknownTypeThrowsIllegalArgument() {
    assertThatThrownBy(() -> ContainerEntry.emptyOf(RedTestEntry.class))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Unknown ContainerEntry type");
  }
}
