package oakgit.engine.store;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import oakgit.UnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.io.TempDir;

class FileEntryStoreTest {

  @Nested
  @DisplayName("in-memory behavior")
  class InMemoryBehavior {

    @UnitTest
    @DisplayName("createContainer makes container available")
    void createContainerMakesAvailable(@TempDir Path dir) throws Exception {
      try (var store = new FileEntryStore(dir)) {
        store.createContainer("NODES");

        assertThat(store.hasContainer("NODES")).isTrue();
      }
    }

    @UnitTest
    @DisplayName("hasContainer returns false for unknown container")
    void hasContainerReturnsFalse(@TempDir Path dir) throws Exception {
      try (var store = new FileEntryStore(dir)) {
        assertThat(store.hasContainer("UNKNOWN")).isFalse();
      }
    }

    @UnitTest
    @DisplayName("put and get round-trip")
    void putAndGet(@TempDir Path dir) throws Exception {
      try (var store = new FileEntryStore(dir)) {
        store.createContainer("NODES");
        var doc = new StorageDocument();
        doc.setId("1:/content");
        doc.setModified(42L);

        store.put("NODES", doc);

        var result = store.get("NODES", "1:/content");
        assertThat(result).isPresent();
        assertThat(result.get().getModified()).isEqualTo(42L);
      }
    }

    @UnitTest
    @DisplayName("get returns empty for missing document")
    void getReturnsMissing(@TempDir Path dir) throws Exception {
      try (var store = new FileEntryStore(dir)) {
        store.createContainer("NODES");

        assertThat(store.get("NODES", "nonexistent")).isEmpty();
      }
    }

    @UnitTest
    @DisplayName("get returns empty for missing container")
    void getFromMissingContainer(@TempDir Path dir) throws Exception {
      try (var store = new FileEntryStore(dir)) {
        assertThat(store.get("MISSING", "id")).isEmpty();
      }
    }

    @UnitTest
    @DisplayName("containsKey returns true for existing doc")
    void containsKeyTrue(@TempDir Path dir) throws Exception {
      try (var store = new FileEntryStore(dir)) {
        store.createContainer("NODES");
        var doc = new StorageDocument();
        doc.setId("test-id");
        store.put("NODES", doc);

        assertThat(store.containsKey("NODES", "test-id")).isTrue();
      }
    }

    @UnitTest
    @DisplayName("containsKey returns false for missing doc")
    void containsKeyFalse(@TempDir Path dir) throws Exception {
      try (var store = new FileEntryStore(dir)) {
        store.createContainer("NODES");

        assertThat(store.containsKey("NODES", "nope")).isFalse();
      }
    }

    @UnitTest
    @DisplayName("remove returns true and removes document")
    void removeExisting(@TempDir Path dir) throws Exception {
      try (var store = new FileEntryStore(dir)) {
        store.createContainer("NODES");
        var doc = new StorageDocument();
        doc.setId("doomed");
        store.put("NODES", doc);

        assertThat(store.remove("NODES", "doomed")).isTrue();
        assertThat(store.get("NODES", "doomed")).isEmpty();
      }
    }

    @UnitTest
    @DisplayName("remove returns false for missing document")
    void removeMissing(@TempDir Path dir) throws Exception {
      try (var store = new FileEntryStore(dir)) {
        store.createContainer("NODES");

        assertThat(store.remove("NODES", "ghost")).isFalse();
      }
    }

    @UnitTest
    @DisplayName("removeAll removes multiple documents")
    void removeAllMultiple(@TempDir Path dir) throws Exception {
      try (var store = new FileEntryStore(dir)) {
        store.createContainer("NODES");
        for (var id : java.util.List.of("a", "b", "c")) {
          var doc = new StorageDocument();
          doc.setId(id);
          store.put("NODES", doc);
        }

        store.removeAll("NODES", java.util.List.of("a", "c"));

        assertThat(store.get("NODES", "a")).isEmpty();
        assertThat(store.get("NODES", "b")).isPresent();
        assertThat(store.get("NODES", "c")).isEmpty();
      }
    }

    @UnitTest
    @DisplayName("getAll returns all documents in container")
    void getAllReturnsAll(@TempDir Path dir) throws Exception {
      try (var store = new FileEntryStore(dir)) {
        store.createContainer("NODES");
        for (var id : java.util.List.of("x", "y")) {
          var doc = new StorageDocument();
          doc.setId(id);
          store.put("NODES", doc);
        }

        var all = store.getAll("NODES");

        assertThat(all).hasSize(2);
        assertThat(all).extracting(StorageDocument::getId).containsExactlyInAnyOrder("x", "y");
      }
    }

    @UnitTest
    @DisplayName("getAll returns empty list for missing container")
    void getAllEmptyContainer(@TempDir Path dir) throws Exception {
      try (var store = new FileEntryStore(dir)) {
        assertThat(store.getAll("MISSING")).isEmpty();
      }
    }
  }

  @Nested
  @DisplayName("async disk persistence")
  class DiskPersistence {

    @UnitTest
    @DisplayName("put writes file to disk after close")
    void putWritesFile(@TempDir Path dir) throws Exception {
      try (var store = new FileEntryStore(dir)) {
        store.createContainer("NODES");
        var doc = new StorageDocument();
        doc.setId("1:/content");
        doc.setModified(99L);
        store.put("NODES", doc);
      }

      var file = dir.resolve("NODES/1_/content/_data.json");
      assertThat(file).exists();
      var restored = StorageDocumentCodec.fromJson(Files.readString(file));
      assertThat(restored.getId()).isEqualTo("1:/content");
      assertThat(restored.getModified()).isEqualTo(99L);
    }

    @UnitTest
    @DisplayName("remove deletes file from disk after close")
    void removeDeletesFile(@TempDir Path dir) throws Exception {
      try (var store = new FileEntryStore(dir)) {
        store.createContainer("NODES");
        var doc = new StorageDocument();
        doc.setId("victim");
        store.put("NODES", doc);
      }

      assertThat(dir.resolve("NODES/victim/_data.json")).exists();

      try (var store = new FileEntryStore(dir)) {
        store.remove("NODES", "victim");
      }

      assertThat(dir.resolve("NODES/victim/_data.json")).doesNotExist();
    }

    @UnitTest
    @DisplayName("createContainer creates directory on disk")
    void createContainerCreatesDir(@TempDir Path dir) throws Exception {
      try (var store = new FileEntryStore(dir)) {
        store.createContainer("DATASTORE_DATA");
      }

      assertThat(dir.resolve("DATASTORE_DATA")).isDirectory();
    }

    @UnitTest
    @DisplayName("id-to-path encoding replaces colons with underscores")
    void idToPathColonEncoding(@TempDir Path dir) throws Exception {
      try (var store = new FileEntryStore(dir)) {
        store.createContainer("NODES");
        var doc = new StorageDocument();
        doc.setId("0:/");
        store.put("NODES", doc);
      }

      assertThat(dir.resolve("NODES/0_/_data.json")).exists();
    }

    @UnitTest
    @DisplayName("deeply nested id creates correct directory structure")
    void deeplyNestedId(@TempDir Path dir) throws Exception {
      try (var store = new FileEntryStore(dir)) {
        store.createContainer("NODES");
        var doc = new StorageDocument();
        doc.setId("1:/content/dam/image.png/jcr:content");
        store.put("NODES", doc);
      }

      assertThat(dir.resolve("NODES/1_/content/dam/image.png/jcr_content/_data.json")).exists();
    }

    @UnitTest
    @DisplayName("simple id without slashes or colons")
    void simpleId(@TempDir Path dir) throws Exception {
      try (var store = new FileEntryStore(dir)) {
        store.createContainer("DATASTORE_DATA");
        var doc = new StorageDocument();
        doc.setId("abc123hash");
        store.put("DATASTORE_DATA", doc);
      }

      assertThat(dir.resolve("DATASTORE_DATA/abc123hash/_data.json")).exists();
    }

    @UnitTest
    @DisplayName("deep copy prevents mutation after enqueue")
    void deepCopyPreventsRace(@TempDir Path dir) throws Exception {
      try (var store = new FileEntryStore(dir)) {
        store.createContainer("NODES");
        var doc = new StorageDocument();
        doc.setId("mutable");
        doc.setLastmod(100L);
        store.put("NODES", doc);

        // mutate the in-memory doc after put
        doc.setLastmod(999L);
      }

      var file = dir.resolve("NODES/mutable/_data.json");
      var restored = StorageDocumentCodec.fromJson(Files.readString(file));
      assertThat(restored.getLastmod()).isEqualTo(100L);
    }
  }

  @Nested
  @DisplayName("startup loading")
  class StartupLoading {

    @UnitTest
    @DisplayName("loads documents from pre-populated directory")
    void loadsPrePopulated(@TempDir Path dir) throws Exception {
      // pre-populate disk
      var nodesDir = dir.resolve("NODES/1_/content");
      Files.createDirectories(nodesDir);
      var doc = new StorageDocument();
      doc.setId("1:/content");
      doc.setModified(77L);
      Files.writeString(nodesDir.resolve("_data.json"), StorageDocumentCodec.toJson(doc));

      try (var store = new FileEntryStore(dir)) {
        assertThat(store.hasContainer("NODES")).isTrue();
        var loaded = store.get("NODES", "1:/content");
        assertThat(loaded).isPresent();
        assertThat(loaded.get().getModified()).isEqualTo(77L);
      }
    }

    @UnitTest
    @DisplayName("loads id from JSON content, not from path")
    void loadsIdFromJson(@TempDir Path dir) throws Exception {
      var containerDir = dir.resolve("SETTINGS/versionGC");
      Files.createDirectories(containerDir);
      var doc = new StorageDocument();
      doc.setId("versionGC");
      doc.setVersion(3);
      Files.writeString(containerDir.resolve("_data.json"), StorageDocumentCodec.toJson(doc));

      try (var store = new FileEntryStore(dir)) {
        var loaded = store.get("SETTINGS", "versionGC");
        assertThat(loaded).isPresent();
        assertThat(loaded.get().getVersion()).isEqualTo(3);
      }
    }

    @UnitTest
    @DisplayName("corrupted file is skipped without failing startup")
    void corruptedFileSkipped(@TempDir Path dir) throws Exception {
      var nodesDir = dir.resolve("NODES/good");
      Files.createDirectories(nodesDir);
      var goodDoc = new StorageDocument();
      goodDoc.setId("good");
      Files.writeString(nodesDir.resolve("_data.json"), StorageDocumentCodec.toJson(goodDoc));

      var badDir = dir.resolve("NODES/bad");
      Files.createDirectories(badDir);
      Files.writeString(badDir.resolve("_data.json"), "not valid json at all");

      try (var store = new FileEntryStore(dir)) {
        assertThat(store.get("NODES", "good")).isPresent();
        assertThat(store.get("NODES", "bad")).isEmpty();
      }
    }

    @UnitTest
    @DisplayName("empty base directory creates no containers")
    void emptyBaseDir(@TempDir Path dir) throws Exception {
      try (var store = new FileEntryStore(dir)) {
        assertThat(store.hasContainer("NODES")).isFalse();
      }
    }

    @UnitTest
    @DisplayName("multiple containers loaded correctly")
    void multipleContainers(@TempDir Path dir) throws Exception {
      for (var container : java.util.List.of("NODES", "SETTINGS", "DATASTORE_DATA")) {
        var idDir = dir.resolve(container + "/item");
        Files.createDirectories(idDir);
        var doc = new StorageDocument();
        doc.setId("item");
        Files.writeString(idDir.resolve("_data.json"), StorageDocumentCodec.toJson(doc));
      }

      try (var store = new FileEntryStore(dir)) {
        assertThat(store.hasContainer("NODES")).isTrue();
        assertThat(store.hasContainer("SETTINGS")).isTrue();
        assertThat(store.hasContainer("DATASTORE_DATA")).isTrue();
        assertThat(store.get("NODES", "item")).isPresent();
        assertThat(store.get("SETTINGS", "item")).isPresent();
        assertThat(store.get("DATASTORE_DATA", "item")).isPresent();
      }
    }
  }

  @Nested
  @DisplayName("shutdown and flush")
  class ShutdownFlush {

    @UnitTest
    @DisplayName("all pending writes flushed on close")
    void allWritesFlushed(@TempDir Path dir) throws Exception {
      try (var store = new FileEntryStore(dir)) {
        store.createContainer("NODES");
        for (var i = 0; i < 10; i++) {
          var doc = new StorageDocument();
          doc.setId("doc-" + i);
          doc.setVersion(i);
          store.put("NODES", doc);
        }
      }

      for (var i = 0; i < 10; i++) {
        assertThat(dir.resolve("NODES/doc-" + i + "/_data.json")).exists();
      }
    }

    @UnitTest
    @DisplayName("data survives close and reopen")
    void dataSurvivesReopen(@TempDir Path dir) throws Exception {
      try (var store = new FileEntryStore(dir)) {
        store.createContainer("NODES");
        var doc = new StorageDocument();
        doc.setId("persistent");
        doc.setModCount(7L);
        doc.setData(new byte[] {1, 2, 3});
        store.put("NODES", doc);
      }

      try (var store = new FileEntryStore(dir)) {
        var loaded = store.get("NODES", "persistent");
        assertThat(loaded).isPresent();
        assertThat(loaded.get().getModCount()).isEqualTo(7L);
        assertThat(loaded.get().getData()).isEqualTo(new byte[] {1, 2, 3});
      }
    }
  }

  @Nested
  @DisplayName("remove prunes empty directories")
  class RemovePrune {

    @UnitTest
    @DisplayName("removing last document in nested path prunes empty parents")
    void prunesEmptyParents(@TempDir Path dir) throws Exception {
      try (var store = new FileEntryStore(dir)) {
        store.createContainer("NODES");
        var doc = new StorageDocument();
        doc.setId("1:/content/dam/deep");
        store.put("NODES", doc);
      }

      assertThat(dir.resolve("NODES/1_/content/dam/deep/_data.json")).exists();

      try (var store = new FileEntryStore(dir)) {
        store.remove("NODES", "1:/content/dam/deep");
      }

      // the deep path should be pruned, but NODES container dir should remain
      assertThat(dir.resolve("NODES/1_/content/dam/deep")).doesNotExist();
      assertThat(dir.resolve("NODES")).isDirectory();
    }
  }

  @Nested
  @DisplayName("docPath")
  class DocPath {

    @UnitTest
    @DisplayName("computes correct path for ID with colon and slashes")
    void pathWithColonAndSlashes(@TempDir Path dir) throws Exception {
      try (var store = new FileEntryStore(dir)) {
        var path = store.docPath("NODES", "1:/content/dam");
        assertThat(path).isEqualTo(dir.resolve("NODES/1_/content/dam/_data.json"));
      }
    }

    @UnitTest
    @DisplayName("computes correct path for simple ID")
    void pathSimpleId(@TempDir Path dir) throws Exception {
      try (var store = new FileEntryStore(dir)) {
        var path = store.docPath("DATASTORE_DATA", "abc123");
        assertThat(path).isEqualTo(dir.resolve("DATASTORE_DATA/abc123/_data.json"));
      }
    }
  }
}
