package pl.piotrjanczukowicz.util;

import static pl.piotrjanczukowicz.util.ValidationUtils.requireNonNull;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Enum lookup helpers.
 *
 * <p>Resolves enum constants from external strings that may differ from {@link Enum#name()} (API payloads, config keys,
 * serialized wire values). Lookups are exact string matches and are cached per enum type after the first call.
 */
public final class EnumUtils {

    private static final ConcurrentHashMap<Class<? extends Enum<?>>, Map<String, ? extends Enum<?>>> BY_WIRE =
            new ConcurrentHashMap<>();

    private EnumUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Returns the constant of {@code enumClass} whose mapped wire string equals {@code value}.
     *
     * <p>{@code wireValue} is applied to every constant of {@code enumClass} on first use; the resulting map is reused
     * for later calls with the same enum type. Matching is case-sensitive {@link String} equality, not
     * {@link Enum#valueOf(Class, String)}. Missing values yield {@link Optional#empty()}, not an exception. Duplicate
     * wire strings on one enum type fail on first lookup with {@link IllegalStateException}.
     *
     * @param value wire string to match
     * @param enumClass enum type
     * @param wireValue mapping from constant to wire string
     * @param <E> enum type
     * @return matching constant, or empty if none match
     * @throws NullPointerException if any argument is {@code null}
     * @throws IllegalStateException if two constants map to the same wire string
     */
    public static <E extends Enum<E>> Optional<E> fromWire(
            final String value, final Class<E> enumClass, final Function<E, String> wireValue) {
        requireNonNull(value, "value");
        requireNonNull(enumClass, "enumClass");
        requireNonNull(wireValue, "wireValue");

        return Optional.ofNullable(index(enumClass, wireValue).get(value));
    }

    @SuppressWarnings("unchecked")
    private static <E extends Enum<E>> Map<String, E> index(
            final Class<E> enumClass, final Function<E, String> wireValue) {
        return (Map<String, E>) BY_WIRE.computeIfAbsent(
                enumClass,
                unused -> Stream.of(enumClass.getEnumConstants())
                        .collect(Collectors.toUnmodifiableMap(wireValue, Function.identity())));
    }
}
