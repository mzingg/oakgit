package com.diconium.oak.oak;

import lombok.AllArgsConstructor;
import org.apache.jackrabbit.oak.cache.CacheStats;
import org.apache.jackrabbit.oak.plugins.document.*;
import org.apache.jackrabbit.oak.plugins.document.cache.CacheInvalidationStats;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@AllArgsConstructor
public class GitNodeStore implements DocumentStore {

    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    private Path gitDirectory;

    @Override
    public <T extends Document> T find(Collection<T> collection, String key) throws DocumentStoreException {
        return null;
    }

    @Override
    public <T extends Document> T find(Collection<T> collection, String key, int maxCacheAge) throws DocumentStoreException {
        return null;
    }

    @Override
    public <T extends Document> List<T> query(Collection<T> collection, String fromKey, String toKey, int limit) throws DocumentStoreException {
        return null;
    }

    @Override
    public <T extends Document> List<T> query(Collection<T> collection, String fromKey, String toKey, String indexedProperty, long startValue, int limit) throws DocumentStoreException {
        return null;
    }

    @Override
    public <T extends Document> void remove(Collection<T> collection, String key) throws DocumentStoreException {

    }

    @Override
    public <T extends Document> void remove(Collection<T> collection, List<String> keys) throws DocumentStoreException {

    }

    @Override
    public <T extends Document> int remove(Collection<T> collection, Map<String, Long> toRemove) throws DocumentStoreException {
        return 0;
    }

    @Override
    public <T extends Document> int remove(Collection<T> collection, String indexedProperty, long startValue, long endValue) throws DocumentStoreException {
        return 0;
    }

    @Override
    public <T extends Document> boolean create(Collection<T> collection, List<UpdateOp> updateOps) throws IllegalArgumentException, DocumentStoreException {
        return false;
    }

    @Override
    public <T extends Document> T createOrUpdate(Collection<T> collection, UpdateOp update) throws IllegalArgumentException, DocumentStoreException {
        UpdateUtils.assertUnconditional(update);
        return this.internalCreateOrUpdate(collection, update, false);
    }

    @Override
    public <T extends Document> List<T> createOrUpdate(Collection<T> collection, List<UpdateOp> updateOps) throws DocumentStoreException {
        return null;
    }

    @Override
    public <T extends Document> T findAndUpdate(Collection<T> collection, UpdateOp update) throws DocumentStoreException {
        return null;
    }

    @Override
    public CacheInvalidationStats invalidateCache() {
        return null;
    }

    @Override
    public CacheInvalidationStats invalidateCache(Iterable<String> keys) {
        return null;
    }

    @Override
    public <T extends Document> void invalidateCache(Collection<T> collection, String key) {

    }

    @Override
    public void dispose() {

    }

    @Override
    public <T extends Document> T getIfCached(Collection<T> collection, String key) {
        return null;
    }

    @Override
    public void setReadWriteMode(String readWriteMode) {

    }

    @Override
    public Iterable<CacheStats> getCacheStats() {
        return null;
    }

    @Override
    public Map<String, String> getMetadata() {
        return null;
    }

    @Override
    public Map<String, String> getStats() {
        return null;
    }

    @Override
    public long determineServerTimeDifferenceMillis() throws UnsupportedOperationException, DocumentStoreException {
        return 0;
    }


    private <T extends Document> T internalCreateOrUpdate(Collection<T> collection, UpdateOp updateOp, boolean checkConditions) {
        Lock lock = this.rwLock.writeLock();
        lock.lock();

        T modifiedDocument = null;
        try {
            T existingDocument = readDocument(collection, updateOp.getId());
            T newDocument = collection.newDocument(this);
            if (existingDocument == null) {
                if (!updateOp.isNew()) {
                    throw new DocumentStoreException("Document does not exist: " + updateOp.getId());
                }
            } else {
                existingDocument.deepCopy(newDocument);
            }

            if (checkConditions && !UpdateUtils.checkConditions(newDocument, updateOp.getConditions())) {
                return modifiedDocument;
            }

            UpdateUtils.applyChanges(newDocument, updateOp);
//            this.maintainModCount(newDocument);
            newDocument.seal();
            writeDocument(collection.toString(), updateOp.getId(), newDocument);
            modifiedDocument = existingDocument;
        } catch (IOException | GitAPIException ignored) {
            // TODO: error handling
        } finally {
            lock.unlock();
        }

        return modifiedDocument;
    }

    private <T extends Document> T writeDocument(String collectionName, String id, T doc) throws IOException, GitAPIException {
        Git git = Git.open(gitDirectory.toFile());
        Path directory = gitDirectory.resolve(collectionName).resolve("." + id);
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }
        if (doc instanceof NodeDocument) {
            Files.write(directory.resolve(".content"), ((NodeDocument) doc).asString().getBytes());
        }
        git.add().addFilepattern(".").call();
        git.commit().setMessage("wrote document with id " + id).call();

        return doc;
    }

    private <T extends Document> T readDocument(Collection<T> collection, String id) throws IOException {
        Path targetPath = gitDirectory.resolve(collection.toString()).resolve("." + id).resolve(".content");
        if (Files.exists(targetPath)) {
            T result = collection.newDocument(this);
            if (result instanceof NodeDocument) {
                result = (T) NodeDocument.fromString(this, new String(Files.readAllBytes(targetPath)));
            }
            return result;
        }

        return null;
    }

}
