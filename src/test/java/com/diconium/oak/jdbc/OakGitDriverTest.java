package com.diconium.oak.jdbc;

import org.eclipse.jgit.api.Git;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

import static com.diconium.oak.jdbc.TestHelpers.getTestDirectory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OakGitDriverTest {

    @AfterEach
    void cleanupDrivers() throws Exception {
        for (Enumeration<Driver> drivers = DriverManager.getDrivers(); drivers.hasMoreElements(); ) {
            Driver driver = drivers.nextElement();
            if (driver instanceof OakGitDriver) {
                DriverManager.deregisterDriver(driver);
            }
        }
    }

    @Test
    void driverInitialisationWithValidPathReturnsConnectionConfiguredForTheGivenPath() throws Exception {
        Path gitDirectory = TestHelpers.getTestDirectory("driver-init-test");
        String absoluteDirectoryPath = gitDirectory.toAbsolutePath().toString();
        Git.init().setDirectory(gitDirectory.toFile()).call();

        DriverManager.registerDriver(new OakGitDriver());
        Connection connection = DriverManager.getConnection("jdbc:oakgit://" + absoluteDirectoryPath);

        assertTrue(connection instanceof OakGitConnection);
        assertEquals(absoluteDirectoryPath, ((OakGitConnection) connection).getGit().getRepository().getWorkTree().getAbsolutePath());
    }

}
