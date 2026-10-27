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
