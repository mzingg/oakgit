package oakgit.jdbc;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.sql.SQLException;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.sql.DataSource;
import oakgit.SandboxTest;
import oakgit.util.TestHelpers;
import oakgit.util.TestRepositoryCreator;
import org.apache.jackrabbit.api.JackrabbitRepository;
import org.apache.jackrabbit.oak.Oak;
import org.apache.jackrabbit.oak.jcr.Jcr;
import org.apache.jackrabbit.oak.plugins.document.DocumentNodeStore;
import org.apache.jackrabbit.oak.plugins.document.rdb.RDBBlobStoreDB;
import org.apache.jackrabbit.oak.plugins.document.rdb.RDBDataSourceFactory;
import org.apache.jackrabbit.oak.plugins.document.rdb.RDBDocumentNodeStoreBuilder;
import org.apache.jackrabbit.oak.plugins.document.rdb.RDBDocumentStoreDB;
import org.apache.jackrabbit.oak.spi.security.OpenSecurityProvider;
import org.eclipse.jgit.api.Git;
import org.junit.jupiter.api.Nested;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

public class OakDatabaseDriverSandboxTest {

  @Nested
  @Testcontainers
  class MySqlTests {

    @Container
    private final MySQLContainer<?> mysql =
        new MySQLContainer<>("mysql:8.0").withDatabaseName("oak");

    @SandboxTest
    void canSaveAndReadJcrProperties() throws Exception {
      DataSource dataSource =
          RDBDataSourceFactory.forJdbcUrl(
              mysql.getJdbcUrl(), mysql.getUsername(), mysql.getPassword());

      DocumentNodeStore store =
          aNewNodeStore(dataSource, RDBDocumentStoreDB.MYSQL, RDBBlobStoreDB.MYSQL);
      Repository contentRepository =
          new Jcr(new Oak(store).with(new OpenSecurityProvider())).createRepository();
      Session session =
          contentRepository.login(
              new SimpleCredentials("admin", "admin".toCharArray()), Oak.DEFAULT_WORKSPACE_NAME);
      Node hello = session.getRootNode().getNode("jcr:system").addNode("hello", "nt:unstructured");
      hello.setProperty("velo", "velo");
      session.save();

      Node actual = session.getNode("/jcr:system/hello");
      assertThat(actual.getProperty("velo").getString()).isEqualTo("velo");
      assertThat(actual.getPrimaryNodeType().getName()).isEqualTo("nt:unstructured");
      store.dispose();
    }
  }

  @Nested
  @Testcontainers
  class PostgresTests {

    @Container
    private final PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:16").withDatabaseName("oak");

    @SandboxTest
    void canSaveAndReadJcrProperties() throws Exception {
      DataSource dataSource =
          RDBDataSourceFactory.forJdbcUrl(
              postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());

      DocumentNodeStore store =
          aNewNodeStore(dataSource, RDBDocumentStoreDB.POSTGRES, RDBBlobStoreDB.POSTGRES);
      Repository contentRepository =
          new Jcr(new Oak(store).with(new OpenSecurityProvider())).createRepository();
      Session session =
          contentRepository.login(
              new SimpleCredentials("admin", "admin".toCharArray()), Oak.DEFAULT_WORKSPACE_NAME);
      Node hello = session.getRootNode().getNode("jcr:system").addNode("hello", "nt:unstructured");
      hello.setProperty("velo", "velo");
      session.save();

      Node actual = session.getNode("/jcr:system/hello");
      assertThat(actual.getProperty("velo").getString()).isEqualTo("velo");
      assertThat(actual.getPrimaryNodeType().getName()).isEqualTo("nt:unstructured");
      store.dispose();
    }
  }

  @Nested
  class DerbyTests {

    @SandboxTest
    void canSaveAndReadJcrProperties() throws Exception {
      long t0 = System.nanoTime();
      System.setProperty("derby.stream.error.field", "oakgit.util.TestHelpers.DERBY_DEV_NULL");
      DataSource dataSource =
          RDBDataSourceFactory.forJdbcUrl(
              "jdbc:derby:memory:derby-oak-connection-test;create=true", "SA", "");

      long t1 = System.nanoTime();
      DocumentNodeStore store =
          aNewNodeStore(dataSource, RDBDocumentStoreDB.DERBY, RDBBlobStoreDB.DERBY);
      long t2 = System.nanoTime();
      Repository contentRepository =
          new Jcr(new Oak(store).with(new OpenSecurityProvider())).createRepository();
      Session session =
          contentRepository.login(
              new SimpleCredentials("admin", "admin".toCharArray()), Oak.DEFAULT_WORKSPACE_NAME);
      long t3 = System.nanoTime();
      Node hello = session.getRootNode().getNode("jcr:system").addNode("hello", "nt:unstructured");
      hello.setProperty("velo", "velo");
      session.save();
      long t4 = System.nanoTime();

      Node actual = session.getNode("/jcr:system/hello");
      assertThat(actual.getProperty("velo").getString()).isEqualTo("velo");
      assertThat(actual.getPrimaryNodeType().getName()).isEqualTo("nt:unstructured");
      long t5 = System.nanoTime();
      store.dispose();
      long t6 = System.nanoTime();
      System.err.printf(
          "[TIMING] DerbyTests | storeInit=%dms repo+login=%dms save=%dms read=%dms dispose=%dms"
              + " total=%dms%n",
          (t2 - t1) / 1_000_000,
          (t3 - t2) / 1_000_000,
          (t4 - t3) / 1_000_000,
          (t5 - t4) / 1_000_000,
          (t6 - t5) / 1_000_000,
          (t6 - t0) / 1_000_000);
    }
  }

  @Nested
  class OakGitTests {

    @SandboxTest
    void canSaveAndReadJcrProperties() throws Exception {
      long t0 = System.nanoTime();
      Path gitDirectory = TestHelpers.aCleanTestDirectory("oak-connection-test");
      Git.init().setDirectory(gitDirectory.toFile()).call();
      String jdbcUrl = "jdbc:oakgit://" + gitDirectory.toAbsolutePath();
      OakGitDriver.resetProcessor(jdbcUrl);
      DataSource dataSource = RDBDataSourceFactory.forJdbcUrl(jdbcUrl, "", "");

      long t1 = System.nanoTime();
      DocumentNodeStore store =
          aNewNodeStore(dataSource, RDBDocumentStoreDB.DEFAULT, RDBBlobStoreDB.DEFAULT);
      long t2 = System.nanoTime();
      Repository contentRepository =
          new Jcr(new Oak(store).with(new OpenSecurityProvider())).createRepository();
      Session session =
          contentRepository.login(new SimpleCredentials("admin", "admin".toCharArray()));
      long t3 = System.nanoTime();
      Node hello = session.getRootNode().getNode("jcr:system").addNode("hello", "nt:unstructured");
      hello.setProperty("velo", "velo");
      session.save();
      long t4 = System.nanoTime();

      Node actual = session.getNode("/jcr:system/hello");
      assertThat(actual.getProperty("velo").getString()).isEqualTo("velo");
      assertThat(actual.getPrimaryNodeType().getName()).isEqualTo("nt:unstructured");
      long t5 = System.nanoTime();
      store.dispose();
      long t6 = System.nanoTime();
      System.err.printf(
          "[TIMING] OakGitTests | storeInit=%dms repo+login=%dms save=%dms read=%dms dispose=%dms"
              + " total=%dms%n",
          (t2 - t1) / 1_000_000,
          (t3 - t2) / 1_000_000,
          (t4 - t3) / 1_000_000,
          (t5 - t4) / 1_000_000,
          (t6 - t5) / 1_000_000,
          (t6 - t0) / 1_000_000);
    }

    @SandboxTest
    void canInstantiateWithAemInitializerAndSaveAndReadJcrProperties() throws Exception {
      Path gitDirectory = TestHelpers.aCleanTestDirectory("oak-connection-test");
      Git.init().setDirectory(gitDirectory.toFile()).call();
      String jdbcUrl = "jdbc:oakgit://" + gitDirectory.toAbsolutePath();
      OakGitDriver.resetProcessor(jdbcUrl);
      DataSource dataSource = RDBDataSourceFactory.forJdbcUrl(jdbcUrl, "", "");
      DocumentNodeStore nodeStore =
          aNewNodeStore(dataSource, RDBDocumentStoreDB.DEFAULT, RDBBlobStoreDB.DEFAULT);
      JackrabbitRepository repository = new TestRepositoryCreator(nodeStore).create();
      Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
      Node hello = session.getRootNode().getNode("jcr:system").addNode("hello", "nt:unstructured");
      hello.setProperty("velo", "velo");
      session.save();

      Node actual = session.getNode("/jcr:system/hello");
      assertThat(actual.getProperty("velo").getString()).isEqualTo("velo");
      assertThat(actual.getPrimaryNodeType().getName()).isEqualTo("nt:unstructured");
      nodeStore.dispose();
    }
  }

  private DocumentNodeStore aNewNodeStore(
      DataSource dataSource, RDBDocumentStoreDB ddb, RDBBlobStoreDB bdb) throws SQLException {
    return new RDBDocumentNodeStoreBuilder()
        .setRDBConnection(dataSource)
        .setLeaseFailureHandler(
            () -> {
              throw new IllegalStateException("Lease failed");
            })
        .setPersistentCache(null)
        .setJournalCache(null)
        .build();
  }
}
