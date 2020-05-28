package oakgit.jdbc;

import oakgit.SandboxTest;
import oakgit.TestHelpers;
import org.apache.jackrabbit.oak.Oak;
import org.apache.jackrabbit.oak.api.ContentRepository;
import org.apache.jackrabbit.oak.api.ContentSession;
import org.apache.jackrabbit.oak.jcr.Jcr;
import org.apache.jackrabbit.oak.plugins.document.DocumentNodeStore;
import org.apache.jackrabbit.oak.plugins.document.rdb.*;
import org.apache.jackrabbit.oak.spi.security.OpenSecurityProvider;
import org.eclipse.jgit.api.Git;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.sql.DataSource;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

public class OakDatabaseDriverSandboxTest {

    @BeforeAll
    private static void configureDerby() {
        System.setProperty("derby.stream.error.field", "oakgit.TestHelpers.DERBY_DEV_NULL");
    }

    @AfterEach
    void cleanupDrivers() throws Exception {
        for (Driver driver : Collections.list(DriverManager.getDrivers())) {
            if (driver instanceof OakGitDriver) {
                DriverManager.deregisterDriver(driver);
            }
        }
    }

    @SandboxTest
    void createContentRepositoryWithMySqlDriverInstantiatesOakSession() throws Exception {
        DataSource dataSource = RDBDataSourceFactory.forJdbcUrl("jdbc:mysql://localhost:15010/oak", "root", "admin");

        ContentRepository contentRepository = new Oak(aNewNodeStore(dataSource, RDBDocumentStoreDB.MYSQL, RDBBlobStoreDB.MYSQL)).with(new OpenSecurityProvider()).createContentRepository();
        ContentSession session = contentRepository.login(new SimpleCredentials("admin", "admin".toCharArray()), Oak.DEFAULT_WORKSPACE_NAME);

        assertThat(session, is(instanceOf(ContentSession.class)));
    }

    @SandboxTest
    void createContentRepositoryWithPostgresDriverInstantiatesOakSession() throws Exception {
        DataSource dataSource = RDBDataSourceFactory.forJdbcUrl("jdbc:postgresql://localhost:15020/oak", "postgres", "admin");

        ContentRepository contentRepository = new Oak(aNewNodeStore(dataSource, RDBDocumentStoreDB.POSTGRES, RDBBlobStoreDB.POSTGRES)).with(new OpenSecurityProvider()).createContentRepository();
        ContentSession session = contentRepository.login(new SimpleCredentials("admin", "admin".toCharArray()), Oak.DEFAULT_WORKSPACE_NAME);

        assertThat(session, is(instanceOf(ContentSession.class)));
    }

    @SandboxTest
    void createContentRepositoryWithPostgresDriverInstantiatesJcrSession() throws Exception {
        DataSource dataSource = RDBDataSourceFactory.forJdbcUrl("jdbc:postgresql://localhost:15020/oak", "postgres", "admin");

        Repository contentRepository = new Jcr(new Oak(aNewNodeStore(dataSource, RDBDocumentStoreDB.POSTGRES, RDBBlobStoreDB.POSTGRES)).with(new OpenSecurityProvider())).createRepository();
        Session session = contentRepository.login(new SimpleCredentials("admin", "admin".toCharArray()), Oak.DEFAULT_WORKSPACE_NAME);
        Node hello = session.getRootNode().getNode("jcr:system").addNode("hello", "nt:unstructured");
        hello.setProperty("velo", "velo");
        session.save();

        assertThat(session, is(instanceOf(Session.class)));
    }

    @SandboxTest
    void createContentRepositoryWithDerbyDriverInstantiatesOakSession() throws Exception {
        DataSource dataSource = RDBDataSourceFactory.forJdbcUrl("jdbc:derby:memory:derby-oak-connection-test;create=true", "SA", "");

        ContentRepository contentRepository = new Oak(aNewNodeStore(dataSource, RDBDocumentStoreDB.DERBY, RDBBlobStoreDB.DERBY)).with(new OpenSecurityProvider()).createContentRepository();
        ContentSession session = contentRepository.login(new SimpleCredentials("admin", "admin".toCharArray()), Oak.DEFAULT_WORKSPACE_NAME);

        assertThat(session, is(instanceOf(ContentSession.class)));
    }

    @SandboxTest
    void createContentRepositoryWithOakGitDriverInstantiatesOakSession() throws Exception {
        Path gitDirectory = TestHelpers.aCleanTestDirectory("oak-connection-test");
        Git.init().setDirectory(gitDirectory.toFile()).call();
        DataSource dataSource = RDBDataSourceFactory.forJdbcUrl("jdbc:oakgit://" + gitDirectory.toAbsolutePath(), "", "");

        ContentRepository contentRepository = new Oak(aNewNodeStore(dataSource, RDBDocumentStoreDB.DEFAULT, RDBBlobStoreDB.DEFAULT)).with(new OpenSecurityProvider()).createContentRepository();
        ContentSession session = contentRepository.login(new SimpleCredentials("admin", "admin".toCharArray()), Oak.DEFAULT_WORKSPACE_NAME);

        assertThat(session, is(instanceOf(ContentSession.class)));
    }

    @SandboxTest
    void oakWithOakGitDriverCanInstantiateJcr() throws Exception {
        Path gitDirectory = TestHelpers.aCleanTestDirectory("oak-connection-test");
        Git.init().setDirectory(gitDirectory.toFile()).call();
        DataSource dataSource = RDBDataSourceFactory.forJdbcUrl("jdbc:oakgit://" + gitDirectory.toAbsolutePath(), "", "");

        Repository contentRepository = new Jcr(new Oak(aNewNodeStore(dataSource, RDBDocumentStoreDB.DEFAULT, RDBBlobStoreDB.DEFAULT)).with(new OpenSecurityProvider())).createRepository();
        Session session = contentRepository.login(new SimpleCredentials("admin", "admin".toCharArray()));
        Node hello = session.getRootNode().getNode("jcr:system").addNode("hello", "nt:unstructured");
        hello.setProperty("velo", "velo");
        session.save();

        assertThat(session, is(instanceOf(Session.class)));
    }

    private DocumentNodeStore aNewNodeStore(DataSource dataSource, RDBDocumentStoreDB ddb, RDBBlobStoreDB bdb) throws SQLException {
        try {
            createDatabases(dataSource.getConnection(), ddb, bdb);
        } catch (Exception ignored) {
            // skip
        }
        return new RDBDocumentNodeStoreBuilder().setRDBConnection(dataSource).build();
    }

    // this was taken from the initialization code of an OAK repository
    private void createDatabases(Connection connection, RDBDocumentStoreDB ddb, RDBBlobStoreDB bdb) throws SQLException {
        connection.setAutoCommit(true);
        RDBOptions defaultOpts = new RDBOptions();

        for (String table : RDBDocumentStore.getTableNames()) {
            connection.createStatement().execute(ddb.getTableCreationStatement(table, defaultOpts.getInitialSchema()));
            for (String indexCreationStm : ddb.getIndexCreationStatements(table, defaultOpts.getInitialSchema())) {
                connection.createStatement().execute(indexCreationStm);
            }
            for (int level = defaultOpts.getInitialSchema() + 1; level <= defaultOpts.getUpgradeToSchema(); level++) {
                for (String upgradeStm : ddb.getTableUpgradeStatements(table, level)) {
                    connection.createStatement().execute(upgradeStm);
                }
            }
        }
        connection.createStatement().execute(bdb.getMetaTableCreationStatement("DATASTORE_META"));
        connection.createStatement().execute(bdb.getDataTableCreationStatement("DATASTORE_DATA"));
    }
}
