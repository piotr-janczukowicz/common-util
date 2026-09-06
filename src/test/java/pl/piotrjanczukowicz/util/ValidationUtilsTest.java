package pl.piotrjanczukowicz.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.lang.reflect.Constructor;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class ValidationUtilsTest {

    @Test
    void constructor_shouldThrow_whenInstantiatedViaReflection() throws Exception {
        final Constructor<ValidationUtils> constructor = ValidationUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final Throwable thrown = catchThrowable(constructor::newInstance);

        assertThat(thrown).hasCauseInstanceOf(UnsupportedOperationException.class);
    }

    @ParameterizedTest
    @MethodSource("nonPositiveDurations")
    void isPositive_shouldReturnFalse_whenNullOrNotPositive(final Duration duration) {
        final boolean positive = ValidationUtils.isPositive(duration);

        assertThat(positive).isFalse();
    }

    @Test
    void isPositive_shouldReturnTrue_whenDurationPositive() {
        final Duration duration = Duration.ofMillis(1);

        final boolean positive = ValidationUtils.isPositive(duration);

        assertThat(positive).isTrue();
    }

    @ParameterizedTest
    @MethodSource("nonPositiveDurations")
    void isNonPositive_shouldReturnTrue_whenNullOrNotPositive(final Duration duration) {
        final boolean nonPositive = ValidationUtils.isNonPositive(duration);

        assertThat(nonPositive).isTrue();
    }

    @Test
    void isNonPositive_shouldReturnFalse_whenDurationPositive() {
        final Duration duration = Duration.ofMillis(1);

        final boolean nonPositive = ValidationUtils.isNonPositive(duration);

        assertThat(nonPositive).isFalse();
    }

    @Test
    void requireAtMost_shouldReturnValue_whenWithinMaximum() {
        final int value = 100;
        final int maximum = 100;
        final String name = "perPage";

        final int result = ValidationUtils.requireAtMost(value, maximum, name);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireAtMost_shouldThrow_whenValueExceedsMaximum() {
        final int value = 101;
        final int maximum = 100;
        final String name = "perPage";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireAtMost(value, maximum, name));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining(name);
    }

    @Test
    void requireAtLeast_shouldReturnValue_whenWithinMinimum() {
        final double value = 0.0;
        final double minimum = 0.0;
        final String name = "percentage";

        final double result = ValidationUtils.requireAtLeast(value, minimum, name);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireAtLeast_shouldThrow_whenValueBelowMinimum() {
        final double value = -1.0;
        final double minimum = 0.0;
        final String name = "percentage";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireAtLeast(value, minimum, name));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining(name);
    }

    @Test
    void requireAtMost_shouldReturnValue_whenWithinMaximumForDouble() {
        final double value = 100.0;
        final double maximum = 100.0;
        final String name = "percentage";

        final double result = ValidationUtils.requireAtMost(value, maximum, name);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireAtMost_shouldThrow_whenDoubleValueExceedsMaximum() {
        final double value = 101.0;
        final double maximum = 100.0;
        final String name = "percentage";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireAtMost(value, maximum, name));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining(name);
    }

    @Test
    void requireAtMost_shouldThrow_whenDoubleValueIsNaN() {
        final double value = Double.NaN;
        final double maximum = 100.0;
        final String name = "percentage";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireAtMost(value, maximum, name));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining(name);
    }

    @Test
    void requireAtMost_shouldThrow_whenDoubleValueIsInfinite() {
        final double value = Double.POSITIVE_INFINITY;
        final double maximum = 100.0;
        final String name = "percentage";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireAtMost(value, maximum, name));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining(name);
    }

    @Test
    void requireBetween_shouldReturnValue_whenWithinBounds() {
        final double value = 50.0;
        final double minimum = 0.0;
        final double maximum = 100.0;
        final String name = "percentage";

        final double result = ValidationUtils.requireBetween(value, minimum, maximum, name);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireBetween_shouldThrow_whenValueBelowMinimum() {
        final double value = -1.0;
        final double minimum = 0.0;
        final double maximum = 100.0;
        final String name = "percentage";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireBetween(value, minimum, maximum, name));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining(name);
    }

    @Test
    void requireBetween_shouldThrow_whenValueAboveMaximum() {
        final double value = 101.0;
        final double minimum = 0.0;
        final double maximum = 100.0;
        final String name = "percentage";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireBetween(value, minimum, maximum, name));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining(name);
    }

    @Test
    void requireBetween_shouldThrow_whenValueIsNaN() {
        final double value = Double.NaN;
        final double minimum = 0.0;
        final double maximum = 100.0;
        final String name = "percentage";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireBetween(value, minimum, maximum, name));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining(name);
    }

    @Test
    void requireBetween_shouldThrow_whenValueIsInfinite() {
        final double value = Double.POSITIVE_INFINITY;
        final double minimum = 0.0;
        final double maximum = 100.0;
        final String name = "percentage";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireBetween(value, minimum, maximum, name));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining(name);
    }

    @Test
    void requireNotBlank_shouldReturnValue_whenNonBlank() {
        final String value = "token";
        final String name = "token";

        final String result = ValidationUtils.requireNotBlank(value, name);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireNotBlank_shouldThrow_whenNull() {
        final String value = null;
        final String name = "token";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireNotBlank(value, name));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessageContaining(name);
    }

    @Test
    void requireNotBlank_shouldThrow_whenBlank() {
        final String value = "  ";
        final String name = "token";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireNotBlank(value, name));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage(name + " must not be blank");
    }

    @Test
    void requireSizeAtMost_shouldReturnValue_whenWithinMaximum() {
        final List<String> values = List.of("a", "b");
        final int maximum = 2;
        final String name = "items";

        final List<String> result = ValidationUtils.requireSizeAtMost(values, maximum, name);

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireSizeAtMost_shouldThrow_whenSizeExceedsMaximum() {
        final List<String> values = List.of("a", "b", "c");
        final int maximum = 2;
        final String name = "items";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireSizeAtMost(values, maximum, name));

        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(name)
                .hasMessageContaining("at most 2");
    }

    @Test
    void requireSizeAtLeast_shouldReturnValue_whenWithinMinimum() {
        final List<String> values = List.of("a", "b");
        final int minimum = 2;
        final String name = "items";

        final List<String> result = ValidationUtils.requireSizeAtLeast(values, minimum, name);

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireSizeAtLeast_shouldThrow_whenSizeBelowMinimum() {
        final List<String> values = List.of("a");
        final int minimum = 2;
        final String name = "items";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireSizeAtLeast(values, minimum, name));

        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(name)
                .hasMessageContaining("at least 2");
    }

    @Test
    void requireSizeExactly_shouldReturnValue_whenSizeMatches() {
        final List<String> values = List.of("a");
        final int size = 1;
        final String name = "items";

        final List<String> result = ValidationUtils.requireSizeExactly(values, size, name);

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireSizeExactly_shouldThrow_whenNull() {
        final List<String> values = null;
        final int size = 1;
        final String name = "items";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireSizeExactly(values, size, name));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessageContaining(name);
    }

    @Test
    void requireSizeExactly_shouldThrow_whenSizeDiffers() {
        final List<String> values = List.of("a", "b");
        final int size = 1;
        final String name = "items";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireSizeExactly(values, size, name));

        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(name)
                .hasMessageContaining("exactly 1");
    }

    @Test
    void requireSizeBetween_shouldReturnValue_whenWithinBounds() {
        final List<String> values = List.of("a", "b");
        final int minimum = 2;
        final int maximum = 100;
        final String name = "items";

        final List<String> result = ValidationUtils.requireSizeBetween(values, minimum, maximum, name);

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireSizeBetween_shouldThrow_whenNull() {
        final List<String> values = null;
        final int minimum = 2;
        final int maximum = 100;
        final String name = "items";

        final Throwable thrown =
                catchThrowable(() -> ValidationUtils.requireSizeBetween(values, minimum, maximum, name));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessageContaining(name);
    }

    @Test
    void requireSizeBetween_shouldThrow_whenSizeBelowMinimum() {
        final List<String> values = List.of("a");
        final int minimum = 2;
        final int maximum = 100;
        final String name = "items";

        final Throwable thrown =
                catchThrowable(() -> ValidationUtils.requireSizeBetween(values, minimum, maximum, name));

        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(name)
                .hasMessageContaining("at least 2");
    }

    @Test
    void requireSizeBetween_shouldThrow_whenSizeExceedsMaximum() {
        final List<String> values = List.of("a", "b", "c");
        final int minimum = 1;
        final int maximum = 2;
        final String name = "items";

        final Throwable thrown =
                catchThrowable(() -> ValidationUtils.requireSizeBetween(values, minimum, maximum, name));

        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(name)
                .hasMessageContaining("at most 2");
    }

    @Test
    void requireMaxLength_shouldReturnValue_whenWithinMaximum() {
        final String value = "hi";
        final int maximum = 2;
        final String name = "summary";

        final String result = ValidationUtils.requireMaxLength(value, maximum, name);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireMaxLength_shouldThrow_whenLongerThanMaximum() {
        final String value = "hey";
        final int maximum = 2;
        final String name = "summary";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireMaxLength(value, maximum, name));

        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(name)
                .hasMessageContaining("at most 2");
    }

    @Test
    void requireMaxLengthWhenPresent_shouldReturnValue_whenWithinMaximum() {
        final String value = "hi";
        final int maximum = 2;
        final String name = "summary";

        final String result = ValidationUtils.requireMaxLengthWhenPresent(value, maximum, name);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireMaxLengthWhenPresent_shouldThrow_whenLongerThanMaximum() {
        final String value = "hey";
        final int maximum = 2;
        final String name = "summary";

        final Throwable thrown =
                catchThrowable(() -> ValidationUtils.requireMaxLengthWhenPresent(value, maximum, name));

        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(name)
                .hasMessageContaining("at most 2");
    }

    @Test
    void requireMatches_shouldReturnValue_whenPatternMatches() {
        final String value = "ssh-rsa AAA";
        final Pattern pattern = Pattern.compile("^ssh-rsa ");
        final String name = "key";

        final String result = ValidationUtils.requireMatches(value, pattern, name);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireMatches_shouldThrow_whenPatternDoesNotMatch() {
        final String value = "not-a-key";
        final Pattern pattern = Pattern.compile("^ssh-rsa ");
        final String name = "key";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireMatches(value, pattern, name));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining(name);
    }

    @Test
    void requireNonNull_shouldReturnValue_whenNonNull() {
        final String value = "token";
        final String name = "token";

        final String result = ValidationUtils.requireNonNull(value, name);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireNonNull_shouldThrow_whenNull() {
        final String value = null;
        final String name = "token";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireNonNull(value, name));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessageContaining(name);
    }

    @Test
    void requirePositive_shouldReturnDuration_whenPositive() {
        final Duration duration = Duration.ofMillis(1);
        final String name = "timeout";

        final Duration result = ValidationUtils.requirePositive(duration, name);

        assertThat(result).isSameAs(duration);
    }

    @Test
    void requirePositive_shouldThrow_whenNull() {
        final Duration duration = null;
        final String name = "timeout";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requirePositive(duration, name));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessageContaining(name);
    }

    @ParameterizedTest
    @MethodSource("zeroAndNegativeDurations")
    void requirePositive_shouldThrow_whenZeroOrNegative(final Duration duration) {
        final String name = "timeout";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requirePositive(duration, name));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining(name);
    }

    @ParameterizedTest
    @MethodSource("zeroAndPositiveDurations")
    void requireNonNegative_shouldReturnDuration_whenZeroOrPositive(final Duration duration) {
        final String name = "delay";

        final Duration result = ValidationUtils.requireNonNegative(duration, name);

        assertThat(result).isEqualTo(duration);
    }

    @Test
    void requireNonNegative_shouldThrow_whenNull() {
        final Duration duration = null;
        final String name = "delay";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireNonNegative(duration, name));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessageContaining(name);
    }

    @Test
    void requireNonNegative_shouldThrow_whenNegative() {
        final Duration duration = Duration.ofSeconds(-1);
        final String name = "delay";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireNonNegative(duration, name));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining(name);
    }

    @Test
    void requireAtLeast_shouldReturnInt_whenWithinMinimum() {
        final int value = 2;
        final int minimum = 2;
        final String name = "count";

        final int result = ValidationUtils.requireAtLeast(value, minimum, name);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireAtLeast_shouldThrow_whenIntBelowMinimum() {
        final int value = 1;
        final int minimum = 2;
        final String name = "count";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireAtLeast(value, minimum, name));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining(name);
    }

    @Test
    void requireAtLeast_shouldThrow_whenDoubleValueIsNaN() {
        final double value = Double.NaN;
        final double minimum = 0.0;
        final String name = "percentage";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireAtLeast(value, minimum, name));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining(name);
    }

    @Test
    void requireAtLeast_shouldThrow_whenDoubleValueIsInfinite() {
        final double value = Double.NEGATIVE_INFINITY;
        final double minimum = 0.0;
        final String name = "percentage";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireAtLeast(value, minimum, name));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining(name);
    }

    @Test
    void requireGreaterOrEqual_shouldPass_whenValueAtLeastMinimum() {
        final Duration value = Duration.ofSeconds(2);
        final Duration minimum = Duration.ofSeconds(2);
        final String name = "wait";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireGreaterOrEqual(value, minimum, name));

        assertThat(thrown).isNull();
    }

    @Test
    void requireGreaterOrEqual_shouldThrow_whenValueBelowMinimum() {
        final Duration value = Duration.ofSeconds(1);
        final Duration minimum = Duration.ofSeconds(2);
        final String name = "wait";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireGreaterOrEqual(value, minimum, name));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining(name);
    }

    @Test
    void requireGreaterOrEqual_shouldThrow_whenValueNull() {
        final Duration value = null;
        final Duration minimum = Duration.ZERO;
        final String name = "wait";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireGreaterOrEqual(value, minimum, name));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessageContaining(name);
    }

    @Test
    void requireGreaterOrEqual_shouldThrow_whenMinimumNull() {
        final Duration value = Duration.ZERO;
        final Duration minimum = null;
        final String name = "wait";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireGreaterOrEqual(value, minimum, name));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessageContaining("minimum");
    }

    @Test
    void requireNonEmpty_shouldReturnValue_whenNonEmpty() {
        final List<String> values = List.of("a");
        final String name = "items";

        final List<String> result = ValidationUtils.requireNonEmpty(values, name);

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireNonEmpty_shouldThrow_whenEmpty() {
        final List<String> values = List.of();
        final String name = "items";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireNonEmpty(values, name));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining(name);
    }

    @Test
    void requireNonEmpty_shouldThrow_whenNull() {
        final List<String> values = null;
        final String name = "items";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireNonEmpty(values, name));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessageContaining(name);
    }

    @Test
    void requireSizeAtMost_shouldThrow_whenNull() {
        final List<String> values = null;
        final int maximum = 2;
        final String name = "items";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireSizeAtMost(values, maximum, name));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessageContaining(name);
    }

    @Test
    void requireSizeAtLeast_shouldThrow_whenNull() {
        final List<String> values = null;
        final int minimum = 1;
        final String name = "items";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireSizeAtLeast(values, minimum, name));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessageContaining(name);
    }

    @Test
    void requireUniqueItems_shouldReturnValue_whenUnique() {
        final List<String> values = List.of("a", "b");
        final String name = "items";

        final List<String> result = ValidationUtils.requireUniqueItems(values, name);

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireUniqueItems_shouldThrow_whenDuplicates() {
        final List<String> values = List.of("a", "a");
        final String name = "items";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireUniqueItems(values, name));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining(name);
    }

    @Test
    void requireUniqueItems_shouldThrow_whenNull() {
        final List<String> values = null;
        final String name = "items";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireUniqueItems(values, name));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessageContaining(name);
    }

    @Test
    void requireMapSizeAtMost_shouldReturnValue_whenWithinMaximum() {
        final Map<String, String> values = Map.of("a", "1");
        final int maximum = 1;
        final String name = "props";

        final Map<String, String> result = ValidationUtils.requireMapSizeAtMost(values, maximum, name);

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireMapSizeAtMost_shouldThrow_whenSizeExceedsMaximum() {
        final Map<String, String> values = Map.of("a", "1", "b", "2");
        final int maximum = 1;
        final String name = "props";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireMapSizeAtMost(values, maximum, name));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining(name);
    }

    @Test
    void requireMapSizeAtMost_shouldThrow_whenNull() {
        final Map<String, String> values = null;
        final int maximum = 1;
        final String name = "props";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireMapSizeAtMost(values, maximum, name));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessageContaining(name);
    }

    @Test
    void requireMaxLengthWhenPresent_shouldReturnNull_whenValueNull() {
        final String value = null;
        final int maximum = 2;
        final String name = "summary";

        final String result = ValidationUtils.requireMaxLengthWhenPresent(value, maximum, name);

        assertThat(result).isNull();
    }

    @Test
    void requireMatches_shouldThrow_whenValueNull() {
        final String value = null;
        final Pattern pattern = Pattern.compile(".");
        final String name = "key";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireMatches(value, pattern, name));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessageContaining(name);
    }

    @Test
    void requireMatches_shouldThrow_whenPatternNull() {
        final String value = "ssh-rsa AAA";
        final Pattern pattern = null;
        final String name = "key";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireMatches(value, pattern, name));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessageContaining("pattern");
    }

    private static Stream<Duration> nonPositiveDurations() {
        return Stream.of(null, Duration.ZERO, Duration.ofSeconds(-1));
    }

    private static Stream<Duration> zeroAndNegativeDurations() {
        return Stream.of(Duration.ZERO, Duration.ofSeconds(-1));
    }

    private static Stream<Duration> zeroAndPositiveDurations() {
        return Stream.of(Duration.ZERO, Duration.ofMillis(1));
    }
}
