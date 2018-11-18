package com.diconium.oak.jdbc;

import java.sql.Connection;

public abstract class DefaultJaggitConnection extends UnsupportedConnection {

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
        return Connection.TRANSACTION_READ_COMMITTED;
    }

    @Override
    public void clearWarnings() {

    }

}
