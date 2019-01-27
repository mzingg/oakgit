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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class SelectFromContainerByIdRangeCommandResult implements CommandResult {

    @NonNull
    private final SelectFromContainerByIdRangeCommand command;

    @NonNull
    @Getter
    private final List<ContainerEntry> foundEntries;

    @Override
    public ResultSet toResultSet() {
        List<String> idList = new ArrayList<>();
        List<Long> modifiedList = new ArrayList<>();
        List<Integer> hasBinaryList = new ArrayList<>();
        List<Integer> deletedOnceList = new ArrayList<>();
        List<Long> modcountList = new ArrayList<>();
        List<Long> cmodcountList = new ArrayList<>();
        List<Long> dsizeList = new ArrayList<>();
        List<Integer> versionList = new ArrayList<>();
        List<Integer> sdtypeList = new ArrayList<>();
        List<Long> sdmaxrevtimeList = new ArrayList<>();

        for (ContainerEntry entry : foundEntries) {
            idList.add(entry.getID());
            modifiedList.add(entry.getModified());
            hasBinaryList.add(entry.isHasBinary() ? 1 : 0);
            deletedOnceList.add(entry.isDeletedOnce() ? 1 : 0);
            modcountList.add(entry.getModcount());
            cmodcountList.add(entry.getCmodcount());
            dsizeList.add(entry.getDsize());
            versionList.add(entry.getVersion());
            sdtypeList.add(entry.getSdtype());
            sdmaxrevtimeList.add(entry.getSdmaxrevtime());
        }

        OakGitResultSet result = new OakGitResultSet(command.getContainerName());

        result.add("ID", SqlType.VARCHAR.id, 512, idList);
        result.add("MODIFIED", SqlType.BIGINT.id, 0, modifiedList);
        result.add("HASBINARY", SqlType.SMALLINT.id, 0, hasBinaryList);
        result.add("DELETEDONCE", SqlType.SMALLINT.id, 0, deletedOnceList);
        result.add("MODCOUNT", SqlType.BIGINT.id, 0, modcountList);
        result.add("CMODCOUNT", SqlType.BIGINT.id, 0, cmodcountList);
        result.add("DSIZE", SqlType.BIGINT.id, 0, dsizeList);
        result.add("VERSION", SqlType.SMALLINT.id, 0, versionList);
        result.add("SDTYPE", SqlType.SMALLINT.id, 0, sdtypeList);
        result.add("SDMAXREVTIME", SqlType.BIGINT.id, 0, sdmaxrevtimeList);
        result.add("DATA", SqlType.VARCHAR.id, 16384);
        result.add("BDATA", SqlType.BLOB.id, 1073741824);

        return result;
    }

    @Override
    public boolean wasSuccessfull() {
        return true;
    }
}
