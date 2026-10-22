package pl.piotrjanczukowicz.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.lang.reflect.Constructor;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class ValidationUtilsTest {

    @Test
    void constructor_whenInstantiatedViaReflection_shouldThrow() throws Exception {
        final Constructor<ValidationUtils> constructor = ValidationUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertThatThrownBy(constructor::newInstance).hasCauseInstanceOf(UnsupportedOperationException.class);
    }

    @ParameterizedTest
    @MethodSource("nonPositiveDurations")
    void isPositive_whenNullOrNotPositive_shouldReturnFalse(final Duration duration) {
        final boolean positive = ValidationUtils.isPositive(duration);

        assertThat(positive).isFalse();
    }

    @Test
    void isPositive_whenDurationPositive_shouldReturnTrue() {
        final Duration duration = Duration.ofMillis(1);

        final boolean positive = ValidationUtils.isPositive(duration);

        assertThat(positive).isTrue();
    }

    @ParameterizedTest
    @MethodSource("nonPositiveDurations")
    void isNonPositive_whenNullOrNotPositive_shouldReturnTrue(final Duration duration) {
        final boolean nonPositive = ValidationUtils.isNonPositive(duration);

        assertThat(nonPositive).isTrue();
    }

    @Test
    void isNonPositive_whenDurationPositive_shouldReturnFalse() {
        final Duration duration = Duration.ofMillis(1);

        final boolean nonPositive = ValidationUtils.isNonPositive(duration);

        assertThat(nonPositive).isFalse();
    }

    @Test
    void requireAtMost_whenWithinMaximum_shouldReturnValue() {
        final int value = 100;
        final int maximum = 100;
        final String fieldName = "perPage";

        final int result = ValidationUtils.requireAtMost(value, maximum, fieldName);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireAtMost_whenValueExceedsMaximum_shouldThrow() {
        final int value = 101;
        final int maximum = 100;
        final String fieldName = "perPage";

        assertThatThrownBy(() -> ValidationUtils.requireAtMost(value, maximum, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("perPage must be at most 100");
    }

    @Test
    void requireAtLeast_whenWithinMinimum_shouldReturnValue() {
        final double value = 0.0;
        final double minimum = 0.0;
        final String fieldName = "percentage";

        final double result = ValidationUtils.requireAtLeast(value, minimum, fieldName);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireAtLeast_whenValueBelowMinimum_shouldThrow() {
        final double value = -1.0;
        final double minimum = 0.0;
        final String fieldName = "percentage";

        assertThatThrownBy(() -> ValidationUtils.requireAtLeast(value, minimum, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("percentage must be finite and at least 0.0");
    }

    @Test
    void requireAtMost_whenWithinMaximumForDouble_shouldReturnValue() {
        final double value = 100.0;
        final double maximum = 100.0;
        final String fieldName = "percentage";

        final double result = ValidationUtils.requireAtMost(value, maximum, fieldName);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireAtMost_whenDoubleValueExceedsMaximum_shouldThrow() {
        final double value = 101.0;
        final double maximum = 100.0;
        final String fieldName = "percentage";

        assertThatThrownBy(() -> ValidationUtils.requireAtMost(value, maximum, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("percentage must be finite and at most 100.0");
    }

    @Test
    void requireAtMost_whenDoubleValueIsNaN_shouldThrow() {
        final double value = Double.NaN;
        final double maximum = 100.0;
        final String fieldName = "percentage";

        assertThatThrownBy(() -> ValidationUtils.requireAtMost(value, maximum, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("percentage must be finite and at most 100.0");
    }

    @Test
    void requireAtMost_whenDoubleValueIsInfinite_shouldThrow() {
        final double value = Double.POSITIVE_INFINITY;
        final double maximum = 100.0;
        final String fieldName = "percentage";

        assertThatThrownBy(() -> ValidationUtils.requireAtMost(value, maximum, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("percentage must be finite and at most 100.0");
    }

    @Test
    void requireBetween_whenWithinBounds_shouldReturnValue() {
        final double value = 50.0;
        final double minimum = 0.0;
        final double maximum = 100.0;
        final String fieldName = "percentage";

        final double result = ValidationUtils.requireBetween(value, minimum, maximum, fieldName);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireBetween_whenValueBelowMinimum_shouldThrow() {
        final double value = -1.0;
        final double minimum = 0.0;
        final double maximum = 100.0;
        final String fieldName = "percentage";

        assertThatThrownBy(() -> ValidationUtils.requireBetween(value, minimum, maximum, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("percentage must be finite and between 0.0 and 100.0");
    }

    @Test
    void requireBetween_whenValueAboveMaximum_shouldThrow() {
        final double value = 101.0;
        final double minimum = 0.0;
        final double maximum = 100.0;
        final String fieldName = "percentage";

        assertThatThrownBy(() -> ValidationUtils.requireBetween(value, minimum, maximum, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("percentage must be finite and between 0.0 and 100.0");
    }

    @Test
    void requireBetween_whenValueIsNaN_shouldThrow() {
        final double value = Double.NaN;
        final double minimum = 0.0;
        final double maximum = 100.0;
        final String fieldName = "percentage";

        assertThatThrownBy(() -> ValidationUtils.requireBetween(value, minimum, maximum, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("percentage must be finite and between 0.0 and 100.0");
    }

    @Test
    void requireBetween_whenValueIsInfinite_shouldThrow() {
        final double value = Double.POSITIVE_INFINITY;
        final double minimum = 0.0;
        final double maximum = 100.0;
        final String fieldName = "percentage";

        assertThatThrownBy(() -> ValidationUtils.requireBetween(value, minimum, maximum, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("percentage must be finite and between 0.0 and 100.0");
    }

    @Test
    void requireNonNull_whenNonNull_shouldReturnValue() {
        final String value = "token";
        final String fieldName = "token";

        final String result = ValidationUtils.requireNonNull(value, fieldName);

        assertThat(result).isSameAs(value);
    }

    @Test
    void requireNonNull_whenNull_shouldThrow() {
        final String value = null;
        final String fieldName = "token";

        assertThatThrownBy(() -> ValidationUtils.requireNonNull(value, fieldName))
                .isInstanceOf(NullPointerException.class)
                .hasMessage(fieldName + " must not be null");
    }

    @Test
    void requireNotBlank_whenNonBlank_shouldReturnValue() {
        final String value = "token";
        final String fieldName = "token";

        final String result = ValidationUtils.requireNotBlank(value, fieldName);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireNotBlank_whenNull_shouldThrow() {
        final String value = null;
        final String fieldName = "token";

        assertThatThrownBy(() -> ValidationUtils.requireNotBlank(value, fieldName))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("token must not be null");
    }

    @Test
    void requireNotBlank_whenBlank_shouldThrow() {
        final String value = "  ";
        final String fieldName = "token";

        assertThatThrownBy(() -> ValidationUtils.requireNotBlank(value, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("token must not be blank");
    }

    @Test
    void requireSizeAtMost_whenWithinMaximum_shouldReturnValue() {
        final List<String> values = List.of("a", "b");
        final int maximum = 2;
        final String fieldName = "items";

        final List<String> result = ValidationUtils.requireSizeAtMost(values, maximum, fieldName);

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireSizeAtMost_whenSizeExceedsMaximum_shouldThrow() {
        final List<String> values = List.of("a", "b", "c");
        final int maximum = 2;
        final String fieldName = "items";

        assertThatThrownBy(() -> ValidationUtils.requireSizeAtMost(values, maximum, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("items must have at most 2 elements");
    }

    @Test
    void requireSizeAtLeast_whenWithinMinimum_shouldReturnValue() {
        final List<String> values = List.of("a", "b");
        final int minimum = 2;
        final String fieldName = "items";

        final List<String> result = ValidationUtils.requireSizeAtLeast(values, minimum, fieldName);

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireSizeAtLeast_whenSizeBelowMinimum_shouldThrow() {
        final List<String> values = List.of("a");
        final int minimum = 2;
        final String fieldName = "items";

        assertThatThrownBy(() -> ValidationUtils.requireSizeAtLeast(values, minimum, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("items must have at least 2 elements");
    }

    @Test
    void requireSizeExactly_whenSizeMatches_shouldReturnValue() {
        final List<String> values = List.of("a");
        final int size = 1;
        final String fieldName = "items";

        final List<String> result = ValidationUtils.requireSizeExactly(values, size, fieldName);

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireSizeExactly_whenNull_shouldThrow() {
        final List<String> values = null;
        final int size = 1;
        final String fieldName = "items";

        assertThatThrownBy(() -> ValidationUtils.requireSizeExactly(values, size, fieldName))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("items must not be null");
    }

    @Test
    void requireSizeExactly_whenSizeDiffers_shouldThrow() {
        final List<String> values = List.of("a", "b");
        final int size = 1;
        final String fieldName = "items";

        assertThatThrownBy(() -> ValidationUtils.requireSizeExactly(values, size, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("items must have exactly 1 elements");
    }

    @Test
    void requireSizeBetween_whenWithinBounds_shouldReturnValue() {
        final List<String> values = List.of("a", "b");
        final int minimum = 2;
        final int maximum = 100;
        final String fieldName = "items";

        final List<String> result = ValidationUtils.requireSizeBetween(values, minimum, maximum, fieldName);

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireSizeBetween_whenNull_shouldThrow() {
        final List<String> values = null;
        final int minimum = 2;
        final int maximum = 100;
        final String fieldName = "items";

        assertThatThrownBy(() -> ValidationUtils.requireSizeBetween(values, minimum, maximum, fieldName))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("items must not be null");
    }

    @Test
    void requireSizeBetween_whenCollectionBoundsInverted_shouldThrow() {
        final List<String> values = List.of("a", "b");
        final int minimum = 5;
        final int maximum = 2;
        final String fieldName = "items";

        final Throwable thrown =
                catchThrowable(() -> ValidationUtils.requireSizeBetween(values, minimum, maximum, fieldName));

        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("items has bounds in the wrong order: minimum 5 is greater than maximum 2");
    }

    @Test
    void requireSizeBetween_whenMapBoundsInverted_shouldThrow() {
        final Map<String, String> values = Map.of("a", "1");
        final int minimum = 5;
        final int maximum = 2;
        final String fieldName = "entries";

        final Throwable thrown =
                catchThrowable(() -> ValidationUtils.requireSizeBetween(values, minimum, maximum, fieldName));

        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("entries has bounds in the wrong order: minimum 5 is greater than maximum 2");
    }

    @Test
    void requireAtLeast_whenDoubleMinimumIsPositiveInfinity_shouldThrow() {
        final double value = 5.0;
        final double minimum = Double.POSITIVE_INFINITY;
        final String fieldName = "ratio";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireAtLeast(value, minimum, fieldName));

        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ratio must be finite and at least Infinity");
    }

    @Test
    void requireAtLeast_whenDoubleValueIsNegativeZeroAndMinimumPositiveZero_shouldReturnValue() {
        final double value = -0.0;
        final double minimum = 0.0;
        final String fieldName = "ratio";

        final double result = ValidationUtils.requireAtLeast(value, minimum, fieldName);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireSizeBetween_whenSizeBelowMinimum_shouldThrow() {
        final List<String> values = List.of("a");
        final int minimum = 2;
        final int maximum = 100;
        final String fieldName = "items";

        assertThatThrownBy(() -> ValidationUtils.requireSizeBetween(values, minimum, maximum, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("items must have between 2 and 100 elements");
    }

    @Test
    void requireSizeBetween_whenSizeExceedsMaximum_shouldThrow() {
        final List<String> values = List.of("a", "b", "c");
        final int minimum = 1;
        final int maximum = 2;
        final String fieldName = "items";

        assertThatThrownBy(() -> ValidationUtils.requireSizeBetween(values, minimum, maximum, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("items must have between 1 and 2 elements");
    }

    @Test
    void requireMaxLength_whenWithinMaximum_shouldReturnValue() {
        final String value = "hi";
        final int maximum = 2;
        final String fieldName = "summary";

        final String result = ValidationUtils.requireMaxLength(value, maximum, fieldName);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireMaxLength_whenLongerThanMaximum_shouldThrow() {
        final String value = "hey";
        final int maximum = 2;
        final String fieldName = "summary";

        assertThatThrownBy(() -> ValidationUtils.requireMaxLength(value, maximum, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("summary must have length at most 2");
    }

    @Test
    void requireMaxLengthIfPresent_whenWithinMaximum_shouldReturnValue() {
        final String value = "hi";
        final int maximum = 2;
        final String fieldName = "summary";

        final String result = ValidationUtils.requireMaxLengthIfPresent(value, maximum, fieldName);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireMaxLengthIfPresent_whenLongerThanMaximum_shouldThrow() {
        final String value = "hey";
        final int maximum = 2;
        final String fieldName = "summary";

        assertThatThrownBy(() -> ValidationUtils.requireMaxLengthIfPresent(value, maximum, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("summary must have length at most 2");
    }

    @Test
    void requireMatches_whenPatternMatches_shouldReturnValue() {
        final String value = "ssh-rsa AAA";
        final Pattern pattern = Pattern.compile("^ssh-rsa ");
        final String fieldName = "key";

        final String result = ValidationUtils.requireMatches(value, pattern, fieldName);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireMatches_whenPatternDoesNotMatch_shouldThrow() {
        final String value = "not-a-key";
        final Pattern pattern = Pattern.compile("^ssh-rsa ");
        final String fieldName = "key";

        assertThatThrownBy(() -> ValidationUtils.requireMatches(value, pattern, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("key must match the required pattern");
    }

    @Test
    void requirePositive_whenPositive_shouldReturnDuration() {
        final Duration duration = Duration.ofMillis(1);
        final String fieldName = "timeout";

        final Duration result = ValidationUtils.requirePositive(duration, fieldName);

        assertThat(result).isSameAs(duration);
    }

    @Test
    void requirePositive_whenNull_shouldThrow() {
        final Duration duration = null;
        final String fieldName = "timeout";

        assertThatThrownBy(() -> ValidationUtils.requirePositive(duration, fieldName))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("timeout must not be null");
    }

    @ParameterizedTest
    @MethodSource("zeroAndNegativeDurations")
    void requirePositive_whenZeroOrNegative_shouldThrow(final Duration duration) {
        final String fieldName = "timeout";

        assertThatThrownBy(() -> ValidationUtils.requirePositive(duration, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("timeout must be positive");
    }

    @ParameterizedTest
    @MethodSource("zeroAndPositiveDurations")
    void requireNonNegative_whenZeroOrPositive_shouldReturnDuration(final Duration duration) {
        final String fieldName = "delay";

        final Duration result = ValidationUtils.requireNonNegative(duration, fieldName);

        assertThat(result).isEqualTo(duration);
    }

    @Test
    void requireNonNegative_whenNull_shouldThrow() {
        final Duration duration = null;
        final String fieldName = "delay";

        assertThatThrownBy(() -> ValidationUtils.requireNonNegative(duration, fieldName))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("delay must not be null");
    }

    @Test
    void requireNonNegative_whenNegative_shouldThrow() {
        final Duration duration = Duration.ofSeconds(-1);
        final String fieldName = "delay";

        assertThatThrownBy(() -> ValidationUtils.requireNonNegative(duration, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("delay must not be negative");
    }

    @Test
    void requireAtMost_whenDurationWithinMaximum_shouldReturnDuration() {
        final Duration value = Duration.ofSeconds(2);
        final Duration maximum = Duration.ofSeconds(2);
        final String fieldName = "wait";

        final Duration result = ValidationUtils.requireAtMost(value, maximum, fieldName);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireAtMost_whenDurationExceedsMaximum_shouldThrow() {
        final Duration value = Duration.ofSeconds(3);
        final Duration maximum = Duration.ofSeconds(2);
        final String fieldName = "wait";

        assertThatThrownBy(() -> ValidationUtils.requireAtMost(value, maximum, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("wait must be at most PT2S");
    }

    @Test
    void requireAtMost_whenDurationNull_shouldThrow() {
        final Duration value = null;
        final Duration maximum = Duration.ZERO;
        final String fieldName = "wait";

        assertThatThrownBy(() -> ValidationUtils.requireAtMost(value, maximum, fieldName))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("wait must not be null");
    }

    @Test
    void requireAtMost_whenMaximumDurationNull_shouldThrow() {
        final Duration value = Duration.ZERO;
        final Duration maximum = null;
        final String fieldName = "wait";

        assertThatThrownBy(() -> ValidationUtils.requireAtMost(value, maximum, fieldName))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("maximum must not be null");
    }

    @Test
    void requireAtLeast_whenWithinMinimum_shouldReturnInt() {
        final int value = 2;
        final int minimum = 2;
        final String fieldName = "count";

        final int result = ValidationUtils.requireAtLeast(value, minimum, fieldName);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireAtLeast_whenIntBelowMinimum_shouldThrow() {
        final int value = 1;
        final int minimum = 2;
        final String fieldName = "count";

        assertThatThrownBy(() -> ValidationUtils.requireAtLeast(value, minimum, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("count must be at least 2");
    }

    @Test
    void requireAtLeast_whenDoubleValueIsNaN_shouldThrow() {
        final double value = Double.NaN;
        final double minimum = 0.0;
        final String fieldName = "percentage";

        assertThatThrownBy(() -> ValidationUtils.requireAtLeast(value, minimum, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("percentage must be finite and at least 0.0");
    }

    @Test
    void requireAtLeast_whenDoubleValueIsInfinite_shouldThrow() {
        final double value = Double.NEGATIVE_INFINITY;
        final double minimum = 0.0;
        final String fieldName = "percentage";

        assertThatThrownBy(() -> ValidationUtils.requireAtLeast(value, minimum, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("percentage must be finite and at least 0.0");
    }

    @Test
    void requireAtLeast_whenDurationAtLeastMinimum_shouldReturnValue() {
        final Duration value = Duration.ofSeconds(2);
        final Duration minimum = Duration.ofSeconds(2);
        final String fieldName = "wait";

        final Duration result = ValidationUtils.requireAtLeast(value, minimum, fieldName);

        assertThat(result).isSameAs(value);
    }

    @Test
    void requireAtLeast_whenDurationBelowMinimum_shouldThrow() {
        final Duration value = Duration.ofSeconds(1);
        final Duration minimum = Duration.ofSeconds(2);
        final String fieldName = "wait";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireAtLeast(value, minimum, fieldName));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage("wait must be at least PT2S");
    }

    @Test
    void requireAtLeast_whenDurationNull_shouldThrow() {
        final Duration value = null;
        final Duration minimum = Duration.ZERO;
        final String fieldName = "wait";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireAtLeast(value, minimum, fieldName));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessage("wait must not be null");
    }

    @Test
    void requireAtLeast_whenMinimumDurationNull_shouldThrow() {
        final Duration value = Duration.ZERO;
        final Duration minimum = null;
        final String fieldName = "wait";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireAtLeast(value, minimum, fieldName));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessage("minimum must not be null");
    }

    @Test
    void requireAtLeast_whenDoubleMinimumIsNaN_shouldThrow() {
        final double value = 1.0;
        final double minimum = Double.NaN;
        final String fieldName = "ratio";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireAtLeast(value, minimum, fieldName));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage("ratio has a NaN minimum");
    }

    @Test
    void requireAtMost_whenDoubleMaximumIsNaN_shouldThrow() {
        final double value = 1.0;
        final double maximum = Double.NaN;
        final String fieldName = "ratio";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireAtMost(value, maximum, fieldName));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage("ratio has a NaN maximum");
    }

    @Test
    void requireBetween_whenIntBoundsSwapped_shouldThrow() {
        final int value = 5;
        final String fieldName = "count";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireBetween(value, 10, 1, fieldName));

        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("count has bounds in the wrong order: minimum 10 is greater than maximum 1");
    }

    @Test
    void requireBetween_whenLongBoundsSwapped_shouldThrow() {
        final long value = 5L;
        final String fieldName = "count";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireBetween(value, 10L, 1L, fieldName));

        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("count has bounds in the wrong order: minimum 10 is greater than maximum 1");
    }

    @Test
    void requireBetween_whenDoubleBoundsSwapped_shouldThrow() {
        final double value = 5.0;
        final String fieldName = "ratio";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireBetween(value, 10.0, 1.0, fieldName));

        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ratio has bounds in the wrong order: minimum 10.0 is greater than maximum 1.0");
    }

    @Test
    void requireBetween_whenDoubleMinimumIsNaN_shouldThrow() {
        final double value = 5.0;
        final String fieldName = "ratio";

        final Throwable thrown =
                catchThrowable(() -> ValidationUtils.requireBetween(value, Double.NaN, 10.0, fieldName));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage("ratio has a NaN minimum");
    }

    @Test
    void requireBetween_whenDoubleMaximumIsNaN_shouldThrow() {
        final double value = 5.0;
        final String fieldName = "ratio";

        final Throwable thrown =
                catchThrowable(() -> ValidationUtils.requireBetween(value, 1.0, Double.NaN, fieldName));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage("ratio has a NaN maximum");
    }

    @Test
    void requireFullMatch_whenWholeValueMatches_shouldReturnValue() {
        final String value = "abc";
        final Pattern pattern = Pattern.compile("[a-z]+");
        final String fieldName = "code";

        final String result = ValidationUtils.requireFullMatch(value, pattern, fieldName);

        assertThat(result).isSameAs(value);
    }

    @Test
    void requireFullMatch_whenOnlyPartOfValueMatches_shouldThrow() {
        final String value = "abc; DROP";
        final Pattern pattern = Pattern.compile("[a-z]+");
        final String fieldName = "code";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireFullMatch(value, pattern, fieldName));

        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("code must match the required pattern");
    }

    @Test
    void requireFullMatchIfPresent_whenWholeValueMatches_shouldReturnValue() {
        final String value = "abc";
        final Pattern pattern = Pattern.compile("[a-z]+");
        final String fieldName = "code";

        final String result = ValidationUtils.requireFullMatchIfPresent(value, pattern, fieldName);

        assertThat(result).isSameAs(value);
    }

    @Test
    void requireFullMatchIfPresent_whenOnlyPartOfValueMatches_shouldThrow() {
        final String value = "abc; DROP";
        final Pattern pattern = Pattern.compile("[a-z]+");
        final String fieldName = "code";

        final Throwable thrown =
                catchThrowable(() -> ValidationUtils.requireFullMatchIfPresent(value, pattern, fieldName));

        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("code must match the required pattern");
    }

    @Test
    void requireFullMatchIfPresent_whenValueNull_shouldReturnNull() {
        final String value = null;
        final Pattern pattern = Pattern.compile("[a-z]+");
        final String fieldName = "code";

        final String result = ValidationUtils.requireFullMatchIfPresent(value, pattern, fieldName);

        assertThat(result).isNull();
    }

    @Test
    void requireFullMatch_whenValueNull_shouldThrow() {
        final String value = null;
        final Pattern pattern = Pattern.compile("[a-z]+");
        final String fieldName = "code";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireFullMatch(value, pattern, fieldName));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessage("code must not be null");
    }

    @Test
    void requireFullMatch_whenPatternNull_shouldThrow() {
        final String value = "abc";
        final Pattern pattern = null;
        final String fieldName = "code";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireFullMatch(value, pattern, fieldName));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessage("pattern must not be null");
    }

    @Test
    void requireNonEmpty_whenNonEmpty_shouldReturnValue() {
        final List<String> values = List.of("a");
        final String fieldName = "items";

        final List<String> result = ValidationUtils.requireNonEmpty(values, fieldName);

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireNonEmpty_whenEmpty_shouldThrow() {
        final List<String> values = List.of();
        final String fieldName = "items";

        assertThatThrownBy(() -> ValidationUtils.requireNonEmpty(values, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("items must not be empty");
    }

    @Test
    void requireNonEmpty_whenNull_shouldThrow() {
        final List<String> values = null;
        final String fieldName = "items";

        assertThatThrownBy(() -> ValidationUtils.requireNonEmpty(values, fieldName))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("items must not be null");
    }

    @Test
    void requireSizeAtMost_whenNull_shouldThrow() {
        final List<String> values = null;
        final int maximum = 2;
        final String fieldName = "items";

        assertThatThrownBy(() -> ValidationUtils.requireSizeAtMost(values, maximum, fieldName))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("items must not be null");
    }

    @Test
    void requireSizeAtLeast_whenNull_shouldThrow() {
        final List<String> values = null;
        final int minimum = 1;
        final String fieldName = "items";

        assertThatThrownBy(() -> ValidationUtils.requireSizeAtLeast(values, minimum, fieldName))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("items must not be null");
    }

    @Test
    void requireUniqueItems_whenUnique_shouldReturnValue() {
        final List<String> values = List.of("a", "b");
        final String fieldName = "items";

        final List<String> result = ValidationUtils.requireUniqueItems(values, fieldName);

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireUniqueItems_whenDuplicates_shouldThrow() {
        final List<String> values = List.of("a", "a");
        final String fieldName = "items";

        assertThatThrownBy(() -> ValidationUtils.requireUniqueItems(values, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("items must not contain duplicates");
    }

    @Test
    void requireUniqueItems_whenNull_shouldThrow() {
        final List<String> values = null;
        final String fieldName = "items";

        assertThatThrownBy(() -> ValidationUtils.requireUniqueItems(values, fieldName))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("items must not be null");
    }

    @Test
    void requireNonEmpty_whenNonEmpty_shouldReturnMap() {
        final Map<String, String> values = Map.of("a", "1");
        final String fieldName = "props";

        final Map<String, String> result = ValidationUtils.requireNonEmpty(values, fieldName);

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireNonEmpty_whenMapEmpty_shouldThrow() {
        final Map<String, String> values = Map.of();
        final String fieldName = "props";

        assertThatThrownBy(() -> ValidationUtils.requireNonEmpty(values, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("props must not be empty");
    }

    @Test
    void requireNonEmpty_whenMapNull_shouldThrow() {
        final Map<String, String> values = null;
        final String fieldName = "props";

        assertThatThrownBy(() -> ValidationUtils.requireNonEmpty(values, fieldName))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("props must not be null");
    }

    @Test
    void requireSizeAtMost_whenWithinMaximum_shouldReturnMap() {
        final Map<String, String> values = Map.of("a", "1");
        final int maximum = 1;
        final String fieldName = "props";

        final Map<String, String> result = ValidationUtils.requireSizeAtMost(values, maximum, fieldName);

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireSizeAtMost_whenMapSizeExceedsMaximum_shouldThrow() {
        final Map<String, String> values = Map.of("a", "1", "b", "2");
        final int maximum = 1;
        final String fieldName = "props";

        assertThatThrownBy(() -> ValidationUtils.requireSizeAtMost(values, maximum, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("props must have at most 1 entries");
    }

    @Test
    void requireSizeAtMost_whenMapNull_shouldThrow() {
        final Map<String, String> values = null;
        final int maximum = 1;
        final String fieldName = "props";

        assertThatThrownBy(() -> ValidationUtils.requireSizeAtMost(values, maximum, fieldName))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("props must not be null");
    }

    @Test
    void requireSizeAtLeast_whenWithinMinimum_shouldReturnMap() {
        final Map<String, String> values = Map.of("a", "1", "b", "2");
        final int minimum = 2;
        final String fieldName = "props";

        final Map<String, String> result = ValidationUtils.requireSizeAtLeast(values, minimum, fieldName);

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireSizeAtLeast_whenMapSizeBelowMinimum_shouldThrow() {
        final Map<String, String> values = Map.of("a", "1");
        final int minimum = 2;
        final String fieldName = "props";

        assertThatThrownBy(() -> ValidationUtils.requireSizeAtLeast(values, minimum, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("props must have at least 2 entries");
    }

    @Test
    void requireSizeAtLeast_whenMapNull_shouldThrow() {
        final Map<String, String> values = null;
        final int minimum = 1;
        final String fieldName = "props";

        assertThatThrownBy(() -> ValidationUtils.requireSizeAtLeast(values, minimum, fieldName))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("props must not be null");
    }

    @Test
    void requireSizeExactly_whenSizeMatches_shouldReturnMap() {
        final Map<String, String> values = Map.of("a", "1");
        final int size = 1;
        final String fieldName = "props";

        final Map<String, String> result = ValidationUtils.requireSizeExactly(values, size, fieldName);

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireSizeExactly_whenMapNull_shouldThrow() {
        final Map<String, String> values = null;
        final int size = 1;
        final String fieldName = "props";

        assertThatThrownBy(() -> ValidationUtils.requireSizeExactly(values, size, fieldName))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("props must not be null");
    }

    @Test
    void requireSizeExactly_whenMapSizeDiffers_shouldThrow() {
        final Map<String, String> values = Map.of("a", "1", "b", "2");
        final int size = 1;
        final String fieldName = "props";

        assertThatThrownBy(() -> ValidationUtils.requireSizeExactly(values, size, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("props must have exactly 1 entries");
    }

    @Test
    void requireSizeBetween_whenWithinBounds_shouldReturnMap() {
        final Map<String, String> values = Map.of("a", "1", "b", "2");
        final int minimum = 2;
        final int maximum = 100;
        final String fieldName = "props";

        final Map<String, String> result = ValidationUtils.requireSizeBetween(values, minimum, maximum, fieldName);

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireSizeBetween_whenMapNull_shouldThrow() {
        final Map<String, String> values = null;
        final int minimum = 2;
        final int maximum = 100;
        final String fieldName = "props";

        assertThatThrownBy(() -> ValidationUtils.requireSizeBetween(values, minimum, maximum, fieldName))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("props must not be null");
    }

    @Test
    void requireSizeBetween_whenMapSizeBelowMinimum_shouldThrow() {
        final Map<String, String> values = Map.of("a", "1");
        final int minimum = 2;
        final int maximum = 100;
        final String fieldName = "props";

        assertThatThrownBy(() -> ValidationUtils.requireSizeBetween(values, minimum, maximum, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("props must have between 2 and 100 entries");
    }

    @Test
    void requireSizeBetween_whenMapSizeExceedsMaximum_shouldThrow() {
        final Map<String, String> values = Map.of("a", "1", "b", "2", "c", "3");
        final int minimum = 1;
        final int maximum = 2;
        final String fieldName = "props";

        assertThatThrownBy(() -> ValidationUtils.requireSizeBetween(values, minimum, maximum, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("props must have between 1 and 2 entries");
    }

    @Test
    void requireMaxLengthIfPresent_whenValueNull_shouldReturnNull() {
        final String value = null;
        final int maximum = 2;
        final String fieldName = "summary";

        final String result = ValidationUtils.requireMaxLengthIfPresent(value, maximum, fieldName);

        assertThat(result).isNull();
    }

    @Test
    void requireMinLength_whenWithinMinimum_shouldReturnValue() {
        final String value = "hi";
        final int minLength = 2;
        final String fieldName = "summary";

        final String result = ValidationUtils.requireMinLength(value, minLength, fieldName);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireMinLength_whenShorterThanMinimum_shouldThrow() {
        final String value = "h";
        final int minLength = 2;
        final String fieldName = "summary";

        assertThatThrownBy(() -> ValidationUtils.requireMinLength(value, minLength, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("summary must have length at least 2");
    }

    @Test
    void requireMinLength_whenValueNull_shouldThrow() {
        final String value = null;
        final int minLength = 1;
        final String fieldName = "summary";

        assertThatThrownBy(() -> ValidationUtils.requireMinLength(value, minLength, fieldName))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("summary must not be null");
    }

    @Test
    void requireMinLengthIfPresent_whenWithinMinimum_shouldReturnValue() {
        final String value = "hi";
        final int minLength = 2;
        final String fieldName = "summary";

        final String result = ValidationUtils.requireMinLengthIfPresent(value, minLength, fieldName);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireMinLengthIfPresent_whenShorterThanMinimum_shouldThrow() {
        final String value = "h";
        final int minLength = 2;
        final String fieldName = "summary";

        assertThatThrownBy(() -> ValidationUtils.requireMinLengthIfPresent(value, minLength, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("summary must have length at least 2");
    }

    @Test
    void requireMinLengthIfPresent_whenValueNull_shouldReturnNull() {
        final String value = null;
        final int minLength = 2;
        final String fieldName = "summary";

        final String result = ValidationUtils.requireMinLengthIfPresent(value, minLength, fieldName);

        assertThat(result).isNull();
    }

    @Test
    void requireMatchesIfPresent_whenPatternMatches_shouldReturnValue() {
        final String value = "ssh-rsa AAA";
        final Pattern pattern = Pattern.compile("^ssh-rsa ");
        final String fieldName = "key";

        final String result = ValidationUtils.requireMatchesIfPresent(value, pattern, fieldName);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireMatchesIfPresent_whenPatternDoesNotMatch_shouldThrow() {
        final String value = "not-a-key";
        final Pattern pattern = Pattern.compile("^ssh-rsa ");
        final String fieldName = "key";

        assertThatThrownBy(() -> ValidationUtils.requireMatchesIfPresent(value, pattern, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("key must match the required pattern");
    }

    @Test
    void requireMatchesIfPresent_whenValueNull_shouldReturnNull() {
        final String value = null;
        final Pattern pattern = Pattern.compile("^ssh-rsa ");
        final String fieldName = "key";

        final String result = ValidationUtils.requireMatchesIfPresent(value, pattern, fieldName);

        assertThat(result).isNull();
    }

    @Test
    void requireMatchesIfPresent_whenValueNullAndPatternNull_shouldReturnNull() {
        final String value = null;
        final Pattern pattern = null;
        final String fieldName = "key";

        final String result = ValidationUtils.requireMatchesIfPresent(value, pattern, fieldName);

        assertThat(result).isNull();
    }

    @Test
    void requireNotBlankIfPresent_whenNonBlank_shouldReturnValue() {
        final String value = "token";
        final String fieldName = "token";

        final String result = ValidationUtils.requireNotBlankIfPresent(value, fieldName);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireNotBlankIfPresent_whenBlank_shouldThrow() {
        final String value = "  ";
        final String fieldName = "token";

        assertThatThrownBy(() -> ValidationUtils.requireNotBlankIfPresent(value, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("token must not be blank");
    }

    @Test
    void requireNotBlankIfPresent_whenValueNull_shouldReturnNull() {
        final String value = null;
        final String fieldName = "token";

        final String result = ValidationUtils.requireNotBlankIfPresent(value, fieldName);

        assertThat(result).isNull();
    }

    @Test
    void requireAtLeast_whenWithinMinimum_shouldReturnLong() {
        final long value = 2L;
        final long minimum = 2L;
        final String fieldName = "id";

        final long result = ValidationUtils.requireAtLeast(value, minimum, fieldName);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireAtLeast_whenLongBelowMinimum_shouldThrow() {
        final long value = 1L;
        final long minimum = 2L;
        final String fieldName = "id";

        assertThatThrownBy(() -> ValidationUtils.requireAtLeast(value, minimum, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("id must be at least 2");
    }

    @Test
    void requireAtMost_whenWithinMaximum_shouldReturnLong() {
        final long value = 100L;
        final long maximum = 100L;
        final String fieldName = "id";

        final long result = ValidationUtils.requireAtMost(value, maximum, fieldName);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireAtMost_whenLongExceedsMaximum_shouldThrow() {
        final long value = 101L;
        final long maximum = 100L;
        final String fieldName = "id";

        assertThatThrownBy(() -> ValidationUtils.requireAtMost(value, maximum, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("id must be at most 100");
    }

    @Test
    void requireBetween_whenWithinBounds_shouldReturnInt() {
        final int value = 50;
        final int minimum = 0;
        final int maximum = 100;
        final String fieldName = "count";

        final int result = ValidationUtils.requireBetween(value, minimum, maximum, fieldName);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireBetween_whenIntBelowMinimum_shouldThrow() {
        final int value = -1;
        final int minimum = 0;
        final int maximum = 100;
        final String fieldName = "count";

        assertThatThrownBy(() -> ValidationUtils.requireBetween(value, minimum, maximum, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("count must be between 0 and 100");
    }

    @Test
    void requireBetween_whenIntAboveMaximum_shouldThrow() {
        final int value = 101;
        final int minimum = 0;
        final int maximum = 100;
        final String fieldName = "count";

        assertThatThrownBy(() -> ValidationUtils.requireBetween(value, minimum, maximum, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("count must be between 0 and 100");
    }

    @Test
    void requireBetween_whenWithinBounds_shouldReturnLong() {
        final long value = 50L;
        final long minimum = 0L;
        final long maximum = 100L;
        final String fieldName = "id";

        final long result = ValidationUtils.requireBetween(value, minimum, maximum, fieldName);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireBetween_whenLongBelowMinimum_shouldThrow() {
        final long value = -1L;
        final long minimum = 0L;
        final long maximum = 100L;
        final String fieldName = "id";

        assertThatThrownBy(() -> ValidationUtils.requireBetween(value, minimum, maximum, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("id must be between 0 and 100");
    }

    @Test
    void requireBetween_whenLongAboveMaximum_shouldThrow() {
        final long value = 101L;
        final long minimum = 0L;
        final long maximum = 100L;
        final String fieldName = "id";

        assertThatThrownBy(() -> ValidationUtils.requireBetween(value, minimum, maximum, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("id must be between 0 and 100");
    }

    @Test
    void requireAtLeastIfPresent_whenIntegerWithinMinimum_shouldReturnValue() {
        final Integer value = 2;
        final int minimum = 2;
        final String fieldName = "count";

        final Integer result = ValidationUtils.requireAtLeastIfPresent(value, minimum, fieldName);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireAtLeastIfPresent_whenIntegerBelowMinimum_shouldThrow() {
        final Integer value = 1;
        final int minimum = 2;
        final String fieldName = "count";

        assertThatThrownBy(() -> ValidationUtils.requireAtLeastIfPresent(value, minimum, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("count must be at least 2");
    }

    @Test
    void requireAtLeastIfPresent_whenIntegerNull_shouldReturnNull() {
        final Integer value = null;
        final int minimum = 2;
        final String fieldName = "count";

        final Integer result = ValidationUtils.requireAtLeastIfPresent(value, minimum, fieldName);

        assertThat(result).isNull();
    }

    @Test
    void requireAtLeastIfPresent_whenLongWithinMinimum_shouldReturnValue() {
        final Long value = 2L;
        final long minimum = 2L;
        final String fieldName = "id";

        final Long result = ValidationUtils.requireAtLeastIfPresent(value, minimum, fieldName);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireAtLeastIfPresent_whenLongBelowMinimum_shouldThrow() {
        final Long value = 1L;
        final long minimum = 2L;
        final String fieldName = "id";

        assertThatThrownBy(() -> ValidationUtils.requireAtLeastIfPresent(value, minimum, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("id must be at least 2");
    }

    @Test
    void requireAtLeastIfPresent_whenLongNull_shouldReturnNull() {
        final Long value = null;
        final long minimum = 2L;
        final String fieldName = "id";

        final Long result = ValidationUtils.requireAtLeastIfPresent(value, minimum, fieldName);

        assertThat(result).isNull();
    }

    @Test
    void requireAtLeastIfPresent_whenDoubleWithinMinimum_shouldReturnValue() {
        final Double value = 0.0;
        final double minimum = 0.0;
        final String fieldName = "percentage";

        final Double result = ValidationUtils.requireAtLeastIfPresent(value, minimum, fieldName);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireAtLeastIfPresent_whenDoubleBelowMinimum_shouldThrow() {
        final Double value = -1.0;
        final double minimum = 0.0;
        final String fieldName = "percentage";

        assertThatThrownBy(() -> ValidationUtils.requireAtLeastIfPresent(value, minimum, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("percentage must be finite and at least 0.0");
    }

    @Test
    void requireAtLeastIfPresent_whenDoubleNull_shouldReturnNull() {
        final Double value = null;
        final double minimum = 0.0;
        final String fieldName = "percentage";

        final Double result = ValidationUtils.requireAtLeastIfPresent(value, minimum, fieldName);

        assertThat(result).isNull();
    }

    @Test
    void requireAtMostIfPresent_whenIntegerWithinMaximum_shouldReturnValue() {
        final Integer value = 100;
        final int maximum = 100;
        final String fieldName = "count";

        final Integer result = ValidationUtils.requireAtMostIfPresent(value, maximum, fieldName);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireAtMostIfPresent_whenIntegerExceedsMaximum_shouldThrow() {
        final Integer value = 101;
        final int maximum = 100;
        final String fieldName = "count";

        assertThatThrownBy(() -> ValidationUtils.requireAtMostIfPresent(value, maximum, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("count must be at most 100");
    }

    @Test
    void requireAtMostIfPresent_whenIntegerNull_shouldReturnNull() {
        final Integer value = null;
        final int maximum = 100;
        final String fieldName = "count";

        final Integer result = ValidationUtils.requireAtMostIfPresent(value, maximum, fieldName);

        assertThat(result).isNull();
    }

    @Test
    void requireAtMostIfPresent_whenLongWithinMaximum_shouldReturnValue() {
        final Long value = 100L;
        final long maximum = 100L;
        final String fieldName = "id";

        final Long result = ValidationUtils.requireAtMostIfPresent(value, maximum, fieldName);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireAtMostIfPresent_whenLongExceedsMaximum_shouldThrow() {
        final Long value = 101L;
        final long maximum = 100L;
        final String fieldName = "id";

        assertThatThrownBy(() -> ValidationUtils.requireAtMostIfPresent(value, maximum, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("id must be at most 100");
    }

    @Test
    void requireAtMostIfPresent_whenLongNull_shouldReturnNull() {
        final Long value = null;
        final long maximum = 100L;
        final String fieldName = "id";

        final Long result = ValidationUtils.requireAtMostIfPresent(value, maximum, fieldName);

        assertThat(result).isNull();
    }

    @Test
    void requireAtMostIfPresent_whenDoubleWithinMaximum_shouldReturnValue() {
        final Double value = 100.0;
        final double maximum = 100.0;
        final String fieldName = "percentage";

        final Double result = ValidationUtils.requireAtMostIfPresent(value, maximum, fieldName);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireAtMostIfPresent_whenDoubleExceedsMaximum_shouldThrow() {
        final Double value = 101.0;
        final double maximum = 100.0;
        final String fieldName = "percentage";

        assertThatThrownBy(() -> ValidationUtils.requireAtMostIfPresent(value, maximum, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("percentage must be finite and at most 100.0");
    }

    @Test
    void requireAtMostIfPresent_whenDoubleNull_shouldReturnNull() {
        final Double value = null;
        final double maximum = 100.0;
        final String fieldName = "percentage";

        final Double result = ValidationUtils.requireAtMostIfPresent(value, maximum, fieldName);

        assertThat(result).isNull();
    }

    @Test
    void requireBetweenIfPresent_whenIntegerWithinBounds_shouldReturnValue() {
        final Integer value = 50;
        final int minimum = 0;
        final int maximum = 100;
        final String fieldName = "count";

        final Integer result = ValidationUtils.requireBetweenIfPresent(value, minimum, maximum, fieldName);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireBetweenIfPresent_whenIntegerOutOfBounds_shouldThrow() {
        final Integer value = 101;
        final int minimum = 0;
        final int maximum = 100;
        final String fieldName = "count";

        assertThatThrownBy(() -> ValidationUtils.requireBetweenIfPresent(value, minimum, maximum, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("count must be between 0 and 100");
    }

    @Test
    void requireBetweenIfPresent_whenIntegerNull_shouldReturnNull() {
        final Integer value = null;
        final int minimum = 0;
        final int maximum = 100;
        final String fieldName = "count";

        final Integer result = ValidationUtils.requireBetweenIfPresent(value, minimum, maximum, fieldName);

        assertThat(result).isNull();
    }

    @Test
    void requireBetweenIfPresent_whenLongWithinBounds_shouldReturnValue() {
        final Long value = 50L;
        final long minimum = 0L;
        final long maximum = 100L;
        final String fieldName = "id";

        final Long result = ValidationUtils.requireBetweenIfPresent(value, minimum, maximum, fieldName);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireBetweenIfPresent_whenLongOutOfBounds_shouldThrow() {
        final Long value = -1L;
        final long minimum = 0L;
        final long maximum = 100L;
        final String fieldName = "id";

        assertThatThrownBy(() -> ValidationUtils.requireBetweenIfPresent(value, minimum, maximum, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("id must be between 0 and 100");
    }

    @Test
    void requireBetweenIfPresent_whenLongNull_shouldReturnNull() {
        final Long value = null;
        final long minimum = 0L;
        final long maximum = 100L;
        final String fieldName = "id";

        final Long result = ValidationUtils.requireBetweenIfPresent(value, minimum, maximum, fieldName);

        assertThat(result).isNull();
    }

    @Test
    void requireBetweenIfPresent_whenDoubleWithinBounds_shouldReturnValue() {
        final Double value = 50.0;
        final double minimum = 0.0;
        final double maximum = 100.0;
        final String fieldName = "percentage";

        final Double result = ValidationUtils.requireBetweenIfPresent(value, minimum, maximum, fieldName);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireBetweenIfPresent_whenDoubleOutOfBounds_shouldThrow() {
        final Double value = 101.0;
        final double minimum = 0.0;
        final double maximum = 100.0;
        final String fieldName = "percentage";

        assertThatThrownBy(() -> ValidationUtils.requireBetweenIfPresent(value, minimum, maximum, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("percentage must be finite and between 0.0 and 100.0");
    }

    @Test
    void requireBetweenIfPresent_whenDoubleNull_shouldReturnNull() {
        final Double value = null;
        final double minimum = 0.0;
        final double maximum = 100.0;
        final String fieldName = "percentage";

        final Double result = ValidationUtils.requireBetweenIfPresent(value, minimum, maximum, fieldName);

        assertThat(result).isNull();
    }

    @Test
    void requireMatches_whenValueNull_shouldThrow() {
        final String value = null;
        final Pattern pattern = Pattern.compile("^ssh-rsa ");
        final String fieldName = "key";

        assertThatThrownBy(() -> ValidationUtils.requireMatches(value, pattern, fieldName))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("key must not be null");
    }

    @Test
    void requireMatches_whenPatternNull_shouldThrow() {
        final String value = "ssh-rsa AAA";
        final Pattern pattern = null;
        final String fieldName = "key";

        assertThatThrownBy(() -> ValidationUtils.requireMatches(value, pattern, fieldName))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("pattern must not be null");
    }

    @ParameterizedTest
    @CsvSource({"true,false", "false,true"})
    void requireExactlyOne_whenExactlyOneOfTwoPresent_shouldNotThrow(final boolean first, final boolean second) {
        final String fieldName = "options";

        assertThatCode(() -> ValidationUtils.requireExactlyOne(present(first), present(second), fieldName))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @CsvSource({"false,false", "true,true"})
    void requireExactlyOne_whenNoneOrBothOfTwoPresent_shouldThrow(final boolean first, final boolean second) {
        final String fieldName = "options";

        final Object firstValue = present(first);
        final Object secondValue = present(second);

        final Throwable thrown =
                catchThrowable(() -> ValidationUtils.requireExactlyOne(firstValue, secondValue, fieldName));

        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("options must have exactly one value present");
    }

    @ParameterizedTest
    @CsvSource({"true,false,false", "false,true,false", "false,false,true"})
    void requireExactlyOne_whenExactlyOneOfThreePresent_shouldNotThrow(
            final boolean first, final boolean second, final boolean third) {
        final String fieldName = "options";

        assertThatCode(() ->
                        ValidationUtils.requireExactlyOne(present(first), present(second), present(third), fieldName))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @CsvSource({"false,false,false", "true,true,false", "true,false,true", "false,true,true", "true,true,true"})
    void requireExactlyOne_whenNoneOrManyOfThreePresent_shouldThrow(
            final boolean first, final boolean second, final boolean third) {
        final String fieldName = "options";

        final Object firstValue = present(first);
        final Object secondValue = present(second);
        final Object thirdValue = present(third);

        final Throwable thrown =
                catchThrowable(() -> ValidationUtils.requireExactlyOne(firstValue, secondValue, thirdValue, fieldName));

        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("options must have exactly one value present");
    }

    @ParameterizedTest
    @CsvSource({"false,false", "true,false", "false,true"})
    void requireAtMostOne_whenAtMostOneOfTwoPresent_shouldNotThrow(final boolean first, final boolean second) {
        final String fieldName = "options";

        assertThatCode(() -> ValidationUtils.requireAtMostOne(present(first), present(second), fieldName))
                .doesNotThrowAnyException();
    }

    @Test
    void requireAtMostOne_whenBothOfTwoPresent_shouldThrow() {
        final String fieldName = "options";

        final Object firstValue = present(true);
        final Object secondValue = present(true);

        final Throwable thrown =
                catchThrowable(() -> ValidationUtils.requireAtMostOne(firstValue, secondValue, fieldName));

        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("options must have at most one value present");
    }

    @ParameterizedTest
    @CsvSource({"false,false,false", "true,false,false", "false,true,false", "false,false,true"})
    void requireAtMostOne_whenAtMostOneOfThreePresent_shouldNotThrow(
            final boolean first, final boolean second, final boolean third) {
        final String fieldName = "options";

        assertThatCode(() ->
                        ValidationUtils.requireAtMostOne(present(first), present(second), present(third), fieldName))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @CsvSource({"true,true,false", "true,false,true", "false,true,true", "true,true,true"})
    void requireAtMostOne_whenManyOfThreePresent_shouldThrow(
            final boolean first, final boolean second, final boolean third) {
        final String fieldName = "options";

        final Object firstValue = present(first);
        final Object secondValue = present(second);
        final Object thirdValue = present(third);

        final Throwable thrown =
                catchThrowable(() -> ValidationUtils.requireAtMostOne(firstValue, secondValue, thirdValue, fieldName));

        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("options must have at most one value present");
    }

    @ParameterizedTest
    @CsvSource({"true,false", "false,true", "true,true"})
    void requireAtLeastOne_whenAnyOfTwoPresent_shouldNotThrow(final boolean first, final boolean second) {
        final String fieldName = "options";

        assertThatCode(() -> ValidationUtils.requireAtLeastOne(present(first), present(second), fieldName))
                .doesNotThrowAnyException();
    }

    @Test
    void requireAtLeastOne_whenNoneOfTwoPresent_shouldThrow() {
        final String fieldName = "options";

        final Object firstValue = present(false);
        final Object secondValue = present(false);

        final Throwable thrown =
                catchThrowable(() -> ValidationUtils.requireAtLeastOne(firstValue, secondValue, fieldName));

        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("options must have at least one value present");
    }

    @ParameterizedTest
    @CsvSource({
        "true,false,false",
        "false,true,false",
        "false,false,true",
        "true,true,false",
        "true,false,true",
        "false,true,true",
        "true,true,true"
    })
    void requireAtLeastOne_whenAnyOfThreePresent_shouldNotThrow(
            final boolean first, final boolean second, final boolean third) {
        final String fieldName = "options";

        assertThatCode(() ->
                        ValidationUtils.requireAtLeastOne(present(first), present(second), present(third), fieldName))
                .doesNotThrowAnyException();
    }

    @Test
    void requireAtLeastOne_whenNoneOfThreePresent_shouldThrow() {
        final String fieldName = "options";

        final Object firstValue = present(false);
        final Object secondValue = present(false);
        final Object thirdValue = present(false);

        final Throwable thrown =
                catchThrowable(() -> ValidationUtils.requireAtLeastOne(firstValue, secondValue, thirdValue, fieldName));

        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("options must have at least one value present");
    }

    @ParameterizedTest
    @CsvSource({"false,false", "true,true"})
    void requireBothOrNeither_whenBothOrNeitherPresent_shouldNotThrow(final boolean first, final boolean second) {
        final String fieldName = "options";

        assertThatCode(() -> ValidationUtils.requireBothOrNeither(present(first), present(second), fieldName))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @CsvSource({"true,false", "false,true"})
    void requireBothOrNeither_whenOnlyOnePresent_shouldThrow(final boolean first, final boolean second) {
        final String fieldName = "options";

        final Object firstValue = present(first);
        final Object secondValue = present(second);

        final Throwable thrown =
                catchThrowable(() -> ValidationUtils.requireBothOrNeither(firstValue, secondValue, fieldName));

        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("options must have both values present or both absent");
    }

    @Test
    void requireExactlyOne_whenFalseBooleanPassed_shouldCountAsPresent() {
        final String fieldName = "options";

        assertThatThrownBy(() -> ValidationUtils.requireExactlyOne(Boolean.FALSE, Boolean.FALSE, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("options must have exactly one value present");
    }

    @Test
    void requirePositive_whenIntPositive_shouldReturnValue() {
        final int value = 1;
        final String fieldName = "value";

        final int result = ValidationUtils.requirePositive(value, fieldName);

        assertThat(result).isEqualTo(value);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void requirePositive_whenIntZeroOrNegative_shouldThrow(final int value) {
        final String fieldName = "value";

        assertThatThrownBy(() -> ValidationUtils.requirePositive(value, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be positive");
    }

    @Test
    void requirePositive_whenLongPositive_shouldReturnValue() {
        final long value = 1L;
        final String fieldName = "value";

        final long result = ValidationUtils.requirePositive(value, fieldName);

        assertThat(result).isEqualTo(value);
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L})
    void requirePositive_whenLongZeroOrNegative_shouldThrow(final long value) {
        final String fieldName = "value";

        assertThatThrownBy(() -> ValidationUtils.requirePositive(value, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be positive");
    }

    @Test
    void requireNonEmpty_whenArrayNonEmpty_shouldReturnSameArray() {
        final String[] values = {"a"};
        final String fieldName = "values";

        final String[] result = ValidationUtils.requireNonEmpty(values, fieldName);

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireNonEmpty_whenArrayEmpty_shouldThrow() {
        final String[] values = {};
        final String fieldName = "values";

        assertThatThrownBy(() -> ValidationUtils.requireNonEmpty(values, fieldName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("values must not be empty");
    }

    @Test
    void requireNonEmpty_whenArrayNull_shouldThrow() {
        final String[] values = null;
        final String fieldName = "values";

        assertThatThrownBy(() -> ValidationUtils.requireNonEmpty(values, fieldName))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("values must not be null");
    }

    private static @Nullable Object present(final boolean present) {
        return present ? "value" : null;
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
