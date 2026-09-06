package pl.piotrjanczukowicz.util;

import static pl.piotrjanczukowicz.util.ValidationUtils.requireNonNull;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Date-time formatting helpers.
 *
 * <p>Converts {@link Instant} values to ISO-8601 UTC text ({@code Z} offset), not a local time zone.
 */
public final class DateTimeUtils {

    private DateTimeUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Formats {@code instant} as ISO-8601 UTC, truncated to whole seconds.
     *
     * <p>Nanos are discarded with {@link ChronoUnit#SECONDS} (not rounded), then formatted with
     * {@link DateTimeFormatter#ISO_INSTANT}. For example {@code 2024-01-01T00:00:00.123456789Z} becomes
     * {@code 2024-01-01T00:00:00Z}.
     *
     * @param instant instant to format
     * @return ISO-8601 UTC timestamp with second precision
     * @throws NullPointerException if {@code instant} is {@code null}
     */
    public static String toIso(final Instant instant) {
        requireNonNull(instant, "instant");

        return DateTimeFormatter.ISO_INSTANT.format(instant.truncatedTo(ChronoUnit.SECONDS));
    }
}
