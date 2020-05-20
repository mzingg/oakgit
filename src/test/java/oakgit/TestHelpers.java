package oakgit;

import oakgit.engine.query.QueryAnalyzer;
import oakgit.engine.query.QueryMatchResult;
import io.vavr.Tuple;
import io.vavr.Tuple3;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TestHelpers {

    public static final OutputStream DERBY_DEV_NULL = new OutputStream() {
        public void write(int b) {
        }
    };

    public static Path aCleanTestDirectory(String directoryName) throws IOException {
        Path target = aCleanTestDirectory().resolve(directoryName);
        FileUtils.deleteQuietly(target.toFile());
        return Files.createDirectories(target);
    }

    public static Path aCleanTestDirectory() throws IOException {
        Path target = Paths.get("target", "test-resources");
        return Files.createDirectories(target);
    }

    public static Tuple3<Git, Path, PersonIdent> aCleanGitEnvironment(String directoryName) throws GitAPIException, IOException {
        Path workspaceDirectory = aCleanTestDirectory(directoryName);
        Git git = Git.init().setDirectory(workspaceDirectory.toFile()).call();
        PersonIdent committer = new PersonIdent("Oak Git", "oak-git@somewhere.com");
        return Tuple.of(git, workspaceDirectory, committer);
    }

    public static Map<Integer, Object> placeholderData(Object... values) {
        Map<Integer, Object> result = new LinkedHashMap<>();

        for (int i = 1; i <= values.length; i++) {
            result.put(i, values[i - 1]);
        }

        return result;
    }

    public static <T> Matcher<Optional<T>> isEmptyOptional() {
        return new BaseMatcher<>() {
            @Override
            public boolean matches(Object o) {
                return o instanceof Optional<?> && ((Optional<?>) o).isEmpty();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("empty Optional");
            }

        };
    }

    public static void testValidQueryMatch(QueryAnalyzer analyzer, String sqlQuery) {
        QueryMatchResult actual = analyzer.matchAndCollect(sqlQuery);

        assertThat(actual, is(not(nullValue())));
        assertThat(actual.isInterested(), is(true));
        assertThat(actual.getOriginQuery(), is(sqlQuery));
        assertThat(actual.getCommandSupplier(), is(not(nullValue())));
    }
}
