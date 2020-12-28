package oakgit.processor.inmemory;

import oakgit.UnitTest;
import oakgit.engine.model.DocumentEntry;
import oakgit.engine.model.test.BlueTestEntry;
import oakgit.engine.model.test.RedTestEntry;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static oakgit.util.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInRelativeOrder;
import static org.hamcrest.Matchers.is;

@UnitTest
class InMemoryContainerTest {

    @Test
    void getNameReturnsGivenName() {
        InMemoryContainer testObj = new InMemoryContainer("testcontainer");

        assertThat(testObj.getName(), is("testcontainer"));
    }

    @Test
    void findByIdWithNonExistingEntryReturnsEmpty() {
        InMemoryContainer testObj = new InMemoryContainer("testcontainer");

        assertThat(testObj.findById("non-existing", BlueTestEntry.class), isEmpty());
    }

    @Test
    void findByIdWithExistingEntryButWrongClassReturnsEmpty() {
        BlueTestEntry expectedValue = new BlueTestEntry("test-id");
        InMemoryContainer testObj = new InMemoryContainer("testcontainer")
                .setEntry(expectedValue);

        assertThat(testObj.findById("test-id", RedTestEntry.class), isEmpty());
    }

    @Test
    void findByIdWithExistingEntryAndCorrectClassReturnsEntry() {
        BlueTestEntry expectedValue = new BlueTestEntry("test-id");
        InMemoryContainer testObj = new InMemoryContainer("testcontainer")
                .setEntry(expectedValue);

        assertThat(testObj.findById("test-id", BlueTestEntry.class), isPresentAndIs(expectedValue));
    }

    @Test
    void findByIdRangeWithReturnsUnsortedSublistBetweenBoundsBasedOnNaturalOrder() {
        InMemoryContainer testObj = new InMemoryContainer("testcontainer")
                .setEntry(new RedTestEntry("0"))
                .setEntry(new RedTestEntry("b"))
                .setEntry(new RedTestEntry("a"))
                .setEntry(new RedTestEntry("2"))
                .setEntry(new RedTestEntry("zzzz"))
                .setEntry(new RedTestEntry("aaaa"))
                .setEntry(new RedTestEntry("76"));

        assertThat(testObj.findByIdRange("0", "a", RedTestEntry.class), containsInRelativeOrder(
                entryWithId("0"), entryWithId("a"), entryWithId("2"), entryWithId("76")
        ));
    }

    @Test
    void findByIdsWithGivenIdListReturnsListInGivenIdOrder() {
        InMemoryContainer testObj = new InMemoryContainer("testcontainer")
                .setEntry(new RedTestEntry("0"))
                .setEntry(new RedTestEntry("b"))
                .setEntry(new RedTestEntry("a"))
                .setEntry(new RedTestEntry("2"))
                .setEntry(new RedTestEntry("zzzz"))
                .setEntry(new RedTestEntry("aaaa"))
                .setEntry(new RedTestEntry("76"));

        assertThat(testObj.findByIds(Arrays.asList("0", "zzzz", "2", "non-existing"), RedTestEntry.class), containsInRelativeOrder(
                entryWithId("0"), entryWithId("zzzz"), entryWithId("2")
        ));
    }

    @Test
    void findByIdAndModCountWithGivenIdAndModCountSupportWithGivenModCountReturnsEntry() {
        DocumentEntry expected = new DocumentEntry().setId("testid").setModCount(100L);
        InMemoryContainer testObj = new InMemoryContainer("testcontainer")
                .setEntry(expected);

        assertThat(testObj.findByIdAndModCount("testid", 100, DocumentEntry.class), isPresentAndIs(expected));
    }

    @Test
    void findByIdAndModCountWithGivenIdAndModCountSupportWithWrongModCountReturnsEmpty() {
        DocumentEntry expected = new DocumentEntry().setId("testid").setModCount(101L);
        InMemoryContainer testObj = new InMemoryContainer("testcontainer")
                .setEntry(expected);

        assertThat(testObj.findByIdAndModCount("testid", 100, DocumentEntry.class), isEmpty());
    }

    @Test
    void findByIdAndModCountWithGivenIdAndNoModCountSupportReturnsEntry() {
        RedTestEntry expected = new RedTestEntry("testid");
        InMemoryContainer testObj = new InMemoryContainer("testcontainer")
                .setEntry(expected);

        assertThat(testObj.findByIdAndModCount("testid", 100, RedTestEntry.class), isPresentAndIs(expected));
    }
}