package oakgit.jdbc;

import oakgit.SandboxTest;
import oakgit.util.TestHelpers;
import oakgit.util.TestRepositoryCreator;
import org.apache.jackrabbit.api.JackrabbitRepository;
import org.apache.jackrabbit.oak.Oak;
import org.apache.jackrabbit.oak.api.ContentRepository;
import org.apache.jackrabbit.oak.api.ContentSession;
import org.apache.jackrabbit.oak.jcr.Jcr;
import org.apache.jackrabbit.oak.plugins.document.DocumentNodeStore;
import org.apache.jackrabbit.oak.plugins.document.rdb.RDBBlobStoreDB;
import org.apache.jackrabbit.oak.plugins.document.rdb.RDBDataSourceFactory;
import org.apache.jackrabbit.oak.plugins.document.rdb.RDBDocumentNodeStoreBuilder;
import org.apache.jackrabbit.oak.plugins.document.rdb.RDBDocumentStoreDB;
import org.apache.jackrabbit.oak.spi.security.OpenSecurityProvider;
import org.eclipse.jgit.api.Git;
import org.junit.jupiter.api.BeforeAll;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.sql.DataSource;
import java.nio.file.Path;
import java.sql.SQLException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

public class OakDatabaseDriverSandboxTest {

  @BeforeAll
  private static void configureDerby() {
    System.setProperty("derby.stream.error.field", "oakgit.util.TestHelpers.DERBY_DEV_NULL");
  }

  @SandboxTest
  void createContentRepositoryWithMySqlDriverInstantiatesJcrSession() throws Exception {
    DataSource dataSource = RDBDataSourceFactory.forJdbcUrl(
        "jdbc:mysql://localhost:15010/oak",
        "root",
        "admin",
        "com.mysql.jdbc.Driver"
    );

    DocumentNodeStore store = aNewNodeStore(dataSource, RDBDocumentStoreDB.MYSQL, RDBBlobStoreDB.MYSQL);
    Repository contentRepository = new Jcr(new Oak(store).with(new OpenSecurityProvider())).createRepository();
    Session session = contentRepository.login(new SimpleCredentials("admin", "admin".toCharArray()), Oak.DEFAULT_WORKSPACE_NAME);
    Node hello = session.getRootNode().getNode("jcr:system").addNode("hello", "nt:unstructured");
    hello.setProperty("velo", "velo");
    session.save();

    Node actual = session.getNode("/jcr:system/hello");
    assertThat(actual.getProperty("velo").getString(), is("velo"));
    assertThat(actual.getPrimaryNodeType().getName(), is("nt:unstructured"));
    store.dispose();
  }


  @SandboxTest
  void createContentRepositoryWithPostgresDriverInstantiatesJcrSession() throws Exception {
    DataSource dataSource = RDBDataSourceFactory.forJdbcUrl(
        "jdbc:postgresql://localhost:15020/oak",
        "postgres",
        "admin",
        "org.postgresql.Driver"
    );

    DocumentNodeStore store = aNewNodeStore(dataSource, RDBDocumentStoreDB.POSTGRES, RDBBlobStoreDB.POSTGRES);
    Repository contentRepository = new Jcr(new Oak(store).with(new OpenSecurityProvider())).createRepository();
    Session session = contentRepository.login(new SimpleCredentials("admin", "admin".toCharArray()), Oak.DEFAULT_WORKSPACE_NAME);
    Node hello = session.getRootNode().getNode("jcr:system").addNode("hello", "nt:unstructured");
    hello.setProperty("velo", "velo");
    session.save();

    Node actual = session.getNode("/jcr:system/hello");
    assertThat(actual.getProperty("velo").getString(), is("velo"));
    assertThat(actual.getPrimaryNodeType().getName(), is("nt:unstructured"));
    store.dispose();
  }

  @SandboxTest
  void createContentRepositoryWithDerbyDriverInstantiatesOakSession() throws Exception {
    DataSource dataSource = RDBDataSourceFactory.forJdbcUrl("jdbc:derby:memory:derby-oak-connection-test;create=true", "SA", "");

    DocumentNodeStore store = aNewNodeStore(dataSource, RDBDocumentStoreDB.DERBY, RDBBlobStoreDB.DERBY);
    ContentRepository contentRepository = new Oak(store).with(new OpenSecurityProvider()).createContentRepository();
    ContentSession session = contentRepository.login(new SimpleCredentials("admin", "admin".toCharArray()), Oak.DEFAULT_WORKSPACE_NAME);

    assertThat(session, is(instanceOf(ContentSession.class)));
    store.dispose();
  }

  @SandboxTest
  void createContentRepositoryWithOakGitDriverInstantiatesOakSession() throws Exception {
    Path gitDirectory = TestHelpers.aCleanTestDirectory("oak-connection-test");
    Git.init().setDirectory(gitDirectory.toFile()).call();
    DataSource dataSource = RDBDataSourceFactory.forJdbcUrl("jdbc:oakgit://" + gitDirectory.toAbsolutePath(), "", "");

    DocumentNodeStore store = aNewNodeStore(dataSource, RDBDocumentStoreDB.DEFAULT, RDBBlobStoreDB.DEFAULT);
    ContentRepository contentRepository = new Oak(store).with(new OpenSecurityProvider()).createContentRepository();
    ContentSession session = contentRepository.login(new SimpleCredentials("admin", "admin".toCharArray()), Oak.DEFAULT_WORKSPACE_NAME);

    assertThat(session, is(instanceOf(ContentSession.class)));
    store.dispose();
  }

  @SandboxTest
  void oakWithOakGitDriverCanInstantiateJcr() throws Exception {
    Path gitDirectory = TestHelpers.aCleanTestDirectory("oak-connection-test");
    Git.init().setDirectory(gitDirectory.toFile()).call();
    DataSource dataSource = RDBDataSourceFactory.forJdbcUrl("jdbc:oakgit://" + gitDirectory.toAbsolutePath(), "", "");

    DocumentNodeStore store = aNewNodeStore(dataSource, RDBDocumentStoreDB.DEFAULT, RDBBlobStoreDB.DEFAULT);
    Repository contentRepository = new Jcr(new Oak(store).with(new OpenSecurityProvider())).createRepository();
    Session session = contentRepository.login(new SimpleCredentials("admin", "admin".toCharArray()));
    Node hello = session.getRootNode().getNode("jcr:system").addNode("hello", "nt:unstructured");
    hello.setProperty("velo", "velo");
    session.save();

    Node actual = session.getNode("/jcr:system/hello");
    assertThat(actual.getProperty("velo").getString(), is("velo"));
    assertThat(actual.getPrimaryNodeType().getName(), is("nt:unstructured"));
    store.dispose();
  }

  @SandboxTest
  void oakWithAemInitializerCanInstantiateJcr() throws Exception {
    Path gitDirectory = TestHelpers.aCleanTestDirectory("oak-connection-test");
    Git.init().setDirectory(gitDirectory.toFile()).call();
    DataSource dataSource = RDBDataSourceFactory.forJdbcUrl("jdbc:oakgit://" + gitDirectory.toAbsolutePath(), "", "");
    DocumentNodeStore nodeStore = aNewNodeStore(dataSource, RDBDocumentStoreDB.DEFAULT, RDBBlobStoreDB.DEFAULT);
    TestRepositoryCreator testRepositoryCreator = new TestRepositoryCreator(nodeStore);

    JackrabbitRepository repository = testRepositoryCreator.create();

    assertThat(repository, is(instanceOf(Repository.class)));
    nodeStore.dispose();
  }

  private DocumentNodeStore aNewNodeStore(DataSource dataSource, RDBDocumentStoreDB ddb, RDBBlobStoreDB bdb) throws SQLException {
    return new RDBDocumentNodeStoreBuilder()
        .setRDBConnection(dataSource)
        .setLeaseFailureHandler(() -> {throw new IllegalStateException("Lease failed");})
        .setPersistentCache(null)
        .setJournalCache(null)
        .build();
  }

}
