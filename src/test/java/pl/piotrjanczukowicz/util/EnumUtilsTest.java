package pl.piotrjanczukowicz.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
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

    @Test
    void fromWire_whenCalledConcurrently_shouldResolveCorrectlyAndBuildEachIndexOnce() throws Exception {
        final int threads = 16;
        final AtomicInteger firstMappings = new AtomicInteger();
        final AtomicInteger secondMappings = new AtomicInteger();
        final Function<ConcurrentEnumA, String> firstWire = constant -> {
            firstMappings.incrementAndGet();
            return constant.name().toLowerCase(Locale.ROOT);
        };
        final Function<ConcurrentEnumB, String> secondWire = constant -> {
            secondMappings.incrementAndGet();
            return "b-" + constant.ordinal();
        };
        final CountDownLatch start = new CountDownLatch(1);
        final ExecutorService executor = Executors.newFixedThreadPool(threads);
        try {
            final List<Future<Boolean>> results = new ArrayList<>();
            for (int i = 0; i < threads; i++) {
                results.add(executor.submit(() -> {
                    start.await();
                    boolean isCorrect = true;
                    for (int round = 0; round < 200; round++) {
                        isCorrect &= EnumUtils.fromWire("one", ConcurrentEnumA.class, firstWire)
                                .filter(ConcurrentEnumA.ONE::equals)
                                .isPresent();
                        isCorrect &= EnumUtils.fromWire("b-2", ConcurrentEnumB.class, secondWire)
                                .filter(ConcurrentEnumB.THIRD::equals)
                                .isPresent();
                        isCorrect &= EnumUtils.fromWire("missing", ConcurrentEnumA.class, firstWire)
                                .isEmpty();
                    }
                    return isCorrect;
                }));
            }

            start.countDown();

            for (final Future<Boolean> result : results) {
                assertThat(result.get(30, TimeUnit.SECONDS)).isTrue();
            }
        } finally {
            executor.shutdownNow();
        }
        assertThat(firstMappings).hasValue(ConcurrentEnumA.values().length);
        assertThat(secondMappings).hasValue(ConcurrentEnumB.values().length);
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

    private enum ConcurrentEnumA {
        ONE,
        TWO
    }

    private enum ConcurrentEnumB {
        FIRST,
        SECOND,
        THIRD
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
