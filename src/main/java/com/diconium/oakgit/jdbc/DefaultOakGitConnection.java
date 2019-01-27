package com.diconium.oakgit.jdbc;

public abstract class DefaultOakGitConnection extends UnsupportedConnection {

    @Override
    public void setAutoCommit(boolean autoCommit) {
        // autoCommit defaults to false and cannot be set
    }

    @Override
    public boolean getAutoCommit() {
        return false;
    }

    @Override
    public int getTransactionIsolation() {
        return TRANSACTION_READ_COMMITTED;
    }

    @Override
    public void clearWarnings() {

    }

}
