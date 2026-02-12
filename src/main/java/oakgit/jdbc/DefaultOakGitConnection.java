package oakgit.jdbc;

public abstract class DefaultOakGitConnection extends UnsupportedConnection {

  private boolean closed;

  @Override
  public void close() {
    closed = true;
  }

  @Override
  public boolean isClosed() {
    return closed;
  }

  @Override
  public boolean isValid(int timeout) {
    return !closed;
  }

  @Override
  public boolean getAutoCommit() {
    return true;
  }

  @Override
  public void setAutoCommit(boolean autoCommit) {
    // autoCommit defaults to false and cannot be set
  }

  @Override
  public void setReadOnly(boolean readOnly) {
    // readOnly has no effect and cannot be set
  }

  @Override
  public int getTransactionIsolation() {
    return TRANSACTION_READ_COMMITTED;
  }

  @Override
  public void clearWarnings() {}
}
