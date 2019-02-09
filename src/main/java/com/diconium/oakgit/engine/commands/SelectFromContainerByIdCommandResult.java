package com.diconium.oakgit.engine.commands;

import com.diconium.oakgit.engine.CommandResult;
import com.diconium.oakgit.jdbc.OakGitResultSet;
import com.diconium.oakgit.engine.model.ContainerEntry;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.sql.ResultSet;

import static com.diconium.oakgit.engine.model.ContainerEntry.*;

@RequiredArgsConstructor
public class SelectFromContainerByIdCommandResult<T extends ContainerEntry<T>> implements CommandResult {

    @NonNull
    private final SelectFromContainerByIdCommand command;

    @NonNull
    @Getter
    private final ContainerEntry<T> foundEntry;

    @Override
    public ResultSet toResultSet() {
        OakGitResultSet result = new OakGitResultSet(command.getContainerName());
        foundEntry.getResultSetModifier().accept(result);
        return result;
    }

    @Override
    public boolean wasSuccessfull() {
        return isValidAndNotEmpty(foundEntry);
    }

    @Override
    public int affectedCount() {
        return isValidAndNotEmpty(foundEntry) ? 1 : 0;
    }
}
