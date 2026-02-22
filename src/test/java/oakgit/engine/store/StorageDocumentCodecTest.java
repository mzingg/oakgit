package oakgit.engine.store;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import oakgit.UnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

class StorageDocumentCodecTest {

  @Nested
  @DisplayName("round-trip serialization")
  class RoundTrip {

    @UnitTest
    @DisplayName("all fields populated")
    void allFieldsPopulated() {
      var doc = new StorageDocument();
      doc.setId("1:/content/dam");
      doc.setModified(100L);
      doc.setModCount(5L);
      doc.setCModCount(3L);
      doc.setDSize(1024L);
      doc.setSdMaxRevTime(200L);
      doc.setLastmod(300L);
      doc.setHasBinary(1);
      doc.setDeletedOnce(0);
      doc.setVersion(2);
      doc.setSdType(4);
      doc.setLvl(1);
      doc.setData(new byte[] {1, 2, 3, 4, 5});
      doc.setBdata(new byte[] {10, 20, 30});

      var json = StorageDocumentCodec.toJson(doc);
      var restored = StorageDocumentCodec.fromJson(json);

      assertThat(restored.getId()).isEqualTo("1:/content/dam");
      assertThat(restored.getModified()).isEqualTo(100L);
      assertThat(restored.getModCount()).isEqualTo(5L);
      assertThat(restored.getCModCount()).isEqualTo(3L);
      assertThat(restored.getDSize()).isEqualTo(1024L);
      assertThat(restored.getSdMaxRevTime()).isEqualTo(200L);
      assertThat(restored.getLastmod()).isEqualTo(300L);
      assertThat(restored.getHasBinary()).isEqualTo(1);
      assertThat(restored.getDeletedOnce()).isEqualTo(0);
      assertThat(restored.getVersion()).isEqualTo(2);
      assertThat(restored.getSdType()).isEqualTo(4);
      assertThat(restored.getLvl()).isEqualTo(1);
      assertThat(restored.getData()).isEqualTo(new byte[] {1, 2, 3, 4, 5});
      assertThat(restored.getBdata()).isEqualTo(new byte[] {10, 20, 30});
    }

    @UnitTest
    @DisplayName("only id set, all other fields null")
    void onlyIdSet() {
      var doc = new StorageDocument();
      doc.setId("simple-id");

      var json = StorageDocumentCodec.toJson(doc);
      var restored = StorageDocumentCodec.fromJson(json);

      assertThat(restored.getId()).isEqualTo("simple-id");
      assertThat(restored.getModified()).isNull();
      assertThat(restored.getModCount()).isNull();
      assertThat(restored.getCModCount()).isNull();
      assertThat(restored.getDSize()).isNull();
      assertThat(restored.getSdMaxRevTime()).isNull();
      assertThat(restored.getLastmod()).isNull();
      assertThat(restored.getHasBinary()).isNull();
      assertThat(restored.getDeletedOnce()).isNull();
      assertThat(restored.getVersion()).isNull();
      assertThat(restored.getSdType()).isNull();
      assertThat(restored.getLvl()).isNull();
      assertThat(restored.getData()).isNull();
      assertThat(restored.getBdata()).isNull();
    }

    @UnitTest
    @DisplayName("mixed fields: some set, some null")
    void mixedFields() {
      var doc = new StorageDocument();
      doc.setId("0:/");
      doc.setModified(42L);
      doc.setVersion(1);
      doc.setData(new byte[] {0});

      var json = StorageDocumentCodec.toJson(doc);
      var restored = StorageDocumentCodec.fromJson(json);

      assertThat(restored.getId()).isEqualTo("0:/");
      assertThat(restored.getModified()).isEqualTo(42L);
      assertThat(restored.getVersion()).isEqualTo(1);
      assertThat(restored.getData()).isEqualTo(new byte[] {0});
      assertThat(restored.getModCount()).isNull();
      assertThat(restored.getBdata()).isNull();
    }

    @UnitTest
    @DisplayName("empty byte arrays")
    void emptyByteArrays() {
      var doc = new StorageDocument();
      doc.setId("test");
      doc.setData(new byte[0]);
      doc.setBdata(new byte[0]);

      var json = StorageDocumentCodec.toJson(doc);
      var restored = StorageDocumentCodec.fromJson(json);

      assertThat(restored.getData()).isEmpty();
      assertThat(restored.getBdata()).isEmpty();
    }

    @UnitTest
    @DisplayName("id with special characters requiring JSON escaping")
    void idWithSpecialCharacters() {
      var doc = new StorageDocument();
      doc.setId("1:/content/dam/\"quoted\"/path\\with\\backslashes");

      var json = StorageDocumentCodec.toJson(doc);
      var restored = StorageDocumentCodec.fromJson(json);

      assertThat(restored.getId()).isEqualTo("1:/content/dam/\"quoted\"/path\\with\\backslashes");
    }
  }

  @Nested
  @DisplayName("toJson")
  class ToJson {

    @UnitTest
    @DisplayName("omits null fields from output")
    void omitsNullFields() {
      var doc = new StorageDocument();
      doc.setId("test");
      doc.setModified(1L);

      var json = StorageDocumentCodec.toJson(doc);

      assertThat(json).isEqualTo("{\n  \"id\": \"test\",\n  \"modified\": 1\n}");
    }

    @UnitTest
    @DisplayName("encodes byte arrays as base64")
    void encodesBase64() {
      var doc = new StorageDocument();
      doc.setId("test");
      doc.setData(new byte[] {72, 101, 108, 108, 111}); // "Hello"

      var json = StorageDocumentCodec.toJson(doc);

      assertThat(json).contains("\"data\": \"SGVsbG8=\"");
    }
  }

  @Nested
  @DisplayName("fromJson")
  class FromJson {

    @UnitTest
    @DisplayName("throws on invalid JSON")
    void throwsOnInvalidJson() {
      assertThatThrownBy(() -> StorageDocumentCodec.fromJson("not json"))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Invalid JSON");
    }

    @UnitTest
    @DisplayName("throws on missing id field")
    void throwsOnMissingId() {
      assertThatThrownBy(() -> StorageDocumentCodec.fromJson("{\"modified\":1}"))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Missing required field: id");
    }

    @UnitTest
    @DisplayName("throws on empty object")
    void throwsOnEmptyObject() {
      assertThatThrownBy(() -> StorageDocumentCodec.fromJson("{}"))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Missing required field: id");
    }

    @UnitTest
    @DisplayName("ignores unknown fields")
    void ignoresUnknownFields() {
      var json = "{\"id\":\"test\",\"unknown\":\"value\",\"modified\":1}";

      var doc = StorageDocumentCodec.fromJson(json);

      assertThat(doc.getId()).isEqualTo("test");
      assertThat(doc.getModified()).isEqualTo(1L);
    }

    @UnitTest
    @DisplayName("handles whitespace in JSON")
    void handlesWhitespace() {
      var json =
          """
          {
            "id" : "test" ,
            "modified" : 42
          }\
          """;

      var doc = StorageDocumentCodec.fromJson(json);

      assertThat(doc.getId()).isEqualTo("test");
      assertThat(doc.getModified()).isEqualTo(42L);
    }
  }
}
