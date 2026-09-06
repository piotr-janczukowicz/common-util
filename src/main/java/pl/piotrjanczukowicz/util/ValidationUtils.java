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
 * instance (so the result can be assigned) or throws. {@code name} is included in the exception message so the failing
 * argument can be identified. {@code null} is rejected with {@link NullPointerException}; other violations use
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
     * @param name argument name included in the exception message
     * @param <T> value type
     * @return {@code value}
     * @throws NullPointerException if {@code value} is {@code null}
     */
    @SuppressFBWarnings(
            value = "NP_PARAMETER_MUST_BE_NONNULL_BUT_MARKED_AS_NULLABLE",
            justification = "null is rejected with NullPointerException")
    public static <T> T requireNonNull(@Nullable final T value, final String name) {
        return Objects.requireNonNull(value, name);
    }

    /**
     * Requires {@code value} to be non-null and not blank.
     *
     * <p>Blank follows {@link String#isBlank()}: empty or only Unicode whitespace. Surrounding whitespace is not
     * stripped; a value that contains non-whitespace still passes even if it also has spaces.
     *
     * @param value value to check
     * @param name argument name included in the exception message
     * @return {@code value}
     * @throws NullPointerException if {@code value} is {@code null}
     * @throws IllegalArgumentException if {@code value} is blank
     */
    public static String requireNotBlank(@Nullable final String value, final String name) {
        final String nonNull = requireNonNull(value, name);
        if (nonNull.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return nonNull;
    }

    /**
     * Requires {@code duration} to be strictly positive.
     *
     * @param duration duration to check
     * @param name argument name included in the exception message
     * @return {@code duration}
     * @throws NullPointerException if {@code duration} is {@code null}
     * @throws IllegalArgumentException if {@code duration} is negative or zero
     */
    public static Duration requirePositive(final Duration duration, final String name) {
        requireNonNull(duration, name);
        if (!isPositive(duration)) {
            throw new IllegalArgumentException(name + " must be positive");
        }
        return duration;
    }

    /**
     * Requires {@code duration} to be zero or positive.
     *
     * @param duration duration to check
     * @param name argument name included in the exception message
     * @return {@code duration}
     * @throws NullPointerException if {@code duration} is {@code null}
     * @throws IllegalArgumentException if {@code duration} is negative
     */
    public static Duration requireNonNegative(final Duration duration, final String name) {
        requireNonNull(duration, name);
        if (duration.isNegative()) {
            throw new IllegalArgumentException(name + " must not be negative");
        }
        return duration;
    }

    /**
     * Requires {@code value} to be at least {@code minimum}.
     *
     * @param value value to check
     * @param minimum inclusive lower bound
     * @param name argument name included in the exception message
     * @return {@code value}
     * @throws IllegalArgumentException if {@code value} is below {@code minimum}
     */
    public static int requireAtLeast(final int value, final int minimum, final String name) {
        if (value < minimum) {
            throw new IllegalArgumentException(name + " must be >= " + minimum);
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
     * @param name argument name included in the exception message
     * @return {@code value}
     * @throws IllegalArgumentException if {@code value} is non-finite or below {@code minimum}
     */
    public static double requireAtLeast(final double value, final double minimum, final String name) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException(name + " must be a finite number");
        }
        if (value < minimum) {
            throw new IllegalArgumentException(name + " must be >= " + minimum);
        }
        return value;
    }

    /**
     * Requires {@code value} to be at most {@code maximum}.
     *
     * @param value value to check
     * @param maximum inclusive upper bound
     * @param name argument name included in the exception message
     * @return {@code value}
     * @throws IllegalArgumentException if {@code value} is above {@code maximum}
     */
    public static int requireAtMost(final int value, final int maximum, final String name) {
        if (value > maximum) {
            throw new IllegalArgumentException(name + " must be <= " + maximum);
        }
        return value;
    }

    /**
     * Requires {@code value} to be finite and at most {@code maximum}.
     *
     * @param value value to check
     * @param maximum inclusive upper bound
     * @param name argument name included in the exception message
     * @return {@code value}
     * @throws IllegalArgumentException if {@code value} is non-finite or above {@code maximum}
     */
    public static double requireAtMost(final double value, final double maximum, final String name) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException(name + " must be a finite number");
        }
        if (value > maximum) {
            throw new IllegalArgumentException(name + " must be <= " + maximum);
        }
        return value;
    }

    /**
     * Requires {@code value} to be finite and within {@code minimum} and {@code maximum}, inclusive.
     *
     * @param value value to check
     * @param minimum inclusive lower bound
     * @param maximum inclusive upper bound
     * @param name argument name included in the exception message
     * @return {@code value}
     * @throws IllegalArgumentException if {@code value} is non-finite or outside the range
     */
    public static double requireBetween(
            final double value, final double minimum, final double maximum, final String name) {
        requireAtLeast(value, minimum, name);
        return requireAtMost(value, maximum, name);
    }

    /**
     * Requires {@code value} to be at least {@code minimum}.
     *
     * @param value duration to check
     * @param minimum inclusive lower bound
     * @param name argument name included in the exception message
     * @throws NullPointerException if {@code value} or {@code minimum} is {@code null}
     * @throws IllegalArgumentException if {@code value} is below {@code minimum}
     */
    public static void requireGreaterOrEqual(final Duration value, final Duration minimum, final String name) {
        requireNonNull(value, name);
        requireNonNull(minimum, "minimum");
        if (value.compareTo(minimum) < 0) {
            throw new IllegalArgumentException(name + " must be >= " + minimum);
        }
    }

    /**
     * Requires {@code values} to be non-null and non-empty.
     *
     * @param values collection to check
     * @param name argument name included in the exception message
     * @param <T> collection type
     * @return {@code values}
     * @throws NullPointerException if {@code values} is {@code null}
     * @throws IllegalArgumentException if {@code values} is empty
     */
    public static <T extends Collection<?>> T requireNonEmpty(final T values, final String name) {
        requireNonNull(values, name);
        if (values.isEmpty()) {
            throw new IllegalArgumentException(name + " must not be empty");
        }
        return values;
    }

    /**
     * Requires {@code values} to contain at most {@code max} elements.
     *
     * @param values collection to check
     * @param max inclusive maximum size
     * @param name argument name included in the exception message
     * @param <T> collection type
     * @return {@code values}
     * @throws NullPointerException if {@code values} is {@code null}
     * @throws IllegalArgumentException if {@code values} has more than {@code max} elements
     */
    public static <T extends Collection<?>> T requireSizeAtMost(final T values, final int max, final String name) {
        requireNonNull(values, name);
        if (values.size() > max) {
            throw new IllegalArgumentException(name + " must contain at most " + max + " items");
        }
        return values;
    }

    /**
     * Requires {@code values} to contain at least {@code min} elements.
     *
     * @param values collection to check
     * @param min inclusive minimum size
     * @param name argument name included in the exception message
     * @param <T> collection type
     * @return {@code values}
     * @throws NullPointerException if {@code values} is {@code null}
     * @throws IllegalArgumentException if {@code values} has fewer than {@code min} elements
     */
    public static <T extends Collection<?>> T requireSizeAtLeast(final T values, final int min, final String name) {
        requireNonNull(values, name);
        if (values.size() < min) {
            throw new IllegalArgumentException(name + " must contain at least " + min + " items");
        }
        return values;
    }

    /**
     * Requires {@code values} to contain exactly {@code size} elements.
     *
     * @param values collection to check
     * @param size required size
     * @param name argument name included in the exception message
     * @param <T> collection type
     * @return {@code values}
     * @throws NullPointerException if {@code values} is {@code null}
     * @throws IllegalArgumentException if {@code values} does not have {@code size} elements
     */
    public static <T extends Collection<?>> T requireSizeExactly(final T values, final int size, final String name) {
        requireNonNull(values, name);
        if (values.size() != size) {
            throw new IllegalArgumentException(name + " must contain exactly " + size + " items");
        }
        return values;
    }

    /**
     * Requires {@code values} to contain between {@code min} and {@code max} elements, inclusive.
     *
     * @param values collection to check
     * @param min inclusive minimum size
     * @param max inclusive maximum size
     * @param name argument name included in the exception message
     * @param <T> collection type
     * @return {@code values}
     * @throws NullPointerException if {@code values} is {@code null}
     * @throws IllegalArgumentException if size is outside {@code min}..{@code max}
     */
    public static <T extends Collection<?>> T requireSizeBetween(
            final T values, final int min, final int max, final String name) {
        requireNonNull(values, name);
        if (values.size() < min) {
            throw new IllegalArgumentException(name + " must contain at least " + min + " items");
        }
        if (values.size() > max) {
            throw new IllegalArgumentException(name + " must contain at most " + max + " items");
        }
        return values;
    }

    /**
     * Requires {@code values} to contain no duplicate elements.
     *
     * <p>Uniqueness follows {@link Object#equals(Object)} and {@link Object#hashCode()} via a {@link HashSet}. Element
     * order is not changed. Two {@code null} elements count as duplicates.
     *
     * @param values collection to check
     * @param name argument name included in the exception message
     * @param <T> collection type
     * @return {@code values}
     * @throws NullPointerException if {@code values} is {@code null}
     * @throws IllegalArgumentException if {@code values} contains duplicates
     */
    public static <T extends Collection<?>> T requireUniqueItems(final T values, final String name) {
        requireNonNull(values, name);
        if (values.size() != new HashSet<>(values).size()) {
            throw new IllegalArgumentException(name + " must not contain duplicates");
        }
        return values;
    }

    /**
     * Requires {@code map} to contain at most {@code max} entries.
     *
     * @param map map to check
     * @param max inclusive maximum size
     * @param name argument name included in the exception message
     * @param <K> key type
     * @param <V> value type
     * @return {@code map}
     * @throws NullPointerException if {@code map} is {@code null}
     * @throws IllegalArgumentException if {@code map} has more than {@code max} entries
     */
    public static <K, V> Map<K, V> requireMapSizeAtMost(final Map<K, V> map, final int max, final String name) {
        requireNonNull(map, name);
        if (map.size() > max) {
            throw new IllegalArgumentException(name + " must contain at most " + max + " properties");
        }
        return map;
    }

    /**
     * Requires {@code value} to have length at most {@code max}.
     *
     * <p>Length is {@link String#length()} (UTF-16 code units), not Unicode code points. The maximum is inclusive.
     *
     * @param value string to check
     * @param max inclusive maximum length
     * @param name argument name included in the exception message
     * @return {@code value}
     * @throws NullPointerException if {@code value} is {@code null}
     * @throws IllegalArgumentException if {@code value} is longer than {@code max}
     */
    public static String requireMaxLength(final String value, final int max, final String name) {
        requireNonNull(value, name);
        if (value.length() > max) {
            throw new IllegalArgumentException(name + " must be at most " + max + " characters");
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
     * @param name argument name included in the exception message
     * @return {@code value}
     * @throws IllegalArgumentException if {@code value} is longer than {@code max}
     */
    public static @Nullable String requireMaxLengthWhenPresent(
            @Nullable final String value, final int max, final String name) {
        if (value != null) {
            requireMaxLength(value, max, name);
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
     * @param name argument name included in the exception message
     * @return {@code value}
     * @throws NullPointerException if {@code value} or {@code pattern} is {@code null}
     * @throws IllegalArgumentException if {@code value} does not match {@code pattern}
     */
    public static String requireMatches(final String value, final Pattern pattern, final String name) {
        requireNonNull(value, name);
        requireNonNull(pattern, "pattern");
        if (!pattern.matcher(value).find()) {
            throw new IllegalArgumentException(name + " must match " + pattern.pattern());
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
