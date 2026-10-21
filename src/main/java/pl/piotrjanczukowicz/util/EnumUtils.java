package pl.piotrjanczukowicz.util;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Enum lookup helpers.
 *
 * <p>Resolves enum constants from external strings that may differ from {@link Enum#name()}. Lookups are exact string
 * matches and are cached per enum type after the first call.
 */
public final class EnumUtils {

    private static final ConcurrentMap<Class<? extends Enum<?>>, Map<String, ? extends Enum<?>>> BY_WIRE =
            new ConcurrentHashMap<>();

    private EnumUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Returns the constant of {@code enumClass} whose mapped wire string equals {@code value}.
     *
     * <p>{@code wireValue} is applied to every constant on first use and cached for that enum type. Matching is
     * case-sensitive {@link String} equality, not {@link Enum#valueOf(Class, String)}. Missing values yield
     * {@link Optional#empty()}. Duplicate wire strings on one enum type fail on first lookup with
     * {@link IllegalStateException}.
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
        Objects.requireNonNull(value, "value must not be null");
        Objects.requireNonNull(enumClass, "enumClass must not be null");
        Objects.requireNonNull(wireValue, "wireValue must not be null");

        return Optional.ofNullable(index(enumClass, wireValue).get(value));
    }

    /**
     * Returns the constant of {@code enumClass} whose mapped wire string equals {@code value}, or {@code defaultValue}
     * when no constant matches.
     *
     * <p>Same matching and caching as {@link #fromWire(String, Class, Function)}.
     *
     * @param value wire string to match
     * @param enumClass enum type
     * @param wireValue mapping from constant to wire string
     * @param defaultValue constant returned when nothing matches
     * @param <E> enum type
     * @return matching constant, or {@code defaultValue}
     * @throws NullPointerException if any argument is {@code null}
     * @throws IllegalStateException if two constants map to the same wire string
     */
    public static <E extends Enum<E>> E fromWire(
            final String value, final Class<E> enumClass, final Function<E, String> wireValue, final E defaultValue) {
        Objects.requireNonNull(defaultValue, "defaultValue must not be null");

        return fromWire(value, enumClass, wireValue).orElse(defaultValue);
    }

    @SuppressWarnings("unchecked")
    private static <E extends Enum<E>> Map<String, E> index(
            final Class<E> enumClass, final Function<E, String> wireValue) {
        // get() first: computeIfAbsent can take the bin monitor even when the key is already present.
        Map<String, ? extends Enum<?>> index = BY_WIRE.get(enumClass);
        if (index == null) {
            index = BY_WIRE.computeIfAbsent(
                    enumClass,
                    unused -> Stream.of(enumClass.getEnumConstants())
                            .collect(Collectors.toUnmodifiableMap(wireValue, Function.identity())));
        }
        return (Map<String, E>) index;
    }
}
