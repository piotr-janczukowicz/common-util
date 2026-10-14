package pl.piotrjanczukowicz.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class IoUtilsTest {

    @Test
    void constructor_shouldThrow_whenInstantiatedViaReflection() throws Exception {
        final Constructor<IoUtils> constructor = IoUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertThatThrownBy(constructor::newInstance).hasCauseInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void maxBufferBytes_shouldBeIntegerMaxMinusEight_whenRead() {
        assertThat(IoUtils.MAX_BUFFER_BYTES).isEqualTo(Integer.MAX_VALUE - 8L);
    }

    @Test
    void readAllBounded_shouldReturnEmpty_whenStreamEmpty() throws IOException {
        final byte[] read = IoUtils.readAllBounded(new ByteArrayInputStream(new byte[0]), 10);

        assertThat(read).isEmpty();
    }

    @Test
    void readAllBounded_shouldReturnAllBytes_whenLengthEqualsLimit() throws IOException {
        final byte[] body = sequence(20_000);

        final byte[] read = IoUtils.readAllBounded(new ByteArrayInputStream(body), 20_000);

        assertThat(read).isEqualTo(body);
    }

    @Test
    void readAllBounded_shouldThrowLimitException_whenOneByteOverLimit() {
        final InputStream in = new ByteArrayInputStream(new byte[20_001]);

        assertThatThrownBy(() -> IoUtils.readAllBounded(in, 20_000))
                .isInstanceOfSatisfying(
                        BoundedReadLimitException.class,
                        exception -> assertThat(exception.limit()).isEqualTo(20_000));
    }

    @Test
    void readAllBounded_shouldReturnAllBytes_whenHintSmallerThanBody() throws IOException {
        final byte[] body = sequence(100);

        final byte[] read = IoUtils.readAllBounded(new ByteArrayInputStream(body), 100, 10);

        assertThat(read).isEqualTo(body);
    }

    @Test
    void readAllBounded_shouldReturnAllBytes_whenHintLargerThanBody() throws IOException {
        final byte[] body = sequence(10);

        final byte[] read = IoUtils.readAllBounded(new ByteArrayInputStream(body), 100, 90);

        assertThat(read).isEqualTo(body);
    }

    @Test
    void readAllBounded_shouldReturnAllBytes_whenHintExceedsFirstAllocationCap() throws IOException {
        final byte[] body = sequence(3);

        final byte[] read = IoUtils.readAllBounded(new ByteArrayInputStream(body), 64L * 1024 * 1024, 1L << 40);

        assertThat(read).isEqualTo(body);
    }

    @Test
    void readAllBounded_shouldReturnAllBytes_whenHintZero() throws IOException {
        final byte[] body = sequence(100_000);

        final byte[] read = IoUtils.readAllBounded(new ByteArrayInputStream(body), 200_000, 0);

        assertThat(read).isEqualTo(body);
    }

    @Test
    void readAllBounded_shouldReturnAllBytes_whenHintNegative() throws IOException {
        final byte[] body = sequence(7);

        final byte[] read = IoUtils.readAllBounded(new ByteArrayInputStream(body), 16, -1);

        assertThat(read).isEqualTo(body);
    }

    @Test
    void readAllBounded_shouldReturnAllBytes_whenBodyLargerThanOneChunk() throws IOException {
        final byte[] body = sequence(100_000);

        final byte[] read = IoUtils.readAllBounded(new ByteArrayInputStream(body), 200_000);

        assertThat(read).isEqualTo(body);
    }

    @Test
    void readAllBounded_shouldReturnAllBytes_whenBodyExactlyFillsBufferWithHint() throws IOException {
        final byte[] body = sequence(100);

        final byte[] read = IoUtils.readAllBounded(new ByteArrayInputStream(body), 100, 100);

        assertThat(read).isEqualTo(body);
    }

    @Test
    void readAllBounded_shouldGrowBuffer_whenBodyLongerThanHint() throws IOException {
        final byte[] body = sequence(100);

        final byte[] read = IoUtils.readAllBounded(new ByteArrayInputStream(body), 100, 10);

        assertThat(read).hasSize(100).isEqualTo(body);
    }

    @Test
    void readAllBounded_shouldThrowLimitException_whenBodyLongerThanHintAndLimit() {
        final InputStream in = new ByteArrayInputStream(new byte[200]);

        assertThatThrownBy(() -> IoUtils.readAllBounded(in, 100, 50))
                .isInstanceOfSatisfying(
                        BoundedReadLimitException.class,
                        exception -> assertThat(exception.limit()).isEqualTo(100));
    }

    @Test
    void readAllBounded_shouldThrowLimitException_whenHintFillsLimitAndOneByteFollows() {
        final InputStream in = new ByteArrayInputStream(new byte[65]);

        assertThatThrownBy(() -> IoUtils.readAllBounded(in, 64, 64)).isInstanceOf(BoundedReadLimitException.class);
    }

    @Test
    void readAllBounded_shouldReturnAllBytes_whenStreamDeliversOneByteAtATime() throws IOException {
        final byte[] body = sequence(20_000);

        final byte[] chunked = IoUtils.readAllBounded(new TrickleStream(body), 100_000);
        final byte[] sized = IoUtils.readAllBounded(new TrickleStream(body), 100_000, body.length);

        assertThat(chunked).isEqualTo(body);
        assertThat(sized).isEqualTo(body);
    }

    @Test
    void readAllBounded_shouldNotCloseStream_whenReturning() throws IOException {
        final CloseTrackingStream in = new CloseTrackingStream(sequence(5));

        IoUtils.readAllBounded(in, 10);

        assertThat(in.isClosed()).isFalse();
    }

    @Test
    void readAllBounded_shouldReturnEmpty_whenLimitZeroAndStreamEmpty() throws IOException {
        final byte[] read = IoUtils.readAllBounded(new ByteArrayInputStream(new byte[0]), 0);

        assertThat(read).isEmpty();
    }

    @Test
    void readAllBounded_shouldThrowLimitException_whenLimitZeroAndStreamHasByte() {
        final InputStream in = new ByteArrayInputStream(new byte[1]);

        assertThatThrownBy(() -> IoUtils.readAllBounded(in, 0))
                .isInstanceOfSatisfying(
                        BoundedReadLimitException.class,
                        exception -> assertThat(exception.limit()).isZero());
    }

    @Test
    void readAllBounded_shouldReturnAllBytes_whenLimitLongMaxValue() throws IOException {
        final byte[] body = sequence(50);

        final byte[] read = IoUtils.readAllBounded(new ByteArrayInputStream(body), Long.MAX_VALUE);

        assertThat(read).isEqualTo(body);
    }

    @Test
    void readAllBounded_shouldThrowNullPointerException_whenStreamNull() {
        final InputStream in = null;

        assertThatThrownBy(() -> IoUtils.readAllBounded(in, 10))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("stream must not be null");
    }

    @Test
    void readAllBounded_shouldThrowNullPointerException_whenStreamNullWithHint() {
        final InputStream in = null;

        assertThatThrownBy(() -> IoUtils.readAllBounded(in, 10, 5))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("stream must not be null");
    }

    @Test
    void readAllBounded_shouldThrowIllegalArgumentException_whenLimitNegative() {
        final InputStream in = new ByteArrayInputStream(new byte[1]);

        assertThatThrownBy(() -> IoUtils.readAllBounded(in, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("maxBytes must not be negative");
    }

    @Test
    void readAllBounded_shouldThrowIllegalArgumentException_whenLimitNegativeWithHint() {
        final InputStream in = new ByteArrayInputStream(new byte[1]);

        assertThatThrownBy(() -> IoUtils.readAllBounded(in, -1, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("maxBytes must not be negative");
    }

    @Test
    void closeSuppressing_shouldCloseResourceAndLeaveFailureUntouched_whenCloseSucceeds() {
        final CloseTrackingStream resource = new CloseTrackingStream(new byte[0]);
        final IOException failure = new IOException("original");

        IoUtils.closeSuppressing(resource, failure);

        assertThat(resource.isClosed()).isTrue();
        assertThat(failure.getSuppressed()).isEmpty();
    }

    @Test
    void closeSuppressing_shouldAddCloseFailureAsSuppressed_whenCloseThrows() {
        final IOException closeFailure = new IOException("close failed");
        final AutoCloseable resource = () -> {
            throw closeFailure;
        };
        final IOException failure = new IOException("original");

        IoUtils.closeSuppressing(resource, failure);

        assertThat(failure.getSuppressed()).containsExactly(closeFailure);
    }

    @Test
    void closeSuppressing_shouldAddRuntimeCloseFailureAsSuppressed_whenCloseThrowsRuntimeException() {
        final IllegalStateException closeFailure = new IllegalStateException("close failed");
        final AutoCloseable resource = () -> {
            throw closeFailure;
        };
        final IOException failure = new IOException("original");

        IoUtils.closeSuppressing(resource, failure);

        assertThat(failure.getSuppressed()).containsExactly(closeFailure);
    }

    @Test
    void closeSuppressing_shouldThrowNullPointerException_whenResourceNull() {
        final AutoCloseable resource = null;
        final IOException failure = new IOException("original");

        assertThatThrownBy(() -> IoUtils.closeSuppressing(resource, failure))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("resource must not be null");
    }

    @Test
    void closeSuppressing_shouldThrowNullPointerException_whenFailureNull() {
        final CloseTrackingStream resource = new CloseTrackingStream(new byte[0]);
        final Throwable failure = null;

        assertThatThrownBy(() -> IoUtils.closeSuppressing(resource, failure))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("failure must not be null");
        assertThat(resource.isClosed()).isFalse();
    }

    private static byte[] sequence(final int length) {
        final byte[] bytes = new byte[length];
        for (int index = 0; index < length; index++) {
            bytes[index] = (byte) index;
        }
        return bytes;
    }

    /** Hands out at most one byte per read call, so every reading loop sees partial reads. */
    private static final class TrickleStream extends InputStream {

        private final byte[] body;
        private int position;

        private TrickleStream(final byte[] body) {
            this.body = Arrays.copyOf(body, body.length);
        }

        @Override
        public int read() {
            if (position == body.length) {
                return -1;
            }
            return body[position++] & 0xFF;
        }

        @Override
        public int read(final byte[] target, final int offset, final int length) {
            if (length == 0) {
                return 0;
            }
            final int next = read();
            if (next < 0) {
                return -1;
            }
            target[offset] = (byte) next;
            return 1;
        }
    }

    /** Reads from a byte array and records whether {@link #close()} was called. */
    private static final class CloseTrackingStream extends InputStream {

        private final ByteArrayInputStream delegate;
        private boolean isClosed;

        private CloseTrackingStream(final byte[] body) {
            this.delegate = new ByteArrayInputStream(body);
        }

        @Override
        public int read() {
            return delegate.read();
        }

        @Override
        public int read(final byte[] target, final int offset, final int length) {
            return delegate.read(target, offset, length);
        }

        @Override
        public void close() {
            isClosed = true;
        }

        private boolean isClosed() {
            return isClosed;
        }
    }
}
