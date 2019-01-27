package com.diconium.oakgit.engine.commands;

import com.diconium.oakgit.engine.CommandResult;
import com.diconium.oakgit.jdbc.OakGitResultSet;
import com.diconium.oakgit.model.ContainerEntry;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.calcite.avatica.SqlType;
import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSet;
import java.util.Optional;

@RequiredArgsConstructor
public class SelectFromContainerByIdCommandResult implements CommandResult {

    @NonNull
    private final SelectFromContainerByIdCommand command;

    @NonNull
    @Getter
    private final Optional<ContainerEntry> foundEntry;

    @Override
    public ResultSet toResultSet() {
        OakGitResultSet result = new OakGitResultSet(command.getContainerName());
        result.add("ID", SqlType.VARCHAR.id, 512, foundEntry.map(ContainerEntry::getID).orElse(StringUtils.EMPTY));
        result.add("MODIFIED", SqlType.BIGINT.id, 0, foundEntry.map(ContainerEntry::getModified).orElse(0L));
        result.add("HASBINARY", SqlType.SMALLINT.id, 0, foundEntry.map(e -> e.isHasBinary() ? 1 : 0).orElse(0));
        result.add("DELETEDONCE", SqlType.SMALLINT.id, 0, foundEntry.map(e -> e.isDeletedOnce() ? 1 : 0).orElse(0));
        result.add("MODCOUNT", SqlType.BIGINT.id, 0, foundEntry.map(ContainerEntry::getModcount).orElse(0L));
        result.add("CMODCOUNT", SqlType.BIGINT.id, 0, foundEntry.map(ContainerEntry::getCmodcount).orElse(0L));
        result.add("DSIZE", SqlType.BIGINT.id, 0, foundEntry.map(ContainerEntry::getDsize).orElse(0L));
        result.add("VERSION", SqlType.SMALLINT.id, 0, foundEntry.map(ContainerEntry::getVersion).orElse( 0));
        result.add("SDTYPE", SqlType.SMALLINT.id, 0, foundEntry.map(ContainerEntry::getSdtype).orElse(0));
        result.add("SDMAXREVTIME", SqlType.BIGINT.id, 0, foundEntry.map(ContainerEntry::getSdmaxrevtime).orElse(0L));
        result.add("DATA", SqlType.VARCHAR.id, 16384);
        result.add("BDATA", SqlType.BLOB.id, 1073741824);
        return result;
    }

    @Override
    public boolean wasSuccessfull() {
        return true;
    }
}
