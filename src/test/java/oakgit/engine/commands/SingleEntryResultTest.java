package oakgit.engine.commands;

import oakgit.UnitTest;
import oakgit.engine.model.DocumentEntry;
import oakgit.jdbc.OakGitResultSet;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SingleEntryResultTest {

  @SuppressWarnings("ConstantConditions")
  @UnitTest
  void ctorWithNullContainerNameThrowsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new SingleEntryResult<>(null, DocumentEntry.class, null, Collections.emptyList())
    );
  }

  @SuppressWarnings("ConstantConditions")
  @UnitTest
  void ctorWithNullTypeThrowsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new SingleEntryResult<DocumentEntry>("NODES", null, null, Collections.emptyList())
    );
  }

  @SuppressWarnings("ConstantConditions")
  @UnitTest
  void ctorWithNullFieldListThrowsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new SingleEntryResult<>("NODES", DocumentEntry.class, null, null)
    );
  }

  @UnitTest
  void wasSuccessfullWithNoFoundEntryReturnsFalse() {
    SingleEntryResult<DocumentEntry> testObj =
        new SingleEntryResult<>("NODES", DocumentEntry.class, null, Collections.emptyList());

    assertThat(testObj.wasSuccessfull(), is(false));
  }

  @UnitTest
  void wasSuccessfullWithFoundEntryReturnsTrue() {
    DocumentEntry foundEntry = new DocumentEntry().setId(UUID.randomUUID().toString());
    SingleEntryResult<DocumentEntry> testObj =
        new SingleEntryResult<>("NODES", DocumentEntry.class, foundEntry, Collections.emptyList());

    assertThat(testObj.wasSuccessfull(), is(true));
  }

  @UnitTest
  void affectedCountWithNoFoundEntryReturnsZero() {
    SingleEntryResult<DocumentEntry> testObj =
        new SingleEntryResult<>("NODES", DocumentEntry.class, null, Collections.emptyList());

    assertThat(testObj.affectedCount(), is(0));
  }

  @UnitTest
  void affectedCountWithFoundEntryReturnsOne() {
    DocumentEntry foundEntry = new DocumentEntry().setId(UUID.randomUUID().toString());
    SingleEntryResult<DocumentEntry> testObj =
        new SingleEntryResult<>("NODES", DocumentEntry.class, foundEntry, Collections.emptyList());

    assertThat(testObj.affectedCount(), is(1));
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