package com.diconium.oak.jdbc;

import com.diconium.oak.TestHelpers;
import org.apache.commons.lang3.SystemUtils;
import org.apache.jackrabbit.oak.Oak;
import org.apache.jackrabbit.oak.api.ContentRepository;
import org.apache.jackrabbit.oak.api.ContentSession;
import org.apache.jackrabbit.oak.plugins.document.DocumentNodeStore;
import org.apache.jackrabbit.oak.plugins.document.rdb.*;
import org.apache.jackrabbit.oak.spi.security.OpenSecurityProvider;
import org.eclipse.jgit.api.Git;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.jcr.SimpleCredentials;
import javax.sql.DataSource;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;

import static com.diconium.oak.TestHelpers.getTestDirectory;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

public class OakDatabaseDriverSandboxTest {

    @BeforeAll
    private static void configureDerby() {
        System.setProperty("derby.stream.error.field", "com.diconium.oak.TestHelpers.DERBY_DEV_NULL");
    }

    @AfterEach
    void cleanupDrivers() throws Exception {
        for (Driver driver: Collections.list(DriverManager.getDrivers())) {
            if (driver instanceof JaggitDriver) {
                DriverManager.deregisterDriver(driver);
            }
        }
    }

    @Test
    void createContentRepositoryWithDerbyDriverInstantiatesOakSession() throws Exception {
        DataSource dataSource = RDBDataSourceFactory.forJdbcUrl("jdbc:derby:memory:derby-oak-connection-test;create=true", "SA", "");

        ContentRepository contentRepository = new Oak(aNodeStore(dataSource)).with(new OpenSecurityProvider()).createContentRepository();
        ContentSession session = contentRepository.login(new SimpleCredentials("admin", "admin".toCharArray()), Oak.DEFAULT_WORKSPACE_NAME);

        assertThat(session, is(instanceOf(ContentSession.class)));
    }

    @Test
    void createContentRepositoryWithJaggitDriverInstantiatesOakSession() throws Exception {
        Path gitDirectory = TestHelpers.getTestDirectory("oak-connection-test");
        Git.init().setDirectory(gitDirectory.toFile()).call();
        DriverManager.registerDriver(new JaggitDriver());
        DataSource dataSource = RDBDataSourceFactory.forJdbcUrl("jdbc:jaggit://" + gitDirectory.toAbsolutePath(), "", "");

        ContentRepository contentRepository = new Oak(aNodeStore(dataSource)).with(new OpenSecurityProvider()).createContentRepository();
        ContentSession session = contentRepository.login(new SimpleCredentials("admin", "admin".toCharArray()), Oak.DEFAULT_WORKSPACE_NAME);

        assertThat(session, is(instanceOf(ContentSession.class)));
    }

    private DocumentNodeStore aNodeStore(DataSource dataSource) throws SQLException {
        createDatabases(dataSource.getConnection());
        return new RDBDocumentNodeStoreBuilder().setRDBConnection(dataSource).build();
    }

    // this was taken from the initialization code of an OAK repository
    private void createDatabases(Connection connection) throws SQLException {
        connection.setAutoCommit(true);
        RDBDocumentStoreDB ddb = RDBDocumentStoreDB.DERBY;
        RDBBlobStoreDB bdb = RDBBlobStoreDB.DERBY;
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