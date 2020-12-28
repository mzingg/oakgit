package oakgit.engine.model;

import oakgit.UnitTest;
import oakgit.engine.model.test.RedTestEntry;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class ContainerEntryTest {

  @UnitTest
  void emptyOfWithValidClassCreatesObjectOfGivenType() {
    RedTestEntry actual = ContainerEntry.emptyOf(RedTestEntry.class);

    assertThat(actual, is(not(nullValue())));
  }

}