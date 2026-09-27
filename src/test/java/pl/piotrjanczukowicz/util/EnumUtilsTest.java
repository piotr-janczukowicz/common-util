package pl.piotrjanczukowicz.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    void constructor_whenInstantiatedViaReflection_shouldThrow() throws Exception {
        final Constructor<EnumUtils> constructor = EnumUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertThatThrownBy(constructor::newInstance).hasCauseInstanceOf(UnsupportedOperationException.class);
    }

    @ParameterizedTest
    @MethodSource("wireHits")
    <E extends Enum<E>> void fromWire_whenWireMatches_shouldReturnConstant(
            final String wire, final Class<E> enumClass, final Function<E, String> wireValue, final E expected) {
        final Optional<E> resolved = EnumUtils.fromWire(wire, enumClass, wireValue);

        assertThat(resolved).contains(expected);
    }

    @Test
    void fromWire_whenLookupIsCached_shouldReturnConstant() {
        final String wire = "secret";
        final Class<SampleWireEnum> enumClass = SampleWireEnum.class;
        final Function<SampleWireEnum, String> wireValue = SampleWireEnum::wireValue;
        EnumUtils.fromWire(wire, enumClass, wireValue);

        final Optional<SampleWireEnum> resolved = EnumUtils.fromWire(wire, enumClass, wireValue);

        assertThat(resolved).contains(SampleWireEnum.SECRET);
    }

    @ParameterizedTest
    @MethodSource("wireMisses")
    <E extends Enum<E>> void fromWire_whenWireDoesNotMatch_shouldReturnEmpty(
            final String wire, final Class<E> enumClass, final Function<E, String> wireValue) {
        final Optional<E> resolved = EnumUtils.fromWire(wire, enumClass, wireValue);

        assertThat(resolved).isEmpty();
    }

    @Test
    void fromWire_whenValueNull_shouldThrow() {
        final String value = null;
        final Class<SampleWireEnum> enumClass = SampleWireEnum.class;
        final Function<SampleWireEnum, String> wireValue = SampleWireEnum::wireValue;

        assertThatThrownBy(() -> EnumUtils.fromWire(value, enumClass, wireValue))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null");
    }

    @Test
    void fromWire_whenEnumClassNull_shouldThrow() {
        final String value = "secret";
        final Class<SampleWireEnum> enumClass = null;
        final Function<SampleWireEnum, String> wireValue = SampleWireEnum::wireValue;

        assertThatThrownBy(() -> EnumUtils.fromWire(value, enumClass, wireValue))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("enumClass must not be null");
    }

    @Test
    void fromWire_whenWireValueNull_shouldThrow() {
        final String value = "secret";
        final Class<SampleWireEnum> enumClass = SampleWireEnum.class;
        final Function<SampleWireEnum, String> wireValue = null;

        assertThatThrownBy(() -> EnumUtils.fromWire(value, enumClass, wireValue))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("wireValue must not be null");
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
