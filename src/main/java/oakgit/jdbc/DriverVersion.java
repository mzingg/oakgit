package oakgit.jdbc;

record DriverVersion(int major, int minor, int patch) {

  static final DriverVersion ZERO = new DriverVersion(0, 0, 0);

  static DriverVersion parse(String version) {
    var parts = version.split("\\.");
    return new DriverVersion(
        parts.length > 0 ? Integer.parseInt(parts[0]) : 0,
        parts.length > 1 ? Integer.parseInt(parts[1]) : 0,
        parts.length > 2 ? Integer.parseInt(parts[2]) : 0);
  }

  @Override
  public String toString() {
    return major + "." + minor + "." + patch;
  }
}
