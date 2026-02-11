---
paths:
  - '**/pom.xml'
---

# Maven 4 Usage

This project uses Maven 4.0.0-rc-5 with Maven 4-specific features.

## Basics

- Use POM 4.1.0 schema: `xmlns="http://maven.apache.org/POM/4.1.0"`
- Minimum Maven version: 4.0.0-rc-5
- No Maven wrapper - use Nix-provided `mvn` command

## Project Structure

This is a **single-module** project with OSGi `bundle` packaging. There is no multi-module structure.

The root `pom.xml` is self-contained — all build configuration, dependency management, and plugin setup are defined directly in it.

## Root POM

The root `pom.xml`:

- Defines `bundle` packaging (OSGi via felix maven-bundle-plugin)
- Contains all dependencies with explicit versions
- Configures OSGi export/import/embed instructions
- Configures compiler (Java 21), surefire, jacoco, enforcer, checkstyle plugins
- AEM/OAK dependency versions are pinned to match the target AEM SDK version

## Checkstyle

- Checkstyle config lives in `.mvn/parent_java/checkstyle.xml` (Google Java Style)
- Activated via `-DwithLinting` property

## Common Commands

```bash
mvn compile                # Build the project
mvn test                   # Run unit tests
mvn -DwithLinting compile  # With checkstyle linting
```
