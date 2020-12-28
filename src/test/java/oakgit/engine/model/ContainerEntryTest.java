package oakgit.engine.model;

import oakgit.engine.model.test.RedTestEntry;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class ContainerEntryTest {

    @Test
    void emptyOfWithValidClassCreatesObjectOfGivenType() {
        RedTestEntry actual = ContainerEntry.emptyOf(RedTestEntry.class);

        assertThat(actual, is(not(nullValue())));
    }

}