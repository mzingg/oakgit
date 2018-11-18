package com.diconium.oak.jdbc;

import com.diconium.oak.TestHelpers;
import org.eclipse.jgit.api.Git;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

import static com.diconium.oak.TestHelpers.getTestDirectory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JaggitDriverTest {

    @AfterEach
    void cleanupDrivers() throws Exception {
        for (Enumeration<Driver> drivers = DriverManager.getDrivers(); drivers.hasMoreElements(); ) {
            Driver driver = drivers.nextElement();
            if (driver instanceof JaggitDriver) {
                DriverManager.deregisterDriver(driver);
            }
        }
    }

    @Test
    void driverInitialisationWithValidPathReturnsConnectionConfiguredForTheGivenPath() throws Exception {
        Path gitDirectory = TestHelpers.getTestDirectory("driver-init-test");
        String absoluteDirectoryPath = gitDirectory.toAbsolutePath().toString();
        Git.init().setDirectory(gitDirectory.toFile()).call();

        DriverManager.registerDriver(new JaggitDriver());
        Connection connection = DriverManager.getConnection("jdbc:jaggit://" + absoluteDirectoryPath);

        assertTrue(connection instanceof JaggitConnection);
        assertEquals(absoluteDirectoryPath, ((JaggitConnection) connection).getGit().getRepository().getWorkTree().getAbsolutePath());
    }

}
