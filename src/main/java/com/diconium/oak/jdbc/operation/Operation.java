package com.diconium.oak.jdbc.operation;

import com.diconium.oak.jdbc.JaggitResultSet;

import java.util.Optional;

public interface Operation {

    Optional<String[]> accept(SqlCommand sqlCommand);

    Optional<JaggitResultSet> get(String ... parameter);

}
