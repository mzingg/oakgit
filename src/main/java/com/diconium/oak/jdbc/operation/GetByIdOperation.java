package com.diconium.oak.jdbc.operation;

import com.diconium.oak.jdbc.JaggitResultSet;

import java.sql.Types;
import java.util.Optional;
import java.util.regex.Pattern;

public class GetByIdOperation implements Operation {

    private final static Pattern SELECT_BY_ID_PATTERN = Pattern.compile("select \\* from (\\w+) where ID = '(\\d+)'");

    @Override
    public Optional<String[]> accept(SqlCommand sqlCommand) {
        return sqlCommand.matches(SELECT_BY_ID_PATTERN);
    }

    @Override
    public Optional<JaggitResultSet> get(String... parameter) {
        // dsize, modcount, data, cmodcount, modified, hasbinary, id, deletedonce, bdata
        return Optional.of(new JaggitResultSet(parameter[0])
                .add("ID", Types.VARCHAR, 512)
                .add("MODIFIED", Types.BIGINT, 0)
                .add("HASBINARY", Types.SMALLINT, 0)
                .add("DELETEDONCE", Types.SMALLINT, 0)
                .add("MODCOUNT", Types.BIGINT, 0)
                .add("CMODCOUNT", Types.BIGINT, 0)
                .add("DSIZE", Types.BIGINT, 0)
                .add("VERSION", Types.SMALLINT, 0)
                .add("SDTYPE", Types.SMALLINT, 0)
                .add("SDMAXREVTIME", Types.BIGINT, 0)
                .add("DATA", Types.VARCHAR, 16384)
                .add("BDATA", Types.BLOB, 1024 * 1024 * 1024));
    }

}
