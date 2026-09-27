package pl.piotrjanczukowicz.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Constructor;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class DateTimeUtilsTest {

    @Test
    void constructor_whenInstantiatedViaReflection_shouldThrow() throws Exception {
        final Constructor<DateTimeUtils> constructor = DateTimeUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertThatThrownBy(constructor::newInstance).hasCauseInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void toIso_whenInstantHasNanos_shouldReturnUtcSeconds() {
        final Instant instant = Instant.parse("2024-01-01T00:00:00.123456789Z");

        final String formatted = DateTimeUtils.toIso(instant);

        assertThat(formatted).isEqualTo("2024-01-01T00:00:00Z");
    }

    @Test
    void toIso_whenAlreadyWholeSeconds_shouldReturnUtcSeconds() {
        final Instant instant = Instant.parse("2023-01-01T00:00:00Z");

        final String formatted = DateTimeUtils.toIso(instant);

        assertThat(formatted).isEqualTo("2023-01-01T00:00:00Z");
    }

    @Test
    void toIso_whenInstantNull_shouldThrow() {
        final Instant instant = null;

        assertThatThrownBy(() -> DateTimeUtils.toIso(instant))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("instant must not be null");
    }
}
