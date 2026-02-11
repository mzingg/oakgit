# oakgit - Git Persistence Layer for Apache OAK

## Project Identity

oakgit is a JDBC driver that implements Apache OAK persistence using a Git object database backend (JGit). It is packaged as an OSGi bundle designed to be dropped into an AEMaaCS SDK instance, where it registers itself as a database driver, allowing OAK's RDB (relational database) document store to persist data into a local Git repository instead of a traditional RDBMS.

## Predefined Technologies

- We use Nix develop environment for building and running the project
- We use Maven 4 for build orchestration
- Java 21 is the target runtime

## Architecture Overview

The project follows a **command pattern** architecture with SQL parsing:

1. **JDBC Bridge** (`oakgit.jdbc`) - Standard JDBC Driver/Connection/Statement/ResultSet implementations. `OakGitDriver` registers itself via `DriverManager` for `jdbc:oakgit://` URLs.

2. **Command Framework** (`oakgit.engine`) - `CommandFactory` receives SQL strings, passes them through a chain of `QueryAnalyzer` implementations, and produces typed `Command` objects (CREATE, INSERT, SELECT variants, UPDATE).

3. **Query Analyzers** (`oakgit.engine.query.analyzer`) - Regex-based SQL parsers that match specific OAK query patterns (CREATE, INSERT for documents/datastore, SELECT by id/range/modified, UPDATE).

4. **Command Processors** (`oakgit.processor`) - Pluggable backends that execute commands:
   - `InMemoryCommandProcessor` - HashMap-based storage (default, used for testing)
   - `GitFilesystem` - JGit-based persistence to a Git repository

5. **Data Models** (`oakgit.engine.model`) - `ContainerEntry<T>` implementations for OAK data types: `DocumentEntry`, `DatastoreDataEntry`, `DatastoreMetaEntry`.

## Project Structure

This is a **single-module** project with OSGi bundle packaging. There is no multi-module structure or XX_category naming convention.

- `src/main/java/oakgit/` - Main source code
- `src/test/java/oakgit/` - Test code
- `.mvn/parent_java/checkstyle.xml` - Checkstyle configuration (Google Java Style)
- `.nix/` - Nix packages and utility scripts
- `ops/` - Operational files (local AEM deployment config, Adobe stub JARs)

## Build System

- **Maven 4** with POM 4.1.0 schema
- **Nix develop** provides JDK 21, Maven 4, and treefmt
- **OSGi bundle** packaging via felix maven-bundle-plugin
- Exported package: `oakgit.jdbc`
- Embedded dependencies: JGit, java-semver, maven-model, plexus-utils, commons-lang3

## Testing

- **JUnit 5** with tag-based test classification:
  - `@UnitTest` (tag: "unit") - runs via maven-surefire-plugin
  - `@SandboxTest` (tag: "sandbox") - isolated integration tests
- **Mockito** for mocking
- **Hamcrest** for assertions in existing tests
- **AssertJ** preferred for new test code (migration from Hamcrest will happen separately)
- Surefire runs tests tagged "unit" by default

## Key Dependencies

- **JGit** - Git object database operations
- **Lombok** - Boilerplate reduction (@Getter, @Setter, @AllArgsConstructor, etc.)
- **Apache OAK** / **Sling** / **Adobe Granite** - AEM platform APIs (provided/test scope)
- **java-semver** + **maven-model** - Version parsing from POM metadata
