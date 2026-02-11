---
paths:
  - '**/*.java'
---

# Java Development

## Language Features

- Target Java 21 language level
- Use records for immutable data classes
- Use sealed classes/interfaces for closed type hierarchies
- Prefer switch expressions over switch statements with pattern matching
- Use pattern matching in instanceof checks
- Avoid null checks where pattern matching can be used
- use Optional.ofNullable() for potentially null values and checks
- Use text blocks for multi-line strings
- Use var for local variables when the type is obvious from context

## Code Style

- Use `@Override` annotation for all overridden methods
- Use `@Deprecated` annotation for deprecated methods and classes
- Use `@SuppressWarnings` annotation only when necessary with a comment explaining why
- Add comments sparingly, prefer self-explanatory code
- Only use final for fields used in lambdas or inner classes
- Prefer imports over fully qualified names
- Use lombok annotations (e.g. @Data, @Getter, @Builder) to reduce boilerplate, but prefer records when possible

## Naming Conventions

- Use PascalCase for class names and interfaces
- Use camelCase for methods, variables, and parameters
- Use UPPER_SNAKE_CASE for constants (static final fields)
- Use descriptive names that reveal intent
- Avoid single-letter names except for loop counters
- Boolean variables/methods should start with is/has/can/should

## API Design

- Use chaining for setters (fluent interfaces)
- Use withX() instead of setX() for fluent APIs
- Use `Optional<T>` for optional return types
- Never use `Optional<T>` for class fields or method parameters
- Use `List<T>`, `Map<K, V>`, `Set<T>` for collections
- Prefer primitive types over wrapper types
- Builder interfaces should return the builder type for method chaining
- Never return null for collections (return empty collection instead)
- Validate method parameters early (fail fast)

## Code Organization

- Use package-info.java to document package-level concepts
- Group related classes in subpackages
- Keep utility classes final with private constructors
- Interfaces should be used for public contracts
- Implementation classes can be package-private when appropriate
- Keep methods small and focused (single responsibility)
- Limit method parameters (max 3-4, use builder pattern for more)
- Make methods private by default, increase visibility only when needed

## OSGi Considerations

- Only `oakgit.jdbc` is exported; all other packages are bundle-private
- Be mindful of package visibility when adding new public APIs
- Dependencies embedded in the bundle (JGit, etc.) are not available to other bundles

## Immutability and Thread Safety

- Prefer immutable objects (use records or final fields)
- Make fields private and final by default
- Don't provide setters unless mutability is required
- Use Collections.unmodifiableList/Map/Set for defensive copies
- Consider using java.util.concurrent collections for thread-safe operations
- Use CompletableFuture for async programming

## Collections

- Use appropriate collection types (List, Set, Map)
- Use diamond operator for type inference: `new ArrayList<>()`
- Prefer List.of(), Set.of(), Map.of() for immutable collections
- Use Stream API for functional-style operations
- Avoid raw types (always use generics)
- Prefer `.getFirst()` and `.getLast()` over `.get(0)` and `.get(size()-1)`

## Exception Handling

- Custom exceptions should extend appropriate base exception class
- Use checked exceptions very sparingly (only for recoverable conditions)
- Use IllegalArgumentException for invalid method arguments
- Use IllegalStateException for invalid object state
- Use UnsupportedOperationException for operations that are not supported
- Document checked exceptions in method signatures
- Prefer specific exception types over generic ones
- Don't catch generic Exception unless at application boundary
- Don't swallow exceptions (at minimum log them)
- Use try-with-resources for AutoCloseable resources
- Include meaningful error messages in exceptions
- Don't use exceptions for flow control

## Null Safety

- Use Optional<T> for return values that may be absent
- Use Objects.requireNonNull() for critical null checks
- Consider using @NonNull/@Nullable annotations for documentation (optional)

## Performance

- Use StringBuilder for string concatenation in loops
- Avoid creating unnecessary objects in tight loops
- Close resources properly (use try-with-resources)
- Be mindful of autoboxing/unboxing costs
- Use primitive types over wrappers when appropriate

## Equals and HashCode

- Always override both equals() and hashCode() together
- Use Objects.equals() and Objects.hash() for implementations
- Records automatically provide correct equals/hashCode implementations

## Logging

- Use SLF4J or similar logging facade
- Use appropriate log levels (ERROR, WARN, INFO, DEBUG, TRACE)
- Use parameterized logging to avoid string concatenation
- Don't log sensitive information (passwords, tokens, PII)
- Log at appropriate points (errors, important state changes)

## Dependency Management

- Use Nix-provided `mvn` command (no Maven wrapper)
- Keep dependencies up to date
- Avoid dependency bloat (minimize dependencies)
- Use dependencyManagement in parent POMs
- Specify explicit versions (no LATEST or RELEASE)

## Testing

- All public methods must have unit tests
- Use JUnit 5 for unit tests
- Use Mockito for mocking
- Use AssertJ for assertions in new test code (existing tests use Hamcrest — migration will happen separately)
- Use descriptive names for test methods
- Follow the Arrange-Act-Assert pattern with inline setup - each test should be self-contained
- Avoid @BeforeEach setup methods - inline the setup in each test for clarity
- Helper methods with fluent APIs (e.g., `new Provider().with(Foo.class, ...)`) are preferred over shared state
- Use @DisplayName to better describe test cases
- Group related tests using @Nested classes
- Prefer parameterized tests (@ParameterizedTest) when testing multiple similar cases
- Static methods are allowed in test utility classes
- Try to group common test utilities and helper methods in dedicated classes
- Use `@UnitTest` annotation (tag: "unit") for unit tests, `@SandboxTest` (tag: "sandbox") for integration tests

### Explicit Assertions

- Always assert specific expected values, not just presence or absence
- Avoid weak assertions like `isNotNull()`, `isNotEmpty()`, `isPresent()` when a specific value can be verified
- Verify exception messages, not just exception types:

  ```java
  // Bad: only checks type
  assertThatThrownBy(() -> doSomething())
      .isInstanceOf(IllegalArgumentException.class);

  // Good: also verifies message
  assertThatThrownBy(() -> doSomething())
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Expected error message");
  ```

- When testing algorithms or computations, verify against known expected results:

  ```java
  // Bad: only checks something was produced
  assertThat(checksum.asString()).isNotEmpty();

  // Good: verifies against known value
  assertThat(checksum.asString()).isEqualTo("uU0nuZNNPgilLlLX2n2r+sSE7+N6U4DukIj3rOLvzek=");
  ```

- Exception: `isNotNull()` is acceptable when testing random/generated values that cannot be predicted, but should be combined with other meaningful assertions
