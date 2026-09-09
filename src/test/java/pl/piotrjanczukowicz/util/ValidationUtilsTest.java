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
        final String message = "perPage must be at most 100";

        final int result = ValidationUtils.requireAtMost(value, maximum, message);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireAtMost_shouldThrow_whenValueExceedsMaximum() {
        final int value = 101;
        final int maximum = 100;
        final String message = "perPage must be at most 100";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireAtMost(value, maximum, message));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage(message);
    }

    @Test
    void requireAtLeast_shouldReturnValue_whenWithinMinimum() {
        final double value = 0.0;
        final double minimum = 0.0;
        final String message = "percentage must be at least 0";

        final double result = ValidationUtils.requireAtLeast(value, minimum, message);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireAtLeast_shouldThrow_whenValueBelowMinimum() {
        final double value = -1.0;
        final double minimum = 0.0;
        final String message = "percentage must be at least 0";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireAtLeast(value, minimum, message));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage(message);
    }

    @Test
    void requireAtMost_shouldReturnValue_whenWithinMaximumForDouble() {
        final double value = 100.0;
        final double maximum = 100.0;
        final String message = "percentage must be at most 100";

        final double result = ValidationUtils.requireAtMost(value, maximum, message);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireAtMost_shouldThrow_whenDoubleValueExceedsMaximum() {
        final double value = 101.0;
        final double maximum = 100.0;
        final String message = "percentage must be at most 100";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireAtMost(value, maximum, message));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage(message);
    }

    @Test
    void requireAtMost_shouldThrow_whenDoubleValueIsNaN() {
        final double value = Double.NaN;
        final double maximum = 100.0;
        final String message = "percentage must be at most 100";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireAtMost(value, maximum, message));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage(message);
    }

    @Test
    void requireAtMost_shouldThrow_whenDoubleValueIsInfinite() {
        final double value = Double.POSITIVE_INFINITY;
        final double maximum = 100.0;
        final String message = "percentage must be at most 100";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireAtMost(value, maximum, message));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage(message);
    }

    @Test
    void requireBetween_shouldReturnValue_whenWithinBounds() {
        final double value = 50.0;
        final double minimum = 0.0;
        final double maximum = 100.0;
        final String message = "percentage must be between 0 and 100";

        final double result = ValidationUtils.requireBetween(value, minimum, maximum, message);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireBetween_shouldThrow_whenValueBelowMinimum() {
        final double value = -1.0;
        final double minimum = 0.0;
        final double maximum = 100.0;
        final String message = "percentage must be between 0 and 100";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireBetween(value, minimum, maximum, message));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage(message);
    }

    @Test
    void requireBetween_shouldThrow_whenValueAboveMaximum() {
        final double value = 101.0;
        final double minimum = 0.0;
        final double maximum = 100.0;
        final String message = "percentage must be between 0 and 100";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireBetween(value, minimum, maximum, message));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage(message);
    }

    @Test
    void requireBetween_shouldThrow_whenValueIsNaN() {
        final double value = Double.NaN;
        final double minimum = 0.0;
        final double maximum = 100.0;
        final String message = "percentage must be between 0 and 100";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireBetween(value, minimum, maximum, message));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage(message);
    }

    @Test
    void requireBetween_shouldThrow_whenValueIsInfinite() {
        final double value = Double.POSITIVE_INFINITY;
        final double minimum = 0.0;
        final double maximum = 100.0;
        final String message = "percentage must be between 0 and 100";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireBetween(value, minimum, maximum, message));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage(message);
    }

    @Test
    void requireNotBlank_shouldReturnValue_whenNonBlank() {
        final String value = "token";
        final String message = "token must not be blank";

        final String result = ValidationUtils.requireNotBlank(value, message);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireNotBlank_shouldThrow_whenNull() {
        final String value = null;
        final String message = "token must not be blank";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireNotBlank(value, message));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessage(message);
    }

    @Test
    void requireNotBlank_shouldThrow_whenBlank() {
        final String value = "  ";
        final String message = "token must not be blank";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireNotBlank(value, message));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage(message);
    }

    @Test
    void requireSizeAtMost_shouldReturnValue_whenWithinMaximum() {
        final List<String> values = List.of("a", "b");
        final int maximum = 2;
        final String message = "items must contain at most 2 entries";

        final List<String> result = ValidationUtils.requireSizeAtMost(values, maximum, message);

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireSizeAtMost_shouldThrow_whenSizeExceedsMaximum() {
        final List<String> values = List.of("a", "b", "c");
        final int maximum = 2;
        final String message = "items must contain at most 2 entries";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireSizeAtMost(values, maximum, message));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage(message);
    }

    @Test
    void requireSizeAtLeast_shouldReturnValue_whenWithinMinimum() {
        final List<String> values = List.of("a", "b");
        final int minimum = 2;
        final String message = "items must contain at least 2 entries";

        final List<String> result = ValidationUtils.requireSizeAtLeast(values, minimum, message);

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireSizeAtLeast_shouldThrow_whenSizeBelowMinimum() {
        final List<String> values = List.of("a");
        final int minimum = 2;
        final String message = "items must contain at least 2 entries";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireSizeAtLeast(values, minimum, message));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage(message);
    }

    @Test
    void requireSizeExactly_shouldReturnValue_whenSizeMatches() {
        final List<String> values = List.of("a");
        final int size = 1;
        final String message = "items must contain exactly 1 entry";

        final List<String> result = ValidationUtils.requireSizeExactly(values, size, message);

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireSizeExactly_shouldThrow_whenNull() {
        final List<String> values = null;
        final int size = 1;
        final String message = "items must contain exactly 1 entry";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireSizeExactly(values, size, message));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessage(message);
    }

    @Test
    void requireSizeExactly_shouldThrow_whenSizeDiffers() {
        final List<String> values = List.of("a", "b");
        final int size = 1;
        final String message = "items must contain exactly 1 entry";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireSizeExactly(values, size, message));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage(message);
    }

    @Test
    void requireSizeBetween_shouldReturnValue_whenWithinBounds() {
        final List<String> values = List.of("a", "b");
        final int minimum = 2;
        final int maximum = 100;
        final String message = "items must contain between 2 and 100 entries";

        final List<String> result = ValidationUtils.requireSizeBetween(values, minimum, maximum, message);

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireSizeBetween_shouldThrow_whenNull() {
        final List<String> values = null;
        final int minimum = 2;
        final int maximum = 100;
        final String message = "items must contain between 2 and 100 entries";

        final Throwable thrown =
                catchThrowable(() -> ValidationUtils.requireSizeBetween(values, minimum, maximum, message));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessage(message);
    }

    @Test
    void requireSizeBetween_shouldThrow_whenSizeBelowMinimum() {
        final List<String> values = List.of("a");
        final int minimum = 2;
        final int maximum = 100;
        final String message = "items must contain between 2 and 100 entries";

        final Throwable thrown =
                catchThrowable(() -> ValidationUtils.requireSizeBetween(values, minimum, maximum, message));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage(message);
    }

    @Test
    void requireSizeBetween_shouldThrow_whenSizeExceedsMaximum() {
        final List<String> values = List.of("a", "b", "c");
        final int minimum = 1;
        final int maximum = 2;
        final String message = "items must contain between 1 and 2 entries";

        final Throwable thrown =
                catchThrowable(() -> ValidationUtils.requireSizeBetween(values, minimum, maximum, message));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage(message);
    }

    @Test
    void requireMaxLength_shouldReturnValue_whenWithinMaximum() {
        final String value = "hi";
        final int maximum = 2;
        final String message = "summary must be at most 2 characters";

        final String result = ValidationUtils.requireMaxLength(value, maximum, message);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireMaxLength_shouldThrow_whenLongerThanMaximum() {
        final String value = "hey";
        final int maximum = 2;
        final String message = "summary must be at most 2 characters";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireMaxLength(value, maximum, message));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage(message);
    }

    @Test
    void requireMaxLengthIfPresent_shouldReturnValue_whenWithinMaximum() {
        final String value = "hi";
        final int maximum = 2;
        final String message = "summary must be at most 2 characters";

        final String result = ValidationUtils.requireMaxLengthIfPresent(value, maximum, message);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireMaxLengthIfPresent_shouldThrow_whenLongerThanMaximum() {
        final String value = "hey";
        final int maximum = 2;
        final String message = "summary must be at most 2 characters";

        final Throwable thrown =
                catchThrowable(() -> ValidationUtils.requireMaxLengthIfPresent(value, maximum, message));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage(message);
    }

    @Test
    void requireMatches_shouldReturnValue_whenPatternMatches() {
        final String value = "ssh-rsa AAA";
        final Pattern pattern = Pattern.compile("^ssh-rsa ");
        final String message = "key must be an ssh-rsa public key";

        final String result = ValidationUtils.requireMatches(value, pattern, message);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireMatches_shouldThrow_whenPatternDoesNotMatch() {
        final String value = "not-a-key";
        final Pattern pattern = Pattern.compile("^ssh-rsa ");
        final String message = "key must be an ssh-rsa public key";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireMatches(value, pattern, message));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage(message);
    }

    @Test
    void requirePositive_shouldReturnDuration_whenPositive() {
        final Duration duration = Duration.ofMillis(1);
        final String message = "timeout must be positive";

        final Duration result = ValidationUtils.requirePositive(duration, message);

        assertThat(result).isSameAs(duration);
    }

    @Test
    void requirePositive_shouldThrow_whenNull() {
        final Duration duration = null;
        final String message = "timeout must be positive";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requirePositive(duration, message));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessage(message);
    }

    @ParameterizedTest
    @MethodSource("zeroAndNegativeDurations")
    void requirePositive_shouldThrow_whenZeroOrNegative(final Duration duration) {
        final String message = "timeout must be positive";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requirePositive(duration, message));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage(message);
    }

    @ParameterizedTest
    @MethodSource("zeroAndPositiveDurations")
    void requireNonNegative_shouldReturnDuration_whenZeroOrPositive(final Duration duration) {
        final String message = "delay must not be negative";

        final Duration result = ValidationUtils.requireNonNegative(duration, message);

        assertThat(result).isEqualTo(duration);
    }

    @Test
    void requireNonNegative_shouldThrow_whenNull() {
        final Duration duration = null;
        final String message = "delay must not be negative";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireNonNegative(duration, message));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessage(message);
    }

    @Test
    void requireNonNegative_shouldThrow_whenNegative() {
        final Duration duration = Duration.ofSeconds(-1);
        final String message = "delay must not be negative";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireNonNegative(duration, message));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage(message);
    }

    @Test
    void requireAtLeast_shouldReturnInt_whenWithinMinimum() {
        final int value = 2;
        final int minimum = 2;
        final String message = "count must be at least 2";

        final int result = ValidationUtils.requireAtLeast(value, minimum, message);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireAtLeast_shouldThrow_whenIntBelowMinimum() {
        final int value = 1;
        final int minimum = 2;
        final String message = "count must be at least 2";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireAtLeast(value, minimum, message));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage(message);
    }

    @Test
    void requireAtLeast_shouldThrow_whenDoubleValueIsNaN() {
        final double value = Double.NaN;
        final double minimum = 0.0;
        final String message = "percentage must be at least 0";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireAtLeast(value, minimum, message));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage(message);
    }

    @Test
    void requireAtLeast_shouldThrow_whenDoubleValueIsInfinite() {
        final double value = Double.NEGATIVE_INFINITY;
        final double minimum = 0.0;
        final String message = "percentage must be at least 0";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireAtLeast(value, minimum, message));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage(message);
    }

    @Test
    void requireGreaterOrEqual_shouldPass_whenValueAtLeastMinimum() {
        final Duration value = Duration.ofSeconds(2);
        final Duration minimum = Duration.ofSeconds(2);
        final String message = "wait must be at least PT2S";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireGreaterOrEqual(value, minimum, message));

        assertThat(thrown).isNull();
    }

    @Test
    void requireGreaterOrEqual_shouldThrow_whenValueBelowMinimum() {
        final Duration value = Duration.ofSeconds(1);
        final Duration minimum = Duration.ofSeconds(2);
        final String message = "wait must be at least PT2S";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireGreaterOrEqual(value, minimum, message));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage(message);
    }

    @Test
    void requireGreaterOrEqual_shouldThrow_whenValueNull() {
        final Duration value = null;
        final Duration minimum = Duration.ZERO;
        final String message = "wait must be at least PT0S";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireGreaterOrEqual(value, minimum, message));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessage(message);
    }

    @Test
    void requireGreaterOrEqual_shouldThrow_whenMinimumNull() {
        final Duration value = Duration.ZERO;
        final Duration minimum = null;
        final String message = "wait must be at least PT0S";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireGreaterOrEqual(value, minimum, message));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessage("minimum must not be null");
    }

    @Test
    void requireNonEmpty_shouldReturnValue_whenNonEmpty() {
        final List<String> values = List.of("a");
        final String message = "items must not be empty";

        final List<String> result = ValidationUtils.requireNonEmpty(values, message);

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireNonEmpty_shouldThrow_whenEmpty() {
        final List<String> values = List.of();
        final String message = "items must not be empty";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireNonEmpty(values, message));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage(message);
    }

    @Test
    void requireNonEmpty_shouldThrow_whenNull() {
        final List<String> values = null;
        final String message = "items must not be empty";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireNonEmpty(values, message));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessage(message);
    }

    @Test
    void requireSizeAtMost_shouldThrow_whenNull() {
        final List<String> values = null;
        final int maximum = 2;
        final String message = "items must contain at most 2 entries";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireSizeAtMost(values, maximum, message));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessage(message);
    }

    @Test
    void requireSizeAtLeast_shouldThrow_whenNull() {
        final List<String> values = null;
        final int minimum = 1;
        final String message = "items must contain at least 1 entry";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireSizeAtLeast(values, minimum, message));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessage(message);
    }

    @Test
    void requireUniqueItems_shouldReturnValue_whenUnique() {
        final List<String> values = List.of("a", "b");
        final String message = "items must not contain duplicates";

        final List<String> result = ValidationUtils.requireUniqueItems(values, message);

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireUniqueItems_shouldThrow_whenDuplicates() {
        final List<String> values = List.of("a", "a");
        final String message = "items must not contain duplicates";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireUniqueItems(values, message));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage(message);
    }

    @Test
    void requireUniqueItems_shouldThrow_whenNull() {
        final List<String> values = null;
        final String message = "items must not contain duplicates";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireUniqueItems(values, message));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessage(message);
    }

    @Test
    void requireNonEmpty_shouldReturnMap_whenNonEmpty() {
        final Map<String, String> values = Map.of("a", "1");
        final String message = "props must not be empty";

        final Map<String, String> result = ValidationUtils.requireNonEmpty(values, message);

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireNonEmpty_shouldThrow_whenMapEmpty() {
        final Map<String, String> values = Map.of();
        final String message = "props must not be empty";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireNonEmpty(values, message));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage(message);
    }

    @Test
    void requireNonEmpty_shouldThrow_whenMapNull() {
        final Map<String, String> values = null;
        final String message = "props must not be empty";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireNonEmpty(values, message));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessage(message);
    }

    @Test
    void requireSizeAtMost_shouldReturnMap_whenWithinMaximum() {
        final Map<String, String> values = Map.of("a", "1");
        final int maximum = 1;
        final String message = "props must contain at most 1 property";

        final Map<String, String> result = ValidationUtils.requireSizeAtMost(values, maximum, message);

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireSizeAtMost_shouldThrow_whenMapSizeExceedsMaximum() {
        final Map<String, String> values = Map.of("a", "1", "b", "2");
        final int maximum = 1;
        final String message = "props must contain at most 1 property";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireSizeAtMost(values, maximum, message));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage(message);
    }

    @Test
    void requireSizeAtMost_shouldThrow_whenMapNull() {
        final Map<String, String> values = null;
        final int maximum = 1;
        final String message = "props must contain at most 1 property";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireSizeAtMost(values, maximum, message));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessage(message);
    }

    @Test
    void requireSizeAtLeast_shouldReturnMap_whenWithinMinimum() {
        final Map<String, String> values = Map.of("a", "1", "b", "2");
        final int minimum = 2;
        final String message = "props must contain at least 2 properties";

        final Map<String, String> result = ValidationUtils.requireSizeAtLeast(values, minimum, message);

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireSizeAtLeast_shouldThrow_whenMapSizeBelowMinimum() {
        final Map<String, String> values = Map.of("a", "1");
        final int minimum = 2;
        final String message = "props must contain at least 2 properties";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireSizeAtLeast(values, minimum, message));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage(message);
    }

    @Test
    void requireSizeAtLeast_shouldThrow_whenMapNull() {
        final Map<String, String> values = null;
        final int minimum = 1;
        final String message = "props must contain at least 1 property";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireSizeAtLeast(values, minimum, message));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessage(message);
    }

    @Test
    void requireSizeExactly_shouldReturnMap_whenSizeMatches() {
        final Map<String, String> values = Map.of("a", "1");
        final int size = 1;
        final String message = "props must contain exactly 1 property";

        final Map<String, String> result = ValidationUtils.requireSizeExactly(values, size, message);

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireSizeExactly_shouldThrow_whenMapNull() {
        final Map<String, String> values = null;
        final int size = 1;
        final String message = "props must contain exactly 1 property";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireSizeExactly(values, size, message));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessage(message);
    }

    @Test
    void requireSizeExactly_shouldThrow_whenMapSizeDiffers() {
        final Map<String, String> values = Map.of("a", "1", "b", "2");
        final int size = 1;
        final String message = "props must contain exactly 1 property";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireSizeExactly(values, size, message));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage(message);
    }

    @Test
    void requireSizeBetween_shouldReturnMap_whenWithinBounds() {
        final Map<String, String> values = Map.of("a", "1", "b", "2");
        final int minimum = 2;
        final int maximum = 100;
        final String message = "props must contain between 2 and 100 properties";

        final Map<String, String> result = ValidationUtils.requireSizeBetween(values, minimum, maximum, message);

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireSizeBetween_shouldThrow_whenMapNull() {
        final Map<String, String> values = null;
        final int minimum = 2;
        final int maximum = 100;
        final String message = "props must contain between 2 and 100 properties";

        final Throwable thrown =
                catchThrowable(() -> ValidationUtils.requireSizeBetween(values, minimum, maximum, message));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessage(message);
    }

    @Test
    void requireSizeBetween_shouldThrow_whenMapSizeBelowMinimum() {
        final Map<String, String> values = Map.of("a", "1");
        final int minimum = 2;
        final int maximum = 100;
        final String message = "props must contain between 2 and 100 properties";

        final Throwable thrown =
                catchThrowable(() -> ValidationUtils.requireSizeBetween(values, minimum, maximum, message));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage(message);
    }

    @Test
    void requireSizeBetween_shouldThrow_whenMapSizeExceedsMaximum() {
        final Map<String, String> values = Map.of("a", "1", "b", "2", "c", "3");
        final int minimum = 1;
        final int maximum = 2;
        final String message = "props must contain between 1 and 2 properties";

        final Throwable thrown =
                catchThrowable(() -> ValidationUtils.requireSizeBetween(values, minimum, maximum, message));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage(message);
    }

    @Test
    void requireMaxLengthIfPresent_shouldReturnNull_whenValueNull() {
        final String value = null;
        final int maximum = 2;
        final String message = "summary must be at most 2 characters";

        final String result = ValidationUtils.requireMaxLengthIfPresent(value, maximum, message);

        assertThat(result).isNull();
    }

    @Test
    void requireMatches_shouldThrow_whenValueNull() {
        final String value = null;
        final Pattern pattern = Pattern.compile(".");
        final String message = "key must be an ssh-rsa public key";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireMatches(value, pattern, message));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessage(message);
    }

    @Test
    void requireMatches_shouldThrow_whenPatternNull() {
        final String value = "ssh-rsa AAA";
        final Pattern pattern = null;
        final String message = "key must be an ssh-rsa public key";

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireMatches(value, pattern, message));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessage("pattern must not be null");
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
