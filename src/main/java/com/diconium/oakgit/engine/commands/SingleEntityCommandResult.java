package com.diconium.oakgit.engine.commands;

import com.diconium.oakgit.engine.CommandResult;
import com.diconium.oakgit.jdbc.OakGitResultSet;
import com.diconium.oakgit.engine.model.ContainerEntry;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.sql.ResultSet;
import java.util.Collections;

import static com.diconium.oakgit.engine.model.ContainerEntry.*;

@RequiredArgsConstructor
@ToString
public class SingleEntityCommandResult<T extends ContainerEntry<T>> implements CommandResult {

    @NonNull
    private final SelectFromContainerByIdCommand command;

    @NonNull
    @Getter
    private final T foundEntry;

    @Override
    public ResultSet toResultSet() {
        OakGitResultSet result = new OakGitResultSet(command.getContainerName());
        foundEntry.getResultSetTypeModifier().accept(result);
        if (wasSuccessfull()) {
            foundEntry.getResultSetModifier(Collections.emptyList()).accept(result);
            result.setWasNull(false);
        }
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
