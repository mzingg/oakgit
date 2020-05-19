package com.diconium.oakgit.engine.query;

import com.diconium.oakgit.engine.Command;
import lombok.Data;

import java.util.Map;
import java.util.function.Function;

@Data
public class QueryMatchResult {
    private boolean interested;
    private String originQuery;
    public Function<Map<Integer, Object>, Command> commandSupplier;
}
