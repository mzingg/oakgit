package oakgit.engine.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import oakgit.UnitTest;

class PlaceholderDataTest {

  @UnitTest
  void getInteger_withLongValue_convertsToInteger() {
    var data = new PlaceholderData().set(1, 10L);

    assertThat(data.getInteger(1)).isEqualTo(10);
  }

  @UnitTest
  void getLong_withIntegerValue_convertsToLong() {
    var data = new PlaceholderData().set(1, 5);

    assertThat(data.getLong(1)).isEqualTo(5L);
  }

  @UnitTest
  void getInteger_withStringValue_throwsIllegalArgumentException() {
    var data = new PlaceholderData().set(1, "not a number");

    assertThatThrownBy(() -> data.getInteger(1))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("is not of type");
  }

  @UnitTest
  void get_withNullValue_returnsNull() {
    var data = new PlaceholderData().set(1, null);

    assertThat(data.get(1)).isNull();
  }
}
