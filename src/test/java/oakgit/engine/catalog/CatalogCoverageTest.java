package oakgit.engine.catalog;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import oakgit.UnitTest;
import oakgit.engine.CommandFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("SQL Pattern Catalog Coverage")
class CatalogCoverageTest {

  private static final OakSqlCatalog CATALOG = OakSqlCatalog.load("1.90.0");
  private static final CommandFactory COMMAND_FACTORY = new CommandFactory();

  @Nested
  @Tag("unit")
  @DisplayName("Implemented patterns")
  class ImplementedPatterns {

    static Stream<Arguments> implementedPatterns() {
      return CATALOG.implementedPatterns().stream()
          .map(p -> Arguments.of(p.id(), p.exampleSql(), p.expectedAnalyzer()));
    }

    @ParameterizedTest(name = "[{0}] matches {2}")
    @MethodSource("implementedPatterns")
    @DisplayName("Implemented pattern is matched by CommandFactory")
    void implementedPatternIsRecognized(String patternId, String exampleSql, String analyzer) {
      var matchResult = COMMAND_FACTORY.match(exampleSql);

      assertThat(matchResult).as("Pattern '%s' with SQL: %s", patternId, exampleSql).isPresent();

      assertThat(matchResult.get().isInterested())
          .as("Pattern '%s' should be matched", patternId)
          .isTrue();
    }
  }

  @UnitTest
  @DisplayName("All catalog patterns are implemented")
  void allPatternsAreImplemented() {
    assertThat(CATALOG.unimplementedPatterns())
        .as("All catalog patterns should be implemented")
        .isEmpty();
  }

  @UnitTest
  @DisplayName("Catalog loads successfully with expected version")
  void catalogLoadsWithExpectedVersion() {
    assertThat(CATALOG.oakVersion()).isEqualTo("1.90.0");
    assertThat(CATALOG.dialect()).isEqualTo("DEFAULT");
    assertThat(CATALOG.patterns()).isNotEmpty();
  }

  @UnitTest
  @DisplayName("All implemented patterns have an expected analyzer specified")
  void allImplementedPatternsHaveAnalyzer() {
    var patternsWithoutAnalyzer =
        CATALOG.implementedPatterns().stream().filter(p -> p.expectedAnalyzer() == null).toList();

    assertThat(patternsWithoutAnalyzer)
        .as("Implemented patterns must specify an expectedAnalyzer")
        .isEmpty();
  }

  @UnitTest
  @DisplayName("Coverage summary is accurate")
  void coverageSummary() {
    long total = CATALOG.patterns().size();
    long implemented = CATALOG.implementedPatterns().size();
    long unimplemented = CATALOG.unimplementedPatterns().size();

    assertThat(implemented + unimplemented).isEqualTo(total);
    assertThat(implemented).isGreaterThan(0);
  }
}
