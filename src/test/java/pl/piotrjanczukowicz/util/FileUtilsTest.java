package pl.piotrjanczukowicz.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.junit.jupiter.api.Test;

class FileUtilsTest {

    @Test
    void constructor_whenInstantiatedViaReflection_shouldThrow() throws Exception {
        final Constructor<FileUtils> constructor = FileUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertThatThrownBy(constructor::newInstance).hasCauseInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void readString_whenResourceExists_shouldReturnContents() {
        final Class<?> type = FileUtilsTest.class;
        final String fileName = "sample.json";

        final String text = FileUtils.readString(type, fileName);

        assertThat(text).contains("ok");
    }

    @Test
    void readBytes_whenResourceExists_shouldReturnContents() {
        final Class<?> type = FileUtilsTest.class;
        final String fileName = "sample.json";

        final byte[] bytes = FileUtils.readBytes(type, fileName);

        assertThat(new String(bytes, StandardCharsets.UTF_8)).contains("ok");
    }

    @Test
    void readString_whenTypeNull_shouldThrow() {
        final Class<?> type = null;
        final String fileName = "sample.json";

        assertThatThrownBy(() -> FileUtils.readString(type, fileName))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("type must not be null");
    }

    @Test
    void readString_whenFileNameBlank_shouldThrow() {
        final Class<?> type = FileUtilsTest.class;
        final String fileName = " ";

        assertThatThrownBy(() -> FileUtils.readString(type, fileName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("fileName must not be blank");
    }

    @Test
    void readString_whenResourceMissing_shouldThrow() {
        final Class<?> type = FileUtilsTest.class;
        final String fileName = "missing.json";

        assertThatThrownBy(() -> FileUtils.readString(type, fileName))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("missing.json");
    }

    @Test
    void readBytes_whenReadFails_shouldThrow() throws IOException {
        final InputStream stream = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("read");
            }
        };
        final Class<?> type = typeWithResource(stream);
        final String fileName = "any.bin";

        assertThatThrownBy(() -> FileUtils.readBytes(type, fileName))
                .isInstanceOf(UncheckedIOException.class)
                .hasCauseInstanceOf(IOException.class);
    }

    @Test
    void readBytes_whenCloseFails_shouldThrow() throws IOException {
        final InputStream stream = new ByteArrayInputStream(new byte[] {1}) {
            @Override
            public void close() throws IOException {
                throw new IOException("close");
            }
        };
        final Class<?> type = typeWithResource(stream);
        final String fileName = "any.bin";

        assertThatThrownBy(() -> FileUtils.readBytes(type, fileName))
                .isInstanceOf(UncheckedIOException.class)
                .hasCauseInstanceOf(IOException.class);
    }

    private static Class<?> typeWithResource(final InputStream stream) throws IOException {
        final byte[] bytecode;
        try (InputStream in = FileUtils.class.getResourceAsStream("FileUtils.class")) {
            bytecode = Objects.requireNonNull(in).readAllBytes();
        }
        return new ResourceClassLoader(stream, bytecode).definedType();
    }

    private static final class ResourceClassLoader extends ClassLoader {

        private final InputStream stream;
        private final byte[] bytecode;

        private ResourceClassLoader(final InputStream stream, final byte[] bytecode) {
            super(FileUtils.class.getClassLoader());
            this.stream = stream;
            this.bytecode = bytecode;
        }

        private Class<?> definedType() {
            return defineClass(FileUtils.class.getName(), bytecode, 0, bytecode.length);
        }

        @Override
        public InputStream getResourceAsStream(final String name) {
            return stream;
        }
    }
}
