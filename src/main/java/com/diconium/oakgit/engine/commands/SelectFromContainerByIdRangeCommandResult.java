package com.diconium.oakgit.engine.commands;

import com.diconium.oakgit.engine.CommandResult;
import com.diconium.oakgit.jdbc.OakGitResultSet;
import com.diconium.oakgit.model.ContainerEntry;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.sql.ResultSet;
import java.util.List;

import static com.diconium.oakgit.model.ContainerEntry.isValidAndNotEmpty;

@RequiredArgsConstructor
public class SelectFromContainerByIdRangeCommandResult<T extends ContainerEntry<T>> implements CommandResult {

    @NonNull
    private final SelectFromContainerByIdRangeCommand command;

    @NonNull
    @Getter
    private final List<ContainerEntry<T>> foundEntries;

    @Override
    public ResultSet toResultSet() {
        OakGitResultSet result = new OakGitResultSet(command.getContainerName());
        foundEntries.stream()
                .filter(ContainerEntry::isValidAndNotEmpty)
                .forEach(e -> e.getResultSetModifier().accept(result));
        return result;
    }

    @Override
    public boolean wasSuccessfull() {
        return affectedCount() > 0;
    }

    @Override
    public int affectedCount() {
        return foundEntries.stream()
                .mapToInt(e -> isValidAndNotEmpty(e) ? 1 : 0)
                .sum();
    }

}
