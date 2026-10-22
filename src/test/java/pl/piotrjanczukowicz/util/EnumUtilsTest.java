package pl.piotrjanczukowicz.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

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
import org.junit.jupiter.params.provider.ValueSource;

class EnumUtilsTest {

    @Test
    void constructor_whenInstantiatedViaReflection_shouldThrow() throws Exception {
        final Constructor<EnumUtils> constructor = EnumUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertThatThrownBy(constructor::newInstance).hasCauseInstanceOf(UnsupportedOperationException.class);
    }

    @ParameterizedTest
    @MethodSource("wireHits")
    void fromWire_whenWireMatches_shouldReturnConstant(final String wire, final SampleWireEnum expected) {
        final EnumUtils.WireIndex<SampleWireEnum> index =
                EnumUtils.index(SampleWireEnum.class, SampleWireEnum::wireValue);

        final Optional<SampleWireEnum> resolved = index.fromWire(wire);

        assertThat(resolved).contains(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {"unknown", "user", ""})
    void fromWire_whenWireDoesNotMatch_shouldReturnEmpty(final String wire) {
        final EnumUtils.WireIndex<SampleWireEnum> index =
                EnumUtils.index(SampleWireEnum.class, SampleWireEnum::wireValue);

        final Optional<SampleWireEnum> resolved = index.fromWire(wire);

        assertThat(resolved).isEmpty();
    }

    @Test
    void index_whenSameEnumIndexedWithDifferentMappings_shouldResolveEachByItsOwnMapping() {
        final EnumUtils.WireIndex<SampleWireEnum> byWire =
                EnumUtils.index(SampleWireEnum.class, SampleWireEnum::wireValue);
        final EnumUtils.WireIndex<SampleWireEnum> byName = EnumUtils.index(SampleWireEnum.class, SampleWireEnum::name);

        assertSoftly(softly -> {
            softly.assertThat(byWire.fromWire("secret")).contains(SampleWireEnum.SECRET);
            softly.assertThat(byWire.fromWire("SECRET")).isEmpty();
            softly.assertThat(byName.fromWire("SECRET")).contains(SampleWireEnum.SECRET);
            softly.assertThat(byName.fromWire("secret")).isEmpty();
        });
    }

    @Test
    void index_whenTwoConstantsShareWireValue_shouldThrow() {
        final Function<SampleWireEnum, String> constantWire = constant -> "same";

        final Throwable thrown = catchThrowable(() -> EnumUtils.index(SampleWireEnum.class, constantWire));

        assertThat(thrown).isInstanceOf(IllegalStateException.class).hasMessageContaining("same");
    }

    @Test
    void index_whenCreated_shouldApplyMappingOncePerConstant() {
        final AtomicInteger mappings = new AtomicInteger();
        final Function<SampleWireEnum, String> countingWire = constant -> {
            mappings.incrementAndGet();
            return constant.wireValue();
        };

        final EnumUtils.WireIndex<SampleWireEnum> index = EnumUtils.index(SampleWireEnum.class, countingWire);
        index.fromWire("secret");
        index.fromWire("missing");

        assertThat(mappings).hasValue(SampleWireEnum.values().length);
    }

    @Test
    void index_whenWireValueReturnsNull_shouldThrowNamingConstant() {
        final Function<SampleWireEnum, String> nullWire = constant -> null;

        final Throwable thrown = catchThrowable(() -> EnumUtils.index(SampleWireEnum.class, nullWire));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessage("wire value of SECRET must not be null");
    }

    @Test
    void indexFromWireWithDefault_whenWireMatches_shouldReturnConstant() {
        final EnumUtils.WireIndex<SampleWireEnum> index =
                EnumUtils.index(SampleWireEnum.class, SampleWireEnum::wireValue);

        final SampleWireEnum resolved = index.fromWire("secret", SampleWireEnum.USER);

        assertThat(resolved).isEqualTo(SampleWireEnum.SECRET);
    }

    @Test
    void indexFromWireWithDefault_whenWireDoesNotMatch_shouldReturnDefault() {
        final EnumUtils.WireIndex<SampleWireEnum> index =
                EnumUtils.index(SampleWireEnum.class, SampleWireEnum::wireValue);

        final SampleWireEnum resolved = index.fromWire("unknown", SampleWireEnum.USER);

        assertThat(resolved).isEqualTo(SampleWireEnum.USER);
    }

    @Test
    void indexFromWireWithDefault_whenDefaultValueNull_shouldThrow() {
        final EnumUtils.WireIndex<SampleWireEnum> index =
                EnumUtils.index(SampleWireEnum.class, SampleWireEnum::wireValue);
        final SampleWireEnum defaultValue = null;

        final Throwable thrown = catchThrowable(() -> index.fromWire("secret", defaultValue));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessage("defaultValue must not be null");
    }

    @Test
    void fromWire_whenValueNull_shouldThrow() {
        final EnumUtils.WireIndex<SampleWireEnum> index =
                EnumUtils.index(SampleWireEnum.class, SampleWireEnum::wireValue);
        final String value = null;

        final Throwable thrown = catchThrowable(() -> index.fromWire(value));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessage("value must not be null");
    }

    @Test
    void index_whenEnumClassNull_shouldThrow() {
        final Class<SampleWireEnum> enumClass = null;

        final Throwable thrown = catchThrowable(() -> EnumUtils.index(enumClass, SampleWireEnum::wireValue));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessage("enumClass must not be null");
    }

    @Test
    void index_whenWireValueNull_shouldThrow() {
        final Class<SampleWireEnum> enumClass = SampleWireEnum.class;
        final Function<SampleWireEnum, String> wireValue = null;

        final Throwable thrown = catchThrowable(() -> EnumUtils.index(enumClass, wireValue));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessage("wireValue must not be null");
    }

    @Test
    void fromWire_whenCalledConcurrently_shouldResolveCorrectly() throws Exception {
        final int threads = 16;
        final EnumUtils.WireIndex<ConcurrentEnum> index = EnumUtils.index(
                ConcurrentEnum.class, constant -> constant.name().toLowerCase(Locale.ROOT));
        final CountDownLatch start = new CountDownLatch(1);
        final ExecutorService executor = Executors.newFixedThreadPool(threads);
        try {
            final List<Future<Boolean>> results = new ArrayList<>();
            for (int i = 0; i < threads; i++) {
                results.add(executor.submit(() -> {
                    start.await();
                    boolean isCorrect = true;
                    for (int round = 0; round < 200; round++) {
                        isCorrect &= index.fromWire("one")
                                .filter(ConcurrentEnum.ONE::equals)
                                .isPresent();
                        isCorrect &= index.fromWire("missing").isEmpty();
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
    }

    private static Stream<Arguments> wireHits() {
        return Stream.of(
                Arguments.of("secret", SampleWireEnum.SECRET),
                Arguments.of("User", SampleWireEnum.USER),
                Arguments.of("+1", SampleWireEnum.PLUS_ONE));
    }

    private enum ConcurrentEnum {
        ONE,
        TWO
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
