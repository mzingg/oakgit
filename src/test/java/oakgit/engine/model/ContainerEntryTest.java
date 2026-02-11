package oakgit.engine.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import oakgit.UnitTest;
import oakgit.engine.model.test.RedTestEntry;

class ContainerEntryTest {

  @UnitTest
  void emptyOfWithValidClassCreatesObjectOfGivenType() {
    RedTestEntry actual = ContainerEntry.emptyOf(RedTestEntry.class);

    assertThat(actual, is(not(nullValue())));
  }
}
