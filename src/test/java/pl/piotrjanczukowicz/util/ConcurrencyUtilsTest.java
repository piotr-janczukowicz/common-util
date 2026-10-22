package pl.piotrjanczukowicz.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.InterruptedIOException;
import java.lang.reflect.Constructor;
import java.time.Duration;
import org.junit.jupiter.api.Test;

class ConcurrencyUtilsTest {

    @Test
    void constructor_shouldThrow_whenInstantiatedViaReflection() throws Exception {
        final Constructor<ConcurrencyUtils> constructor = ConcurrencyUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertThatThrownBy(constructor::newInstance).hasCauseInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void sleepInterruptibly_shouldReturnWithoutSleeping_whenDelayZero() throws InterruptedIOException {
        Thread.currentThread().interrupt();
        try {
            ConcurrencyUtils.sleepInterruptibly(Duration.ZERO, "interrupted");

            assertThat(Thread.currentThread().isInterrupted()).isTrue();
        } finally {
            Thread.interrupted();
        }
    }

    @Test
    void sleepInterruptibly_shouldSleepForDelay_whenDelayPositive() throws InterruptedIOException {
        final long startedNanos = System.nanoTime();

        ConcurrencyUtils.sleepInterruptibly(Duration.ofMillis(50), "interrupted");

        assertThat(Duration.ofNanos(System.nanoTime() - startedNanos)).isGreaterThanOrEqualTo(Duration.ofMillis(20));
    }

    @Test
    void sleepInterruptibly_shouldThrowInterruptedIoWithCause_whenThreadAlreadyInterrupted() {
        Thread.currentThread().interrupt();
        try {
            final Duration delay = Duration.ofSeconds(1);

            assertThatThrownBy(() -> ConcurrencyUtils.sleepInterruptibly(delay, "waiting stopped"))
                    .isInstanceOf(InterruptedIOException.class)
                    .hasMessage("waiting stopped")
                    .hasCauseInstanceOf(InterruptedException.class);
            assertThat(Thread.interrupted()).isTrue();
        } finally {
            Thread.interrupted();
        }
    }

    @Test
    void sleepInterruptibly_shouldThrowNullPointerException_whenDelayNull() {
        final Duration delay = null;

        assertThatThrownBy(() -> ConcurrencyUtils.sleepInterruptibly(delay, "waiting stopped"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("delay must not be null");
    }

    @Test
    void sleepInterruptibly_shouldThrowIllegalArgumentException_whenDelayNegative() {
        final Duration delay = Duration.ofSeconds(-1);

        assertThatThrownBy(() -> ConcurrencyUtils.sleepInterruptibly(delay, "waiting stopped"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("delay must not be negative");
    }

    @Test
    void sleepInterruptibly_shouldThrowNullPointerException_whenMessageNull() {
        final String message = null;

        assertThatThrownBy(() -> ConcurrencyUtils.sleepInterruptibly(Duration.ZERO, message))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("message must not be null");
    }

    @Test
    void sleepInterruptibly_shouldThrowIllegalArgumentException_whenMessageBlank() {
        assertThatThrownBy(() -> ConcurrencyUtils.sleepInterruptibly(Duration.ZERO, " "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("message must not be blank");
    }

    @Test
    void toInterruptedIo_shouldReturnExceptionWithMessageAndCause_whenCalled() {
        final InterruptedException interrupted = new InterruptedException("stop");

        try {
            final InterruptedIOException failure = ConcurrencyUtils.toInterruptedIo(interrupted, "gave up");

            assertThat(failure).hasMessage("gave up").hasCause(interrupted);
        } finally {
            Thread.interrupted();
        }
    }

    @Test
    void toInterruptedIo_shouldRestoreInterruptFlag_whenCalled() {
        final InterruptedException interrupted = new InterruptedException("stop");

        try {
            ConcurrencyUtils.toInterruptedIo(interrupted, "gave up");

            assertThat(Thread.interrupted()).isTrue();
        } finally {
            Thread.interrupted();
        }
    }

    @Test
    void toInterruptedIo_shouldThrowNullPointerException_whenInterruptedNull() {
        final InterruptedException interrupted = null;

        assertThatThrownBy(() -> ConcurrencyUtils.toInterruptedIo(interrupted, "gave up"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("interrupted must not be null");
    }

    @Test
    void toInterruptedIo_shouldThrowIllegalArgumentException_whenMessageBlank() {
        final InterruptedException interrupted = new InterruptedException("stop");

        assertThatThrownBy(() -> ConcurrencyUtils.toInterruptedIo(interrupted, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("message must not be blank");
    }
}
