package pl.piotrjanczukowicz.util;

import static pl.piotrjanczukowicz.util.ValidationUtils.requireNonNull;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/** Date-time formatting helpers. */
public final class DateTimeUtils {

    private DateTimeUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Formats {@code instant} as ISO-8601 UTC, truncated to whole seconds.
     *
     * @param instant instant to format
     * @return ISO-8601 UTC timestamp
     * @throws NullPointerException if {@code instant} is {@code null}
     */
    public static String toIso(final Instant instant) {
        requireNonNull(instant, "instant");

        return DateTimeFormatter.ISO_INSTANT.format(instant.truncatedTo(ChronoUnit.SECONDS));
    }
}
