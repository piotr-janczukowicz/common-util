package pl.piotrjanczukowicz.util;

import static pl.piotrjanczukowicz.util.ValidationUtils.requireNonNull;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Enum lookup helpers. */
public final class EnumUtils {

    private static final ConcurrentHashMap<Class<? extends Enum<?>>, Map<String, ? extends Enum<?>>> BY_WIRE =
            new ConcurrentHashMap<>();

    private EnumUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Returns the constant of {@code enumClass} whose {@code wireValue} equals {@code value}.
     *
     * @param value wire string to match
     * @param enumClass enum type
     * @param wireValue mapping from constant to wire string
     * @param <E> enum type
     * @return matching constant, or empty if none match
     * @throws NullPointerException if any argument is {@code null}
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
