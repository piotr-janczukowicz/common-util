package pl.piotrjanczukowicz.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.lang.reflect.Constructor;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class EnumUtilsTest {

    @Test
    void constructor_shouldThrow_whenInstantiatedViaReflection() throws Exception {
        final Constructor<EnumUtils> constructor = EnumUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final Throwable thrown = catchThrowable(constructor::newInstance);

        assertThat(thrown).hasCauseInstanceOf(UnsupportedOperationException.class);
    }

    @ParameterizedTest
    @MethodSource("wireHits")
    <E extends Enum<E>> void fromWire_shouldReturnConstant_whenWireMatches(
            final String wire, final Class<E> enumClass, final Function<E, String> wireValue, final E expected) {
        final Optional<E> resolved = EnumUtils.fromWire(wire, enumClass, wireValue);

        assertThat(resolved).contains(expected);
    }

    @Test
    void fromWire_shouldReturnConstant_whenLookupIsCached() {
        final String wire = "secret";
        final Class<SampleWireEnum> enumClass = SampleWireEnum.class;
        final Function<SampleWireEnum, String> wireValue = SampleWireEnum::wireValue;
        EnumUtils.fromWire(wire, enumClass, wireValue);

        final Optional<SampleWireEnum> resolved = EnumUtils.fromWire(wire, enumClass, wireValue);

        assertThat(resolved).contains(SampleWireEnum.SECRET);
    }

    @ParameterizedTest
    @MethodSource("wireMisses")
    <E extends Enum<E>> void fromWire_shouldReturnEmpty_whenWireDoesNotMatch(
            final String wire, final Class<E> enumClass, final Function<E, String> wireValue) {
        final Optional<E> resolved = EnumUtils.fromWire(wire, enumClass, wireValue);

        assertThat(resolved).isEmpty();
    }

    @Test
    void fromWire_shouldThrow_whenValueNull() {
        final String value = null;
        final Class<SampleWireEnum> enumClass = SampleWireEnum.class;
        final Function<SampleWireEnum, String> wireValue = SampleWireEnum::wireValue;

        final Throwable thrown = catchThrowable(() -> EnumUtils.fromWire(value, enumClass, wireValue));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessageContaining("value");
    }

    @Test
    void fromWire_shouldThrow_whenEnumClassNull() {
        final String value = "secret";
        final Class<SampleWireEnum> enumClass = null;
        final Function<SampleWireEnum, String> wireValue = SampleWireEnum::wireValue;

        final Throwable thrown = catchThrowable(() -> EnumUtils.fromWire(value, enumClass, wireValue));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessageContaining("enumClass");
    }

    @Test
    void fromWire_shouldThrow_whenWireValueNull() {
        final String value = "secret";
        final Class<SampleWireEnum> enumClass = SampleWireEnum.class;
        final Function<SampleWireEnum, String> wireValue = null;

        final Throwable thrown = catchThrowable(() -> EnumUtils.fromWire(value, enumClass, wireValue));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessageContaining("wireValue");
    }

    private static Stream<Arguments> wireHits() {
        return Stream.of(
                Arguments.of(
                        "secret",
                        SampleWireEnum.class,
                        (Function<SampleWireEnum, String>) SampleWireEnum::wireValue,
                        SampleWireEnum.SECRET),
                Arguments.of(
                        "User",
                        SampleWireEnum.class,
                        (Function<SampleWireEnum, String>) SampleWireEnum::wireValue,
                        SampleWireEnum.USER),
                Arguments.of(
                        "+1",
                        SampleWireEnum.class,
                        (Function<SampleWireEnum, String>) SampleWireEnum::wireValue,
                        SampleWireEnum.PLUS_ONE));
    }

    private static Stream<Arguments> wireMisses() {
        return Stream.of(
                Arguments.of(
                        "unknown", SampleWireEnum.class, (Function<SampleWireEnum, String>) SampleWireEnum::wireValue),
                Arguments.of(
                        "user", SampleWireEnum.class, (Function<SampleWireEnum, String>) SampleWireEnum::wireValue));
    }

    private enum SampleWireEnum {
        SECRET("secret"),
        USER("User"),
        PLUS_ONE("+1");

        private final String wireValue;

        SampleWireEnum(final String wireValue) {
            this.wireValue = wireValue;
        }

        String wireValue() {
            return wireValue;
        }
    }
}
