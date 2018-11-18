package com.diconium.oak.jdbc.operation;


import com.diconium.oak.jdbc.JaggitResultSet;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class OperationDetector {

    private final static List<Operation> AVAILABLE_OPERATIONS = Arrays.asList(
            new GetByIdOperation()
    );

    public Optional<JaggitResultSet> forSql(String sql) {
        SqlCommand sqlCommand = new SqlCommand(sql);
        for (Operation op : AVAILABLE_OPERATIONS) {
            Optional<String[]> parameters = op.accept(sqlCommand);
            if (parameters.isPresent()) {
                return op.get(parameters.get());
            }
        }

        return Optional.of(JaggitResultSet.EMPTY_RESULT_SET);
    }

}
