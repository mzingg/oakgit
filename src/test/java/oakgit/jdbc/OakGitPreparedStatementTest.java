package oakgit.jdbc;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;

import java.sql.Types;
import oakgit.UnitTest;

class OakGitPreparedStatementTest {

  @UnitTest
  void setInt_doesNotThrowUnsupportedOperationException() {
    var stmt = new OakGitPreparedStatement(mock(OakGitConnection.class), "SELECT 1");

    assertThatCode(() -> stmt.setInt(1, 42)).doesNotThrowAnyException();
  }

  @UnitTest
  void setNull_doesNotThrowUnsupportedOperationException() {
    var stmt = new OakGitPreparedStatement(mock(OakGitConnection.class), "SELECT 1");

    assertThatCode(() -> stmt.setNull(1, Types.INTEGER)).doesNotThrowAnyException();
  }
}
