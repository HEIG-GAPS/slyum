# Agent Directives for Slyum

This file provides guidelines for AI agents (such as GitHub Copilot, Codex, or
similar tools) contributing to the Slyum project.

## Goal

Slyum is a desktop application that allows users to design UML 1.4 class
diagrams. Further development will add UML 2.5 compatibility. Other diagram
types will also be added in the future.

Agents **must never** break the compatibility described above. Every time a new
UML version is implemented, existing projects **must** always be openable with
the new version of the application.

## Project Management and Dependencies

Maven is used as the project management tool. The project must be compiled with
**Java 25** and **JavaFX 25**.

Dependencies must be kept up to date every time a pull request is created. To
achieve this, use the `versions-maven-plugin` goals to identify updates:

```bash
mvn versions:display-dependency-updates
mvn versions:display-plugin-updates
mvn versions:display-property-updates
```

Run all relevant `versions-maven-plugin` goals and update any outdated
dependency or plugin version before marking a pull request as ready.

All code must be compatible with the Checkstyle rules defined in
`gaps_java_checks.xml`. Run the following command to verify compliance:

```bash
mvn checkstyle:check
```

All code must also comply with SonarQube rules.

## Testing

The project uses **JUnit 5** and **JaCoCo**. The codebase must be covered by at
least **80%** of tests. The goal is to reach **100%** coverage.

- No tests shall be removed, unless the class or method under test has been
  deleted. If a test fails, fix the production code. If a test must be dropped,
  provide a clear explanation in the pull request.
- Run the test suite with:

```bash
mvn test
```

- Generate the JaCoCo coverage report with:

```bash
mvn jacoco:report
```

## Accessibility

All UI components **must** comply with current accessibility rules (WCAG 2.1 AA
or higher). This applies to labels, keyboard navigation, colour contrast, and
screen-reader support.

## Internationalisation

Localised messages must be available in:

| Locale | Language          |
|--------|-------------------|
| fr-CH  | Swiss French      |
| en-GB  | British English   |
| it-CH  | Swiss Italian     |
| de-CH  | Swiss German      |

The locale is resolved automatically from the operating system. Every user-
visible string must be stored in the corresponding resource bundle and **never**
hard-coded in Java source files.

## Deployment and Updates

The application must be deployable for the following platforms and
architectures:

| Operating system         | Architecture |
|--------------------------|--------------|
| Microsoft Windows        | amd64        |
| Microsoft Windows        | aarch64      |
| Apple macOS              | amd64        |
| Apple macOS              | aarch64      |
| Linux (deb **and** rpm)  | amd64        |
| Linux (deb **and** rpm)  | aarch64      |

Verify that build configuration (packaging, JVM bundling, native launchers)
covers every combination above before submitting a pull request.

## Pull Requests

A pull request is marked as **ready for review** only when:

1. All automated tests have passed.
2. The JaCoCo coverage threshold (≥ 80%) is satisfied.
3. Checkstyle reports no violations (`mvn checkstyle:check`).
4. All dependency/plugin versions have been reviewed and updated where
   applicable.
5. The project builds and packages successfully for all target platforms.
