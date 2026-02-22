package oakgit.engine.store;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileEntryStore implements EntryStore {

  private static final Logger LOG = LoggerFactory.getLogger(FileEntryStore.class);
  private static final String DATA_FILE = "_data.json";

  private final Map<String, Map<String, StorageDocument>> containers = new HashMap<>();
  private final Path baseDirectory;
  private final LinkedBlockingQueue<WriteTask> writeQueue = new LinkedBlockingQueue<>();
  private final Thread writerThread;

  public FileEntryStore(Path baseDirectory) {
    this.baseDirectory = baseDirectory;
    try {
      Files.createDirectories(baseDirectory);
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to create base directory: " + baseDirectory, e);
    }
    loadFromDisk();
    writerThread =
        Thread.ofPlatform().daemon().name("oakgit-file-writer").start(this::processWriteQueue);
  }

  @Override
  public void createContainer(String name) {
    containers.putIfAbsent(name, new HashMap<>());
    var containerDir = baseDirectory.resolve(name);
    try {
      Files.createDirectories(containerDir);
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to create container directory: " + containerDir, e);
    }
  }

  @Override
  public boolean hasContainer(String name) {
    return containers.containsKey(name);
  }

  @Override
  public boolean containsKey(String container, String id) {
    var entries = containers.get(container);
    return entries != null && entries.containsKey(id);
  }

  @Override
  public void put(String container, StorageDocument doc) {
    containers.get(container).put(doc.id(), doc);
    writeQueue.add(new Put(container, doc.id(), doc));
  }

  @Override
  public Optional<StorageDocument> get(String container, String id) {
    var entries = containers.get(container);
    if (entries == null) {
      return Optional.empty();
    }
    return Optional.ofNullable(entries.get(id));
  }

  @Override
  public List<StorageDocument> getAll(String container) {
    var entries = containers.get(container);
    if (entries == null) {
      return List.of();
    }
    return List.copyOf(entries.values());
  }

  @Override
  public boolean remove(String container, String id) {
    var entries = containers.get(container);
    if (entries == null) {
      return false;
    }
    var removed = entries.remove(id) != null;
    if (removed) {
      writeQueue.add(new Remove(container, id));
    }
    return removed;
  }

  @Override
  public void removeAll(String container, List<String> ids) {
    var entries = containers.get(container);
    if (entries != null) {
      ids.forEach(
          id -> {
            if (entries.remove(id) != null) {
              writeQueue.add(new Remove(container, id));
            }
          });
    }
  }

  @Override
  public void close() {
    writeQueue.add(Shutdown.INSTANCE);
    try {
      writerThread.join(5000);
      if (writerThread.isAlive()) {
        LOG.warn("Writer thread did not finish within 5s, interrupting");
        writerThread.interrupt();
        writerThread.join(1000);
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  @Override
  public void discardAndClose() {
    writeQueue.clear();
    close();
    deleteContainerContents();
  }

  private void deleteContainerContents() {
    if (!Files.isDirectory(baseDirectory)) {
      return;
    }
    try {
      Files.walkFileTree(
          baseDirectory,
          new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                throws IOException {
              if (file.getFileName().toString().equals(DATA_FILE)) {
                Files.delete(file);
              }
              return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                throws IOException {
              if (!dir.equals(baseDirectory)) {
                try (var listing = Files.list(dir)) {
                  if (listing.findAny().isEmpty()) {
                    Files.delete(dir);
                  }
                }
              }
              return FileVisitResult.CONTINUE;
            }
          });
    } catch (IOException e) {
      LOG.error("Failed to clean data files from {}", baseDirectory, e);
    }
  }

  Path docPath(String container, String id) {
    return baseDirectory.resolve(container).resolve(encodedId(id)).resolve(DATA_FILE);
  }

  private static String encodedId(String id) {
    return id.replace(':', '_');
  }

  private void loadFromDisk() {
    if (!Files.isDirectory(baseDirectory)) {
      return;
    }
    try (var containerDirs = Files.list(baseDirectory)) {
      containerDirs
          .filter(Files::isDirectory)
          .forEach(
              containerDir -> {
                var containerName = containerDir.getFileName().toString();
                var entries = new HashMap<String, StorageDocument>();
                containers.put(containerName, entries);
                loadContainer(containerDir, entries);
              });
    } catch (IOException e) {
      LOG.error("Failed to scan base directory: {}", baseDirectory, e);
    }
  }

  private void loadContainer(Path containerDir, Map<String, StorageDocument> entries) {
    try {
      Files.walkFileTree(
          containerDir,
          new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
              if (file.getFileName().toString().equals(DATA_FILE)) {
                loadDocument(file, entries);
              }
              return FileVisitResult.CONTINUE;
            }
          });
    } catch (IOException e) {
      LOG.error("Failed to walk container directory: {}", containerDir, e);
    }
  }

  private void loadDocument(Path file, Map<String, StorageDocument> entries) {
    try {
      var json = Files.readString(file);
      var doc = StorageDocumentCodec.fromJson(json);
      entries.put(doc.id(), doc);
    } catch (Exception e) {
      LOG.error("Failed to load document from {}", file, e);
    }
  }

  private void processWriteQueue() {
    while (true) {
      try {
        var task = writeQueue.take();
        switch (task) {
          case Put put -> writeToDisk(put);
          case Remove remove -> removeFromDisk(remove);
          case Shutdown ignored -> {
            return;
          }
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return;
      }
    }
  }

  private void writeToDisk(Put put) {
    var path = docPath(put.container(), put.id());
    var json = StorageDocumentCodec.toJson(put.doc());
    try {
      Files.createDirectories(path.getParent());
      Files.writeString(path, json);
    } catch (java.nio.file.NoSuchFileException e) {
      // Parent directory may have been concurrently deleted (e.g. during shutdown); retry once
      try {
        Files.createDirectories(path.getParent());
        Files.writeString(path, json);
      } catch (IOException retryEx) {
        LOG.error("Failed to write document {}/{} to {}", put.container(), put.id(), path, retryEx);
      }
    } catch (IOException e) {
      LOG.error("Failed to write document {}/{} to {}", put.container(), put.id(), path, e);
    }
  }

  private void removeFromDisk(Remove remove) {
    var path = docPath(remove.container(), remove.id());
    try {
      Files.deleteIfExists(path);
      pruneEmptyParents(path.getParent(), baseDirectory.resolve(remove.container()));
    } catch (IOException e) {
      LOG.error("Failed to remove document {}/{} at {}", remove.container(), remove.id(), path, e);
    }
  }

  private static void pruneEmptyParents(Path dir, Path stopAt) throws IOException {
    var current = dir;
    while (current != null && !current.equals(stopAt)) {
      try (var listing = Files.list(current)) {
        if (listing.findAny().isPresent()) {
          break;
        }
      }
      Files.delete(current);
      current = current.getParent();
    }
  }

  sealed interface WriteTask permits Put, Remove, Shutdown {}

  record Put(String container, String id, StorageDocument doc) implements WriteTask {}

  record Remove(String container, String id) implements WriteTask {}

  record Shutdown() implements WriteTask {
    static final Shutdown INSTANCE = new Shutdown();
  }
}
