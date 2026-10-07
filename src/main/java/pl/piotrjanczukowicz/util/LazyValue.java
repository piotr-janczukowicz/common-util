package pl.piotrjanczukowicz.util;

import static pl.piotrjanczukowicz.util.ValidationUtils.requireNonNull;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;

/**
 * Thread-safe memoized supplier. A raced double construction keeps the first value.
 *
 * @param <T> value type
 */
public final class LazyValue<T> {

    private final AtomicReference<@Nullable T> value = new AtomicReference<>();
    private final Supplier<T> supplier;

    /**
     * Memoizes {@code supplier}.
     *
     * @param supplier value factory
     * @throws NullPointerException if {@code supplier} is {@code null}
     */
    public LazyValue(final Supplier<T> supplier) {
        this.supplier = requireNonNull(supplier, "supplier");
    }

    /**
     * Cached value, creating it on the first call.
     *
     * @return supplier result
     * @throws NullPointerException if the supplier returns {@code null}
     */
    public T get() {
        T result = value.get();
        if (result == null) {
            final T created = requireNonNull(supplier.get(), "value");
            result = value.compareAndSet(null, created) ? created : Objects.requireNonNull(value.get(), "value");
        }
        return result;
    }
}
