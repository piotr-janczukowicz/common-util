package pl.piotrjanczukowicz.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        final String message = "perPage must be at most 100";

        final int result = ValidationUtils.requireAtMost(value, maximum, message);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireAtMost_whenValueExceedsMaximum_shouldThrow() {
        final int value = 101;
        final int maximum = 100;
        final String message = "perPage must be at most 100";

        assertThatThrownBy(() -> ValidationUtils.requireAtMost(value, maximum, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireAtLeast_whenWithinMinimum_shouldReturnValue() {
        final double value = 0.0;
        final double minimum = 0.0;
        final String message = "percentage must be at least 0";

        final double result = ValidationUtils.requireAtLeast(value, minimum, message);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireAtLeast_whenValueBelowMinimum_shouldThrow() {
        final double value = -1.0;
        final double minimum = 0.0;
        final String message = "percentage must be at least 0";

        assertThatThrownBy(() -> ValidationUtils.requireAtLeast(value, minimum, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireAtMost_whenWithinMaximumForDouble_shouldReturnValue() {
        final double value = 100.0;
        final double maximum = 100.0;
        final String message = "percentage must be at most 100";

        final double result = ValidationUtils.requireAtMost(value, maximum, message);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireAtMost_whenDoubleValueExceedsMaximum_shouldThrow() {
        final double value = 101.0;
        final double maximum = 100.0;
        final String message = "percentage must be at most 100";

        assertThatThrownBy(() -> ValidationUtils.requireAtMost(value, maximum, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireAtMost_whenDoubleValueIsNaN_shouldThrow() {
        final double value = Double.NaN;
        final double maximum = 100.0;
        final String message = "percentage must be at most 100";

        assertThatThrownBy(() -> ValidationUtils.requireAtMost(value, maximum, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireAtMost_whenDoubleValueIsInfinite_shouldThrow() {
        final double value = Double.POSITIVE_INFINITY;
        final double maximum = 100.0;
        final String message = "percentage must be at most 100";

        assertThatThrownBy(() -> ValidationUtils.requireAtMost(value, maximum, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireBetween_whenWithinBounds_shouldReturnValue() {
        final double value = 50.0;
        final double minimum = 0.0;
        final double maximum = 100.0;
        final String message = "percentage must be between 0 and 100";

        final double result = ValidationUtils.requireBetween(value, minimum, maximum, message);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireBetween_whenValueBelowMinimum_shouldThrow() {
        final double value = -1.0;
        final double minimum = 0.0;
        final double maximum = 100.0;
        final String message = "percentage must be between 0 and 100";

        assertThatThrownBy(() -> ValidationUtils.requireBetween(value, minimum, maximum, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireBetween_whenValueAboveMaximum_shouldThrow() {
        final double value = 101.0;
        final double minimum = 0.0;
        final double maximum = 100.0;
        final String message = "percentage must be between 0 and 100";

        assertThatThrownBy(() -> ValidationUtils.requireBetween(value, minimum, maximum, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireBetween_whenValueIsNaN_shouldThrow() {
        final double value = Double.NaN;
        final double minimum = 0.0;
        final double maximum = 100.0;
        final String message = "percentage must be between 0 and 100";

        assertThatThrownBy(() -> ValidationUtils.requireBetween(value, minimum, maximum, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireBetween_whenValueIsInfinite_shouldThrow() {
        final double value = Double.POSITIVE_INFINITY;
        final double minimum = 0.0;
        final double maximum = 100.0;
        final String message = "percentage must be between 0 and 100";

        assertThatThrownBy(() -> ValidationUtils.requireBetween(value, minimum, maximum, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
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
        final String message = "token must not be blank";

        final String result = ValidationUtils.requireNotBlank(value, message);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireNotBlank_whenNull_shouldThrow() {
        final String value = null;
        final String message = "token must not be blank";

        assertThatThrownBy(() -> ValidationUtils.requireNotBlank(value, message))
                .isInstanceOf(NullPointerException.class)
                .hasMessage(message);
    }

    @Test
    void requireNotBlank_whenBlank_shouldThrow() {
        final String value = "  ";
        final String message = "token must not be blank";

        assertThatThrownBy(() -> ValidationUtils.requireNotBlank(value, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireSizeAtMost_whenWithinMaximum_shouldReturnValue() {
        final List<String> values = List.of("a", "b");
        final int maximum = 2;
        final String message = "items must contain at most 2 entries";

        final List<String> result = ValidationUtils.requireSizeAtMost(values, maximum, message);

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireSizeAtMost_whenSizeExceedsMaximum_shouldThrow() {
        final List<String> values = List.of("a", "b", "c");
        final int maximum = 2;
        final String message = "items must contain at most 2 entries";

        assertThatThrownBy(() -> ValidationUtils.requireSizeAtMost(values, maximum, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireSizeAtLeast_whenWithinMinimum_shouldReturnValue() {
        final List<String> values = List.of("a", "b");
        final int minimum = 2;
        final String message = "items must contain at least 2 entries";

        final List<String> result = ValidationUtils.requireSizeAtLeast(values, minimum, message);

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireSizeAtLeast_whenSizeBelowMinimum_shouldThrow() {
        final List<String> values = List.of("a");
        final int minimum = 2;
        final String message = "items must contain at least 2 entries";

        assertThatThrownBy(() -> ValidationUtils.requireSizeAtLeast(values, minimum, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireSizeExactly_whenSizeMatches_shouldReturnValue() {
        final List<String> values = List.of("a");
        final int size = 1;
        final String message = "items must contain exactly 1 entry";

        final List<String> result = ValidationUtils.requireSizeExactly(values, size, message);

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireSizeExactly_whenNull_shouldThrow() {
        final List<String> values = null;
        final int size = 1;
        final String message = "items must contain exactly 1 entry";

        assertThatThrownBy(() -> ValidationUtils.requireSizeExactly(values, size, message))
                .isInstanceOf(NullPointerException.class)
                .hasMessage(message);
    }

    @Test
    void requireSizeExactly_whenSizeDiffers_shouldThrow() {
        final List<String> values = List.of("a", "b");
        final int size = 1;
        final String message = "items must contain exactly 1 entry";

        assertThatThrownBy(() -> ValidationUtils.requireSizeExactly(values, size, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireSizeBetween_whenWithinBounds_shouldReturnValue() {
        final List<String> values = List.of("a", "b");
        final int minimum = 2;
        final int maximum = 100;
        final String message = "items must contain between 2 and 100 entries";

        final List<String> result = ValidationUtils.requireSizeBetween(values, minimum, maximum, message);

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireSizeBetween_whenNull_shouldThrow() {
        final List<String> values = null;
        final int minimum = 2;
        final int maximum = 100;
        final String message = "items must contain between 2 and 100 entries";

        assertThatThrownBy(() -> ValidationUtils.requireSizeBetween(values, minimum, maximum, message))
                .isInstanceOf(NullPointerException.class)
                .hasMessage(message);
    }

    @Test
    void requireSizeBetween_whenSizeBelowMinimum_shouldThrow() {
        final List<String> values = List.of("a");
        final int minimum = 2;
        final int maximum = 100;
        final String message = "items must contain between 2 and 100 entries";

        assertThatThrownBy(() -> ValidationUtils.requireSizeBetween(values, minimum, maximum, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireSizeBetween_whenSizeExceedsMaximum_shouldThrow() {
        final List<String> values = List.of("a", "b", "c");
        final int minimum = 1;
        final int maximum = 2;
        final String message = "items must contain between 1 and 2 entries";

        assertThatThrownBy(() -> ValidationUtils.requireSizeBetween(values, minimum, maximum, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireMaxLength_whenWithinMaximum_shouldReturnValue() {
        final String value = "hi";
        final int maximum = 2;
        final String message = "summary must be at most 2 characters";

        final String result = ValidationUtils.requireMaxLength(value, maximum, message);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireMaxLength_whenLongerThanMaximum_shouldThrow() {
        final String value = "hey";
        final int maximum = 2;
        final String message = "summary must be at most 2 characters";

        assertThatThrownBy(() -> ValidationUtils.requireMaxLength(value, maximum, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireMaxLengthIfPresent_whenWithinMaximum_shouldReturnValue() {
        final String value = "hi";
        final int maximum = 2;
        final String message = "summary must be at most 2 characters";

        final String result = ValidationUtils.requireMaxLengthIfPresent(value, maximum, message);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireMaxLengthIfPresent_whenLongerThanMaximum_shouldThrow() {
        final String value = "hey";
        final int maximum = 2;
        final String message = "summary must be at most 2 characters";

        assertThatThrownBy(() -> ValidationUtils.requireMaxLengthIfPresent(value, maximum, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireMatches_whenPatternMatches_shouldReturnValue() {
        final String value = "ssh-rsa AAA";
        final Pattern pattern = Pattern.compile("^ssh-rsa ");
        final String message = "key must be an ssh-rsa public key";

        final String result = ValidationUtils.requireMatches(value, pattern, message);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireMatches_whenPatternDoesNotMatch_shouldThrow() {
        final String value = "not-a-key";
        final Pattern pattern = Pattern.compile("^ssh-rsa ");
        final String message = "key must be an ssh-rsa public key";

        assertThatThrownBy(() -> ValidationUtils.requireMatches(value, pattern, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requirePositive_whenPositive_shouldReturnDuration() {
        final Duration duration = Duration.ofMillis(1);
        final String message = "timeout must be positive";

        final Duration result = ValidationUtils.requirePositive(duration, message);

        assertThat(result).isSameAs(duration);
    }

    @Test
    void requirePositive_whenNull_shouldThrow() {
        final Duration duration = null;
        final String message = "timeout must be positive";

        assertThatThrownBy(() -> ValidationUtils.requirePositive(duration, message))
                .isInstanceOf(NullPointerException.class)
                .hasMessage(message);
    }

    @ParameterizedTest
    @MethodSource("zeroAndNegativeDurations")
    void requirePositive_whenZeroOrNegative_shouldThrow(final Duration duration) {
        final String message = "timeout must be positive";

        assertThatThrownBy(() -> ValidationUtils.requirePositive(duration, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @ParameterizedTest
    @MethodSource("zeroAndPositiveDurations")
    void requireNonNegative_whenZeroOrPositive_shouldReturnDuration(final Duration duration) {
        final String message = "delay must not be negative";

        final Duration result = ValidationUtils.requireNonNegative(duration, message);

        assertThat(result).isEqualTo(duration);
    }

    @Test
    void requireNonNegative_whenNull_shouldThrow() {
        final Duration duration = null;
        final String message = "delay must not be negative";

        assertThatThrownBy(() -> ValidationUtils.requireNonNegative(duration, message))
                .isInstanceOf(NullPointerException.class)
                .hasMessage(message);
    }

    @Test
    void requireNonNegative_whenNegative_shouldThrow() {
        final Duration duration = Duration.ofSeconds(-1);
        final String message = "delay must not be negative";

        assertThatThrownBy(() -> ValidationUtils.requireNonNegative(duration, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireAtLeast_whenWithinMinimum_shouldReturnInt() {
        final int value = 2;
        final int minimum = 2;
        final String message = "count must be at least 2";

        final int result = ValidationUtils.requireAtLeast(value, minimum, message);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireAtLeast_whenIntBelowMinimum_shouldThrow() {
        final int value = 1;
        final int minimum = 2;
        final String message = "count must be at least 2";

        assertThatThrownBy(() -> ValidationUtils.requireAtLeast(value, minimum, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireAtLeast_whenDoubleValueIsNaN_shouldThrow() {
        final double value = Double.NaN;
        final double minimum = 0.0;
        final String message = "percentage must be at least 0";

        assertThatThrownBy(() -> ValidationUtils.requireAtLeast(value, minimum, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireAtLeast_whenDoubleValueIsInfinite_shouldThrow() {
        final double value = Double.NEGATIVE_INFINITY;
        final double minimum = 0.0;
        final String message = "percentage must be at least 0";

        assertThatThrownBy(() -> ValidationUtils.requireAtLeast(value, minimum, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireGreaterOrEqual_whenValueAtLeastMinimum_shouldPass() {
        final Duration value = Duration.ofSeconds(2);
        final Duration minimum = Duration.ofSeconds(2);
        final String message = "wait must be at least PT2S";

        assertThatCode(() -> ValidationUtils.requireGreaterOrEqual(value, minimum, message))
                .doesNotThrowAnyException();
    }

    @Test
    void requireGreaterOrEqual_whenValueBelowMinimum_shouldThrow() {
        final Duration value = Duration.ofSeconds(1);
        final Duration minimum = Duration.ofSeconds(2);
        final String message = "wait must be at least PT2S";

        assertThatThrownBy(() -> ValidationUtils.requireGreaterOrEqual(value, minimum, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireGreaterOrEqual_whenValueNull_shouldThrow() {
        final Duration value = null;
        final Duration minimum = Duration.ZERO;
        final String message = "wait must be at least PT0S";

        assertThatThrownBy(() -> ValidationUtils.requireGreaterOrEqual(value, minimum, message))
                .isInstanceOf(NullPointerException.class)
                .hasMessage(message);
    }

    @Test
    void requireGreaterOrEqual_whenMinimumNull_shouldThrow() {
        final Duration value = Duration.ZERO;
        final Duration minimum = null;
        final String message = "wait must be at least PT0S";

        assertThatThrownBy(() -> ValidationUtils.requireGreaterOrEqual(value, minimum, message))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("minimum must not be null");
    }

    @Test
    void requireNonEmpty_whenNonEmpty_shouldReturnValue() {
        final List<String> values = List.of("a");
        final String message = "items must not be empty";

        final List<String> result = ValidationUtils.requireNonEmpty(values, message);

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireNonEmpty_whenEmpty_shouldThrow() {
        final List<String> values = List.of();
        final String message = "items must not be empty";

        assertThatThrownBy(() -> ValidationUtils.requireNonEmpty(values, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireNonEmpty_whenNull_shouldThrow() {
        final List<String> values = null;
        final String message = "items must not be empty";

        assertThatThrownBy(() -> ValidationUtils.requireNonEmpty(values, message))
                .isInstanceOf(NullPointerException.class)
                .hasMessage(message);
    }

    @Test
    void requireSizeAtMost_whenNull_shouldThrow() {
        final List<String> values = null;
        final int maximum = 2;
        final String message = "items must contain at most 2 entries";

        assertThatThrownBy(() -> ValidationUtils.requireSizeAtMost(values, maximum, message))
                .isInstanceOf(NullPointerException.class)
                .hasMessage(message);
    }

    @Test
    void requireSizeAtLeast_whenNull_shouldThrow() {
        final List<String> values = null;
        final int minimum = 1;
        final String message = "items must contain at least 1 entry";

        assertThatThrownBy(() -> ValidationUtils.requireSizeAtLeast(values, minimum, message))
                .isInstanceOf(NullPointerException.class)
                .hasMessage(message);
    }

    @Test
    void requireUniqueItems_whenUnique_shouldReturnValue() {
        final List<String> values = List.of("a", "b");
        final String message = "items must not contain duplicates";

        final List<String> result = ValidationUtils.requireUniqueItems(values, message);

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireUniqueItems_whenDuplicates_shouldThrow() {
        final List<String> values = List.of("a", "a");
        final String message = "items must not contain duplicates";

        assertThatThrownBy(() -> ValidationUtils.requireUniqueItems(values, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireUniqueItems_whenNull_shouldThrow() {
        final List<String> values = null;
        final String message = "items must not contain duplicates";

        assertThatThrownBy(() -> ValidationUtils.requireUniqueItems(values, message))
                .isInstanceOf(NullPointerException.class)
                .hasMessage(message);
    }

    @Test
    void requireNonEmpty_whenNonEmpty_shouldReturnMap() {
        final Map<String, String> values = Map.of("a", "1");
        final String message = "props must not be empty";

        final Map<String, String> result = ValidationUtils.requireNonEmpty(values, message);

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireNonEmpty_whenMapEmpty_shouldThrow() {
        final Map<String, String> values = Map.of();
        final String message = "props must not be empty";

        assertThatThrownBy(() -> ValidationUtils.requireNonEmpty(values, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireNonEmpty_whenMapNull_shouldThrow() {
        final Map<String, String> values = null;
        final String message = "props must not be empty";

        assertThatThrownBy(() -> ValidationUtils.requireNonEmpty(values, message))
                .isInstanceOf(NullPointerException.class)
                .hasMessage(message);
    }

    @Test
    void requireSizeAtMost_whenWithinMaximum_shouldReturnMap() {
        final Map<String, String> values = Map.of("a", "1");
        final int maximum = 1;
        final String message = "props must contain at most 1 property";

        final Map<String, String> result = ValidationUtils.requireSizeAtMost(values, maximum, message);

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireSizeAtMost_whenMapSizeExceedsMaximum_shouldThrow() {
        final Map<String, String> values = Map.of("a", "1", "b", "2");
        final int maximum = 1;
        final String message = "props must contain at most 1 property";

        assertThatThrownBy(() -> ValidationUtils.requireSizeAtMost(values, maximum, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireSizeAtMost_whenMapNull_shouldThrow() {
        final Map<String, String> values = null;
        final int maximum = 1;
        final String message = "props must contain at most 1 property";

        assertThatThrownBy(() -> ValidationUtils.requireSizeAtMost(values, maximum, message))
                .isInstanceOf(NullPointerException.class)
                .hasMessage(message);
    }

    @Test
    void requireSizeAtLeast_whenWithinMinimum_shouldReturnMap() {
        final Map<String, String> values = Map.of("a", "1", "b", "2");
        final int minimum = 2;
        final String message = "props must contain at least 2 properties";

        final Map<String, String> result = ValidationUtils.requireSizeAtLeast(values, minimum, message);

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireSizeAtLeast_whenMapSizeBelowMinimum_shouldThrow() {
        final Map<String, String> values = Map.of("a", "1");
        final int minimum = 2;
        final String message = "props must contain at least 2 properties";

        assertThatThrownBy(() -> ValidationUtils.requireSizeAtLeast(values, minimum, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireSizeAtLeast_whenMapNull_shouldThrow() {
        final Map<String, String> values = null;
        final int minimum = 1;
        final String message = "props must contain at least 1 property";

        assertThatThrownBy(() -> ValidationUtils.requireSizeAtLeast(values, minimum, message))
                .isInstanceOf(NullPointerException.class)
                .hasMessage(message);
    }

    @Test
    void requireSizeExactly_whenSizeMatches_shouldReturnMap() {
        final Map<String, String> values = Map.of("a", "1");
        final int size = 1;
        final String message = "props must contain exactly 1 property";

        final Map<String, String> result = ValidationUtils.requireSizeExactly(values, size, message);

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireSizeExactly_whenMapNull_shouldThrow() {
        final Map<String, String> values = null;
        final int size = 1;
        final String message = "props must contain exactly 1 property";

        assertThatThrownBy(() -> ValidationUtils.requireSizeExactly(values, size, message))
                .isInstanceOf(NullPointerException.class)
                .hasMessage(message);
    }

    @Test
    void requireSizeExactly_whenMapSizeDiffers_shouldThrow() {
        final Map<String, String> values = Map.of("a", "1", "b", "2");
        final int size = 1;
        final String message = "props must contain exactly 1 property";

        assertThatThrownBy(() -> ValidationUtils.requireSizeExactly(values, size, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireSizeBetween_whenWithinBounds_shouldReturnMap() {
        final Map<String, String> values = Map.of("a", "1", "b", "2");
        final int minimum = 2;
        final int maximum = 100;
        final String message = "props must contain between 2 and 100 properties";

        final Map<String, String> result = ValidationUtils.requireSizeBetween(values, minimum, maximum, message);

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireSizeBetween_whenMapNull_shouldThrow() {
        final Map<String, String> values = null;
        final int minimum = 2;
        final int maximum = 100;
        final String message = "props must contain between 2 and 100 properties";

        assertThatThrownBy(() -> ValidationUtils.requireSizeBetween(values, minimum, maximum, message))
                .isInstanceOf(NullPointerException.class)
                .hasMessage(message);
    }

    @Test
    void requireSizeBetween_whenMapSizeBelowMinimum_shouldThrow() {
        final Map<String, String> values = Map.of("a", "1");
        final int minimum = 2;
        final int maximum = 100;
        final String message = "props must contain between 2 and 100 properties";

        assertThatThrownBy(() -> ValidationUtils.requireSizeBetween(values, minimum, maximum, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireSizeBetween_whenMapSizeExceedsMaximum_shouldThrow() {
        final Map<String, String> values = Map.of("a", "1", "b", "2", "c", "3");
        final int minimum = 1;
        final int maximum = 2;
        final String message = "props must contain between 1 and 2 properties";

        assertThatThrownBy(() -> ValidationUtils.requireSizeBetween(values, minimum, maximum, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireMaxLengthIfPresent_whenValueNull_shouldReturnNull() {
        final String value = null;
        final int maximum = 2;
        final String message = "summary must be at most 2 characters";

        final String result = ValidationUtils.requireMaxLengthIfPresent(value, maximum, message);

        assertThat(result).isNull();
    }

    @Test
    void requireMinLength_whenWithinMinimum_shouldReturnValue() {
        final String value = "hi";
        final int minLength = 2;
        final String message = "summary must be at least 2 characters";

        final String result = ValidationUtils.requireMinLength(value, minLength, message);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireMinLength_whenShorterThanMinimum_shouldThrow() {
        final String value = "h";
        final int minLength = 2;
        final String message = "summary must be at least 2 characters";

        assertThatThrownBy(() -> ValidationUtils.requireMinLength(value, minLength, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireMinLength_whenValueNull_shouldThrow() {
        final String value = null;
        final int minLength = 1;
        final String message = "summary must be at least 1 character";

        assertThatThrownBy(() -> ValidationUtils.requireMinLength(value, minLength, message))
                .isInstanceOf(NullPointerException.class)
                .hasMessage(message);
    }

    @Test
    void requireMinLengthIfPresent_whenWithinMinimum_shouldReturnValue() {
        final String value = "hi";
        final int minLength = 2;
        final String message = "summary must be at least 2 characters";

        final String result = ValidationUtils.requireMinLengthIfPresent(value, minLength, message);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireMinLengthIfPresent_whenShorterThanMinimum_shouldThrow() {
        final String value = "h";
        final int minLength = 2;
        final String message = "summary must be at least 2 characters";

        assertThatThrownBy(() -> ValidationUtils.requireMinLengthIfPresent(value, minLength, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireMinLengthIfPresent_whenValueNull_shouldReturnNull() {
        final String value = null;
        final int minLength = 2;
        final String message = "summary must be at least 2 characters";

        final String result = ValidationUtils.requireMinLengthIfPresent(value, minLength, message);

        assertThat(result).isNull();
    }

    @Test
    void requireMatchesIfPresent_whenPatternMatches_shouldReturnValue() {
        final String value = "ssh-rsa AAA";
        final Pattern pattern = Pattern.compile("^ssh-rsa ");
        final String message = "key must be an ssh-rsa public key";

        final String result = ValidationUtils.requireMatchesIfPresent(value, pattern, message);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireMatchesIfPresent_whenPatternDoesNotMatch_shouldThrow() {
        final String value = "not-a-key";
        final Pattern pattern = Pattern.compile("^ssh-rsa ");
        final String message = "key must be an ssh-rsa public key";

        assertThatThrownBy(() -> ValidationUtils.requireMatchesIfPresent(value, pattern, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireMatchesIfPresent_whenValueNull_shouldReturnNull() {
        final String value = null;
        final Pattern pattern = Pattern.compile("^ssh-rsa ");
        final String message = "key must be an ssh-rsa public key";

        final String result = ValidationUtils.requireMatchesIfPresent(value, pattern, message);

        assertThat(result).isNull();
    }

    @Test
    void requireMatchesIfPresent_whenValueNullAndPatternNull_shouldReturnNull() {
        final String value = null;
        final Pattern pattern = null;
        final String message = "key must be an ssh-rsa public key";

        final String result = ValidationUtils.requireMatchesIfPresent(value, pattern, message);

        assertThat(result).isNull();
    }

    @Test
    void requireNotBlankIfPresent_whenNonBlank_shouldReturnValue() {
        final String value = "token";
        final String message = "token must not be blank";

        final String result = ValidationUtils.requireNotBlankIfPresent(value, message);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireNotBlankIfPresent_whenBlank_shouldThrow() {
        final String value = "  ";
        final String message = "token must not be blank";

        assertThatThrownBy(() -> ValidationUtils.requireNotBlankIfPresent(value, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireNotBlankIfPresent_whenValueNull_shouldReturnNull() {
        final String value = null;
        final String message = "token must not be blank";

        final String result = ValidationUtils.requireNotBlankIfPresent(value, message);

        assertThat(result).isNull();
    }

    @Test
    void requireAtLeast_whenWithinMinimum_shouldReturnLong() {
        final long value = 2L;
        final long minimum = 2L;
        final String message = "id must be at least 2";

        final long result = ValidationUtils.requireAtLeast(value, minimum, message);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireAtLeast_whenLongBelowMinimum_shouldThrow() {
        final long value = 1L;
        final long minimum = 2L;
        final String message = "id must be at least 2";

        assertThatThrownBy(() -> ValidationUtils.requireAtLeast(value, minimum, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireAtMost_whenWithinMaximum_shouldReturnLong() {
        final long value = 100L;
        final long maximum = 100L;
        final String message = "id must be at most 100";

        final long result = ValidationUtils.requireAtMost(value, maximum, message);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireAtMost_whenLongExceedsMaximum_shouldThrow() {
        final long value = 101L;
        final long maximum = 100L;
        final String message = "id must be at most 100";

        assertThatThrownBy(() -> ValidationUtils.requireAtMost(value, maximum, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireBetween_whenWithinBounds_shouldReturnInt() {
        final int value = 50;
        final int minimum = 0;
        final int maximum = 100;
        final String message = "count must be between 0 and 100";

        final int result = ValidationUtils.requireBetween(value, minimum, maximum, message);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireBetween_whenIntBelowMinimum_shouldThrow() {
        final int value = -1;
        final int minimum = 0;
        final int maximum = 100;
        final String message = "count must be between 0 and 100";

        assertThatThrownBy(() -> ValidationUtils.requireBetween(value, minimum, maximum, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireBetween_whenIntAboveMaximum_shouldThrow() {
        final int value = 101;
        final int minimum = 0;
        final int maximum = 100;
        final String message = "count must be between 0 and 100";

        assertThatThrownBy(() -> ValidationUtils.requireBetween(value, minimum, maximum, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireBetween_whenWithinBounds_shouldReturnLong() {
        final long value = 50L;
        final long minimum = 0L;
        final long maximum = 100L;
        final String message = "id must be between 0 and 100";

        final long result = ValidationUtils.requireBetween(value, minimum, maximum, message);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireBetween_whenLongBelowMinimum_shouldThrow() {
        final long value = -1L;
        final long minimum = 0L;
        final long maximum = 100L;
        final String message = "id must be between 0 and 100";

        assertThatThrownBy(() -> ValidationUtils.requireBetween(value, minimum, maximum, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireBetween_whenLongAboveMaximum_shouldThrow() {
        final long value = 101L;
        final long minimum = 0L;
        final long maximum = 100L;
        final String message = "id must be between 0 and 100";

        assertThatThrownBy(() -> ValidationUtils.requireBetween(value, minimum, maximum, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireAtLeastIfPresent_whenIntegerWithinMinimum_shouldReturnValue() {
        final Integer value = 2;
        final int minimum = 2;
        final String message = "count must be at least 2";

        final Integer result = ValidationUtils.requireAtLeastIfPresent(value, minimum, message);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireAtLeastIfPresent_whenIntegerBelowMinimum_shouldThrow() {
        final Integer value = 1;
        final int minimum = 2;
        final String message = "count must be at least 2";

        assertThatThrownBy(() -> ValidationUtils.requireAtLeastIfPresent(value, minimum, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireAtLeastIfPresent_whenIntegerNull_shouldReturnNull() {
        final Integer value = null;
        final int minimum = 2;
        final String message = "count must be at least 2";

        final Integer result = ValidationUtils.requireAtLeastIfPresent(value, minimum, message);

        assertThat(result).isNull();
    }

    @Test
    void requireAtLeastIfPresent_whenLongWithinMinimum_shouldReturnValue() {
        final Long value = 2L;
        final long minimum = 2L;
        final String message = "id must be at least 2";

        final Long result = ValidationUtils.requireAtLeastIfPresent(value, minimum, message);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireAtLeastIfPresent_whenLongBelowMinimum_shouldThrow() {
        final Long value = 1L;
        final long minimum = 2L;
        final String message = "id must be at least 2";

        assertThatThrownBy(() -> ValidationUtils.requireAtLeastIfPresent(value, minimum, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireAtLeastIfPresent_whenLongNull_shouldReturnNull() {
        final Long value = null;
        final long minimum = 2L;
        final String message = "id must be at least 2";

        final Long result = ValidationUtils.requireAtLeastIfPresent(value, minimum, message);

        assertThat(result).isNull();
    }

    @Test
    void requireAtLeastIfPresent_whenDoubleWithinMinimum_shouldReturnValue() {
        final Double value = 0.0;
        final double minimum = 0.0;
        final String message = "percentage must be at least 0";

        final Double result = ValidationUtils.requireAtLeastIfPresent(value, minimum, message);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireAtLeastIfPresent_whenDoubleBelowMinimum_shouldThrow() {
        final Double value = -1.0;
        final double minimum = 0.0;
        final String message = "percentage must be at least 0";

        assertThatThrownBy(() -> ValidationUtils.requireAtLeastIfPresent(value, minimum, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireAtLeastIfPresent_whenDoubleNull_shouldReturnNull() {
        final Double value = null;
        final double minimum = 0.0;
        final String message = "percentage must be at least 0";

        final Double result = ValidationUtils.requireAtLeastIfPresent(value, minimum, message);

        assertThat(result).isNull();
    }

    @Test
    void requireAtMostIfPresent_whenIntegerWithinMaximum_shouldReturnValue() {
        final Integer value = 100;
        final int maximum = 100;
        final String message = "count must be at most 100";

        final Integer result = ValidationUtils.requireAtMostIfPresent(value, maximum, message);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireAtMostIfPresent_whenIntegerExceedsMaximum_shouldThrow() {
        final Integer value = 101;
        final int maximum = 100;
        final String message = "count must be at most 100";

        assertThatThrownBy(() -> ValidationUtils.requireAtMostIfPresent(value, maximum, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireAtMostIfPresent_whenIntegerNull_shouldReturnNull() {
        final Integer value = null;
        final int maximum = 100;
        final String message = "count must be at most 100";

        final Integer result = ValidationUtils.requireAtMostIfPresent(value, maximum, message);

        assertThat(result).isNull();
    }

    @Test
    void requireAtMostIfPresent_whenLongWithinMaximum_shouldReturnValue() {
        final Long value = 100L;
        final long maximum = 100L;
        final String message = "id must be at most 100";

        final Long result = ValidationUtils.requireAtMostIfPresent(value, maximum, message);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireAtMostIfPresent_whenLongExceedsMaximum_shouldThrow() {
        final Long value = 101L;
        final long maximum = 100L;
        final String message = "id must be at most 100";

        assertThatThrownBy(() -> ValidationUtils.requireAtMostIfPresent(value, maximum, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireAtMostIfPresent_whenLongNull_shouldReturnNull() {
        final Long value = null;
        final long maximum = 100L;
        final String message = "id must be at most 100";

        final Long result = ValidationUtils.requireAtMostIfPresent(value, maximum, message);

        assertThat(result).isNull();
    }

    @Test
    void requireAtMostIfPresent_whenDoubleWithinMaximum_shouldReturnValue() {
        final Double value = 100.0;
        final double maximum = 100.0;
        final String message = "percentage must be at most 100";

        final Double result = ValidationUtils.requireAtMostIfPresent(value, maximum, message);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireAtMostIfPresent_whenDoubleExceedsMaximum_shouldThrow() {
        final Double value = 101.0;
        final double maximum = 100.0;
        final String message = "percentage must be at most 100";

        assertThatThrownBy(() -> ValidationUtils.requireAtMostIfPresent(value, maximum, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireAtMostIfPresent_whenDoubleNull_shouldReturnNull() {
        final Double value = null;
        final double maximum = 100.0;
        final String message = "percentage must be at most 100";

        final Double result = ValidationUtils.requireAtMostIfPresent(value, maximum, message);

        assertThat(result).isNull();
    }

    @Test
    void requireBetweenIfPresent_whenIntegerWithinBounds_shouldReturnValue() {
        final Integer value = 50;
        final int minimum = 0;
        final int maximum = 100;
        final String message = "count must be between 0 and 100";

        final Integer result = ValidationUtils.requireBetweenIfPresent(value, minimum, maximum, message);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireBetweenIfPresent_whenIntegerOutOfBounds_shouldThrow() {
        final Integer value = 101;
        final int minimum = 0;
        final int maximum = 100;
        final String message = "count must be between 0 and 100";

        assertThatThrownBy(() -> ValidationUtils.requireBetweenIfPresent(value, minimum, maximum, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireBetweenIfPresent_whenIntegerNull_shouldReturnNull() {
        final Integer value = null;
        final int minimum = 0;
        final int maximum = 100;
        final String message = "count must be between 0 and 100";

        final Integer result = ValidationUtils.requireBetweenIfPresent(value, minimum, maximum, message);

        assertThat(result).isNull();
    }

    @Test
    void requireBetweenIfPresent_whenLongWithinBounds_shouldReturnValue() {
        final Long value = 50L;
        final long minimum = 0L;
        final long maximum = 100L;
        final String message = "id must be between 0 and 100";

        final Long result = ValidationUtils.requireBetweenIfPresent(value, minimum, maximum, message);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireBetweenIfPresent_whenLongOutOfBounds_shouldThrow() {
        final Long value = -1L;
        final long minimum = 0L;
        final long maximum = 100L;
        final String message = "id must be between 0 and 100";

        assertThatThrownBy(() -> ValidationUtils.requireBetweenIfPresent(value, minimum, maximum, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireBetweenIfPresent_whenLongNull_shouldReturnNull() {
        final Long value = null;
        final long minimum = 0L;
        final long maximum = 100L;
        final String message = "id must be between 0 and 100";

        final Long result = ValidationUtils.requireBetweenIfPresent(value, minimum, maximum, message);

        assertThat(result).isNull();
    }

    @Test
    void requireBetweenIfPresent_whenDoubleWithinBounds_shouldReturnValue() {
        final Double value = 50.0;
        final double minimum = 0.0;
        final double maximum = 100.0;
        final String message = "percentage must be between 0 and 100";

        final Double result = ValidationUtils.requireBetweenIfPresent(value, minimum, maximum, message);

        assertThat(result).isEqualTo(value);
    }

    @Test
    void requireBetweenIfPresent_whenDoubleOutOfBounds_shouldThrow() {
        final Double value = 101.0;
        final double minimum = 0.0;
        final double maximum = 100.0;
        final String message = "percentage must be between 0 and 100";

        assertThatThrownBy(() -> ValidationUtils.requireBetweenIfPresent(value, minimum, maximum, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireBetweenIfPresent_whenDoubleNull_shouldReturnNull() {
        final Double value = null;
        final double minimum = 0.0;
        final double maximum = 100.0;
        final String message = "percentage must be between 0 and 100";

        final Double result = ValidationUtils.requireBetweenIfPresent(value, minimum, maximum, message);

        assertThat(result).isNull();
    }

    @Test
    void requireMatches_whenValueNull_shouldThrow() {
        final String value = null;
        final Pattern pattern = Pattern.compile("^ssh-rsa ");
        final String message = "key must be an ssh-rsa public key";

        assertThatThrownBy(() -> ValidationUtils.requireMatches(value, pattern, message))
                .isInstanceOf(NullPointerException.class)
                .hasMessage(message);
    }

    @Test
    void requireMatches_whenPatternNull_shouldThrow() {
        final String value = "ssh-rsa AAA";
        final Pattern pattern = null;
        final String message = "key must be an ssh-rsa public key";

        assertThatThrownBy(() -> ValidationUtils.requireMatches(value, pattern, message))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("pattern must not be null");
    }

    @ParameterizedTest
    @CsvSource({"true,false", "false,true"})
    void requireExactlyOne_whenExactlyOneOfTwoPresent_shouldNotThrow(final boolean first, final boolean second) {
        final String message = "exactly one is required";

        assertThatCode(() -> ValidationUtils.requireExactlyOne(present(first), present(second), message))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @CsvSource({"false,false", "true,true"})
    void requireExactlyOne_whenNoneOrBothOfTwoPresent_shouldThrow(final boolean first, final boolean second) {
        final String message = "exactly one is required";

        assertThatThrownBy(() -> ValidationUtils.requireExactlyOne(present(first), present(second), message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @ParameterizedTest
    @CsvSource({"true,false,false", "false,true,false", "false,false,true"})
    void requireExactlyOne_whenExactlyOneOfThreePresent_shouldNotThrow(
            final boolean first, final boolean second, final boolean third) {
        final String message = "exactly one is required";

        assertThatCode(() ->
                        ValidationUtils.requireExactlyOne(present(first), present(second), present(third), message))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @CsvSource({"false,false,false", "true,true,false", "true,false,true", "false,true,true", "true,true,true"})
    void requireExactlyOne_whenNoneOrManyOfThreePresent_shouldThrow(
            final boolean first, final boolean second, final boolean third) {
        final String message = "exactly one is required";

        assertThatThrownBy(() ->
                        ValidationUtils.requireExactlyOne(present(first), present(second), present(third), message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @ParameterizedTest
    @CsvSource({"false,false", "true,false", "false,true"})
    void requireAtMostOne_whenAtMostOneOfTwoPresent_shouldNotThrow(final boolean first, final boolean second) {
        final String message = "mutually exclusive";

        assertThatCode(() -> ValidationUtils.requireAtMostOne(present(first), present(second), message))
                .doesNotThrowAnyException();
    }

    @Test
    void requireAtMostOne_whenBothOfTwoPresent_shouldThrow() {
        final String message = "mutually exclusive";

        assertThatThrownBy(() -> ValidationUtils.requireAtMostOne(present(true), present(true), message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @ParameterizedTest
    @CsvSource({"false,false,false", "true,false,false", "false,true,false", "false,false,true"})
    void requireAtMostOne_whenAtMostOneOfThreePresent_shouldNotThrow(
            final boolean first, final boolean second, final boolean third) {
        final String message = "mutually exclusive";

        assertThatCode(() -> ValidationUtils.requireAtMostOne(present(first), present(second), present(third), message))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @CsvSource({"true,true,false", "true,false,true", "false,true,true", "true,true,true"})
    void requireAtMostOne_whenManyOfThreePresent_shouldThrow(
            final boolean first, final boolean second, final boolean third) {
        final String message = "mutually exclusive";

        assertThatThrownBy(() ->
                        ValidationUtils.requireAtMostOne(present(first), present(second), present(third), message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @ParameterizedTest
    @CsvSource({"true,false", "false,true", "true,true"})
    void requireAtLeastOne_whenAnyOfTwoPresent_shouldNotThrow(final boolean first, final boolean second) {
        final String message = "at least one is required";

        assertThatCode(() -> ValidationUtils.requireAtLeastOne(present(first), present(second), message))
                .doesNotThrowAnyException();
    }

    @Test
    void requireAtLeastOne_whenNoneOfTwoPresent_shouldThrow() {
        final String message = "at least one is required";

        assertThatThrownBy(() -> ValidationUtils.requireAtLeastOne(present(false), present(false), message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
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
        final String message = "at least one is required";

        assertThatCode(() ->
                        ValidationUtils.requireAtLeastOne(present(first), present(second), present(third), message))
                .doesNotThrowAnyException();
    }

    @Test
    void requireAtLeastOne_whenNoneOfThreePresent_shouldThrow() {
        final String message = "at least one is required";

        assertThatThrownBy(() ->
                        ValidationUtils.requireAtLeastOne(present(false), present(false), present(false), message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @ParameterizedTest
    @CsvSource({"false,false", "true,true"})
    void requireBothOrNeither_whenBothOrNeitherPresent_shouldNotThrow(final boolean first, final boolean second) {
        final String message = "must be set together";

        assertThatCode(() -> ValidationUtils.requireBothOrNeither(present(first), present(second), message))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @CsvSource({"true,false", "false,true"})
    void requireBothOrNeither_whenOnlyOnePresent_shouldThrow(final boolean first, final boolean second) {
        final String message = "must be set together";

        assertThatThrownBy(() -> ValidationUtils.requireBothOrNeither(present(first), present(second), message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireExactlyOne_whenFalseBooleanPassed_shouldCountAsPresent() {
        final String message = "exactly one is required";

        assertThatThrownBy(() -> ValidationUtils.requireExactlyOne(Boolean.FALSE, Boolean.FALSE, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requirePositive_whenIntPositive_shouldReturnValue() {
        final int value = 1;
        final String message = "value must be positive";

        final int result = ValidationUtils.requirePositive(value, message);

        assertThat(result).isEqualTo(value);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void requirePositive_whenIntZeroOrNegative_shouldThrow(final int value) {
        final String message = "value must be positive";

        assertThatThrownBy(() -> ValidationUtils.requirePositive(value, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requirePositive_whenLongPositive_shouldReturnValue() {
        final long value = 1L;
        final String message = "value must be positive";

        final long result = ValidationUtils.requirePositive(value, message);

        assertThat(result).isEqualTo(value);
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L})
    void requirePositive_whenLongZeroOrNegative_shouldThrow(final long value) {
        final String message = "value must be positive";

        assertThatThrownBy(() -> ValidationUtils.requirePositive(value, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireNonEmpty_whenArrayNonEmpty_shouldReturnSameArray() {
        final String[] values = {"a"};
        final String message = "values must not be empty";

        final String[] result = ValidationUtils.requireNonEmpty(values, message);

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireNonEmpty_whenArrayEmpty_shouldThrow() {
        final String[] values = {};
        final String message = "values must not be empty";

        assertThatThrownBy(() -> ValidationUtils.requireNonEmpty(values, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    @Test
    void requireNonEmpty_whenArrayNull_shouldThrow() {
        final String[] values = null;
        final String message = "values must not be empty";

        assertThatThrownBy(() -> ValidationUtils.requireNonEmpty(values, message))
                .isInstanceOf(NullPointerException.class)
                .hasMessage(message);
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
