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
import static org.mockito.Mockito.*;

class MultipleEntriesResultTest {

  @SuppressWarnings("ConstantConditions")
  @UnitTest
  void ctorWithNullContainerNameThrowsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new MultipleEntriesResult<>(null, DocumentEntry.class, Collections.emptyList(), Collections.emptyList())
    );
  }

  @SuppressWarnings("ConstantConditions")
  @UnitTest
  void ctorWithNullTypeThrowsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new MultipleEntriesResult<DocumentEntry>("NODES", null, Collections.emptyList(), Collections.emptyList())
    );
  }

  @SuppressWarnings("ConstantConditions")
  @UnitTest
  void ctorWithNullFoundEntriesThrowsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new MultipleEntriesResult<>("NODES", DocumentEntry.class, null, Collections.emptyList())
    );
  }

  @SuppressWarnings("ConstantConditions")
  @UnitTest
  void ctorWithNullFieldListThrowsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new MultipleEntriesResult<>("NODES", DocumentEntry.class, Collections.emptyList(), null)
    );
  }
  
  @UnitTest
  void wasSuccessfullWithEmptyFoundEntriesReturnsFalse() {
    MultipleEntriesResult<DocumentEntry> testObj =
        new MultipleEntriesResult<>("NODES", DocumentEntry.class, Collections.emptyList(), Collections.emptyList());

    assertThat(testObj.wasSuccessfull(), is(false));
  }

  @UnitTest
  void wasSuccessfullWithNonEmptyFoundEntriesReturnsTrue() {
    List<DocumentEntry> foundEntries = Collections.singletonList(new DocumentEntry());
    MultipleEntriesResult<DocumentEntry> testObj =
        new MultipleEntriesResult<>("NODES", DocumentEntry.class, foundEntries, Collections.emptyList());

    assertThat(testObj.wasSuccessfull(), is(true));
  }

  @UnitTest
  void affectedCountWithEmptyFoundEntriesReturnsZero() {
    MultipleEntriesResult<DocumentEntry> testObj =
        new MultipleEntriesResult<>("NODES", DocumentEntry.class, Collections.emptyList(), Collections.emptyList());

    assertThat(testObj.affectedCount(), is(0));
  }

  @UnitTest
  void affectedCountWithNonEmptyFoundEntriesReturnsAmountOfFoundEntries() {
    List<DocumentEntry> foundEntries = List.of(new DocumentEntry(), new DocumentEntry());
    MultipleEntriesResult<DocumentEntry> testObj =
        new MultipleEntriesResult<>("NODES", DocumentEntry.class, foundEntries, Collections.emptyList());

    assertThat(testObj.affectedCount(), is(2));
  }

  @UnitTest
  void toResultSetWithEmptyFoundEntriesCallsTypeModifierButNeverGetFoundEntries() {
    DocumentEntry emptyType = spy(new DocumentEntry());
    List<String> resultFieldList = Collections.emptyList();
    MultipleEntriesResult<DocumentEntry> testObj =
        spy(new MultipleEntriesResult<>("NODES", DocumentEntry.class, Collections.emptyList(), resultFieldList));

    testObj.toResultSet(new OakGitResultSet("NODES"), emptyType);

    verify(emptyType, times(1)).getResultSetTypeModifier(eq(resultFieldList));
    verify(testObj, times(1)).wasSuccessfull();
    verify(testObj, never()).getFoundEntries();
  }

  @UnitTest
  void toResultSetWithNonEmptyFoundEntriesCallsTypeModifierAndGetFoundEntriesAndResultModifier() {
    DocumentEntry emptyType = spy(new DocumentEntry());
    List<String> resultFieldList = Collections.emptyList();
    DocumentEntry aFoundEntry = spy(new DocumentEntry().setId(UUID.randomUUID().toString()));
    List<DocumentEntry> foundEntries = Collections.singletonList(aFoundEntry);
    MultipleEntriesResult<DocumentEntry> testObj =
        spy(new MultipleEntriesResult<>("NODES", DocumentEntry.class, foundEntries, resultFieldList));

    testObj.toResultSet(new OakGitResultSet("NODES"), emptyType);

    verify(emptyType, times(1)).getResultSetTypeModifier(eq(resultFieldList));
    verify(testObj, times(1)).wasSuccessfull();
    verify(testObj, times(1)).getFoundEntries();
    verify(aFoundEntry, times(1)).getResultSetModifier(eq(resultFieldList));
  }

  @UnitTest
  void toResultSetWithNoArgumentsCallsArgumentVariantWithNewValues() {
    MultipleEntriesResult<DocumentEntry> testObj =
        spy(new MultipleEntriesResult<>("NODES", DocumentEntry.class, Collections.emptyList(), Collections.emptyList()));

    testObj.toResultSet();

    verify(testObj, times(1)).toResultSet(any(), any());
  }

}