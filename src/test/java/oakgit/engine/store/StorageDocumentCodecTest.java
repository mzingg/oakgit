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
      var doc =
          StorageDocument.builder()
              .id("1:/content/dam")
              .modified(100L)
              .modCount(5L)
              .cModCount(3L)
              .dSize(1024L)
              .sdMaxRevTime(200L)
              .lastmod(300L)
              .hasBinary(1)
              .deletedOnce(0)
              .version(2)
              .sdType(4)
              .lvl(1)
              .data(new byte[] {1, 2, 3, 4, 5})
              .bdata(new byte[] {10, 20, 30})
              .build();

      var json = StorageDocumentCodec.toJson(doc);
      var restored = StorageDocumentCodec.fromJson(json);

      assertThat(restored.id()).isEqualTo("1:/content/dam");
      assertThat(restored.modified()).isEqualTo(100L);
      assertThat(restored.modCount()).isEqualTo(5L);
      assertThat(restored.cModCount()).isEqualTo(3L);
      assertThat(restored.dSize()).isEqualTo(1024L);
      assertThat(restored.sdMaxRevTime()).isEqualTo(200L);
      assertThat(restored.lastmod()).isEqualTo(300L);
      assertThat(restored.hasBinary()).isEqualTo(1);
      assertThat(restored.deletedOnce()).isEqualTo(0);
      assertThat(restored.version()).isEqualTo(2);
      assertThat(restored.sdType()).isEqualTo(4);
      assertThat(restored.lvl()).isEqualTo(1);
      assertThat(restored.data()).isEqualTo(new byte[] {1, 2, 3, 4, 5});
      assertThat(restored.bdata()).isEqualTo(new byte[] {10, 20, 30});
    }

    @UnitTest
    @DisplayName("only id set, all other fields null")
    void onlyIdSet() {
      var doc = StorageDocument.builder().id("simple-id").build();

      var json = StorageDocumentCodec.toJson(doc);
      var restored = StorageDocumentCodec.fromJson(json);

      assertThat(restored.id()).isEqualTo("simple-id");
      assertThat(restored.modified()).isNull();
      assertThat(restored.modCount()).isNull();
      assertThat(restored.cModCount()).isNull();
      assertThat(restored.dSize()).isNull();
      assertThat(restored.sdMaxRevTime()).isNull();
      assertThat(restored.lastmod()).isNull();
      assertThat(restored.hasBinary()).isNull();
      assertThat(restored.deletedOnce()).isNull();
      assertThat(restored.version()).isNull();
      assertThat(restored.sdType()).isNull();
      assertThat(restored.lvl()).isNull();
      assertThat(restored.data()).isNull();
      assertThat(restored.bdata()).isNull();
    }

    @UnitTest
    @DisplayName("mixed fields: some set, some null")
    void mixedFields() {
      var doc =
          StorageDocument.builder().id("0:/").modified(42L).version(1).data(new byte[] {0}).build();

      var json = StorageDocumentCodec.toJson(doc);
      var restored = StorageDocumentCodec.fromJson(json);

      assertThat(restored.id()).isEqualTo("0:/");
      assertThat(restored.modified()).isEqualTo(42L);
      assertThat(restored.version()).isEqualTo(1);
      assertThat(restored.data()).isEqualTo(new byte[] {0});
      assertThat(restored.modCount()).isNull();
      assertThat(restored.bdata()).isNull();
    }

    @UnitTest
    @DisplayName("empty byte arrays")
    void emptyByteArrays() {
      var doc = StorageDocument.builder().id("test").data(new byte[0]).bdata(new byte[0]).build();

      var json = StorageDocumentCodec.toJson(doc);
      var restored = StorageDocumentCodec.fromJson(json);

      assertThat(restored.data()).isEmpty();
      assertThat(restored.bdata()).isEmpty();
    }

    @UnitTest
    @DisplayName("id with special characters requiring JSON escaping")
    void idWithSpecialCharacters() {
      var doc =
          StorageDocument.builder().id("1:/content/dam/\"quoted\"/path\\with\\backslashes").build();

      var json = StorageDocumentCodec.toJson(doc);
      var restored = StorageDocumentCodec.fromJson(json);

      assertThat(restored.id()).isEqualTo("1:/content/dam/\"quoted\"/path\\with\\backslashes");
    }
  }

  @Nested
  @DisplayName("toJson")
  class ToJson {

    @UnitTest
    @DisplayName("omits null fields from output")
    void omitsNullFields() {
      var doc = StorageDocument.builder().id("test").modified(1L).build();

      var json = StorageDocumentCodec.toJson(doc);

      assertThat(json).isEqualTo("{\n  \"id\": \"test\",\n  \"modified\": 1\n}");
    }

    @UnitTest
    @DisplayName("encodes byte arrays as base64")
    void encodesBase64() {
      var doc =
          StorageDocument.builder()
              .id("test")
              .data(new byte[] {72, 101, 108, 108, 111}) // "Hello"
              .build();

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

      assertThat(doc.id()).isEqualTo("test");
      assertThat(doc.modified()).isEqualTo(1L);
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

      assertThat(doc.id()).isEqualTo("test");
      assertThat(doc.modified()).isEqualTo(42L);
    }
  }
}
