package oakgit.util;

import org.osgi.framework.*;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Dictionary;

public class TestBundleContext implements BundleContext {
  @Override
  public String getProperty(String key) {
    return null;
  }

  @Override
  public Bundle getBundle() {
    return null;
  }

  @Override
  public Bundle installBundle(String location, InputStream input) throws BundleException {
    return null;
  }

  @Override
  public Bundle installBundle(String location) throws BundleException {
    return null;
  }

  @Override
  public Bundle getBundle(long id) {
    return null;
  }

  @Override
  public Bundle[] getBundles() {
    return new Bundle[0];
  }

  @Override
  public void addServiceListener(ServiceListener listener, String filter) throws InvalidSyntaxException {

  }

  @Override
  public void addServiceListener(ServiceListener listener) {

  }

  @Override
  public void removeServiceListener(ServiceListener listener) {

  }

  @Override
  public void addBundleListener(BundleListener listener) {

  }

  @Override
  public void removeBundleListener(BundleListener listener) {

  }

  @Override
  public void addFrameworkListener(FrameworkListener listener) {

  }

  @Override
  public void removeFrameworkListener(FrameworkListener listener) {

  }

  @Override
  public ServiceRegistration<?> registerService(String[] clazzes, Object service, Dictionary<String, ?> properties) {
    return null;
  }

  @Override
  public ServiceRegistration<?> registerService(String clazz, Object service, Dictionary<String, ?> properties) {
    return null;
  }

  @Override
  public <S> ServiceRegistration<S> registerService(Class<S> clazz, S service, Dictionary<String, ?> properties) {
    return null;
  }

  @Override
  public <S> ServiceRegistration<S> registerService(Class<S> clazz, ServiceFactory<S> factory, Dictionary<String, ?> properties) {
    return null;
  }

  @Override
  public ServiceReference<?>[] getServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
    return new ServiceReference[0];
  }

  @Override
  public ServiceReference<?>[] getAllServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
    return new ServiceReference[0];
  }

  @Override
  public ServiceReference<?> getServiceReference(String clazz) {
    return null;
  }

  @Override
  public <S> ServiceReference<S> getServiceReference(Class<S> clazz) {
    return null;
  }

  @Override
  public <S> Collection<ServiceReference<S>> getServiceReferences(Class<S> clazz, String filter) throws InvalidSyntaxException {
    return null;
  }

  @Override
  public <S> S getService(ServiceReference<S> reference) {
    return null;
  }

  @Override
  public boolean ungetService(ServiceReference<?> reference) {
    return false;
  }

  @Override
  public <S> ServiceObjects<S> getServiceObjects(ServiceReference<S> reference) {
    return null;
  }

  @Override
  public File getDataFile(String filename) {
    return null;
  }

  @Override
  public Filter createFilter(String filter) throws InvalidSyntaxException {
    return null;
  }

  @Override
  public Bundle getBundle(String location) {
    return null;
  }
}
