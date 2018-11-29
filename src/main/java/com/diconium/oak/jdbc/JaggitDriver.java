package com.diconium.oak.jdbc;

import com.github.zafarkhaja.semver.Version;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

public class JaggitDriver implements Driver {

    static {
        try {
            DriverManager.registerDriver(new JaggitDriver());
        } catch (SQLException e) {
            throw new RuntimeException("Can't register jaggit driver!");
        }
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        JaggitDriverConfiguration configuration = JaggitDriverConfiguration.fromUrl(url, readMavenVersion(), readMavenModel().getArtifactId());
        if (configuration != JaggitDriverConfiguration.INVALID_CONFIGURATION) {
            return new JaggitConnection(configuration);
        }

        throw new SQLException("Invalid connection url");
    }

    @Override
    public boolean acceptsURL(String url) {
        return JaggitDriverConfiguration.fromUrl(url, readMavenVersion(), readMavenModel().getArtifactId()) != JaggitDriverConfiguration.INVALID_CONFIGURATION;
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) {
        return new DriverPropertyInfo[0];
    }

    @Override
    public int getMajorVersion() {
        return readMavenVersion().getMajorVersion();
    }

    @Override
    public int getMinorVersion() {
        return readMavenVersion().getMinorVersion();
    }

    @Override
    public boolean jdbcCompliant() {
        return false;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }

    private static Version readMavenVersion() {
        return Version.valueOf(StringUtils.substringBefore(readMavenModel().getVersion(), "-"));
    }

    private static Model readMavenModel() {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = new Model();
        try {
            if ((new File("pom.xml")).exists()) {
                model = reader.read(new FileReader("pom.xml"));
            } else {
                model = reader.read(
                        new InputStreamReader(
                                JaggitDriver.class.getResourceAsStream(
                                        "/META-INF/maven/jaggit/jaggit/pom.xml"
                                )
                        )
                );
            }
        } catch (IOException | XmlPullParserException ignored) {
            // fall through to empty model
        }

        return model;
    }
}