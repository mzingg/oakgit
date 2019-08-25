package com.diconium.oakgit.queryparsing;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class SingleValueId implements QueryId {

    public static QueryId INVALID_ID = new SingleValueId("INVALID");

    @NonNull
    private final String idValue;

    @Override
    public String value() {
        return idValue;
    }

}
