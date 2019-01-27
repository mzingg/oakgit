package com.diconium.oakgit.model;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.Wither;

@Getter
@Wither
@RequiredArgsConstructor
@AllArgsConstructor
public class ContainerEntry {

    @NonNull
    @Wither(AccessLevel.NONE)
    private final String ID;

    private long modified = 0L;

    private boolean hasBinary = false;

    private boolean deletedOnce = false;

    private long modcount = 0L;

    private long cmodcount = 0L;

    private long dsize = 0L;

    private int version = 0;

    private int sdtype = 0;

    private long sdmaxrevtime = 0L;

    private byte[] data = new byte[0];

    private byte[] bdata = new byte[0];

}
