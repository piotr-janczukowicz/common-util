package pl.piotrjanczukowicz.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.lang.reflect.Constructor;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class DateTimeUtilsTest {

    @Test
    void constructor_shouldThrow_whenInstantiatedViaReflection() throws Exception {
        final Constructor<DateTimeUtils> constructor = DateTimeUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final Throwable thrown = catchThrowable(constructor::newInstance);

        assertThat(thrown).hasCauseInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void toIso_shouldReturnUtcSeconds_whenInstantHasNanos() {
        final Instant instant = Instant.parse("2024-01-01T00:00:00.123456789Z");

        final String formatted = DateTimeUtils.toIso(instant);

        assertThat(formatted).isEqualTo("2024-01-01T00:00:00Z");
    }

    @Test
    void toIso_shouldReturnUtcSeconds_whenAlreadyWholeSeconds() {
        final Instant instant = Instant.parse("2023-01-01T00:00:00Z");

        final String formatted = DateTimeUtils.toIso(instant);

        assertThat(formatted).isEqualTo("2023-01-01T00:00:00Z");
    }

    @Test
    void toIso_shouldThrow_whenInstantNull() {
        final Instant instant = null;

        final Throwable thrown = catchThrowable(() -> DateTimeUtils.toIso(instant));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessage("instant must not be null");
    }
}
