package com.diconium.oakgit.queryparsing;

import lombok.*;

@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class SingleValueAndModCountId implements QueryId {

    @NonNull
    private final String idValue;

    @Getter
    @NonNull
    private final String modCount;

    @Override
    public String value() {
        return idValue;
    }

}
