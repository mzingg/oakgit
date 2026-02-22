package oakgit.jdbc;

import java.util.Dictionary;
import java.util.Hashtable;
import javax.sql.DataSource;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;

/**
 * OSGi activator that registers a ManagedServiceFactory for oakgit DataSource instances. Each
 * factory configuration with PID {@code oakgit.jdbc.DataSource} creates a {@link
 * javax.sql.DataSource} service with the configured JDBC URL and {@code datasource.name} property.
 *
 * <p>This bypasses DriverManager and DBCP entirely, avoiding OSGi classloader isolation issues.
 */
public class OakGitActivator implements BundleActivator {

  private static final String FACTORY_PID = "oakgit.jdbc.DataSource";
  private ServiceRegistration<ManagedServiceFactory> factoryRegistration;
  private final DataSourceFactory factory = new DataSourceFactory();

  @Override
  public void start(BundleContext context) {
    factory.context = context;
    var props = new Hashtable<String, Object>();
    props.put(Constants.SERVICE_PID, FACTORY_PID);
    factoryRegistration = context.registerService(ManagedServiceFactory.class, factory, props);
  }

  @Override
  public void stop(BundleContext context) {
    if (factoryRegistration != null) {
      factoryRegistration.unregister();
    }
    factory.destroyAll();
    OakGitDriver.closeAll();
  }

  private static class DataSourceFactory implements ManagedServiceFactory {

    BundleContext context;
    private final java.util.concurrent.ConcurrentHashMap<String, ServiceRegistration<DataSource>>
        registrations = new java.util.concurrent.ConcurrentHashMap<>();

    @Override
    public String getName() {
      return "OakGit JDBC DataSource Factory";
    }

    @Override
    public void updated(String pid, Dictionary<String, ?> properties)
        throws ConfigurationException {
      deleted(pid);

      var url = (String) properties.get("url");
      if (url == null) {
        throw new ConfigurationException("url", "JDBC URL is required");
      }

      var datasourceName = (String) properties.get("datasource.name");
      if (datasourceName == null) {
        throw new ConfigurationException("datasource.name", "datasource.name is required");
      }

      var ds = new OakGitDataSource(url);
      var svcProps = new Hashtable<String, Object>();
      svcProps.put("datasource.name", datasourceName);
      var reg = context.registerService(DataSource.class, ds, svcProps);
      registrations.put(pid, reg);
    }

    @Override
    public void deleted(String pid) {
      var reg = registrations.remove(pid);
      if (reg != null) {
        reg.unregister();
      }
    }

    void destroyAll() {
      registrations.forEach((pid, reg) -> reg.unregister());
      registrations.clear();
    }
  }
}
