package pl.piotrjanczukowicz.util;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.Duration;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import org.jspecify.annotations.Nullable;

/**
 * Argument validation helpers.
 *
 * <p>Fail-fast checks for method arguments and configuration. Each {@code require*} method either returns the same
 * instance (so the result can be assigned) or throws. {@code message} is used as the exception text so callers can
 * describe the failure. {@code null} is rejected with {@link NullPointerException}; other violations use
 * {@link IllegalArgumentException}. Numeric bounds are inclusive unless a method says otherwise.
 */
public final class ValidationUtils {

    private ValidationUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Requires {@code value} to be non-null.
     *
     * @param value value to check
     * @param fieldName name included in the exception message
     * @param <T> value type
     * @return {@code value}
     * @throws NullPointerException if {@code value} is {@code null}
     */
    @SuppressFBWarnings(
            value = "NP_PARAMETER_MUST_BE_NONNULL_BUT_MARKED_AS_NULLABLE",
            justification = "null is rejected with NullPointerException")
    public static <T> T requireNonNull(@Nullable final T value, final String fieldName) {
        return Objects.requireNonNull(value, fieldName + " must not be null");
    }

    /**
     * Requires {@code value} to be non-null and not blank.
     *
     * <p>Blank follows {@link String#isBlank()}: empty or only Unicode whitespace. Surrounding whitespace is not
     * stripped; a value that contains non-whitespace still passes even if it also has spaces.
     *
     * @param value value to check
     * @param message exception text
     * @return {@code value}
     * @throws NullPointerException if {@code value} is {@code null}
     * @throws IllegalArgumentException if {@code value} is blank
     */
    @SuppressFBWarnings(
            value = "NP_PARAMETER_MUST_BE_NONNULL_BUT_MARKED_AS_NULLABLE",
            justification = "null is rejected with NullPointerException")
    public static String requireNotBlank(@Nullable final String value, final String message) {
        final String nonNull = Objects.requireNonNull(value, message);
        if (nonNull.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return nonNull;
    }

    /**
     * Requires {@code value} to be not blank when it is non-null.
     *
     * <p>{@code null} is allowed and returned unchanged. A present value is checked with
     * {@link #requireNotBlank(String, String)}.
     *
     * @param value value to check, or {@code null}
     * @param message exception text
     * @return {@code value}
     * @throws IllegalArgumentException if {@code value} is blank
     */
    public static @Nullable String requireNotBlankIfPresent(@Nullable final String value, final String message) {
        if (value != null) {
            requireNotBlank(value, message);
        }
        return value;
    }

    /**
     * Requires {@code duration} to be strictly positive.
     *
     * @param duration duration to check
     * @param message exception text
     * @return {@code duration}
     * @throws NullPointerException if {@code duration} is {@code null}
     * @throws IllegalArgumentException if {@code duration} is negative or zero
     */
    public static Duration requirePositive(final Duration duration, final String message) {
        Objects.requireNonNull(duration, message);

        if (!isPositive(duration)) {
            throw new IllegalArgumentException(message);
        }
        return duration;
    }

    /**
     * Requires {@code duration} to be zero or positive.
     *
     * @param duration duration to check
     * @param message exception text
     * @return {@code duration}
     * @throws NullPointerException if {@code duration} is {@code null}
     * @throws IllegalArgumentException if {@code duration} is negative
     */
    public static Duration requireNonNegative(final Duration duration, final String message) {
        Objects.requireNonNull(duration, message);

        if (duration.isNegative()) {
            throw new IllegalArgumentException(message);
        }
        return duration;
    }

    /**
     * Requires {@code value} to be at least {@code minimum}.
     *
     * @param value value to check
     * @param minimum inclusive lower bound
     * @param message exception text
     * @return {@code value}
     * @throws IllegalArgumentException if {@code value} is below {@code minimum}
     */
    public static int requireAtLeast(final int value, final int minimum, final String message) {
        if (value < minimum) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    /**
     * Requires {@code value} to be at least {@code minimum}.
     *
     * @param value value to check
     * @param minimum inclusive lower bound
     * @param message exception text
     * @return {@code value}
     * @throws IllegalArgumentException if {@code value} is below {@code minimum}
     */
    public static long requireAtLeast(final long value, final long minimum, final String message) {
        if (value < minimum) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    /**
     * Requires {@code value} to be finite and at least {@code minimum}.
     *
     * <p>The lower bound is inclusive. {@link Double#NaN}, infinities, and values below {@code minimum} fail. Finite is
     * checked before the bound so a NaN does not compare as in-range.
     *
     * @param value value to check
     * @param minimum inclusive lower bound
     * @param message exception text
     * @return {@code value}
     * @throws IllegalArgumentException if {@code value} is non-finite or below {@code minimum}
     */
    public static double requireAtLeast(final double value, final double minimum, final String message) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException(message);
        }
        if (value < minimum) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    /**
     * Requires {@code value} to be at least {@code minimum} when it is non-null.
     *
     * <p>{@code null} is allowed and returned unchanged. A present value is checked with {@link #requireAtLeast(int,
     * int, String)}.
     *
     * @param value value to check, or {@code null}
     * @param minimum inclusive lower bound
     * @param message exception text
     * @return {@code value}
     * @throws IllegalArgumentException if {@code value} is below {@code minimum}
     */
    public static @Nullable Integer requireAtLeastIfPresent(
            @Nullable final Integer value, final int minimum, final String message) {
        if (value != null) {
            requireAtLeast(value, minimum, message);
        }
        return value;
    }

    /**
     * Requires {@code value} to be at least {@code minimum} when it is non-null.
     *
     * <p>{@code null} is allowed and returned unchanged. A present value is checked with {@link #requireAtLeast(long,
     * long, String)}.
     *
     * @param value value to check, or {@code null}
     * @param minimum inclusive lower bound
     * @param message exception text
     * @return {@code value}
     * @throws IllegalArgumentException if {@code value} is below {@code minimum}
     */
    public static @Nullable Long requireAtLeastIfPresent(
            @Nullable final Long value, final long minimum, final String message) {
        if (value != null) {
            requireAtLeast(value, minimum, message);
        }
        return value;
    }

    /**
     * Requires {@code value} to be finite and at least {@code minimum} when it is non-null.
     *
     * <p>{@code null} is allowed and returned unchanged. A present value is checked with {@link #requireAtLeast(double,
     * double, String)}.
     *
     * @param value value to check, or {@code null}
     * @param minimum inclusive lower bound
     * @param message exception text
     * @return {@code value}
     * @throws IllegalArgumentException if {@code value} is non-finite or below {@code minimum}
     */
    public static @Nullable Double requireAtLeastIfPresent(
            @Nullable final Double value, final double minimum, final String message) {
        if (value != null) {
            requireAtLeast(value, minimum, message);
        }
        return value;
    }

    /**
     * Requires {@code value} to be at most {@code maximum}.
     *
     * @param value value to check
     * @param maximum inclusive upper bound
     * @param message exception text
     * @return {@code value}
     * @throws IllegalArgumentException if {@code value} is above {@code maximum}
     */
    public static int requireAtMost(final int value, final int maximum, final String message) {
        if (value > maximum) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    /**
     * Requires {@code value} to be at most {@code maximum}.
     *
     * @param value value to check
     * @param maximum inclusive upper bound
     * @param message exception text
     * @return {@code value}
     * @throws IllegalArgumentException if {@code value} is above {@code maximum}
     */
    public static long requireAtMost(final long value, final long maximum, final String message) {
        if (value > maximum) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    /**
     * Requires {@code value} to be finite and at most {@code maximum}.
     *
     * @param value value to check
     * @param maximum inclusive upper bound
     * @param message exception text
     * @return {@code value}
     * @throws IllegalArgumentException if {@code value} is non-finite or above {@code maximum}
     */
    public static double requireAtMost(final double value, final double maximum, final String message) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException(message);
        }
        if (value > maximum) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    /**
     * Requires {@code value} to be at most {@code maximum} when it is non-null.
     *
     * <p>{@code null} is allowed and returned unchanged. A present value is checked with {@link #requireAtMost(int,
     * int, String)}.
     *
     * @param value value to check, or {@code null}
     * @param maximum inclusive upper bound
     * @param message exception text
     * @return {@code value}
     * @throws IllegalArgumentException if {@code value} is above {@code maximum}
     */
    public static @Nullable Integer requireAtMostIfPresent(
            @Nullable final Integer value, final int maximum, final String message) {
        if (value != null) {
            requireAtMost(value, maximum, message);
        }
        return value;
    }

    /**
     * Requires {@code value} to be at most {@code maximum} when it is non-null.
     *
     * <p>{@code null} is allowed and returned unchanged. A present value is checked with {@link #requireAtMost(long,
     * long, String)}.
     *
     * @param value value to check, or {@code null}
     * @param maximum inclusive upper bound
     * @param message exception text
     * @return {@code value}
     * @throws IllegalArgumentException if {@code value} is above {@code maximum}
     */
    public static @Nullable Long requireAtMostIfPresent(
            @Nullable final Long value, final long maximum, final String message) {
        if (value != null) {
            requireAtMost(value, maximum, message);
        }
        return value;
    }

    /**
     * Requires {@code value} to be finite and at most {@code maximum} when it is non-null.
     *
     * <p>{@code null} is allowed and returned unchanged. A present value is checked with {@link #requireAtMost(double,
     * double, String)}.
     *
     * @param value value to check, or {@code null}
     * @param maximum inclusive upper bound
     * @param message exception text
     * @return {@code value}
     * @throws IllegalArgumentException if {@code value} is non-finite or above {@code maximum}
     */
    public static @Nullable Double requireAtMostIfPresent(
            @Nullable final Double value, final double maximum, final String message) {
        if (value != null) {
            requireAtMost(value, maximum, message);
        }
        return value;
    }

    /**
     * Requires {@code value} to be within {@code minimum} and {@code maximum}, inclusive.
     *
     * @param value value to check
     * @param minimum inclusive lower bound
     * @param maximum inclusive upper bound
     * @param message exception text
     * @return {@code value}
     * @throws IllegalArgumentException if {@code value} is outside the range
     */
    public static int requireBetween(final int value, final int minimum, final int maximum, final String message) {
        requireAtLeast(value, minimum, message);
        return requireAtMost(value, maximum, message);
    }

    /**
     * Requires {@code value} to be within {@code minimum} and {@code maximum}, inclusive.
     *
     * @param value value to check
     * @param minimum inclusive lower bound
     * @param maximum inclusive upper bound
     * @param message exception text
     * @return {@code value}
     * @throws IllegalArgumentException if {@code value} is outside the range
     */
    public static long requireBetween(final long value, final long minimum, final long maximum, final String message) {
        requireAtLeast(value, minimum, message);
        return requireAtMost(value, maximum, message);
    }

    /**
     * Requires {@code value} to be finite and within {@code minimum} and {@code maximum}, inclusive.
     *
     * @param value value to check
     * @param minimum inclusive lower bound
     * @param maximum inclusive upper bound
     * @param message exception text
     * @return {@code value}
     * @throws IllegalArgumentException if {@code value} is non-finite or outside the range
     */
    public static double requireBetween(
            final double value, final double minimum, final double maximum, final String message) {
        requireAtLeast(value, minimum, message);
        return requireAtMost(value, maximum, message);
    }

    /**
     * Requires {@code value} to be within {@code minimum} and {@code maximum}, inclusive, when it is non-null.
     *
     * <p>{@code null} is allowed and returned unchanged. A present value is checked with {@link #requireBetween(int,
     * int, int, String)}.
     *
     * @param value value to check, or {@code null}
     * @param minimum inclusive lower bound
     * @param maximum inclusive upper bound
     * @param message exception text
     * @return {@code value}
     * @throws IllegalArgumentException if {@code value} is outside the range
     */
    public static @Nullable Integer requireBetweenIfPresent(
            @Nullable final Integer value, final int minimum, final int maximum, final String message) {
        if (value != null) {
            requireBetween(value, minimum, maximum, message);
        }
        return value;
    }

    /**
     * Requires {@code value} to be within {@code minimum} and {@code maximum}, inclusive, when it is non-null.
     *
     * <p>{@code null} is allowed and returned unchanged. A present value is checked with {@link #requireBetween(long,
     * long, long, String)}.
     *
     * @param value value to check, or {@code null}
     * @param minimum inclusive lower bound
     * @param maximum inclusive upper bound
     * @param message exception text
     * @return {@code value}
     * @throws IllegalArgumentException if {@code value} is outside the range
     */
    public static @Nullable Long requireBetweenIfPresent(
            @Nullable final Long value, final long minimum, final long maximum, final String message) {
        if (value != null) {
            requireBetween(value, minimum, maximum, message);
        }
        return value;
    }

    /**
     * Requires {@code value} to be finite and within {@code minimum} and {@code maximum}, inclusive, when it is
     * non-null.
     *
     * <p>{@code null} is allowed and returned unchanged. A present value is checked with {@link #requireBetween(double,
     * double, double, String)}.
     *
     * @param value value to check, or {@code null}
     * @param minimum inclusive lower bound
     * @param maximum inclusive upper bound
     * @param message exception text
     * @return {@code value}
     * @throws IllegalArgumentException if {@code value} is non-finite or outside the range
     */
    public static @Nullable Double requireBetweenIfPresent(
            @Nullable final Double value, final double minimum, final double maximum, final String message) {
        if (value != null) {
            requireBetween(value, minimum, maximum, message);
        }
        return value;
    }

    /**
     * Requires {@code value} to be at least {@code minimum}.
     *
     * @param value duration to check
     * @param minimum inclusive lower bound
     * @param message exception text
     * @throws NullPointerException if {@code value} or {@code minimum} is {@code null}
     * @throws IllegalArgumentException if {@code value} is below {@code minimum}
     */
    public static void requireGreaterOrEqual(final Duration value, final Duration minimum, final String message) {
        Objects.requireNonNull(value, message);
        Objects.requireNonNull(minimum, "minimum must not be null");

        if (value.compareTo(minimum) < 0) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Requires {@code values} to be non-null and non-empty.
     *
     * @param values collection to check
     * @param message exception text
     * @param <T> collection type
     * @return {@code values}
     * @throws NullPointerException if {@code values} is {@code null}
     * @throws IllegalArgumentException if {@code values} is empty
     */
    public static <T extends Collection<?>> T requireNonEmpty(final T values, final String message) {
        Objects.requireNonNull(values, message);

        if (values.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return values;
    }

    /**
     * Requires {@code map} to be non-null and non-empty.
     *
     * @param map map to check
     * @param message exception text
     * @param <K> key type
     * @param <V> value type
     * @return {@code map}
     * @throws NullPointerException if {@code map} is {@code null}
     * @throws IllegalArgumentException if {@code map} is empty
     */
    public static <K, V> Map<K, V> requireNonEmpty(final Map<K, V> map, final String message) {
        Objects.requireNonNull(map, message);

        if (map.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return map;
    }

    /**
     * Requires {@code values} to contain at most {@code max} elements.
     *
     * @param values collection to check
     * @param max inclusive maximum size
     * @param message exception text
     * @param <T> collection type
     * @return {@code values}
     * @throws NullPointerException if {@code values} is {@code null}
     * @throws IllegalArgumentException if {@code values} has more than {@code max} elements
     */
    public static <T extends Collection<?>> T requireSizeAtMost(final T values, final int max, final String message) {
        Objects.requireNonNull(values, message);

        if (values.size() > max) {
            throw new IllegalArgumentException(message);
        }
        return values;
    }

    /**
     * Requires {@code map} to contain at most {@code max} entries.
     *
     * @param map map to check
     * @param max inclusive maximum size
     * @param message exception text
     * @param <K> key type
     * @param <V> value type
     * @return {@code map}
     * @throws NullPointerException if {@code map} is {@code null}
     * @throws IllegalArgumentException if {@code map} has more than {@code max} entries
     */
    public static <K, V> Map<K, V> requireSizeAtMost(final Map<K, V> map, final int max, final String message) {
        Objects.requireNonNull(map, message);

        if (map.size() > max) {
            throw new IllegalArgumentException(message);
        }
        return map;
    }

    /**
     * Requires {@code values} to contain at least {@code min} elements.
     *
     * @param values collection to check
     * @param min inclusive minimum size
     * @param message exception text
     * @param <T> collection type
     * @return {@code values}
     * @throws NullPointerException if {@code values} is {@code null}
     * @throws IllegalArgumentException if {@code values} has fewer than {@code min} elements
     */
    public static <T extends Collection<?>> T requireSizeAtLeast(final T values, final int min, final String message) {
        Objects.requireNonNull(values, message);

        if (values.size() < min) {
            throw new IllegalArgumentException(message);
        }
        return values;
    }

    /**
     * Requires {@code map} to contain at least {@code min} entries.
     *
     * @param map map to check
     * @param min inclusive minimum size
     * @param message exception text
     * @param <K> key type
     * @param <V> value type
     * @return {@code map}
     * @throws NullPointerException if {@code map} is {@code null}
     * @throws IllegalArgumentException if {@code map} has fewer than {@code min} entries
     */
    public static <K, V> Map<K, V> requireSizeAtLeast(final Map<K, V> map, final int min, final String message) {
        Objects.requireNonNull(map, message);

        if (map.size() < min) {
            throw new IllegalArgumentException(message);
        }
        return map;
    }

    /**
     * Requires {@code values} to contain exactly {@code size} elements.
     *
     * @param values collection to check
     * @param size required size
     * @param message exception text
     * @param <T> collection type
     * @return {@code values}
     * @throws NullPointerException if {@code values} is {@code null}
     * @throws IllegalArgumentException if {@code values} does not have {@code size} elements
     */
    public static <T extends Collection<?>> T requireSizeExactly(final T values, final int size, final String message) {
        Objects.requireNonNull(values, message);

        if (values.size() != size) {
            throw new IllegalArgumentException(message);
        }
        return values;
    }

    /**
     * Requires {@code map} to contain exactly {@code size} entries.
     *
     * @param map map to check
     * @param size required size
     * @param message exception text
     * @param <K> key type
     * @param <V> value type
     * @return {@code map}
     * @throws NullPointerException if {@code map} is {@code null}
     * @throws IllegalArgumentException if {@code map} does not have {@code size} entries
     */
    public static <K, V> Map<K, V> requireSizeExactly(final Map<K, V> map, final int size, final String message) {
        Objects.requireNonNull(map, message);

        if (map.size() != size) {
            throw new IllegalArgumentException(message);
        }
        return map;
    }

    /**
     * Requires {@code values} to contain between {@code min} and {@code max} elements, inclusive.
     *
     * @param values collection to check
     * @param min inclusive minimum size
     * @param max inclusive maximum size
     * @param message exception text
     * @param <T> collection type
     * @return {@code values}
     * @throws NullPointerException if {@code values} is {@code null}
     * @throws IllegalArgumentException if size is outside {@code min}..{@code max}
     */
    public static <T extends Collection<?>> T requireSizeBetween(
            final T values, final int min, final int max, final String message) {
        Objects.requireNonNull(values, message);

        if (values.size() < min) {
            throw new IllegalArgumentException(message);
        }
        if (values.size() > max) {
            throw new IllegalArgumentException(message);
        }
        return values;
    }

    /**
     * Requires {@code map} to contain between {@code min} and {@code max} entries, inclusive.
     *
     * @param map map to check
     * @param min inclusive minimum size
     * @param max inclusive maximum size
     * @param message exception text
     * @param <K> key type
     * @param <V> value type
     * @return {@code map}
     * @throws NullPointerException if {@code map} is {@code null}
     * @throws IllegalArgumentException if size is outside {@code min}..{@code max}
     */
    public static <K, V> Map<K, V> requireSizeBetween(
            final Map<K, V> map, final int min, final int max, final String message) {
        Objects.requireNonNull(map, message);

        if (map.size() < min) {
            throw new IllegalArgumentException(message);
        }
        if (map.size() > max) {
            throw new IllegalArgumentException(message);
        }
        return map;
    }

    /**
     * Requires {@code values} to contain no duplicate elements.
     *
     * <p>Uniqueness follows {@link Object#equals(Object)} and {@link Object#hashCode()} via a {@link HashSet}. Element
     * order is not changed. Two {@code null} elements count as duplicates.
     *
     * @param values collection to check
     * @param message exception text
     * @param <T> collection type
     * @return {@code values}
     * @throws NullPointerException if {@code values} is {@code null}
     * @throws IllegalArgumentException if {@code values} contains duplicates
     */
    public static <T extends Collection<?>> T requireUniqueItems(final T values, final String message) {
        Objects.requireNonNull(values, message);

        if (values.size() != new HashSet<>(values).size()) {
            throw new IllegalArgumentException(message);
        }
        return values;
    }

    /**
     * Requires {@code value} to have length at most {@code max}.
     *
     * <p>Length is {@link String#length()} (UTF-16 code units), not Unicode code points. The maximum is inclusive.
     *
     * @param value string to check
     * @param max inclusive maximum length
     * @param message exception text
     * @return {@code value}
     * @throws NullPointerException if {@code value} is {@code null}
     * @throws IllegalArgumentException if {@code value} is longer than {@code max}
     */
    public static String requireMaxLength(final String value, final int max, final String message) {
        Objects.requireNonNull(value, message);

        if (value.length() > max) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    /**
     * Requires {@code value} to have length at most {@code max} when it is non-null.
     *
     * <p>{@code null} is allowed and returned unchanged. A present value is checked with
     * {@link #requireMaxLength(String, int, String)}.
     *
     * @param value string to check, or {@code null}
     * @param max inclusive maximum length
     * @param message exception text
     * @return {@code value}
     * @throws IllegalArgumentException if {@code value} is longer than {@code max}
     */
    public static @Nullable String requireMaxLengthIfPresent(
            @Nullable final String value, final int max, final String message) {
        if (value != null) {
            requireMaxLength(value, max, message);
        }
        return value;
    }

    /**
     * Requires {@code value} to have length at least {@code min}.
     *
     * <p>Length is {@link String#length()} (UTF-16 code units), not Unicode code points. The minimum is inclusive.
     *
     * @param value string to check
     * @param min inclusive minimum length
     * @param message exception text
     * @return {@code value}
     * @throws NullPointerException if {@code value} is {@code null}
     * @throws IllegalArgumentException if {@code value} is shorter than {@code min}
     */
    public static String requireMinLength(final String value, final int min, final String message) {
        Objects.requireNonNull(value, message);

        if (value.length() < min) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    /**
     * Requires {@code value} to have length at least {@code min} when it is non-null.
     *
     * <p>{@code null} is allowed and returned unchanged. A present value is checked with
     * {@link #requireMinLength(String, int, String)}.
     *
     * @param value string to check, or {@code null}
     * @param min inclusive minimum length
     * @param message exception text
     * @return {@code value}
     * @throws IllegalArgumentException if {@code value} is shorter than {@code min}
     */
    public static @Nullable String requireMinLengthIfPresent(
            @Nullable final String value, final int min, final String message) {
        if (value != null) {
            requireMinLength(value, min, message);
        }
        return value;
    }

    /**
     * Requires {@code value} to match {@code pattern}.
     *
     * <p>Matching uses {@link java.util.regex.Matcher#find()}, so a substring match succeeds; the pattern does not have
     * to cover the entire string unless it is anchored. Use {@code ^} and {@code $} when the whole value must match.
     *
     * @param value string to check
     * @param pattern regex applied with {@link java.util.regex.Matcher#find()}
     * @param message exception text
     * @return {@code value}
     * @throws NullPointerException if {@code value} or {@code pattern} is {@code null}
     * @throws IllegalArgumentException if {@code value} does not match {@code pattern}
     */
    public static String requireMatches(final String value, final Pattern pattern, final String message) {
        Objects.requireNonNull(value, message);
        Objects.requireNonNull(pattern, "pattern must not be null");

        if (!pattern.matcher(value).find()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    /**
     * Requires {@code value} to match {@code pattern} when it is non-null.
     *
     * <p>{@code null} is allowed and returned unchanged. A present value is checked with {@link #requireMatches(String,
     * Pattern, String)}.
     *
     * @param value string to check, or {@code null}
     * @param pattern regex applied with {@link java.util.regex.Matcher#find()}
     * @param message exception text
     * @return {@code value}
     * @throws NullPointerException if {@code value} is present and {@code pattern} is {@code null}
     * @throws IllegalArgumentException if {@code value} does not match {@code pattern}
     */
    public static @Nullable String requireMatchesIfPresent(
            @Nullable final String value, final Pattern pattern, final String message) {
        if (value != null) {
            requireMatches(value, pattern, message);
        }
        return value;
    }

    /**
     * Returns {@code true} if {@code duration} is {@code null}, negative, or zero.
     *
     * @param duration duration to inspect, or {@code null}
     * @return {@code true} if {@code duration} is missing or not positive
     */
    public static boolean isNonPositive(@Nullable final Duration duration) {
        return duration == null || duration.isNegative() || duration.isZero();
    }

    /**
     * Returns {@code true} if {@code duration} is non-null and strictly positive.
     *
     * @param duration duration to inspect, or {@code null}
     * @return {@code true} if {@code duration} is positive
     */
    static boolean isPositive(@Nullable final Duration duration) {
        return duration != null && !duration.isNegative() && !duration.isZero();
    }
}
