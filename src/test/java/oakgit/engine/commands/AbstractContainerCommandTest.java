package oakgit.engine.commands;

import oakgit.UnitTest;
import oakgit.engine.ContainerCommand;
import oakgit.engine.model.DatastoreDataEntry;
import oakgit.engine.model.DatastoreMetaEntry;
import oakgit.engine.model.DocumentEntry;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AbstractContainerCommandTest {

  @SuppressWarnings("ConstantConditions")
  @UnitTest
  void ctorWithNullThrowsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new AbstractContainerCommand<DocumentEntry>(null) {
        }
    );

  }

  @UnitTest
  void ctorWithClusterNodesHasDocumentEntryType() {
    ContainerCommand<DocumentEntry> target = new AbstractContainerCommand<>("CLUSTERNODES") {
    };

    assertThat(target.getEntryType(), is(DocumentEntry.class));
  }

  @UnitTest
  void ctorWithJournalHasDocumentEntryType() {
    ContainerCommand<DocumentEntry> target = new AbstractContainerCommand<>("JOURNAL") {
    };

    assertThat(target.getEntryType(), is(DocumentEntry.class));
  }

  @UnitTest
  void ctorWithNodesHasDocumentEntryType() {
    ContainerCommand<DocumentEntry> target = new AbstractContainerCommand<>("NODES") {
    };

    assertThat(target.getEntryType(), is(DocumentEntry.class));
  }

  @UnitTest
  void ctorWithSettingsHasDocumentEntryType() {
    ContainerCommand<DocumentEntry> target = new AbstractContainerCommand<>("SETTINGS") {
    };

    assertThat(target.getEntryType(), is(DocumentEntry.class));
  }

  @UnitTest
  void ctorWithDatastoreDataHasDatastoreDataType() {
    ContainerCommand<DatastoreDataEntry> target = new AbstractContainerCommand<>("DATASTORE_DATA") {
    };

    assertThat(target.getEntryType(), is(DatastoreDataEntry.class));
  }

  @UnitTest
  void ctorWithDatastorMetaHasDatastoreMetaType() {
    ContainerCommand<DatastoreMetaEntry> target = new AbstractContainerCommand<>("DATASTORE_META") {
    };

    assertThat(target.getEntryType(), is(DatastoreMetaEntry.class));
  }

  @UnitTest
  void ctorWithUnknownContainerThrowsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new AbstractContainerCommand<DocumentEntry>("ANY_OTHER_TABLE") {
        }
    );

  }

  @UnitTest
  void getContainerNameReturnsPassedContainerName() {
    ContainerCommand<DocumentEntry> target = new AbstractContainerCommand<>("NODES") {
    };

    assertThat(target.getContainerName(), is("NODES"));
  }
}