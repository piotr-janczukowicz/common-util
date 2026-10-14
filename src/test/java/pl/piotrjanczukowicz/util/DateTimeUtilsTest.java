package pl.piotrjanczukowicz.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Constructor;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
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

    @Test
    void parseRfc1123_shouldReturnInstant_whenGmtDate() {
        final Optional<Instant> parsed = DateTimeUtils.parseRfc1123("Tue, 3 Jun 2008 11:05:30 GMT");

        assertThat(parsed).contains(Instant.parse("2008-06-03T11:05:30Z"));
    }

    @Test
    void parseRfc1123_shouldReturnSamePointInUtc_whenNonGmtOffset() {
        final Optional<Instant> parsed = DateTimeUtils.parseRfc1123("Tue, 3 Jun 2008 13:05:30 +0200");

        assertThat(parsed).contains(Instant.parse("2008-06-03T11:05:30Z"));
    }

    @Test
    void parseRfc1123_shouldReturnEmpty_whenTextInvalid() {
        final Optional<Instant> parsed = DateTimeUtils.parseRfc1123("not a date");

        assertThat(parsed).isEmpty();
    }

    @Test
    void parseRfc1123_shouldReturnEmpty_whenRfc850Date() {
        final Optional<Instant> parsed = DateTimeUtils.parseRfc1123("Sunday, 06-Nov-94 08:49:37 GMT");

        assertThat(parsed).isEmpty();
    }

    @Test
    void parseRfc1123_shouldReturnEmpty_whenAsctimeDate() {
        final Optional<Instant> parsed = DateTimeUtils.parseRfc1123("Sun Nov  6 08:49:37 1994");

        assertThat(parsed).isEmpty();
    }

    @Test
    void parseRfc1123_shouldThrowNullPointerException_whenValueNull() {
        final String value = null;

        assertThatThrownBy(() -> DateTimeUtils.parseRfc1123(value))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null");
    }

    @Test
    void toRfc1123_shouldFormatInUtc_whenInstantGiven() {
        final Instant instant = Instant.parse("2008-06-03T11:05:30Z");

        final String formatted = DateTimeUtils.toRfc1123(instant);

        assertThat(formatted).isEqualTo("Tue, 3 Jun 2008 11:05:30 GMT");
    }

    @Test
    void toRfc1123_shouldDropFractionalSeconds_whenInstantHasNanos() {
        final Instant instant = Instant.parse("2008-06-03T11:05:30.987654321Z");

        final String formatted = DateTimeUtils.toRfc1123(instant);

        assertThat(formatted).isEqualTo("Tue, 3 Jun 2008 11:05:30 GMT");
    }

    @Test
    void toRfc1123_shouldMatchRfc1123Formatter_whenInstantGiven() {
        final Instant instant = Instant.parse("2024-02-29T23:59:59Z");

        final String formatted = DateTimeUtils.toRfc1123(instant);

        assertThat(formatted).isEqualTo(DateTimeFormatter.RFC_1123_DATE_TIME.format(instant.atOffset(ZoneOffset.UTC)));
    }

    @Test
    void toRfc1123_shouldThrowNullPointerException_whenInstantNull() {
        final Instant instant = null;

        assertThatThrownBy(() -> DateTimeUtils.toRfc1123(instant))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("instant must not be null");
    }

    @Test
    void parseRfc1123_shouldReturnOriginalInstant_whenRoundTripped() {
        final Instant instant = Instant.parse("2024-02-29T23:59:59Z");

        final Optional<Instant> parsed = DateTimeUtils.parseRfc1123(DateTimeUtils.toRfc1123(instant));

        assertThat(parsed).contains(instant);
    }
}
