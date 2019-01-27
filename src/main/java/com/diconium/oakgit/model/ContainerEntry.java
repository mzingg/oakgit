package com.diconium.oakgit.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Wither;

@Getter
@Wither
@RequiredArgsConstructor
@AllArgsConstructor
public class ContainerEntry {

    @NonNull
    private final String ID;

    private long MODIFIED = 0L;

    private boolean HASBINARY = false;

    private boolean DELETEDONCE = false;

    private long MODCOUNT = 0L;

    private long CMODCOUNT = 0L;

    private long DSIZE = 0L;

    private short VERSION = 0;

    private short SDTYPE = 0;

    private long SDMAXREVTIME = 0L;

    private byte[] DATA = new byte[0];

    private byte[] BDATA = new byte[0];

}
