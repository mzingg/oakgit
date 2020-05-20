package oakgit.engine.commands;

import oakgit.engine.CommandResult;
import oakgit.jdbc.OakGitResultSet;
import oakgit.engine.model.ContainerEntry;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.sql.ResultSet;
import java.util.Collections;

import static oakgit.engine.model.ContainerEntry.*;

@RequiredArgsConstructor
@ToString
public class SingleEntryResult<T extends ContainerEntry<T>> implements CommandResult {

    @NonNull
    private final String containerName;

    @NonNull
    @Getter
    private final T foundEntry;

    @Override
    public ResultSet toResultSet() {
        OakGitResultSet result = new OakGitResultSet(containerName);
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
