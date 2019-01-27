package com.diconium.oakgit.queryparsing;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class RangeQueryId implements QueryId {

    @NonNull
    private final String leftIdValue;

    @NonNull
    private final String rightIdValue;

    @Override
    public String value() {
        throw new UnsupportedOperationException();
    }

    public String leftValue() {
        return leftIdValue;
    }

    public String rightValue() {
        return rightIdValue;
    }
}
