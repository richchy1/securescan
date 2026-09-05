# SecureScan — Lightweight Java Security Analyzer

SecureScan is a lightweight Java security analyzer built using AST-based static analysis with JavaParser. It was created primarily to learn and demonstrate fundamental program-analysis concepts such as parsing source code into an Abstract Syntax Tree (AST), traversing AST nodes, and implementing pattern-based security rules. SecureScan is **not** intended to replace full-featured SAST tools and currently does not perform dataflow analysis, taint tracking, or interprocedural analysis.

## Architecture

```text
Java Source
    ↓
Parser (JavaParser)
    ↓
Rules (visitor-based)
    ↓
Findings
    ↓
Console Reporter
```

Each security rule implements a common `Rule` interface:

```java
analyze(CompilationUnit, String) -> List<Finding>
```

This makes the analyzer extensible: adding a new detector requires creating a new rule class and adding one line to `Main`, without changing the scanner's core driver logic.

## Detection Rules

### 1. Weak Cryptography

Detects usage of known weak cryptographic algorithms such as:

```java
MessageDigest.getInstance("MD5");
MessageDigest.getInstance("SHA-1");
```

The rule traverses the AST looking for `MethodCallExpr` nodes named `getInstance`. It then checks whether the first argument is a string literal and whether that string matches one of the configured weak algorithms (`MD5`, `SHA1`, `SHA-1`, or `DES`).

**Limitation:** The rule matches any `getInstance(...)` call with these string arguments regardless of the calling class. It does not perform type or symbol resolution, so it could theoretically produce a false positive for an unrelated method named `getInstance`.

### 2. Hardcoded Secrets

Detects variables whose names suggest they may contain sensitive information, such as:

```java
String password = "letmein123";
String apiKey = "my-secret-key";
String token = "abc123";
```

The rule traverses `VariableDeclarator` nodes and checks whether the variable name contains suspicious keywords such as `password`, `secret`, `key`, `token`, or `credential`. It then checks whether the variable is initialized with a string literal.

**Limitation:** This is heuristic name-based detection. It can produce false positives when a variable name looks suspicious but does not contain a real secret, and false negatives when secrets use unexpected variable names or are constructed dynamically.

### 3. SQL Injection (Pattern-Based)

Detects SQL queries that are constructed using string concatenation directly inside database execution calls, for example:

```java
stmt.executeQuery(
    "SELECT * FROM users WHERE name='" + name + "'"
);
```

The rule searches for calls such as `executeQuery`, `executeUpdate`, and `execute`. It checks whether the query argument is a `BinaryExpr` using the `+` operator, which indicates string concatenation.

**Limitation:** This is syntactic pattern matching, not taint analysis. It only catches concatenation directly in the query call argument. It will not catch injection through an intermediate variable such as:

```java
String q = "SELECT * FROM users WHERE name='" + name + "'";
stmt.executeQuery(q);
```

The current implementation also only checks the immediate `BinaryExpr` structure and does not perform deeper dataflow or variable tracking.

## Usage

Build the project:

```bash
mvn clean package
```

Run SecureScan against a single Java file:

```bash
java -jar target/SecureScan-1.0-SNAPSHOT.jar path/to/File.java
```

Run SecureScan against a directory:

```bash
java -jar target/SecureScan-1.0-SNAPSHOT.jar path/to/directory/
```

The directory scanner recursively searches for `.java` files and applies all configured security rules to each file.

## Example Output

Example:

```text
=== SecureScan Results ===

testdata/vulnerable/WeakCrypto.java:8 [HIGH] Weak cryptography algorithm: MD5
testdata/vulnerable/WeakCrypto.java:17 [HIGH] Weak cryptography algorithm: SHA-1
testdata/vulnerable/HardcodedSecret.java:3 [HIGH] Possible hardcoded secret in variable: password
testdata/vulnerable/SqlInjectionExample.java:10 [HIGH] Possible SQL injection: SQL query is built using string concatenation
```

A safe directory with no detected issues produces:

```text
=== SecureScan Results ===
No security issues found.
```

## Limitations

- Single-file syntactic analysis only — no cross-file, interprocedural, or dataflow analysis.
- No dataflow or taint tracking.
- No symbol resolution — JavaParser SymbolSolver is not currently used, so matching is type-unaware.
- The rule set currently contains three detectors.
- Hardcoded-secret detection relies on heuristic variable-name matching and may produce false positives and false negatives.
- SQL injection detection is pattern-based and cannot follow values through intermediate variables.
- Weak cryptography detection does not verify the type of the object calling `getInstance`.
- SecureScan is a learning project and is **not a replacement for production SAST tools** such as SonarQube, Semgrep, or CodeQL.

## Future Work

- Add symbol resolution using JavaParser's SymbolSolver for type-aware matching.
- Implement basic intraprocedural taint tracking for the SQL injection rule.
- Improve SQL injection detection to track queries through intermediate variables.
- Add additional security rules, such as:
  - Insecure deserialization
  - Path traversal
  - Command injection
  - Insecure randomness
- Improve reporting with structured output formats such as JSON.
- Add automated unit tests for each security rule.
