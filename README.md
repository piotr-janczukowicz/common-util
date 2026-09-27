# common-util

Java utilities and annotations for callers: fail-fast argument checks, ISO-8601 instant formatting, enum lookup from wire strings, and classpath resource reads. Annotate records with `@RecordBuilder` to generate type-safe builders at compile time.

## Dependency

Java library. Add Maven coordinates `pl.piotrjanczukowicz:common-util:1.0.0-SNAPSHOT`.

```xml
<dependency>
  <groupId>pl.piotrjanczukowicz</groupId>
  <artifactId>common-util</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## Usage

Call `ValidationUtils.requireNotBlank` to reject null or blank strings and keep the same value on success. Null fails with `NullPointerException`; blank fails with `IllegalArgumentException`.

```java
import pl.piotrjanczukowicz.util.ValidationUtils;

String name = ValidationUtils.requireNotBlank(rawName, "name must not be blank");
```

## License

Apache License, Version 2.0
