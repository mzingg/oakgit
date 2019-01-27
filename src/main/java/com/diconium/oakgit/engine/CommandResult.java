package com.diconium.oakgit.engine;

import com.diconium.oakgit.jdbc.OakGitResultSet;

import java.sql.ResultSet;

public interface CommandResult {

    CommandResult NO_RESULT = new CommandResult() {
        @Override
        public ResultSet toResultSet() {
            return null;
        }

        @Override
        public boolean wasSuccessfull() {
            return false;
        }
    };

    static CommandResult emptyResult(String tableName) {
        return new CommandResult() {
            @Override
            public ResultSet toResultSet() {
                return new OakGitResultSet(tableName);
            }

            @Override
            public boolean wasSuccessfull() {
                return true;
            }
        };
    }

    ResultSet toResultSet();

    boolean wasSuccessfull();


}
