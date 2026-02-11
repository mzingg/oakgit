package oakgit.engine.model;

import static org.assertj.core.api.Assertions.assertThat;

import oakgit.UnitTest;
import oakgit.engine.model.test.RedTestEntry;

class ContainerEntryTest {

  @UnitTest
  void emptyOfWithValidClassCreatesObjectOfGivenType() {
    RedTestEntry actual = ContainerEntry.emptyOf(RedTestEntry.class);

    assertThat(actual).isNotNull();
  }
}
