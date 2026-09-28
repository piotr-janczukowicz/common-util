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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

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
        final String message = "token must not be null";

        final String result = ValidationUtils.requireNonNull(value, message);

        assertThat(result).isSameAs(value);
    }

    @Test
    void requireNonNull_whenNull_shouldThrow() {
        final String value = null;
        final String message = "token must not be null";

        assertThatThrownBy(() -> ValidationUtils.requireNonNull(value, message))
                .isInstanceOf(NullPointerException.class)
                .hasMessage(message);
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
