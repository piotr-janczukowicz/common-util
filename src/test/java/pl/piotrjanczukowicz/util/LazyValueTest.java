package pl.piotrjanczukowicz.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class LazyValueTest {

    @Test
    void get_whenCalledTwice_shouldCreateValueOnce() {
        final AtomicInteger calls = new AtomicInteger();
        final LazyValue<String> lazy = new LazyValue<>(() -> "value" + calls.incrementAndGet());

        final String first = lazy.get();
        final String second = lazy.get();

        assertThat(first).isEqualTo("value1").isSameAs(second);
        assertThat(calls).hasValue(1);
    }

    @Test
    void constructor_whenSupplierIsNull_shouldThrow() {
        assertThatThrownBy(() -> new LazyValue<>(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("supplier");
    }

    @Test
    void get_whenSupplierReturnsNull_shouldThrow() {
        final LazyValue<String> lazy = new LazyValue<>(() -> null);

        assertThatThrownBy(lazy::get).isInstanceOf(NullPointerException.class).hasMessageContaining("value");
    }

    @Test
    void get_whenSupplierRaces_shouldKeepFirstValue() throws Exception {
        final CountDownLatch started = new CountDownLatch(2);
        final CountDownLatch release = new CountDownLatch(1);
        final AtomicInteger calls = new AtomicInteger();
        final LazyValue<String> lazy = new LazyValue<>(() -> {
            calls.incrementAndGet();
            started.countDown();
            try {
                assertThat(release.await(5, TimeUnit.SECONDS)).isTrue();
            } catch (final InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(ex);
            }
            return "value";
        });
        final ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            final Future<String> left = pool.submit(lazy::get);
            final Future<String> right = pool.submit(lazy::get);
            assertThat(started.await(5, TimeUnit.SECONDS)).isTrue();
            release.countDown();

            assertThat(left.get()).isSameAs(right.get()).isEqualTo("value");
            assertThat(calls).hasValue(2);
            assertThat(lazy.get()).isEqualTo("value");
            assertThat(calls).hasValue(2);
        } finally {
            pool.shutdown();
        }
    }
}
