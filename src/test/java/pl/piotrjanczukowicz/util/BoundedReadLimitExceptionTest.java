package pl.piotrjanczukowicz.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.junit.jupiter.api.Test;

class BoundedReadLimitExceptionTest {

    @Test
    void limit_shouldReturnLimitPassedToConstructor_whenCreated() {
        final BoundedReadLimitException exception = new BoundedReadLimitException(4096);

        assertThat(exception.limit()).isEqualTo(4096);
    }

    @Test
    void getMessage_shouldNameLimit_whenCreated() {
        final BoundedReadLimitException exception = new BoundedReadLimitException(4096);

        assertThat(exception).hasMessage("Stream exceeds the limit of 4096 bytes");
    }

    @Test
    void constructor_shouldBeIoException_whenCreated() {
        final IOException exception = new BoundedReadLimitException(1);

        assertThat(exception).isInstanceOf(BoundedReadLimitException.class);
    }
}
