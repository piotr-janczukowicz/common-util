# common-util

Shared Java utilities and annotations. This artifact is one JAR with two packages: `pl.piotrjanczukowicz.annotation` (`@RecordBuilder`) and `pl.piotrjanczukowicz.util` (`ValidationUtils`, `DateTimeUtils`, `EnumUtils`). It inherits `common-parent` for the Java **21** build and quality plugins. License: Apache 2.0.

`@RecordBuilder` is a record-builder template. Consuming projects still need `record-builder-processor` on the annotation processor path.

## Coordinates

| Field | Value |
| --- | --- |
| groupId | `pl.piotrjanczukowicz` |
| artifactId | `common-util` |
| version | `1.0.0-SNAPSHOT` |
| packaging | `jar` |

Published to GitHub Packages, not Maven Central:

`https://maven.pkg.github.com/piotr-janczukowicz/common-util`

## Consume

Declare the dependency:

```xml
<dependency>
  <groupId>pl.piotrjanczukowicz</groupId>
  <artifactId>common-util</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```

Until GitHub Packages has a published build, install from this repository:

```bash
mvn install
```

## GitHub Packages

GitHub Maven packages require authentication even when public. Do not add `<repositories>` to a project POM; the inherited Enforcer rule forbids it. Configure the registry in `~/.m2/settings.xml` (or a CI settings file).

Use a classic personal access token with `read:packages`. Point the server password at an environment variable; do not put the token in the file.

This POM’s parent is `common-parent` (which itself depends on `common-dependencies` and `common-build-config`), so settings must list those GitHub Packages repositories as well:

```xml
<settings>
  <servers>
    <server>
      <id>github-common-util</id>
      <username>YOUR_GITHUB_USERNAME</username>
      <password>${env.GH_PACKAGES_TOKEN}</password>
    </server>
    <server>
      <id>github-common-parent</id>
      <username>YOUR_GITHUB_USERNAME</username>
      <password>${env.GH_PACKAGES_TOKEN}</password>
    </server>
    <server>
      <id>github-common-dependencies</id>
      <username>YOUR_GITHUB_USERNAME</username>
      <password>${env.GH_PACKAGES_TOKEN}</password>
    </server>
  </servers>
  <profiles>
    <profile>
      <id>github</id>
      <repositories>
        <repository>
          <id>github-common-util</id>
          <url>https://maven.pkg.github.com/piotr-janczukowicz/common-util</url>
          <snapshots>
            <enabled>true</enabled>
          </snapshots>
        </repository>
        <repository>
          <id>github-common-parent</id>
          <url>https://maven.pkg.github.com/piotr-janczukowicz/common-parent</url>
          <snapshots>
            <enabled>true</enabled>
          </snapshots>
        </repository>
        <repository>
          <id>github-common-dependencies</id>
          <url>https://maven.pkg.github.com/piotr-janczukowicz/common-dependencies</url>
          <snapshots>
            <enabled>true</enabled>
          </snapshots>
        </repository>
      </repositories>
      <pluginRepositories>
        <pluginRepository>
          <id>github-common-parent</id>
          <url>https://maven.pkg.github.com/piotr-janczukowicz/common-parent</url>
          <snapshots>
            <enabled>true</enabled>
          </snapshots>
        </pluginRepository>
      </pluginRepositories>
    </profile>
  </profiles>
  <activeProfiles>
    <activeProfile>github</activeProfile>
  </activeProfiles>
</settings>
```

Pushes to `main` deploy with the workflow `github.token` (`packages: write`). Do not create a repository secret whose name starts with `GITHUB_`.

## Default build

Inheriting `common-parent` binds Checkstyle on `validate` and SpotBugs on `verify` because this module has `src/main/java`. Library and plugin versions come from `common-dependencies`.
