[![Codacy Badge](https://app.codacy.com/project/badge/Grade/2aef4762ea294ad89c04966d6bde8f6c)](https://app.codacy.com/gh/piotr-janczukowicz/common-util/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)
[![Codacy Coverage](https://app.codacy.com/project/badge/Coverage/2aef4762ea294ad89c04966d6bde8f6c)](https://app.codacy.com/gh/piotr-janczukowicz/common-util/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_coverage)
[![Build](https://github.com/piotr-janczukowicz/common-util/actions/workflows/main.yml/badge.svg)](https://github.com/piotr-janczukowicz/common-util/actions/workflows/main.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.piotr-janczukowicz/common-util)](https://central.sonatype.com/artifact/io.github.piotr-janczukowicz/common-util)
[![License: MIT](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
![Java 21](https://img.shields.io/badge/Java-21-orange.svg)
# common-util

Java utilities and annotations for callers: fail-fast argument checks, ISO-8601 instant formatting, enum lookup from wire strings, and classpath resource reads. Annotate records with `@RecordBuilder` to generate type-safe builders at compile time.

## Dependency

Java library. Add Maven coordinates `io.github.piotr-janczukowicz:common-util:7`.

```xml
<dependency>
  <groupId>io.github.piotr-janczukowicz</groupId>
  <artifactId>common-util</artifactId>
  <version>7</version>
</dependency>
```

## Usage

Call `ValidationUtils.requireNotBlank` to reject null or blank strings and keep the same value on success. Null fails with `NullPointerException`; blank fails with `IllegalArgumentException`.

```java
import io.github.piotr_janczukowicz.util.ValidationUtils;

String name = ValidationUtils.requireNotBlank(rawName, "name");
```

## License

MIT License
