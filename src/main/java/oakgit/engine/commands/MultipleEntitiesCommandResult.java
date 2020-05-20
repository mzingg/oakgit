package oakgit.engine.commands;

import oakgit.engine.CommandResult;
import oakgit.engine.model.ContainerEntry;
import oakgit.jdbc.OakGitResultSet;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;

import static oakgit.engine.model.ContainerEntry.isValidAndNotEmpty;

@RequiredArgsConstructor
@ToString
public class MultipleEntitiesCommandResult<T extends ContainerEntry<T>> implements CommandResult {

    @NonNull
    private final MultipleEntitiesCommandResultProvider command;

    @NonNull
    @Getter
    private final List<T> foundEntries;

    @Override
    public ResultSet toResultSet() {
        OakGitResultSet result = new OakGitResultSet(command.getContainerName());
        if (foundEntries.size() > 0) {
            foundEntries.get(0).getResultSetTypeModifier().accept(result);
        }
        if (wasSuccessfull()) {
            foundEntries.stream()
                    .filter(ContainerEntry::isValidAndNotEmpty)
                    .forEach(e -> e.getResultSetModifier(Collections.emptyList()).accept(result));
        }

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
