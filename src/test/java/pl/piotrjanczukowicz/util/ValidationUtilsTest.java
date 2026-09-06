package pl.piotrjanczukowicz.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.lang.reflect.Constructor;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class ValidationUtilsTest {

    @Test
    void constructor_shouldThrow_whenInstantiatedViaReflection() throws Exception {
        final Constructor<ValidationUtils> constructor = ValidationUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final Throwable thrown = catchThrowable(constructor::newInstance);

        assertThat(thrown).hasCauseInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void isPositive_shouldReturnFalse_whenNullOrNotPositive() {
        assertSoftly(softly -> {
            softly.assertThat(ValidationUtils.isPositive(null)).isFalse();
            softly.assertThat(ValidationUtils.isPositive(Duration.ZERO)).isFalse();
            softly.assertThat(ValidationUtils.isPositive(Duration.ofSeconds(-1)))
                    .isFalse();
        });
    }

    @Test
    void isPositive_shouldReturnTrue_whenDurationPositive() {
        final boolean positive = ValidationUtils.isPositive(Duration.ofMillis(1));

        assertThat(positive).isTrue();
    }

    @Test
    void isNonPositive_shouldReturnTrue_whenNullOrNotPositive() {
        assertSoftly(softly -> {
            softly.assertThat(ValidationUtils.isNonPositive(null)).isTrue();
            softly.assertThat(ValidationUtils.isNonPositive(Duration.ZERO)).isTrue();
            softly.assertThat(ValidationUtils.isNonPositive(Duration.ofSeconds(-1)))
                    .isTrue();
        });
    }

    @Test
    void requireAtMost_shouldReturnValue_whenWithinMaximum() {
        final int value = ValidationUtils.requireAtMost(100, 100, "perPage");

        assertThat(value).isEqualTo(100);
    }

    @Test
    void requireAtMost_shouldThrow_whenValueExceedsMaximum() {
        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireAtMost(101, 100, "perPage"));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("perPage");
    }

    @Test
    void requireAtLeast_shouldReturnValue_whenWithinMinimum() {
        final double value = ValidationUtils.requireAtLeast(0.0, 0.0, "percentage");

        assertThat(value).isEqualTo(0.0);
    }

    @Test
    void requireAtLeast_shouldThrow_whenValueBelowMinimum() {
        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireAtLeast(-1.0, 0.0, "percentage"));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("percentage");
    }

    @Test
    void requireAtMost_shouldReturnValue_whenWithinMaximumForDouble() {
        final double value = ValidationUtils.requireAtMost(100.0, 100.0, "percentage");

        assertThat(value).isEqualTo(100.0);
    }

    @Test
    void requireAtMost_shouldThrow_whenDoubleValueExceedsMaximum() {
        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireAtMost(101.0, 100.0, "percentage"));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("percentage");
    }

    @Test
    void requireAtMost_shouldThrow_whenDoubleValueIsNaN() {
        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireAtMost(Double.NaN, 100.0, "percentage"));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("percentage");
    }

    @Test
    void requireAtMost_shouldThrow_whenDoubleValueIsInfinite() {
        final Throwable thrown =
                catchThrowable(() -> ValidationUtils.requireAtMost(Double.POSITIVE_INFINITY, 100.0, "percentage"));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("percentage");
    }

    @Test
    void requireBetween_shouldReturnValue_whenWithinBounds() {
        final double value = ValidationUtils.requireBetween(50.0, 0.0, 100.0, "percentage");

        assertThat(value).isEqualTo(50.0);
    }

    @Test
    void requireBetween_shouldThrow_whenValueBelowMinimum() {
        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireBetween(-1.0, 0.0, 100.0, "percentage"));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("percentage");
    }

    @Test
    void requireBetween_shouldThrow_whenValueAboveMaximum() {
        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireBetween(101.0, 0.0, 100.0, "percentage"));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("percentage");
    }

    @Test
    void requireBetween_shouldThrow_whenValueIsNaN() {
        final Throwable thrown =
                catchThrowable(() -> ValidationUtils.requireBetween(Double.NaN, 0.0, 100.0, "percentage"));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("percentage");
    }

    @Test
    void requireBetween_shouldThrow_whenValueIsInfinite() {
        final Throwable thrown = catchThrowable(
                () -> ValidationUtils.requireBetween(Double.POSITIVE_INFINITY, 0.0, 100.0, "percentage"));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("percentage");
    }

    @Test
    void isNonPositive_shouldReturnFalse_whenDurationPositive() {
        final boolean nonPositive = ValidationUtils.isNonPositive(Duration.ofMillis(1));

        assertThat(nonPositive).isFalse();
    }

    @Test
    void requireNotBlank_shouldReturnValue_whenNonBlank() {
        assertThat(ValidationUtils.requireNotBlank("token", "token")).isEqualTo("token");
    }

    @Test
    void requireNotBlank_shouldThrow_whenNull() {
        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireNotBlank(null, "token"));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessageContaining("token");
    }

    @Test
    void requireNotBlank_shouldThrow_whenBlank() {
        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireNotBlank("  ", "token"));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage("token must not be blank");
    }

    @Test
    void requireSizeAtMost_shouldReturnValue_whenWithinMaximum() {
        final List<String> values = List.of("a", "b");

        final List<String> result = ValidationUtils.requireSizeAtMost(values, 2, "items");

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireSizeAtMost_shouldThrow_whenSizeExceedsMaximum() {
        final Throwable thrown =
                catchThrowable(() -> ValidationUtils.requireSizeAtMost(List.of("a", "b", "c"), 2, "items"));

        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("items")
                .hasMessageContaining("at most 2");
    }

    @Test
    void requireSizeAtLeast_shouldReturnValue_whenWithinMinimum() {
        final List<String> values = List.of("a", "b");

        final List<String> result = ValidationUtils.requireSizeAtLeast(values, 2, "items");

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireSizeAtLeast_shouldThrow_whenSizeBelowMinimum() {
        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireSizeAtLeast(List.of("a"), 2, "items"));

        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("items")
                .hasMessageContaining("at least 2");
    }

    @Test
    void requireSizeExactly_shouldReturnValue_whenSizeMatches() {
        final List<String> values = List.of("a");

        final List<String> result = ValidationUtils.requireSizeExactly(values, 1, "items");

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireSizeExactly_shouldThrow_whenNull() {
        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireSizeExactly(null, 1, "items"));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessageContaining("items");
    }

    @Test
    void requireSizeExactly_shouldThrow_whenSizeDiffers() {
        final Throwable thrown =
                catchThrowable(() -> ValidationUtils.requireSizeExactly(List.of("a", "b"), 1, "items"));

        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("items")
                .hasMessageContaining("exactly 1");
    }

    @Test
    void requireSizeBetween_shouldReturnValue_whenWithinBounds() {
        final List<String> values = List.of("a", "b");

        final List<String> result = ValidationUtils.requireSizeBetween(values, 2, 100, "items");

        assertThat(result).isSameAs(values);
    }

    @Test
    void requireSizeBetween_shouldThrow_whenNull() {
        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireSizeBetween(null, 2, 100, "items"));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessageContaining("items");
    }

    @Test
    void requireSizeBetween_shouldThrow_whenSizeBelowMinimum() {
        final Throwable thrown =
                catchThrowable(() -> ValidationUtils.requireSizeBetween(List.of("a"), 2, 100, "items"));

        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("items")
                .hasMessageContaining("at least 2");
    }

    @Test
    void requireSizeBetween_shouldThrow_whenSizeExceedsMaximum() {
        final Throwable thrown =
                catchThrowable(() -> ValidationUtils.requireSizeBetween(List.of("a", "b", "c"), 1, 2, "items"));

        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("items")
                .hasMessageContaining("at most 2");
    }

    @Test
    void requireMaxLength_shouldReturnValue_whenWithinMaximum() {
        final String value = ValidationUtils.requireMaxLength("hi", 2, "summary");

        assertThat(value).isEqualTo("hi");
    }

    @Test
    void requireMaxLength_shouldThrow_whenLongerThanMaximum() {
        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireMaxLength("hey", 2, "summary"));

        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("summary")
                .hasMessageContaining("at most 2");
    }

    @Test
    void requireMaxLengthWhenPresent_shouldReturnValue_whenWithinMaximum() {
        final String value = ValidationUtils.requireMaxLengthWhenPresent("hi", 2, "summary");

        assertThat(value).isEqualTo("hi");
    }

    @Test
    void requireMaxLengthWhenPresent_shouldThrow_whenLongerThanMaximum() {
        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireMaxLengthWhenPresent("hey", 2, "summary"));

        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("summary")
                .hasMessageContaining("at most 2");
    }

    @Test
    void requireMatches_shouldReturnValue_whenPatternMatches() {
        final Pattern pattern = Pattern.compile("^ssh-rsa ");

        final String value = ValidationUtils.requireMatches("ssh-rsa AAA", pattern, "key");

        assertThat(value).isEqualTo("ssh-rsa AAA");
    }

    @Test
    void requireMatches_shouldThrow_whenPatternDoesNotMatch() {
        final Pattern pattern = Pattern.compile("^ssh-rsa ");

        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireMatches("not-a-key", pattern, "key"));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("key");
    }

    @Test
    void requireNonNull_shouldReturnValue_whenNonNull() {
        assertThat(ValidationUtils.requireNonNull("token", "token")).isEqualTo("token");
    }

    @Test
    void requireNonNull_shouldThrow_whenNull() {
        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireNonNull(null, "token"));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessageContaining("token");
    }

    @Test
    void requirePositive_shouldReturnDuration_whenPositive() {
        final Duration duration = Duration.ofMillis(1);

        assertThat(ValidationUtils.requirePositive(duration, "timeout")).isSameAs(duration);
    }

    @Test
    void requirePositive_shouldThrow_whenNull() {
        final Throwable thrown = catchThrowable(() -> ValidationUtils.requirePositive(null, "timeout"));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessageContaining("timeout");
    }

    @Test
    void requirePositive_shouldThrow_whenZeroOrNegative() {
        assertSoftly(softly -> {
            softly.assertThat(catchThrowable(() -> ValidationUtils.requirePositive(Duration.ZERO, "timeout")))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("timeout");
            softly.assertThat(catchThrowable(() -> ValidationUtils.requirePositive(Duration.ofSeconds(-1), "timeout")))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("timeout");
        });
    }

    @Test
    void requireNonNegative_shouldReturnDuration_whenZeroOrPositive() {
        assertSoftly(softly -> {
            softly.assertThat(ValidationUtils.requireNonNegative(Duration.ZERO, "delay"))
                    .isEqualTo(Duration.ZERO);
            softly.assertThat(ValidationUtils.requireNonNegative(Duration.ofMillis(1), "delay"))
                    .isEqualTo(Duration.ofMillis(1));
        });
    }

    @Test
    void requireNonNegative_shouldThrow_whenNull() {
        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireNonNegative(null, "delay"));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessageContaining("delay");
    }

    @Test
    void requireNonNegative_shouldThrow_whenNegative() {
        final Throwable thrown =
                catchThrowable(() -> ValidationUtils.requireNonNegative(Duration.ofSeconds(-1), "delay"));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("delay");
    }

    @Test
    void requireAtLeast_shouldReturnInt_whenWithinMinimum() {
        assertThat(ValidationUtils.requireAtLeast(2, 2, "count")).isEqualTo(2);
    }

    @Test
    void requireAtLeast_shouldThrow_whenIntBelowMinimum() {
        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireAtLeast(1, 2, "count"));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("count");
    }

    @Test
    void requireAtLeast_shouldThrow_whenDoubleValueIsNaN() {
        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireAtLeast(Double.NaN, 0.0, "percentage"));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("percentage");
    }

    @Test
    void requireAtLeast_shouldThrow_whenDoubleValueIsInfinite() {
        final Throwable thrown =
                catchThrowable(() -> ValidationUtils.requireAtLeast(Double.NEGATIVE_INFINITY, 0.0, "percentage"));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("percentage");
    }

    @Test
    void requireGreaterOrEqual_shouldPass_whenValueAtLeastMinimum() {
        ValidationUtils.requireGreaterOrEqual(Duration.ofSeconds(2), Duration.ofSeconds(2), "wait");
    }

    @Test
    void requireGreaterOrEqual_shouldThrow_whenValueBelowMinimum() {
        final Throwable thrown = catchThrowable(
                () -> ValidationUtils.requireGreaterOrEqual(Duration.ofSeconds(1), Duration.ofSeconds(2), "wait"));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("wait");
    }

    @Test
    void requireGreaterOrEqual_shouldThrow_whenValueNull() {
        final Throwable thrown =
                catchThrowable(() -> ValidationUtils.requireGreaterOrEqual(null, Duration.ZERO, "wait"));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessageContaining("wait");
    }

    @Test
    void requireGreaterOrEqual_shouldThrow_whenMinimumNull() {
        final Throwable thrown =
                catchThrowable(() -> ValidationUtils.requireGreaterOrEqual(Duration.ZERO, null, "wait"));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessageContaining("minimum");
    }

    @Test
    void requireNonEmpty_shouldReturnValue_whenNonEmpty() {
        final List<String> values = List.of("a");

        assertThat(ValidationUtils.requireNonEmpty(values, "items")).isSameAs(values);
    }

    @Test
    void requireNonEmpty_shouldThrow_whenEmpty() {
        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireNonEmpty(List.of(), "items"));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("items");
    }

    @Test
    void requireNonEmpty_shouldThrow_whenNull() {
        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireNonEmpty(null, "items"));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessageContaining("items");
    }

    @Test
    void requireSizeAtMost_shouldThrow_whenNull() {
        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireSizeAtMost(null, 2, "items"));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessageContaining("items");
    }

    @Test
    void requireSizeAtLeast_shouldThrow_whenNull() {
        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireSizeAtLeast(null, 1, "items"));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessageContaining("items");
    }

    @Test
    void requireUniqueItems_shouldReturnValue_whenUnique() {
        final List<String> values = List.of("a", "b");

        assertThat(ValidationUtils.requireUniqueItems(values, "items")).isSameAs(values);
    }

    @Test
    void requireUniqueItems_shouldThrow_whenDuplicates() {
        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireUniqueItems(List.of("a", "a"), "items"));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("items");
    }

    @Test
    void requireUniqueItems_shouldThrow_whenNull() {
        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireUniqueItems(null, "items"));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessageContaining("items");
    }

    @Test
    void requireMapSizeAtMost_shouldReturnValue_whenWithinMaximum() {
        final Map<String, String> values = Map.of("a", "1");

        assertThat(ValidationUtils.requireMapSizeAtMost(values, 1, "props")).isSameAs(values);
    }

    @Test
    void requireMapSizeAtMost_shouldThrow_whenSizeExceedsMaximum() {
        final Throwable thrown =
                catchThrowable(() -> ValidationUtils.requireMapSizeAtMost(Map.of("a", "1", "b", "2"), 1, "props"));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("props");
    }

    @Test
    void requireMapSizeAtMost_shouldThrow_whenNull() {
        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireMapSizeAtMost(null, 1, "props"));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessageContaining("props");
    }

    @Test
    void requireMaxLengthWhenPresent_shouldReturnNull_whenValueNull() {
        assertThat(ValidationUtils.requireMaxLengthWhenPresent(null, 2, "summary"))
                .isNull();
    }

    @Test
    void requireMatches_shouldThrow_whenValueNull() {
        final Throwable thrown =
                catchThrowable(() -> ValidationUtils.requireMatches(null, Pattern.compile("."), "key"));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessageContaining("key");
    }

    @Test
    void requireMatches_shouldThrow_whenPatternNull() {
        final Throwable thrown = catchThrowable(() -> ValidationUtils.requireMatches("ssh-rsa AAA", null, "key"));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessageContaining("pattern");
    }
}
