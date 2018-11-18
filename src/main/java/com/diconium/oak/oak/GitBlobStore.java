package com.diconium.oak.oak;

import lombok.AllArgsConstructor;
import org.apache.jackrabbit.oak.spi.blob.BlobOptions;
import org.apache.jackrabbit.oak.spi.blob.BlobStore;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

@AllArgsConstructor
public class GitBlobStore implements BlobStore {

    private Path gitDirectory;

    @Override
    public String writeBlob(InputStream in) throws IOException {
        return null;
    }

    @Override
    public String writeBlob(InputStream in, BlobOptions options) throws IOException {
        return null;
    }

    @Override
    public int readBlob(String blobId, long pos, byte[] buff, int off, int length) throws IOException {
        return 0;
    }

    @Override
    public long getBlobLength(String blobId) throws IOException {
        return 0;
    }

    @Override
    public InputStream getInputStream(String blobId) throws IOException {
        return null;
    }

    @Override
    public String getBlobId(String reference) {
        return null;
    }

    @Override
    public String getReference(String blobId) {
        return null;
    }

}
