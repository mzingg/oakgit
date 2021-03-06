package oakgit.util;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.ComponentInstance;

import java.util.Dictionary;
import java.util.Hashtable;

public class TestComponentContext implements ComponentContext {

  private final Dictionary<String, Object> properties;
  private final BundleContext bundleContext;

  public TestComponentContext() {
    this.properties = new Hashtable<>();
    this.bundleContext = new TestBundleContext();
  }

  @Override
  public Dictionary<String, Object> getProperties() {
    return properties;
  }

  @Override
  public <S> S locateService(String name) {
    return null;
  }

  @Override
  public <S> S locateService(String name, ServiceReference<S> reference) {
    return null;
  }

  @Override
  public Object[] locateServices(String name) {
    return new Object[0];
  }

  @Override
  public BundleContext getBundleContext() {
    return null;
  }

  @Override
  public Bundle getUsingBundle() {
    return null;
  }

  @Override
  public <S> ComponentInstance<S> getComponentInstance() {
    return null;
  }

  @Override
  public void enableComponent(String name) {

  }

  @Override
  public void disableComponent(String name) {

  }

  @Override
  public ServiceReference<?> getServiceReference() {
    return null;
  }
}
