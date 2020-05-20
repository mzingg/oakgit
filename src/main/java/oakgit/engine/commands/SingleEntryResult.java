package oakgit.engine.commands;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import oakgit.engine.CommandResult;
import oakgit.engine.model.ContainerEntry;
import oakgit.jdbc.OakGitResultSet;

import java.sql.ResultSet;
import java.util.List;

import static oakgit.engine.model.ContainerEntry.isValidAndNotEmpty;

@RequiredArgsConstructor
@Getter
@ToString
public class SingleEntryResult<T extends ContainerEntry<T>> implements CommandResult {

    @NonNull
    private final String containerName;

    @NonNull
    private final T foundEntry;

    @NonNull
    private final List<String> resultFieldList;

    @Override
    public ResultSet toResultSet() {
        OakGitResultSet result = new OakGitResultSet(containerName);
        foundEntry.getResultSetTypeModifier().accept(result);
        if (wasSuccessfull()) {
            foundEntry.getResultSetModifier(resultFieldList).accept(result);
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
