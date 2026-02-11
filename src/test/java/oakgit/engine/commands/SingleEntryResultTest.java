package oakgit.engine.commands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import oakgit.UnitTest;
import oakgit.engine.model.DocumentEntry;
import oakgit.jdbc.OakGitResultSet;

class SingleEntryResultTest {

  @SuppressWarnings("ConstantConditions")
  @UnitTest
  void ctorWithNullContainerNameThrowsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new SingleEntryResult<>(null, DocumentEntry.class, null, Collections.emptyList()));
  }

  @SuppressWarnings("ConstantConditions")
  @UnitTest
  void ctorWithNullTypeThrowsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new SingleEntryResult<DocumentEntry>("NODES", null, null, Collections.emptyList()));
  }

  @SuppressWarnings("ConstantConditions")
  @UnitTest
  void ctorWithNullFieldListThrowsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new SingleEntryResult<>("NODES", DocumentEntry.class, null, null));
  }

  @UnitTest
  void wasSuccessfullWithNoFoundEntryReturnsFalse() {
    SingleEntryResult<DocumentEntry> testObj =
        new SingleEntryResult<>("NODES", DocumentEntry.class, null, Collections.emptyList());

    assertThat(testObj.wasSuccessfull()).isFalse();
  }

  @UnitTest
  void wasSuccessfullWithFoundEntryReturnsTrue() {
    DocumentEntry foundEntry = new DocumentEntry().setId(UUID.randomUUID().toString());
    SingleEntryResult<DocumentEntry> testObj =
        new SingleEntryResult<>("NODES", DocumentEntry.class, foundEntry, Collections.emptyList());

    assertThat(testObj.wasSuccessfull()).isTrue();
  }

  @UnitTest
  void affectedCountWithNoFoundEntryReturnsZero() {
    SingleEntryResult<DocumentEntry> testObj =
        new SingleEntryResult<>("NODES", DocumentEntry.class, null, Collections.emptyList());

    assertThat(testObj.affectedCount()).isEqualTo(0);
  }

  @UnitTest
  void affectedCountWithFoundEntryReturnsOne() {
    DocumentEntry foundEntry = new DocumentEntry().setId(UUID.randomUUID().toString());
    SingleEntryResult<DocumentEntry> testObj =
        new SingleEntryResult<>("NODES", DocumentEntry.class, foundEntry, Collections.emptyList());

    assertThat(testObj.affectedCount()).isEqualTo(1);
  }

  @UnitTest
  void toResultSetWithEmptyFoundEntriesCallsTypeModifierButNeverGetFoundEntries() {
    DocumentEntry emptyType = spy(new DocumentEntry());
    List<String> resultFieldList = Collections.emptyList();
    SingleEntryResult<DocumentEntry> testObj =
        spy(new SingleEntryResult<>("NODES", DocumentEntry.class, null, resultFieldList));

    testObj.toResultSet(new OakGitResultSet("NODES"), emptyType);

    verify(emptyType, times(1)).getResultSetTypeModifier(eq(resultFieldList));
    verify(testObj, times(1)).wasSuccessfull();
    verify(testObj, never()).getFoundEntry();
  }

  @UnitTest
  void toResultSetWithNonEmptyFoundEntriesCallsTypeModifierAndGetFoundEntriesAndResultModifier() {
    DocumentEntry emptyType = spy(new DocumentEntry());
    List<String> resultFieldList = Collections.emptyList();
    DocumentEntry aFoundEntry = spy(new DocumentEntry().setId(UUID.randomUUID().toString()));
    SingleEntryResult<DocumentEntry> testObj =
        spy(new SingleEntryResult<>("NODES", DocumentEntry.class, aFoundEntry, resultFieldList));

    testObj.toResultSet(new OakGitResultSet("NODES"), emptyType);

    verify(emptyType, times(1)).getResultSetTypeModifier(eq(resultFieldList));
    verify(testObj, times(1)).wasSuccessfull();
    verify(testObj, times(1)).getFoundEntry();
    verify(aFoundEntry, times(1)).getResultSetModifier(eq(resultFieldList));
  }

  @UnitTest
  void toResultSetWithNoArgumentsCallsArgumentVariantWithNewValues() {
    SingleEntryResult<DocumentEntry> testObj =
        spy(new SingleEntryResult<>("NODES", DocumentEntry.class, null, Collections.emptyList()));

    testObj.toResultSet();

    verify(testObj, times(1)).toResultSet(any(), any());
  }
}
